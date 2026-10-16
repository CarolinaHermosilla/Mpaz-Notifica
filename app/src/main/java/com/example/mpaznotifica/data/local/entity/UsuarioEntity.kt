package com.example.mpaznotifica.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Usuario que inicia sesión en la aplicación.
 *
 * El rol es un atributo de la cuenta (campo "perfil"): el usuario no lo elige en
 * ninguna pantalla, lo asigna la escuela al crear la cuenta. Hoy existen dos roles
 * (profesor o profesora jefe, e Inspectoría General) y el modelo admite más.
 *
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
