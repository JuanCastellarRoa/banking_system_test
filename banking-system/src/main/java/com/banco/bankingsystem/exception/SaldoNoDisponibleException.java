package com.banco.bankingsystem.exception;

public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
