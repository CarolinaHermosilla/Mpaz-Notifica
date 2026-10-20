package com.example.mpaznotifica.ui.screens.inicio

/**
 * Estado de la pantalla Inicio.
 *
 * Los datos de la cabecera ya no están fijos en el código: se cargan desde la
 * cuenta que inició sesión, usando el id que llega en la ruta de navegación.
 * Así, si entra la profesora jefe o alguien de Inspectoría General, el saludo,
 * el rol y el alcance de los envíos cambian solos.
 *
 * Los tres contadores y la lista de comunicaciones recientes también salen de la
 * base de datos local: los borradores y las enviadas de la tabla de
 * comunicaciones, y el total de apoderados de la tabla de apoderados.
 */
data class InicioUiState(
    /** Mientras es true, los contadores y la lista todavía se están leyendo. */
    val cargando: Boolean = true,

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
 * de la pantalla Inicio.
 */
data class ComunicacionResumen(
    val titulo: String,
    val tipo: String,
    val fecha: String,
    /** Ya viene redactado para mostrarlo: por ejemplo "8 apoderados". */
    val destinatarios: String,
    val enviada: Boolean
)
