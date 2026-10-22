package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comunicaciones")
data class ComunicacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val canal: String = "",
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
