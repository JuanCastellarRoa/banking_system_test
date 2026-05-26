package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.ReporteLineaDTO;
import com.banco.bankingsystem.dto.ReporteResponseDTO;
import com.banco.bankingsystem.exception.GlobalExceptionHandler;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@Import(GlobalExceptionHandler.class)
class ReporteControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private ReporteService reporteService;

    @Test
    void reporte_exitoso_retorna_200() throws Exception {
        ReporteResponseDTO response = ReporteResponseDTO.builder()
                .clienteId("abc123")
                .fechaInicio(LocalDate.of(2022, 1, 1))
                .fechaFin(LocalDate.of(2022, 12, 31))
                .movimientos(List.of())
                .totalDebitos(BigDecimal.ZERO)
                .totalCreditos(new BigDecimal("600"))
                .pdfBase64("base64")
                .build();

        when(reporteService.generar(eq("abc123"),
                eq(LocalDate.of(2022, 1, 1)),
                eq(LocalDate.of(2022, 12, 31))))
                .thenReturn(response);

        mockMvc.perform(get("/reportes")
                .param("cliente", "abc123")
                .param("fechaInicio", "2022-01-01")
                .param("fechaFin", "2022-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("abc123"))
                .andExpect(jsonPath("$.pdfBase64").value("base64"));
    }

    @Test
    void reporte_cliente_no_encontrado_retorna_404() throws Exception {
        when(reporteService.generar(eq("xyz"), any(), any()))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado"));

        mockMvc.perform(get("/reportes")
                .param("cliente", "xyz")
                .param("fechaInicio", "2022-01-01")
                .param("fechaFin", "2022-12-31"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reporte_con_movimientos_retorna_200() throws Exception {
        ReporteLineaDTO linea = ReporteLineaDTO.builder()
                .fecha(LocalDate.of(2022, 10, 2))
                .cliente("Jose Lema")
                .numeroCuenta(225487L)
                .tipo("AHORRO")
                .saldoInicial(new BigDecimal("100"))
                .estado(true)
                .movimiento(new BigDecimal("600"))
                .saldoDisponible(new BigDecimal("700"))
                .build();

        ReporteResponseDTO response = ReporteResponseDTO.builder()
                .clienteId("abc123")
                .fechaInicio(LocalDate.of(2022, 1, 1))
                .fechaFin(LocalDate.of(2022, 12, 31))
                .movimientos(List.of(linea))
                .totalDebitos(BigDecimal.ZERO)
                .totalCreditos(new BigDecimal("600"))
                .pdfBase64("pdf")
                .build();

        when(reporteService.generar(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/reportes")
                .param("cliente", "abc123")
                .param("fechaInicio", "2022-01-01")
                .param("fechaFin", "2022-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movimientos[0].cliente").value("Jose Lema"));
    }
}
