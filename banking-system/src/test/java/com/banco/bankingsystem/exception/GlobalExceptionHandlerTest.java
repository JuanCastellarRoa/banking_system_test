package com.banco.bankingsystem.exception;

import com.banco.bankingsystem.controller.ClienteController;
import com.banco.bankingsystem.dto.ClienteDTO;
import com.banco.bankingsystem.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private ClienteService clienteService;

    @Test
    void saldo_no_disponible_retorna_400() throws Exception {
        when(clienteService.obtenerPorClienteId("abc"))
                .thenThrow(new SaldoNoDisponibleException());

        mockMvc.perform(get("/clientes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Saldo no disponible"));
    }

    @Test
    void cupo_diario_excedido_retorna_400() throws Exception {
        when(clienteService.obtenerPorClienteId("abc"))
                .thenThrow(new CupoDiarioExcedidoException());

        mockMvc.perform(get("/clientes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cupo diario Excedido"));
    }

    @Test
    void recurso_no_encontrado_retorna_404() throws Exception {
        when(clienteService.obtenerPorClienteId("xyz"))
                .thenThrow(new RecursoNoEncontradoException("no existe"));

        mockMvc.perform(get("/clientes/xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void regla_negocio_retorna_409() throws Exception {
        when(clienteService.obtenerPorClienteId("abc"))
                .thenThrow(new ReglaNegocioException("regla violada"));

        mockMvc.perform(get("/clientes/abc"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("regla violada"));
    }

    @Test
    void validation_error_retorna_400_con_detalles() throws Exception {
        // Enviar DTO sin campos obligatorios → dispara MethodArgumentNotValidException
        ClienteDTO dto = ClienteDTO.builder().build(); // campos @NotBlank sin valor

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validación"));
    }

    @Test
    void exception_generica_retorna_500() throws Exception {
        when(clienteService.obtenerPorClienteId("abc"))
                .thenThrow(new RuntimeException("error inesperado"));

        mockMvc.perform(get("/clientes/abc"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
