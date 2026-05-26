package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.dto.ReporteLineaDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportePdfGeneratorTest {

    private final ReportePdfGenerator generator = new ReportePdfGenerator();

    @Test
    void generarBase64_retorna_string_base64_no_vacio() {
        List<ReporteLineaDTO> lineas = List.of(
                ReporteLineaDTO.builder()
                        .fecha(LocalDate.of(2022, 10, 2))
                        .cliente("Jose Lema")
                        .numeroCuenta(225487L)
                        .tipo("AHORRO")
                        .saldoInicial(new BigDecimal("100"))
                        .estado(true)
                        .movimiento(new BigDecimal("600"))
                        .saldoDisponible(new BigDecimal("700"))
                        .build()
        );

        String result = generator.generarBase64("abc123",
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 12, 31),
                lineas);

        assertThat(result).isNotBlank();
        // Debe ser Base64 decodificable
        byte[] decoded = Base64.getDecoder().decode(result);
        assertThat(decoded).isNotEmpty();
        // Los PDFs comienzan con %PDF
        assertThat(new String(decoded, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void generarBase64_sin_lineas_retorna_pdf_valido() {
        String result = generator.generarBase64("cliente1",
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 6, 30),
                List.of());

        assertThat(result).isNotBlank();
        byte[] decoded = Base64.getDecoder().decode(result);
        assertThat(new String(decoded, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void generarBase64_multiple_lineas() {
        List<ReporteLineaDTO> lineas = List.of(
                ReporteLineaDTO.builder()
                        .fecha(LocalDate.of(2022, 2, 1))
                        .cliente("Maria")
                        .numeroCuenta(111L)
                        .tipo("CORRIENTE")
                        .saldoInicial(new BigDecimal("500"))
                        .estado(true)
                        .movimiento(new BigDecimal("-200"))
                        .saldoDisponible(new BigDecimal("300"))
                        .build(),
                ReporteLineaDTO.builder()
                        .fecha(LocalDate.of(2022, 3, 15))
                        .cliente("Maria")
                        .numeroCuenta(111L)
                        .tipo("CORRIENTE")
                        .saldoInicial(new BigDecimal("500"))
                        .estado(false)
                        .movimiento(new BigDecimal("100"))
                        .saldoDisponible(new BigDecimal("400"))
                        .build()
        );

        String result = generator.generarBase64("clienteX",
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 12, 31),
                lineas);

        assertThat(result).isNotBlank();
    }
}
