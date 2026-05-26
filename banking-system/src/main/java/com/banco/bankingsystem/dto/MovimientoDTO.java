package com.banco.bankingsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para registrar un movimiento.
 * El campo {@code valor} debe ser positivo para crédito y negativo para débito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Long movimientoId;

    private LocalDate fecha;

    @NotNull
    private Long numeroCuenta;

    @NotNull
    private BigDecimal valor;

    private BigDecimal saldo;

    private String tipoMovimiento;
}
