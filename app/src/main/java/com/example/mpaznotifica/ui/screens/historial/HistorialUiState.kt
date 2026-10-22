package com.example.mpaznotifica.ui.screens.historial

data class HistorialUiState(
    val cargando: Boolean = true,
    val comunicaciones: List<ComunicacionHistorial> = emptyList()
) {
    val total: Int get() = comunicaciones.size
}

data class ComunicacionHistorial(
    val titulo: String,
    val tipo: String,
    val fecha: String,
    val canal: String,
    val destinatarios: Int
)
