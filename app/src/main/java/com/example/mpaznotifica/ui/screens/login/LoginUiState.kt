package com.example.mpaznotifica.ui.screens.login

/**
 * Estado de la pantalla de Login. Toda la información que se muestra en pantalla
 * sale de aquí, incluidas las validaciones de cada campo.
 *
 * Nota: el rol ya no se elige en esta pantalla. El rol es un atributo de la cuenta
 * (campo "perfil" de la tabla de usuarios) y se asigna al crear la cuenta,
 * con el código que la escuela envía por SMS.
 */
data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val enviando: Boolean = false
)
