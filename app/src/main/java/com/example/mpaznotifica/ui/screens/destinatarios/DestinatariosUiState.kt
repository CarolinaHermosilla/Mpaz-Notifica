package com.example.mpaznotifica.ui.screens.destinatarios

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio

data class DestinatariosUiState(
    val esInspectoria: Boolean = false,
    val curso: String = "",
    val apoderados: List<ApoderadoEntity> = emptyList(),
    val grupos: List<GrupoConIntegrantes> = emptyList(),
    val idsSeleccionados: Set<Int> = emptySet(),
    val canalSeleccionado: String = ComunicacionRepositorio.CANALES.first(),

    val tituloComunicacion: String = "",
    val tipoComunicacion: String = "",
    val hayBorrador: Boolean = false,

    val errorDestinatarios: String? = null,
    val enviando: Boolean = false,
    val cargando: Boolean = true
) {
    val cantidadSeleccionados: Int
        get() = idsSeleccionados.size

    val todosMarcados: Boolean
        get() = apoderados.isNotEmpty() && idsSeleccionados.size == apoderados.size

    val etiquetaAlcance: String
        get() = if (esInspectoria) "Todo el colegio (${apoderados.size})"
        else "Todo el curso (${apoderados.size})"

    val textoAviso: String
        get() = if (esInspectoria) {
            "Puedes enviar a todo el colegio, a un curso, a un apoderado o a los " +
                "apoderados de un taller."
        } else {
            "Solo puedes enviar a los apoderados de $curso. Para avisar a todo el " +
                "colegio o a otros cursos, la comunicación debe enviarla Inspectoría General."
        }
}
