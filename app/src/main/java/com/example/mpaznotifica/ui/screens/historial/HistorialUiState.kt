package com.example.mpaznotifica.ui.screens.historial

/**
 * Estado de la pantalla Historial.
 *
 * El listado sale de la base de datos local: son las comunicaciones que quedaron
 * con estado "Enviada" en la pantalla de Destinatarios.
 */
data class HistorialUiState(
    val cargando: Boolean = true,
    val comunicaciones: List<ComunicacionHistorial> = emptyList()
) {
    /** Cuántas comunicaciones enviadas hay en total. */
    val total: Int get() = comunicaciones.size
}

/**
 * Una comunicación enviada, ya lista para mostrarse en pantalla.
 *
 * @param destinatarios Cantidad de apoderados que la recibieron.
 */
data class ComunicacionHistorial(
    val titulo: String,
    val tipo: String,
    val fecha: String,
    val canal: String,
    val destinatarios: Int
)
