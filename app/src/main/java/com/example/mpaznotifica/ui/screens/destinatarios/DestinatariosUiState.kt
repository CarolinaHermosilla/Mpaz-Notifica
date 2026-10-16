package com.example.mpaznotifica.ui.screens.destinatarios

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio

/**
 * Estado de la pantalla Destinatarios.
 *
 * Guarda los apoderados del curso, los grupos guardados, quiénes están marcados,
 * el canal elegido y los datos de la comunicación que se va a enviar (el tipo y
 * el título salen del borrador que dejó la pantalla de Comunicación).
 */
data class DestinatariosUiState(
    val esInspectoria: Boolean = false,
    val curso: String = "",
    val apoderados: List<ApoderadoEntity> = emptyList(),
    val grupos: List<GrupoConIntegrantes> = emptyList(),
    val idsSeleccionados: Set<Int> = emptySet(),
    val canalSeleccionado: String = ComunicacionRepositorio.CANALES.first(),

    // Datos del borrador que viene desde la pantalla de Comunicación.
    val tituloComunicacion: String = "",
    val tipoComunicacion: String = "",
    val hayBorrador: Boolean = false,

    val errorDestinatarios: String? = null,
    val enviando: Boolean = false,
    val cargando: Boolean = true
) {
    val cantidadSeleccionados: Int
        get() = idsSeleccionados.size

    /** Cuando están marcados todos los apoderados, el alcance es todo el curso (o colegio). */
    val todosMarcados: Boolean
        get() = apoderados.isNotEmpty() && idsSeleccionados.size == apoderados.size

    /** Texto de la cápsula de alcance, según el rol de quien entró. */
    val etiquetaAlcance: String
        get() = if (esInspectoria) "Todo el colegio (${apoderados.size})"
        else "Todo el curso (${apoderados.size})"

    /** Aviso celeste de arriba, que cambia según el rol. */
    val textoAviso: String
        get() = if (esInspectoria) {
            "Puedes enviar a todo el colegio, a un curso, a un apoderado o a los " +
                "apoderados de un taller."
        } else {
            "Solo puedes enviar a los apoderados de $curso. Para avisar a todo el " +
                "colegio o a otros cursos, la comunicación debe enviarla Inspectoría General."
        }
}
