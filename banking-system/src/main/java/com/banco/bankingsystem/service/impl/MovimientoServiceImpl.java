package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.domain.TipoMovimiento;
import com.banco.bankingsystem.dto.MovimientoDTO;
import com.banco.bankingsystem.exception.CupoDiarioExcedidoException;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.SaldoNoDisponibleException;
import com.banco.bankingsystem.repository.CuentaRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import com.banco.bankingsystem.service.MovimientoService;
import com.banco.bankingsystem.strategy.MovimientoStrategy;
import com.banco.bankingsystem.strategy.MovimientoStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    /** Límite diario de retiro (suma absoluta de débitos por cuenta y día). */
    public static final BigDecimal LIMITE_DIARIO_RETIRO = new BigDecimal("1000");

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoStrategyFactory strategyFactory;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                 CuentaRepository cuentaRepository,
                                 MovimientoStrategyFactory strategyFactory) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public MovimientoDTO crear(MovimientoDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(dto.getNumeroCuenta())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada: " + dto.getNumeroCuenta()));

        BigDecimal valor = dto.getValor();
        LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();

        MovimientoStrategy strategy = strategyFactory.resolver(valor);
        BigDecimal saldoActual = obtenerSaldoActual(cuenta);

        // Validar cupo diario para débitos
        if (valor.signum() < 0) {
            validarCupoDiario(cuenta, fecha, valor);
        }

        BigDecimal nuevoSaldo = strategy.calcularNuevoSaldo(cuenta, saldoActual, valor);

        Movimiento mov = new Movimiento();
        mov.setCuenta(cuenta);
        mov.setFecha(fecha);
        mov.setValor(valor);
        mov.setSaldo(nuevoSaldo);
        mov.setTipoMovimiento(valor.signum() > 0 ? TipoMovimiento.CREDITO : TipoMovimiento.DEBITO);

        return toDto(movimientoRepository.save(mov));
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> listar() {
        return movimientoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public MovimientoDTO actualizar(Long id, MovimientoDTO dto) {
        Movimiento mov = buscar(id);
        Cuenta cuenta = mov.getCuenta();

        BigDecimal valorNuevo = dto.getValor() != null ? dto.getValor() : mov.getValor();
        LocalDate fechaNueva = dto.getFecha() != null ? dto.getFecha() : mov.getFecha();

        // Saldo de la cuenta justo antes de que se aplicara este movimiento
        BigDecimal saldoAntes = mov.getSaldo().subtract(mov.getValor());

        // Validar cupo diario excluyendo el movimiento que se está editando
        if (valorNuevo.signum() < 0) {
            validarCupoDiarioExcluyendo(cuenta, fechaNueva, valorNuevo, id);
        }

        // Calcular y validar nuevo saldo
        BigDecimal nuevoSaldo = saldoAntes.add(valorNuevo);
        if (nuevoSaldo.signum() < 0) {
            throw new SaldoNoDisponibleException();
        }

        mov.setFecha(fechaNueva);
        mov.setValor(valorNuevo);
        mov.setSaldo(nuevoSaldo);
        mov.setTipoMovimiento(valorNuevo.signum() >= 0 ? TipoMovimiento.CREDITO : TipoMovimiento.DEBITO);

        return toDto(movimientoRepository.save(mov));
    }

    @Override
    public MovimientoDTO actualizarParcial(Long id, MovimientoDTO dto) {
        return actualizar(id, dto);
    }

    @Override
    public void eliminar(Long id) {
        Movimiento mov = buscar(id);
        movimientoRepository.delete(mov);
    }

    private Movimiento buscar(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado: " + id));
    }

    /**
     * Saldo actual = último saldo registrado del movimiento más reciente,
     * o el saldo inicial de la cuenta si no hay movimientos.
     */
    private BigDecimal obtenerSaldoActual(Cuenta cuenta) {
        return movimientoRepository
                .findByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(cuenta.getNumeroCuenta())
                .stream()
                .findFirst()
                .map(Movimiento::getSaldo)
                .orElse(cuenta.getSaldoInicial());
    }

    private void validarCupoDiario(Cuenta cuenta, LocalDate fecha, BigDecimal valor) {
        validarCupoDiarioExcluyendo(cuenta, fecha, valor, null);
    }

    private void validarCupoDiarioExcluyendo(Cuenta cuenta, LocalDate fecha, BigDecimal valor, Long excluirId) {
        BigDecimal totalDebitosDia = movimientoRepository
                .findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
                        cuenta.getNumeroCuenta(), fecha, TipoMovimiento.DEBITO)
                .stream()
                .filter(m -> excluirId == null || !m.getMovimientoId().equals(excluirId))
                .map(Movimiento::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal proyectado = totalDebitosDia.add(valor.abs());
        if (proyectado.compareTo(LIMITE_DIARIO_RETIRO) > 0) {
            throw new CupoDiarioExcedidoException();
        }
    }

    private MovimientoDTO toDto(Movimiento m) {
        return MovimientoDTO.builder()
                .movimientoId(m.getMovimientoId())
                .fecha(m.getFecha())
                .numeroCuenta(m.getCuenta().getNumeroCuenta())
                .valor(m.getValor())
                .saldo(m.getSaldo())
                .tipoMovimiento(m.getTipoMovimiento().name())
                .build();
    }
}
