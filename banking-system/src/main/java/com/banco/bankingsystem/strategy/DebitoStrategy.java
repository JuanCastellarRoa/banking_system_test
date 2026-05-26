package com.banco.bankingsystem.strategy;

import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.exception.SaldoNoDisponibleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebitoStrategy implements MovimientoStrategy {

    @Override
    public boolean aplica(BigDecimal valor) {
        return valor != null && valor.signum() < 0;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(Cuenta cuenta, BigDecimal saldoActual, BigDecimal valor) {
        // Si el saldo es 0 y se intenta un débito → "Saldo no disponible"
        if (saldoActual.compareTo(BigDecimal.ZERO) == 0) {
            throw new SaldoNoDisponibleException();
        }
        BigDecimal nuevoSaldo = saldoActual.add(valor); // valor es negativo
        if (nuevoSaldo.signum() < 0) {
            throw new SaldoNoDisponibleException();
        }
        return nuevoSaldo;
    }
}
