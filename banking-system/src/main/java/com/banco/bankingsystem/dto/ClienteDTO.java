package com.banco.bankingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long personaId;

    private String clienteId;

    @NotBlank
    private String nombre;

    @NotBlank
    private String genero;

    @NotNull
    private Integer edad;

    @NotBlank
    private String identificacion;

    @NotBlank
    private String direccion;

    @NotBlank
    private String telefono;

    private String contrasena;

    @NotNull
    private Boolean estado;
}
