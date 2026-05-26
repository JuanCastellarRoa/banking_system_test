package com.banco.bankingsystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Persona {

    @NotBlank
    @Column(name = "cliente_id", nullable = false, unique = true, length = 50)
    private String clienteId;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String contrasena;

    @NotNull
    @Column(nullable = false)
    private Boolean estado;
}
