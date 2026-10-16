package com.example.mpaznotifica.ui.screens.grupos

import com.example.mpaznotifica.data.model.GrupoConIntegrantes

/**
 * Estado de la pantalla Grupos.
 *
 * "grupos" llega ya armado desde el repositorio: cada grupo con su lista de
 * integrantes, para que la pantalla solo tenga que dibujarlo.
 */
data class GruposUiState(
    val grupos: List<GrupoConIntegrantes> = emptyList(),
    val cargando: Boolean = true
)
