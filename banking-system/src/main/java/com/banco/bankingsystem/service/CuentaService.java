package com.banco.bankingsystem.service;

import com.banco.bankingsystem.dto.CuentaDTO;

import java.util.List;

public interface CuentaService {
    CuentaDTO crear(CuentaDTO dto);
    CuentaDTO obtener(Long numeroCuenta);
    List<CuentaDTO> listar();
    CuentaDTO actualizar(Long numeroCuenta, CuentaDTO dto);
    CuentaDTO actualizarParcial(Long numeroCuenta, CuentaDTO dto);
    void eliminar(Long numeroCuenta);
}
