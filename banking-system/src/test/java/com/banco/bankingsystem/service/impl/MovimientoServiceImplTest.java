package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.domain.TipoCuenta;
import com.banco.bankingsystem.domain.TipoMovimiento;
import com.banco.bankingsystem.dto.MovimientoDTO;
import com.banco.bankingsystem.exception.CupoDiarioExcedidoException;
import com.banco.bankingsystem.exception.SaldoNoDisponibleException;
import com.banco.bankingsystem.repository.CuentaRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import com.banco.bankingsystem.strategy.CreditoStrategy;
import com.banco.bankingsystem.strategy.DebitoStrategy;
import com.banco.bankingsystem.strategy.MovimientoStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock private MovimientoRepository movimientoRepository;
    @Mock private CuentaRepository cuentaRepository;

    private MovimientoServiceImpl service;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        MovimientoStrategyFactory factory = new MovimientoStrategyFactory(
                List.of(new CreditoStrategy(), new DebitoStrategy()));
        service = new MovimientoServiceImpl(movimientoRepository, cuentaRepository, factory);

        cuenta = new Cuenta();
        cuenta.setNumeroCuenta(478758L);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldoInicial(new BigDecimal("2000"));
        cuenta.setEstado(true);
    }

    @Test
    void credito_aumenta_saldo_y_persiste_movimiento() {
        when(cuentaRepository.findById(478758L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(478758L))
                .thenReturn(Collections.emptyList());
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        MovimientoDTO dto = MovimientoDTO.builder()
                .numeroCuenta(478758L)
                .fecha(LocalDate.now())
                .valor(new BigDecimal("500"))
                .build();

        MovimientoDTO out = service.crear(dto);

        assertThat(out.getSaldo()).isEqualByComparingTo("2500");
        assertThat(out.getTipoMovimiento()).isEqualTo(TipoMovimiento.CREDITO.name());
    }

    @Test
    void debito_sobre_saldo_cero_lanza_saldo_no_disponible() {
        cuenta.setSaldoInicial(BigDecimal.ZERO);
        when(cuentaRepository.findById(478758L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(478758L))
                .thenReturn(Collections.emptyList());
        when(movimientoRepository.findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
                anyLong(), any(LocalDate.class), eq(TipoMovimiento.DEBITO)))
                .thenReturn(Collections.emptyList());

        MovimientoDTO dto = MovimientoDTO.builder()
                .numeroCuenta(478758L)
                .fecha(LocalDate.now())
                .valor(new BigDecimal("-100"))
                .build();

        SaldoNoDisponibleException ex = assertThrows(SaldoNoDisponibleException.class, () -> service.crear(dto));
        assertThat(ex.getMessage()).isEqualTo("Saldo no disponible");
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void debito_que_excede_cupo_diario_lanza_cupo_excedido() {
        when(cuentaRepository.findById(478758L)).thenReturn(Optional.of(cuenta));

        Movimiento previo = new Movimiento();
        previo.setValor(new BigDecimal("-900"));
        previo.setTipoMovimiento(TipoMovimiento.DEBITO);
        previo.setFecha(LocalDate.now());

        when(movimientoRepository.findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
                anyLong(), any(LocalDate.class), eq(TipoMovimiento.DEBITO)))
                .thenReturn(List.of(previo));

        MovimientoDTO dto = MovimientoDTO.builder()
                .numeroCuenta(478758L)
                .fecha(LocalDate.now())
                .valor(new BigDecimal("-200")) // 900 + 200 = 1100 > 1000
                .build();

        CupoDiarioExcedidoException ex = assertThrows(CupoDiarioExcedidoException.class, () -> service.crear(dto));
        assertThat(ex.getMessage()).isEqualTo("Cupo diario Excedido");
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void crear_debito_sin_fecha_usa_fecha_actual() {
        when(cuentaRepository.findById(478758L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(478758L))
                .thenReturn(Collections.emptyList());
        when(movimientoRepository.findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
                anyLong(), any(LocalDate.class), eq(TipoMovimiento.DEBITO)))
                .thenReturn(Collections.emptyList());
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        MovimientoDTO dto = MovimientoDTO.builder()
                .numeroCuenta(478758L)
                .valor(new BigDecimal("-200"))
                .build(); // fecha null → debe usar LocalDate.now()

        MovimientoDTO out = service.crear(dto);

        assertThat(out.getFecha()).isEqualTo(LocalDate.now());
        assertThat(out.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEBITO.name());
    }

    @Test
    void obtener_movimiento_exitoso() {
        Movimiento mov = buildMovimiento(1L, new BigDecimal("500"), new BigDecimal("2500"), TipoMovimiento.CREDITO);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));

        MovimientoDTO out = service.obtener(1L);

        assertThat(out.getMovimientoId()).isEqualTo(1L);
        assertThat(out.getSaldo()).isEqualByComparingTo("2500");
    }

    @Test
    void obtener_movimiento_no_encontrado_lanza_excepcion() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(com.banco.bankingsystem.exception.RecursoNoEncontradoException.class,
                () -> service.obtener(99L));
    }

    @Test
    void listar_movimientos_retorna_lista() {
        Movimiento mov = buildMovimiento(1L, new BigDecimal("500"), new BigDecimal("2500"), TipoMovimiento.CREDITO);
        when(movimientoRepository.findAll()).thenReturn(List.of(mov));

        List<MovimientoDTO> out = service.listar();

        assertThat(out).hasSize(1);
        assertThat(out.get(0).getValor()).isEqualByComparingTo("500");
    }

    @Test
    void actualizar_movimiento_exitoso() {
        Movimiento mov = buildMovimiento(1L, new BigDecimal("500"), new BigDecimal("2500"), TipoMovimiento.CREDITO);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // valor positivo → no valida cupo diario
        MovimientoDTO dto = MovimientoDTO.builder()
                .valor(new BigDecimal("300"))
                .fecha(LocalDate.now())
                .build();

        MovimientoDTO out = service.actualizar(1L, dto);

        assertThat(out.getValor()).isEqualByComparingTo("300");
    }

    @Test
    void actualizar_movimiento_a_debito_saldo_negativo_lanza_excepcion() {
        // mov: valor=500, saldo=600 → saldoAntes = 100
        // valorNuevo=-500 (dentro de cupo: 500 <= 1000) → nuevoSaldo = 100 - 500 = -400 → SaldoNoDisponibleException
        Movimiento mov = buildMovimiento(1L, new BigDecimal("500"), new BigDecimal("600"), TipoMovimiento.CREDITO);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));
        when(movimientoRepository.findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
                anyLong(), any(LocalDate.class), eq(TipoMovimiento.DEBITO)))
                .thenReturn(Collections.emptyList());

        MovimientoDTO dto = MovimientoDTO.builder()
                .valor(new BigDecimal("-500")) // saldoAntes = 100; nuevoSaldo = 100-500 = -400
                .fecha(LocalDate.now())
                .build();

        assertThrows(SaldoNoDisponibleException.class, () -> service.actualizar(1L, dto));
    }

    @Test
    void eliminar_movimiento_exitoso() {
        Movimiento mov = buildMovimiento(1L, new BigDecimal("500"), new BigDecimal("2500"), TipoMovimiento.CREDITO);
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(mov));

        service.eliminar(1L);

        verify(movimientoRepository).delete(mov);
    }

    private Movimiento buildMovimiento(Long id, BigDecimal valor, BigDecimal saldo, TipoMovimiento tipo) {
        Movimiento m = new Movimiento();
        m.setMovimientoId(id);
        m.setFecha(LocalDate.now());
        m.setValor(valor);
        m.setSaldo(saldo);
        m.setTipoMovimiento(tipo);
        m.setCuenta(cuenta);
        return m;
    }
}
