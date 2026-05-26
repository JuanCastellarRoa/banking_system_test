package com.banco.bankingsystem.service;

import com.banco.bankingsystem.dto.MovimientoDTO;

import java.util.List;

public interface MovimientoService {
    MovimientoDTO crear(MovimientoDTO dto);
    MovimientoDTO obtener(Long id);
    List<MovimientoDTO> listar();
    MovimientoDTO actualizar(Long id, MovimientoDTO dto);
    MovimientoDTO actualizarParcial(Long id, MovimientoDTO dto);
    void eliminar(Long id);
}
