package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "grupo_apoderado",
    primaryKeys = ["grupoId", "apoderadoId"]
)
data class GrupoApoderadoEntity(
    val grupoId: Int,
    val apoderadoId: Int
)
