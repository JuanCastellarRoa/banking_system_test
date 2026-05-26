package com.banco.bankingsystem.strategy;

import com.banco.bankingsystem.domain.Cuenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditoStrategy implements MovimientoStrategy {

    @Override
    public boolean aplica(BigDecimal valor) {
        return valor != null && valor.signum() > 0;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(Cuenta cuenta, BigDecimal saldoActual, BigDecimal valor) {
        return saldoActual.add(valor);
    }
}
