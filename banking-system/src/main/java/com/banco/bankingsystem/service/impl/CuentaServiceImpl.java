package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.dto.CuentaDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.CuentaRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import com.banco.bankingsystem.service.CuentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
//import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoRepository movimientoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository,
                              ClienteRepository clienteRepository,
                              MovimientoRepository movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public CuentaDTO crear(CuentaDTO dto) {
        // Auto-generación comentada: el número de cuenta lo ingresa el usuario
        // long numero;
        // do {
        //     numero = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        // } while (cuentaRepository.existsById(numero));
        // dto.setNumeroCuenta(numero);

        if (cuentaRepository.existsById(dto.getNumeroCuenta())) {
            throw new ReglaNegocioException("Ya existe una cuenta con el número: " + dto.getNumeroCuenta());
        }

        Cliente cliente = clienteRepository.findByClienteId(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + dto.getClienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado() == null ? Boolean.TRUE : dto.getEstado());
        cuenta.setCliente(cliente);
        return toDto(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO obtener(Long numeroCuenta) {
        return toDto(buscar(numeroCuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> listar() {
        return cuentaRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public CuentaDTO actualizar(Long numeroCuenta, CuentaDTO dto) {
        Cuenta cuenta = buscar(numeroCuenta);
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findByClienteId(dto.getClienteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + dto.getClienteId()));
            cuenta.setCliente(cliente);
        }
        return toDto(cuentaRepository.save(cuenta));
    }

    @Override
    public CuentaDTO actualizarParcial(Long numeroCuenta, CuentaDTO dto) {
        Cuenta cuenta = buscar(numeroCuenta);
        Optional.ofNullable(dto.getTipoCuenta()).ifPresent(cuenta::setTipoCuenta);
        Optional.ofNullable(dto.getSaldoInicial()).ifPresent(cuenta::setSaldoInicial);
        Optional.ofNullable(dto.getEstado()).ifPresent(cuenta::setEstado);
        Optional.ofNullable(dto.getClienteId()).ifPresent(cid -> {
            Cliente cliente = clienteRepository.findByClienteId(cid)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + cid));
            cuenta.setCliente(cliente);
        });
        return toDto(cuentaRepository.save(cuenta));
    }

    @Override
    public void eliminar(Long numeroCuenta) {
        Cuenta cuenta = buscar(numeroCuenta);
        BigDecimal saldoActual = movimientoRepository
                .findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(numeroCuenta)
                .map(Movimiento::getSaldo)
                .orElse(cuenta.getSaldoInicial());
        if (saldoActual.compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException(
                "No se puede eliminar la cuenta: el saldo debe ser 0 (saldo actual: " + saldoActual + ").");
        }
        cuentaRepository.delete(cuenta);
    }

    private Cuenta buscar(Long numeroCuenta) {
        return cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada: " + numeroCuenta));
    }

    private CuentaDTO toDto(Cuenta c) {
        BigDecimal saldo = movimientoRepository
                .findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(c.getNumeroCuenta())
                .map(Movimiento::getSaldo)
                .orElse(c.getSaldoInicial());
        return CuentaDTO.builder()
                .numeroCuenta(c.getNumeroCuenta())
                .tipoCuenta(c.getTipoCuenta())
                .saldoInicial(c.getSaldoInicial())
                .saldo(saldo)
                .estado(c.getEstado())
                .clienteId(c.getCliente() != null ? c.getCliente().getClienteId() : null)
                .build();
    }
}
