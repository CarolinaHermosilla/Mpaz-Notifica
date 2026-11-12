package com.example.mpaznotifica.ui.screens.login

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val enviando: Boolean = false,
    val aviso: String? = null
)
