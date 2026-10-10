package com.example.mpaznotifica.ui.screens.login

/**
 * Estado de la pantalla de Login. Toda la información que se muestra en pantalla
 * sale de aquí, incluidas las validaciones de cada campo.
 */
data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val perfilSeleccionado: String = PERFIL_PROFESOR,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val enviando: Boolean = false
) {
    companion object {
        const val PERFIL_PROFESOR = "Profesor o profesora jefe"
        const val PERFIL_INSPECTORIA = "Inspectoría General"
    }
}
