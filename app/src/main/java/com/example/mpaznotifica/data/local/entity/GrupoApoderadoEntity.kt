package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity

/**
 * Tabla intermedia que une un grupo con sus apoderados.
 *
 * Un grupo tiene varios apoderados y un apoderado puede estar en varios grupos,
 * así que se guarda una fila por cada combinación.
 */
@Entity(
    tableName = "grupo_apoderado",
    primaryKeys = ["grupoId", "apoderadoId"]
)
data class GrupoApoderadoEntity(
    val grupoId: Int,
    val apoderadoId: Int
)
