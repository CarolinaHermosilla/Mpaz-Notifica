package com.example.mpaznotifica.ui.screens.inicio

/**
 * Estado de la pantalla Inicio.
 *
 * Los datos de la cabecera ya no están fijos en el código: se cargan desde la
 * cuenta que inició sesión, usando el id que llega en la ruta de navegación.
 * Así, si entra la profesora jefe o alguien de Inspectoría General, el saludo,
 * el rol y el alcance de los envíos cambian solos.
 *
 * Los contadores y la lista de comunicaciones recientes todavía no tienen de dónde
 * leerse: las tablas de comunicaciones y de apoderados se agregan en una etapa
 * posterior. Mientras eso no exista, la lista va vacía y los contadores en cero.
 */
data class InicioUiState(
    // Datos de la sesión (vienen de la cuenta que inició sesión).
    val saludo: String = "",
    val rolYCurso: String = "",
    val curso: String = "",
    val iniciales: String = "",
    val textoAlcance: String = "",

    // Totales de las tarjetas con contador.
    val borradores: Int = 0,
    val enviadas: Int = 0,
    val apoderados: Int = 0,

    // Comunicaciones recientes que van debajo de las tarjetas.
    val comunicacionesRecientes: List<ComunicacionResumen> = emptyList()
)

/**
 * Resumen de una comunicación, para las tarjetas de "Comunicaciones recientes"
 * y también para las del Historial.
 */
data class ComunicacionResumen(
    val titulo: String,
    val tipo: String,
    val fecha: String,
    val destinatarios: String,
    val enviada: Boolean
)
