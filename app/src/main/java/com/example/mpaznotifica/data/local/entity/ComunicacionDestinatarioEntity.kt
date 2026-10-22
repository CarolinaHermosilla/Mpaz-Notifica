package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "comunicacion_destinatario",
    primaryKeys = ["comunicacionId", "apoderadoId"]
)
data class ComunicacionDestinatarioEntity(
    val comunicacionId: Int,
    val apoderadoId: Int
)
