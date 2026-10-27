package com.autodev.licencias.modelo;

/**
 * Tipo de venta: primera compra del programa o renovación de una licencia anterior.
 */
public enum TipoLicencia {
    NUEVA("Nueva"),
    RENOVACION("Renovación");

    private final String etiqueta;

    TipoLicencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return texto que se guarda en la base de datos y se muestra en pantalla */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte el texto guardado en la base de datos al tipo.
     *
     * @param etiqueta "Nueva" o "Renovación"
     * @return el tipo correspondiente
     */
    public static TipoLicencia desdeEtiqueta(String etiqueta) {
        for (TipoLicencia tipo : values()) {
            if (tipo.etiqueta.equalsIgnoreCase(etiqueta)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo desconocido: " + etiqueta);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
