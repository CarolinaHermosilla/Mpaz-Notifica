package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Apoderado del curso. Es el destinatario real de las comunicaciones.
 *
 * El estudiante se guarda de forma referencial y anonimizada (por ejemplo
 * "Estudiante C. Cárdenas"), tal como exige el requerimiento: no se usan
 * nombres ni datos reales de estudiantes.
 *
 * El campo "taller" queda vacío cuando el estudiante no participa en ninguno;
 * cuando sí participa, guarda el nombre del taller (por ejemplo "Taller de Ajedrez").
 */
@Entity(tableName = "apoderados")
data class ApoderadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val estudiante: String,
    val curso: String,
    val taller: String? = null
)
