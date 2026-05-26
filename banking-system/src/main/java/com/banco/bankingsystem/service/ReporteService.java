package com.banco.bankingsystem.service;

import com.banco.bankingsystem.dto.ReporteResponseDTO;

import java.time.LocalDate;

public interface ReporteService {
    ReporteResponseDTO generar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
