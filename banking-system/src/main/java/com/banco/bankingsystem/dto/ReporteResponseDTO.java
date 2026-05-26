package com.banco.bankingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {
    private String clienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<ReporteLineaDTO> movimientos;
    /** Suma absoluta de todos los débitos del período. */
    private BigDecimal totalDebitos;
    /** Suma de todos los créditos del período. */
    private BigDecimal totalCreditos;
    /** PDF del reporte codificado en Base64. */
    private String pdfBase64;
}
