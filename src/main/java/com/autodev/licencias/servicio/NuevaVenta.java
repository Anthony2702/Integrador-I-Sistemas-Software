package com.autodev.licencias.servicio;

import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.TipoLicencia;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que ingresa el vendedor en la pantalla "Registrar venta".
 *
 * @param cliente cliente que compra
 * @param programa programa vendido
 * @param tipo nueva o renovación
 * @param fechaVenta fecha en que se cobró
 * @param monto monto antes del descuento
 * @param descuentoPorcentaje descuento de 0 a 100
 * @param medioPago Yape, Plin, Transferencia, ...
 * @param notas observaciones (opcional)
 */
public record NuevaVenta(
        Cliente cliente,
        Programa programa,
        TipoLicencia tipo,
        LocalDate fechaVenta,
        BigDecimal monto,
        BigDecimal descuentoPorcentaje,
        String medioPago,
        String notas) {
}
