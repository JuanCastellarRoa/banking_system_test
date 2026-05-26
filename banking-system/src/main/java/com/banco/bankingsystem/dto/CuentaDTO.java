package com.banco.bankingsystem.dto;

import com.banco.bankingsystem.domain.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    @NotNull(message = "El número de cuenta es obligatorio")
    private Long numeroCuenta;

    @NotNull
    private TipoCuenta tipoCuenta;

    @NotNull
    private BigDecimal saldoInicial;

    private BigDecimal saldo;

    @NotNull
    private Boolean estado;

    @NotBlank
    private String clienteId;
}
