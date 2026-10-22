package com.example.mpaznotifica.ui.screens.comunicacion

data class ComunicacionUiState(
    val tipoSeleccionado: String = "Circular",
    val titulo: String = "",
    val mensaje: String = "",
    val adjuntos: List<Adjunto> = emptyList(),

    val errorTitulo: String? = null,
    val errorMensaje: String? = null,

    val aviso: String? = null,

    val pedirPermisoCamara: Boolean = false,

    val mostrarVistaPrevia: Boolean = false,

    val firma: String = "",

    val curso: String = "",
    val rol: String = "",
    val esInspectoria: Boolean = false
) {
    companion object {
        val TIPOS = listOf(
            "Circular",
            "Citación",
            "Material de apoyo"
        )

        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar esta comunicación a todo el colegio, a un curso, a un " +
                "apoderado o a los apoderados de un taller. Los destinatarios exactos " +
                "se eligen en el paso siguiente."

        const val TEXTO_DESTINATARIOS_SIN_DATOS = "Los apoderados del curso"
    }

    val textoAlcance: String
        get() = if (esInspectoria) {
            TEXTO_ALCANCE_INSPECTORIA
        } else if (curso.isBlank()) {
            ""
        } else {
            "Se enviará a los apoderados de $curso. " +
                "Los destinatarios exactos se eligen en el paso siguiente."
        }

    val textoDestinatarios: String
        get() = when {
            esInspectoria -> "A elegir en el paso siguiente (alcance de todo el colegio)"
            curso.isNotBlank() -> "Apoderados de $curso"
            else -> TEXTO_DESTINATARIOS_SIN_DATOS
        }
}
