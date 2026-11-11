package com.example.mpaznotifica.ui.screens.inicio

data class InicioUiState(
    val cargando: Boolean = true,

    val saludo: String = "",
    val rolYCurso: String = "",
    val curso: String = "",
    val iniciales: String = "",
    val textoAlcance: String = "",

    val borradores: Int = 0,
    val enviadas: Int = 0,
    val apoderados: Int = 0,

    val comunicacionesRecientes: List<ComunicacionResumen> = emptyList(),

    /** Aviso cuando no se pudo conectar con el servidor. */
    val aviso: String? = null
)

data class ComunicacionResumen(
    val titulo: String,
    val tipo: String,
    val fecha: String,
    val destinatarios: String,
    val enviada: Boolean
)
