package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Prueba de la exportación de licencias a Excel con Apache POI.
 */
class ExportadorExcelTest {

    @TempDir
    Path carpeta;

    private Licencia licencia(String cliente, LocalDate fecha) {
        Licencia licencia = new Licencia();
        licencia.setCliente(new Cliente(1, cliente, "20601234561", "987654321"));
        licencia.setPrograma(new Programa(1, "AUTODEV", new BigDecimal("250.00")));
        licencia.setFechaVenta(fecha);
        licencia.setFechaEmision(fecha);
        licencia.calcularVencimiento();
        licencia.setMonto(new BigDecimal("250.00"));
        licencia.setMedioPago("Yape");
        return licencia;
    }

    @Test
    void exportaUnaFilaPorLicenciaConEncabezados() throws Exception {
        Path archivo = carpeta.resolve("licencias.xlsx");
        List<Licencia> licencias = List.of(
                licencia("Comercial Andina SAC", LocalDate.of(2026, 10, 25)),
                licencia("Botica Salud Total", LocalDate.of(2026, 9, 1)));

        new ExportadorExcel().exportarLicencias(licencias, LicenciaServicioTest.HOY, archivo);

        try (InputStream entrada = Files.newInputStream(archivo); Workbook libro = new XSSFWorkbook(entrada)) {
            Sheet hoja = libro.getSheet("Licencias");
            assertEquals("Cliente", hoja.getRow(0).getCell(1).getStringCellValue());
            assertEquals(2, hoja.getLastRowNum());
            assertEquals("Comercial Andina SAC", hoja.getRow(1).getCell(1).getStringCellValue());
            assertEquals(250.0, hoja.getRow(1).getCell(6).getNumericCellValue());
        }
    }
}
