package com.banco.bankingsystem.exception;

public class CupoDiarioExcedidoException extends RuntimeException {
    public CupoDiarioExcedidoException() {
        super("Cupo diario Excedido");
    }
}
