package com.autodev.licencias.modelo;

/**
 * Estados por los que pasa una licencia.
 */
public enum EstadoLicencia {
    /** Le quedan más días que el plazo de aviso. */
    VIGENTE("Vigente"),
    /** Le quedan 30 días o menos (plazo de aviso). */
    POR_VENCER("Por vencer"),
    /** Ya pasó su fecha de vencimiento y no se renovó. */
    VENCIDA("Vencida"),
    /** El cliente ya pagó la renovación; la reemplaza una licencia nueva. */
    RENOVADA("Renovada");

    private final String etiqueta;

    EstadoLicencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return texto que se guarda en la base de datos y se muestra en pantalla */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte el texto guardado en la base de datos al estado.
     *
     * @param etiqueta por ejemplo "Por vencer"
     * @return el estado correspondiente
     */
    public static EstadoLicencia desdeEtiqueta(String etiqueta) {
        for (EstadoLicencia estado : values()) {
            if (estado.etiqueta.equalsIgnoreCase(etiqueta)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + etiqueta);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
