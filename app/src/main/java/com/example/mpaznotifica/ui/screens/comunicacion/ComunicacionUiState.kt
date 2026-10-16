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
    val firma: String = ""
) {
    companion object {
        /** Opciones del selector de tipo de comunicación. */
        val TIPOS = listOf(
            "Circular",
            "Citación",
            "Material de apoyo"
        )

        /** Curso del profesor que tiene la sesión iniciada (igual que en Inicio). */
        const val CURSO = "5.º Básico B"
    }
}
