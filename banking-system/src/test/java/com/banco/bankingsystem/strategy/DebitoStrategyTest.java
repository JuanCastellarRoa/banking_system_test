package com.banco.bankingsystem.strategy;

import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.exception.SaldoNoDisponibleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DebitoStrategyTest {

    private final DebitoStrategy strategy = new DebitoStrategy();

    @Test
    void debe_lanzar_saldo_no_disponible_cuando_saldo_es_cero() {
        Cuenta cuenta = new Cuenta();
        assertThrows(SaldoNoDisponibleException.class,
                () -> strategy.calcularNuevoSaldo(cuenta, BigDecimal.ZERO, new BigDecimal("-100")));
    }

    @Test
    void debe_lanzar_saldo_no_disponible_cuando_resultado_es_negativo() {
        Cuenta cuenta = new Cuenta();
        assertThrows(SaldoNoDisponibleException.class,
                () -> strategy.calcularNuevoSaldo(cuenta, new BigDecimal("50"), new BigDecimal("-100")));
    }

    @Test
    void debe_calcular_nuevo_saldo_correctamente_para_debito_valido() {
        Cuenta cuenta = new Cuenta();
        BigDecimal nuevoSaldo = strategy.calcularNuevoSaldo(cuenta, new BigDecimal("500"), new BigDecimal("-200"));
        assertThat(nuevoSaldo).isEqualByComparingTo("300");
    }
}
