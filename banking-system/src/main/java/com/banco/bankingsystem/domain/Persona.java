package com.banco.bankingsystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Long personaId;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String genero;

    @NotNull
    @Column(nullable = false)
    private Integer edad;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String direccion;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String telefono;
}
