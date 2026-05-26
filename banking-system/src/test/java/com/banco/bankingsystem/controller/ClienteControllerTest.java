package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.ClienteDTO;
import com.banco.bankingsystem.exception.GlobalExceptionHandler;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(GlobalExceptionHandler.class)
class ClienteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private ClienteService clienteService;

    private ClienteDTO buildDto() {
        return ClienteDTO.builder()
                .personaId(1L)
                .clienteId("abc123")
                .nombre("Jose Lema")
                .genero("M")
                .edad(35)
                .identificacion("1234567890")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .contrasena("1234")
                .estado(true)
                .build();
    }

    @Test
    void crear_retorna_201() throws Exception {
        ClienteDTO dto = buildDto();
        when(clienteService.crear(any(ClienteDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("abc123"));
    }

    @Test
    void listar_retorna_200() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(buildDto()));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Jose Lema"));
    }

    @Test
    void obtener_retorna_200() throws Exception {
        when(clienteService.obtenerPorClienteId("abc123")).thenReturn(buildDto());

        mockMvc.perform(get("/clientes/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Jose Lema"));
    }

    @Test
    void obtener_no_encontrado_retorna_404() throws Exception {
        when(clienteService.obtenerPorClienteId("xyz")).thenThrow(new RecursoNoEncontradoException("no existe"));

        mockMvc.perform(get("/clientes/xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_retorna_200() throws Exception {
        ClienteDTO dto = buildDto();
        when(clienteService.actualizar(eq("abc123"), any(ClienteDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/clientes/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarParcial_retorna_200() throws Exception {
        ClienteDTO dto = ClienteDTO.builder().nombre("Nuevo").build();
        when(clienteService.actualizarParcial(eq("abc123"), any(ClienteDTO.class))).thenReturn(buildDto());

        mockMvc.perform(patch("/clientes/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_retorna_204() throws Exception {
        doNothing().when(clienteService).eliminar("abc123");

        mockMvc.perform(delete("/clientes/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_conflicto_retorna_409() throws Exception {
        doThrow(new ReglaNegocioException("tiene cuentas")).when(clienteService).eliminar("abc123");

        mockMvc.perform(delete("/clientes/abc123"))
                .andExpect(status().isConflict());
    }
}
