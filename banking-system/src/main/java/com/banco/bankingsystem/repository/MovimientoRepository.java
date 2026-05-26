package com.banco.bankingsystem.repository;

import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.domain.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    Optional<Movimiento> findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(Long numeroCuenta);

    List<Movimiento> findByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(Long numeroCuenta);

    List<Movimiento> findByCuentaClienteClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
            String clienteId, LocalDate inicio, LocalDate fin);

    List<Movimiento> findByCuentaNumeroCuentaAndFechaAndTipoMovimiento(
            Long numeroCuenta, LocalDate fecha, TipoMovimiento tipoMovimiento);
}
