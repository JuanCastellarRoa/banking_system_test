package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.domain.TipoCuenta;
import com.banco.bankingsystem.dto.CuentaDTO;
import com.banco.bankingsystem.exception.GlobalExceptionHandler;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.service.CuentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
@Import(GlobalExceptionHandler.class)
class CuentaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private CuentaService cuentaService;

    private CuentaDTO buildDto() {
        return CuentaDTO.builder()
                .numeroCuenta(225487L)
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500"))
                .saldo(new BigDecimal("500"))
                .estado(true)
                .clienteId("abc123")
                .build();
    }

    @Test
    void crear_retorna_201() throws Exception {
        when(cuentaService.crear(any(CuentaDTO.class))).thenReturn(buildDto());

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value(225487));
    }

    @Test
    void listar_retorna_200() throws Exception {
        when(cuentaService.listar()).thenReturn(List.of(buildDto()));

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value("abc123"));
    }

    @Test
    void obtener_retorna_200() throws Exception {
        when(cuentaService.obtener(225487L)).thenReturn(buildDto());

        mockMvc.perform(get("/cuentas/225487"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(500));
    }

    @Test
    void obtener_no_encontrada_retorna_404() throws Exception {
        when(cuentaService.obtener(999L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        mockMvc.perform(get("/cuentas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_retorna_200() throws Exception {
        when(cuentaService.actualizar(eq(225487L), any(CuentaDTO.class))).thenReturn(buildDto());

        mockMvc.perform(put("/cuentas/225487")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarParcial_retorna_200() throws Exception {
        CuentaDTO dto = CuentaDTO.builder().estado(false).build();
        when(cuentaService.actualizarParcial(eq(225487L), any(CuentaDTO.class))).thenReturn(buildDto());

        mockMvc.perform(patch("/cuentas/225487")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_retorna_204() throws Exception {
        doNothing().when(cuentaService).eliminar(225487L);

        mockMvc.perform(delete("/cuentas/225487"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_saldo_no_cero_retorna_409() throws Exception {
        doThrow(new ReglaNegocioException("saldo no 0")).when(cuentaService).eliminar(225487L);

        mockMvc.perform(delete("/cuentas/225487"))
                .andExpect(status().isConflict());
    }
}
