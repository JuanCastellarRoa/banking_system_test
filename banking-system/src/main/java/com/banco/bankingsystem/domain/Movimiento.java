package com.banco.bankingsystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long movimientoId;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "numero_cuenta_fk", referencedColumnName = "numero_cuenta", nullable = false)
    private Cuenta cuenta;
}
