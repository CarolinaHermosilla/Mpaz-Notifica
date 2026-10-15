package com.example.mpaznotifica.ui.screens.inicio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Lógica de la pantalla Inicio.
 *
 * Por ahora la pantalla es informativa: la cabecera es fija (igual al mockup) y
 * los contadores y la lista se llenarán cuando existan las tablas de
 * comunicaciones y apoderados en la base de datos local.
 */
class InicioViewModel : ViewModel() {

    var uiState by mutableStateOf(InicioUiState())
        private set

    // ---------------------------------------------------------------------
    // Punto de conexión pendiente con la base de datos local:
    // los contadores (Borradores, Enviadas, Apoderados) y la lista de
    // "Comunicaciones recientes" se cargarán desde los DAO que defina el otro
    // integrante del equipo, en una etapa posterior.
    // ---------------------------------------------------------------------

    /** Acción del botón "Cerrar sesión" del encabezado. */
    fun cerrarSesion(onListo: () -> Unit) {
        onListo()
    }
}
