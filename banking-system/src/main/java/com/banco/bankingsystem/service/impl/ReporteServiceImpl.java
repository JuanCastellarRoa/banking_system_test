package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.dto.ReporteLineaDTO;
import com.banco.bankingsystem.dto.ReporteResponseDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import com.banco.bankingsystem.service.ReporteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final ClienteRepository clienteRepository;
    private final MovimientoRepository movimientoRepository;
    private final ReportePdfGenerator pdfGenerator;

    public ReporteServiceImpl(ClienteRepository clienteRepository,
                              MovimientoRepository movimientoRepository,
                              ReportePdfGenerator pdfGenerator) {
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public ReporteResponseDTO generar(String clienteId, LocalDate inicio, LocalDate fin) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + clienteId));

        List<Movimiento> movs = movimientoRepository
                .findByCuentaClienteClienteIdAndFechaBetweenOrderByFechaAscMovimientoIdAsc(
                        clienteId, inicio, fin);

        List<ReporteLineaDTO> lineas = movs.stream()
                .map(m -> ReporteLineaDTO.builder()
                        .fecha(m.getFecha())
                        .cliente(cliente.getNombre())
                        .numeroCuenta(m.getCuenta().getNumeroCuenta())
                        .tipo(m.getCuenta().getTipoCuenta().name())
                        .saldoInicial(m.getCuenta().getSaldoInicial())
                        .estado(m.getCuenta().getEstado())
                        .movimiento(m.getValor())
                        .saldoDisponible(m.getSaldo())
                        .build())
                .toList();

        String pdfBase64 = pdfGenerator.generarBase64(cliente.getNombre(), inicio, fin, lineas);

        BigDecimal totalDebitos = movs.stream()
                .map(Movimiento::getValor)
                .filter(v -> v.signum() < 0)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = movs.stream()
                .map(Movimiento::getValor)
                .filter(v -> v.signum() > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReporteResponseDTO.builder()
                .clienteId(clienteId)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .movimientos(lineas)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .pdfBase64(pdfBase64)
                .build();
    }
}
