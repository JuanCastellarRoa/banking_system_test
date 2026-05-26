package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.dto.ReporteLineaDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Component
public class ReportePdfGenerator {

    public String generarBase64(String clienteId, LocalDate inicio, LocalDate fin, List<ReporteLineaDTO> lineas) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            try (Document doc = new Document(pdfDoc)) {
                doc.add(new Paragraph("Estado de Cuenta")
                        .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
                doc.add(new Paragraph(String.format(
                        "Cliente: %s    Periodo: %s a %s", clienteId, inicio, fin)));

                float[] cols = {70, 100, 80, 60, 70, 50, 80, 80};
                Table table = new Table(UnitValue.createPointArray(cols));
                String[] headers = {"Fecha", "Cliente", "NumCuenta", "Tipo",
                        "SaldoInicial", "Estado", "Movimiento", "SaldoDisp."};
                for (String h : headers) {
                    table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));
                }
                for (ReporteLineaDTO l : lineas) {
                    table.addCell(String.valueOf(l.getFecha()));
                    table.addCell(l.getCliente());
                    table.addCell(String.valueOf(l.getNumeroCuenta()));
                    table.addCell(l.getTipo());
                    table.addCell(String.valueOf(l.getSaldoInicial()));
                    table.addCell(String.valueOf(l.getEstado()));
                    table.addCell(String.valueOf(l.getMovimiento()));
                    table.addCell(String.valueOf(l.getSaldoDisponible()));
                }
                doc.add(table);
            }
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}
