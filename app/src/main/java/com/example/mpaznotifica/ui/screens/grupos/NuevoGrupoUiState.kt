package com.example.mpaznotifica.ui.screens.grupos

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.model.GrupoConIntegrantes

data class NuevoGrupoUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val apoderados: List<ApoderadoEntity> = emptyList(),
    val idsSeleccionados: Set<Int> = emptySet(),
    val gruposExistentes: List<GrupoConIntegrantes> = emptyList(),

    val errorNombre: String? = null,
    val errorIntegrantes: String? = null,

    val guardando: Boolean = false,
    val cargando: Boolean = true
) {
    val cantidadSeleccionados: Int
        get() = idsSeleccionados.size
}
