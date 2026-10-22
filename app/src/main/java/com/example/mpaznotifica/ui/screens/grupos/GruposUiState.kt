package com.example.mpaznotifica.ui.screens.grupos

import com.example.mpaznotifica.data.model.GrupoConIntegrantes

data class GruposUiState(
    val grupos: List<GrupoConIntegrantes> = emptyList(),
    val cargando: Boolean = true
)
