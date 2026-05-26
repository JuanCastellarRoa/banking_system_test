package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.domain.TipoCuenta;
import com.banco.bankingsystem.domain.TipoMovimiento;
import com.banco.bankingsystem.dto.ReporteResponseDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private MovimientoRepository movimientoRepository;
    @Mock private ReportePdfGenerator pdfGenerator;

    private ReporteServiceImpl service;

    private Cliente cliente;
    private Cuenta cuenta;
    private LocalDate inicio;
    private LocalDate fin;

    @BeforeEach
    void setUp() {
        service = new ReporteServiceImpl(clienteRepository, movimientoRepository, pdfGenerator);

        cliente = new Cliente();
        cliente.setClienteId("abc123");
        cliente.setNombre("Jose Lema");

        cuenta = new Cuenta();
        cuenta.setNumeroCuenta(225487L);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldoInicial(new BigDecimal("100"));
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);

        inicio = LocalDate.of(2022, 1, 1);
        fin = LocalDate.of(2022, 12, 31);
    }

    private Movimiento buildMovimiento(BigDecimal valor, BigDecimal saldo, TipoMovimiento tipo) {
        Movimiento m = new Movimiento();
        m.setFecha(LocalDate.of(2022, 10, 2));
        m.setValor(valor);
        m.setSaldo(saldo);
        m.setTipoMovimiento(tipo);
        m.setCuenta(cuenta);
        return m;
    }

    @Test
    void generar_reporte_exitoso_con_creditos_y_debitos() {
        Movimiento credito = buildMovimiento(new BigDecimal("600"), new BigDecimal("700"), TipoMovimiento.CREDITO);
        Movimiento debito = buildMovimiento(new BigDecimal("-540"), new BigDecimal("160"), TipoMovimiento.DEBITO);

        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaClienteClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
                eq("abc123"), eq(inicio), eq(fin)))
                .thenReturn(List.of(credito, debito));
        when(pdfGenerator.generarBase64(anyString(), any(), any(), any())).thenReturn("base64pdf");

        ReporteResponseDTO resultado = service.generar("abc123", inicio, fin);

        assertThat(resultado.getTotalCreditos()).isEqualByComparingTo("600");
        assertThat(resultado.getTotalDebitos()).isEqualByComparingTo("540");
        assertThat(resultado.getMovimientos()).hasSize(2);
        assertThat(resultado.getPdfBase64()).isEqualTo("base64pdf");
    }

    @Test
    void generar_reporte_sin_movimientos() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaClienteClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
                eq("abc123"), eq(inicio), eq(fin)))
                .thenReturn(List.of());
        when(pdfGenerator.generarBase64(anyString(), any(), any(), any())).thenReturn("emptyPdf");

        ReporteResponseDTO resultado = service.generar("abc123", inicio, fin);

        assertThat(resultado.getTotalCreditos()).isEqualByComparingTo("0");
        assertThat(resultado.getTotalDebitos()).isEqualByComparingTo("0");
        assertThat(resultado.getMovimientos()).isEmpty();
    }

    @Test
    void generar_reporte_cliente_no_encontrado_lanza_excepcion() {
        when(clienteRepository.findByClienteId("xyz")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.generar("xyz", inicio, fin));
    }

    @Test
    void generar_reporte_solo_creditos() {
        Movimiento c1 = buildMovimiento(new BigDecimal("200"), new BigDecimal("300"), TipoMovimiento.CREDITO);
        Movimiento c2 = buildMovimiento(new BigDecimal("400"), new BigDecimal("700"), TipoMovimiento.CREDITO);

        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(cliente));
        when(movimientoRepository.findByCuentaClienteClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
                eq("abc123"), eq(inicio), eq(fin)))
                .thenReturn(List.of(c1, c2));
        when(pdfGenerator.generarBase64(anyString(), any(), any(), any())).thenReturn("pdf");

        ReporteResponseDTO resultado = service.generar("abc123", inicio, fin);

        assertThat(resultado.getTotalCreditos()).isEqualByComparingTo("600");
        assertThat(resultado.getTotalDebitos()).isEqualByComparingTo("0");
    }
}
