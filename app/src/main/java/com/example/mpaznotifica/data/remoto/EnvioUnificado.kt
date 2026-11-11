package com.example.mpaznotifica.data.remoto

/**
 * Un envío ya unificado, sin importar de qué servicio vino.
 *
 * ms-historial devuelve los registros de envío, pero ms-mensajeria también sabe
 * qué comunicaciones quedaron enviadas. Se juntan los dos para que el Historial
 * no pierda ninguna comunicación aunque un servicio no haya alcanzado a avisar
 * al otro.
 */
data class EnvioUnificado(
    val mensajeId: Long,
    val titulo: String,
    val tipo: String,
    val canal: String,
    val fecha: String,
    val cantidadDestinatarios: Int
)
