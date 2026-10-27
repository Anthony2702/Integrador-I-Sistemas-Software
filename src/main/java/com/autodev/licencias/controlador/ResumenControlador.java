package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.ResumenPanel;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la pantalla "Resumen".
 */
public class ResumenControlador {

    private static final int MESES_A_MOSTRAR = 12;

    private final ResumenPanel vista;
    private final Contexto contexto;

    public ResumenControlador(ResumenPanel vista, Contexto contexto) {
        this.vista = vista;
        this.contexto = contexto;

        List<YearMonth> meses = new ArrayList<>();
        YearMonth actual = YearMonth.from(contexto.getLicencias().hoy());
        for (int i = 0; i < MESES_A_MOSTRAR; i++) {
            meses.add(actual.minusMonths(i));
        }
        vista.cargarMeses(meses);
        vista.getCboMes().addActionListener(e -> {
            try {
                refrescar();
            } catch (RuntimeException ex) {
                Dialogos.error(vista, ex);
            }
        });
    }

    /** Vuelve a calcular el resumen del mes elegido. */
    public void refrescar() {
        YearMonth mes = (YearMonth) vista.getCboMes().getSelectedItem();
        if (mes != null) {
            vista.mostrar(contexto.getReportes().resumen(mes));
        }
    }
}
