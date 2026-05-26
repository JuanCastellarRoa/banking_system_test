package com.banco.bankingsystem.strategy;

import com.banco.bankingsystem.domain.Cuenta;

import java.math.BigDecimal;

/**
 * Estrategia de cálculo del nuevo saldo para un movimiento.
 * Aplica el patrón Strategy para evitar condicionales tipo-de-movimiento
 * dispersos en la capa de servicio.
 */
public interface MovimientoStrategy {

    /** Indica si esta estrategia aplica al signo del valor recibido. */
    boolean aplica(BigDecimal valor);

    /**
     * Calcula el nuevo saldo de la cuenta tras aplicar el movimiento.
     * Debe lanzar excepciones de negocio si la operación no es válida.
     */
    BigDecimal calcularNuevoSaldo(Cuenta cuenta, BigDecimal saldoActual, BigDecimal valor);
}
