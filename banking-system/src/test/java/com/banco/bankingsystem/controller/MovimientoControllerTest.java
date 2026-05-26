package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.MovimientoDTO;
import com.banco.bankingsystem.exception.GlobalExceptionHandler;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.SaldoNoDisponibleException;
import com.banco.bankingsystem.service.MovimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
@Import(GlobalExceptionHandler.class)
class MovimientoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private MovimientoService movimientoService;

    private MovimientoDTO buildDto() {
        return MovimientoDTO.builder()
                .movimientoId(1L)
                .fecha(LocalDate.of(2022, 1, 10))
                .numeroCuenta(225487L)
                .valor(new BigDecimal("600"))
                .saldo(new BigDecimal("700"))
                .tipoMovimiento("CREDITO")
                .build();
    }

    @Test
    void crear_retorna_201() throws Exception {
        when(movimientoService.crear(any(MovimientoDTO.class))).thenReturn(buildDto());

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movimientoId").value(1));
    }

    @Test
    void crear_saldo_insuficiente_retorna_400() throws Exception {
        when(movimientoService.crear(any(MovimientoDTO.class)))
                .thenThrow(new SaldoNoDisponibleException());

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_retorna_200() throws Exception {
        when(movimientoService.listar()).thenReturn(List.of(buildDto()));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimiento").value("CREDITO"));
    }

    @Test
    void obtener_retorna_200() throws Exception {
        when(movimientoService.obtener(1L)).thenReturn(buildDto());

        mockMvc.perform(get("/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCuenta").value(225487));
    }

    @Test
    void obtener_no_encontrado_retorna_404() throws Exception {
        when(movimientoService.obtener(99L)).thenThrow(new RecursoNoEncontradoException("no existe"));

        mockMvc.perform(get("/movimientos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_retorna_200() throws Exception {
        when(movimientoService.actualizar(eq(1L), any(MovimientoDTO.class))).thenReturn(buildDto());

        mockMvc.perform(put("/movimientos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarParcial_retorna_200() throws Exception {
        MovimientoDTO dto = MovimientoDTO.builder().valor(new BigDecimal("100")).build();
        when(movimientoService.actualizarParcial(eq(1L), any(MovimientoDTO.class))).thenReturn(buildDto());

        mockMvc.perform(patch("/movimientos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_retorna_204() throws Exception {
        doNothing().when(movimientoService).eliminar(1L);

        mockMvc.perform(delete("/movimientos/1"))
                .andExpect(status().isNoContent());
    }
}
