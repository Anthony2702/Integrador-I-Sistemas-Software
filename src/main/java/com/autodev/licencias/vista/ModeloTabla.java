package com.autodev.licencias.vista;

import javax.swing.table.DefaultTableModel;

/**
 * Modelo de tabla de solo lectura. Indica el tipo de cada columna para que se ordene bien.
 */
public class ModeloTabla extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    private final Class<?>[] tipos;

    /**
     * @param columnas nombres de las columnas
     * @param tipos tipo de dato de cada columna (Integer, String, ...)
     */
    public ModeloTabla(String[] columnas, Class<?>[] tipos) {
        super(columnas, 0);
        this.tipos = tipos;
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        return tipos[columna];
    }

    /** Borra todas las filas. */
    public void limpiar() {
        setRowCount(0);
    }
}
