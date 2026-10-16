package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity

/**
 * Tabla intermedia que registra qué apoderados recibieron cada comunicación.
 * Es lo que permite después revisar el historial por destinatario.
 */
@Entity(
    tableName = "comunicacion_destinatario",
    primaryKeys = ["comunicacionId", "apoderadoId"]
)
data class ComunicacionDestinatarioEntity(
    val comunicacionId: Int,
    val apoderadoId: Int
)
