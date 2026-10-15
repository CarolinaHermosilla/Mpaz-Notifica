package com.example.mpaznotifica.ui.screens.inicio

/**
 * Estado de la pantalla Inicio.
 *
 * Los tres contadores y la lista de comunicaciones recientes todavía no tienen
 * de dónde leerse: las tablas de comunicaciones y de apoderados las define la
 * otra integrante del equipo en la base de datos local. Mientras eso no exista,
 * la lista va vacía y los contadores en cero, y la pantalla muestra su mensaje
 * de "todavía no hay comunicaciones".
 */
data class InicioUiState(
    // Datos de la sesión, tal como aparecen en la captura del mockup.
    // El nombre del profesor todavía no lo entrega nadie: cuando el Login
    // devuelva el usuario que inició sesión, estos valores se cargan desde ahí.
    val saludo: String = SALUDO_FIJO,
    val nombreProfesor: String = NOMBRE_PROFESOR,
    val curso: String = CURSO,
    val iniciales: String = INICIALES,

    // Totales de las tarjetas con contador.
    val borradores: Int = 0,
    val enviadas: Int = 0,
    val apoderados: Int = 0,

    // Comunicaciones recientes que van debajo de las tarjetas.
    val comunicacionesRecientes: List<ComunicacionResumen> = emptyList()
) {
    companion object {
        const val SALUDO_FIJO = "Hola, Andrea"
        const val NOMBRE_PROFESOR = "Profesora jefe de 5.º Básico B"
        const val CURSO = "5.º Básico B"

        /** Letras del círculo del encabezado: Andrea Muñoz. */
        const val INICIALES = "AM"
    }
}

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
