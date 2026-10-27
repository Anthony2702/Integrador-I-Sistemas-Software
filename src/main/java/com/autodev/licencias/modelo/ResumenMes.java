package com.autodev.licencias.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Map;

/**
 * Datos del reporte "Resumen": ventas del mes elegido y estado actual de la cartera.
 *
 * @param mes mes del reporte
 * @param ingresos total cobrado en el mes
 * @param meta meta de ventas del mes
 * @param licenciasVendidas cantidad de licencias vendidas en el mes
 * @param ingresosPorPrograma total cobrado por programa (incluye los que no vendieron)
 * @param vigentes licencias vigentes hoy
 * @param porVencer licencias que vencen en 30 días o menos
 * @param vencidas licencias vencidas sin renovar
 * @param clientesActivos clientes con al menos una licencia vigente o por vencer
 */
public record ResumenMes(
        YearMonth mes,
        BigDecimal ingresos,
        BigDecimal meta,
        int licenciasVendidas,
        Map<String, BigDecimal> ingresosPorPrograma,
        int vigentes,
        int porVencer,
        int vencidas,
        int clientesActivos) {

    /** @return porcentaje de la meta alcanzado (0 si no hay meta) */
    public int porcentajeMeta() {
        if (meta == null || meta.signum() <= 0) {
            return 0;
        }
        return ingresos.multiply(BigDecimal.valueOf(100)).divide(meta, 0, RoundingMode.DOWN).intValue();
    }
}
