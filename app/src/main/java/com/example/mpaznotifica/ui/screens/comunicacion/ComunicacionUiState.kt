package com.example.mpaznotifica.ui.screens.comunicacion

/**
 * Estado de la pantalla Comunicación.
 *
 * Toda la información que se ve en pantalla sale de acá, incluidos los mensajes
 * de error de cada campo: la pantalla solo muestra el estado y avisa cuando el
 * profesor escribe, adjunta una imagen o aprieta un botón.
 */
data class ComunicacionUiState(
    val tipoSeleccionado: String = "Circular",
    val titulo: String = "",
    val mensaje: String = "",
    val adjuntos: List<Adjunto> = emptyList(),

    val errorTitulo: String? = null,
    val errorMensaje: String? = null,

    /** Aviso que se muestra abajo (por ejemplo, si no hay aplicación de cámara). */
    val aviso: String? = null,

    /** Cuando queda en true, la pantalla pide el permiso de cámara. */
    val pedirPermisoCamara: Boolean = false,

    /** Controla si la hoja de vista previa está abierta. */
    val mostrarVistaPrevia: Boolean = false,

    /**
     * Autor de la comunicación: se carga desde la cuenta que inició sesión,
     * porque la comunicación la firma quien la envía, no siempre la misma persona.
     */
    val firma: String = "",

    /**
     * Curso y rol de la cuenta que inició sesión. Se usan para la caja que
     * recuerda a quiénes llega la comunicación y para el campo "Destinatarios"
     * de la vista previa: una cuenta de Inspectoría General no está amarrada a
     * un curso, así que el texto cambia según el rol.
     */
    val curso: String = "",
    val rol: String = "",
    val esInspectoria: Boolean = false
) {
    companion object {
        /** Opciones del selector de tipo de comunicación. */
        val TIPOS = listOf(
            "Circular",
            "Citación",
            "Material de apoyo"
        )

        /**
         * Texto que aparece cuando la cuenta es de Inspectoría General, que puede
         * enviar a todo el colegio y no solo a los apoderados de un curso.
         */
        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar esta comunicación a todo el colegio, a un curso, a un " +
                "apoderado o a los apoderados de un taller. Los destinatarios exactos " +
                "se eligen en el paso siguiente."

        /** Texto de respaldo mientras se leen los datos de la cuenta. */
        const val TEXTO_DESTINATARIOS_SIN_DATOS = "Los apoderados del curso"
    }

    /** A quiénes llega la comunicación, según la cuenta que inició sesión. */
    val textoAlcance: String
        get() = if (esInspectoria) {
            TEXTO_ALCANCE_INSPECTORIA
        } else if (curso.isBlank()) {
            // Todavía no se han leído los datos de la cuenta.
            ""
        } else {
            "Se enviará a los apoderados de $curso. " +
                "Los destinatarios exactos se eligen en el paso siguiente."
        }

    /** Cómo se muestra el destino en el campo "Destinatarios" de la vista previa. */
    val textoDestinatarios: String
        get() = when {
            esInspectoria -> "A elegir en el paso siguiente (alcance de todo el colegio)"
            curso.isNotBlank() -> "Apoderados de $curso"
            else -> TEXTO_DESTINATARIOS_SIN_DATOS
        }
}
