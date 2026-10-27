package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.EstadoLicencia;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Pinta la columna "Estado" con un color por estado (verde, ámbar, rojo o gris).
 */
public class EstadoRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        Component celda = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
        if (valor instanceof EstadoLicencia estado && !seleccionada) {
            celda.setForeground(Estilos.colorEstado(estado));
        }
        celda.setFont(celda.getFont().deriveFont(Font.BOLD));
        return celda;
    }
}
