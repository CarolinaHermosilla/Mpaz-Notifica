package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apoderados")
data class ApoderadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val estudiante: String,
    val curso: String,
    val taller: String? = null
)
