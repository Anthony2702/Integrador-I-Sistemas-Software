package com.autodev.licencias.servicio;

import com.autodev.licencias.dao.ProgramaDAO;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.ResumenMes;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Arma el reporte "Resumen": ingresos del mes, ingresos por programa y estado de la cartera.
 */
public class ReporteServicio {

    private static final BigDecimal CERO = new BigDecimal("0.00");

    private final LicenciaServicio licencias;
    private final ProgramaDAO programaDAO;
    private final BigDecimal metaMensual;

    public ReporteServicio(LicenciaServicio licencias, ProgramaDAO programaDAO, BigDecimal metaMensual) {
        this.licencias = licencias;
        this.programaDAO = programaDAO;
        this.metaMensual = metaMensual;
    }

    /**
     * Calcula el resumen de un mes.
     *
     * @param mes mes elegido en la pantalla
     * @return datos del reporte
     */
    public ResumenMes resumen(YearMonth mes) {
        List<Licencia> todas = licencias.listar();
        List<Licencia> delMes = todas.stream()
                .filter(l -> YearMonth.from(l.getFechaVenta()).equals(mes))
                .toList();

        // Guava: agrupa las ventas del mes por nombre de programa
        ImmutableListMultimap<String, Licencia> porPrograma =
                Multimaps.index(delMes, l -> l.getPrograma().getNombre());
        Map<String, BigDecimal> ingresosPorPrograma = new LinkedHashMap<>();
        for (Programa programa : programaDAO.listar()) {
            ingresosPorPrograma.put(programa.getNombre(), sumar(porPrograma.get(programa.getNombre())));
        }

        Set<Cliente> clientesActivos = todas.stream()
                .filter(l -> l.getEstado() == EstadoLicencia.VIGENTE || l.getEstado() == EstadoLicencia.POR_VENCER)
                .map(Licencia::getCliente)
                .collect(Collectors.toSet());

        return new ResumenMes(mes, sumar(delMes), metaMensual, delMes.size(), ingresosPorPrograma,
                contar(todas, EstadoLicencia.VIGENTE),
                contar(todas, EstadoLicencia.POR_VENCER),
                contar(todas, EstadoLicencia.VENCIDA),
                clientesActivos.size());
    }

    private BigDecimal sumar(List<Licencia> lista) {
        return lista.stream().map(Licencia::getMontoFinal).reduce(CERO, BigDecimal::add);
    }

    private int contar(List<Licencia> lista, EstadoLicencia estado) {
        return (int) lista.stream().filter(l -> l.getEstado() == estado).count();
    }
}
