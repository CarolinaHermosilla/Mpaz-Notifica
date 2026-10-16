package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Grupo de apoderados. Sirve para enviar la misma comunicación a varios
 * apoderados sin tener que seleccionarlos uno por uno.
 *
 * "creadoPorTaller" queda en true cuando la agrupación ya existe en la escuela
 * (por ejemplo, los apoderados de los estudiantes de un taller). Los grupos que
 * crea el propio usuario quedan en false.
 */
@Entity(tableName = "grupos")
data class GrupoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val creadoPorTaller: Boolean = false
)
