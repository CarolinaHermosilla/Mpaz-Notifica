package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Comunicación redactada en la aplicación.
 *
 * Primero se guarda como BORRADOR (cuando se sale de la pantalla de Comunicación)
 * y después pasa a ENVIADA cuando se eligen los destinatarios y se confirma el
 * envío en la pantalla de Destinatarios.
 *
 * La fecha se guarda ya formateada como texto (por ejemplo "02-10-2026 09:15"),
 * que es como se muestra en la aplicación.
 */
@Entity(tableName = "comunicaciones")
data class ComunicacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val canal: String = "",
    /** Nombres de los archivos adjuntos, separados por coma. */
    val adjuntos: String = "",
    val fecha: String = "",
    val estado: String = ESTADO_BORRADOR,
    val usuarioId: Int
) {
    companion object {
        const val ESTADO_BORRADOR = "Borrador"
        const val ESTADO_ENVIADA = "Enviada"
    }
}
