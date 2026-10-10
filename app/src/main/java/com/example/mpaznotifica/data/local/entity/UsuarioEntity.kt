package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario que inicia sesión en la aplicación (profesor o profesora jefe).
 * Los datos son ficticios, tal como exige el requerimiento.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val perfil: String,
    val curso: String
)
