package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GrupoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val creadoPorTaller: Boolean = false
)
