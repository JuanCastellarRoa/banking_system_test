package com.banco.bankingsystem.repository;

import com.banco.bankingsystem.domain.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByClienteClienteId(String clienteId);
    boolean existsByClienteClienteIdAndEstadoTrue(String clienteId);
}
