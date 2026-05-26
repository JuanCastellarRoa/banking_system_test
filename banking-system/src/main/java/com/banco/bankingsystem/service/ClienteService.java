package com.banco.bankingsystem.service;

import com.banco.bankingsystem.dto.ClienteDTO;

import java.util.List;

public interface ClienteService {
    ClienteDTO crear(ClienteDTO dto);
    ClienteDTO obtenerPorClienteId(String clienteId);
    List<ClienteDTO> listar();
    ClienteDTO actualizar(String clienteId, ClienteDTO dto);
    ClienteDTO actualizarParcial(String clienteId, ClienteDTO dto);
    void eliminar(String clienteId);
}
