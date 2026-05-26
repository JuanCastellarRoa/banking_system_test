package com.banco.bankingsystem.strategy;

import com.banco.bankingsystem.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MovimientoStrategyFactory {

    private final List<MovimientoStrategy> strategies;

    public MovimientoStrategyFactory(List<MovimientoStrategy> strategies) {
        this.strategies = strategies;
    }

    public MovimientoStrategy resolver(BigDecimal valor) {
        if (valor == null || valor.signum() == 0) {
            throw new ReglaNegocioException("El valor del movimiento no puede ser cero o nulo");
        }
        return strategies.stream()
                .filter(s -> s.aplica(valor))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No existe estrategia para el movimiento"));
    }
}
