package com.example.mpaznotifica.ui.screens.login

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    var uiState by mutableStateOf(LoginUiState())
        private set

    init {
        viewModelScope.launch {
            repositorio.asegurarUsuariosDePrueba()
        }
    }

    fun onCorreoCambia(valor: String) {
        uiState = uiState.copy(correo = valor, errorCorreo = null)
    }

    fun onContrasenaCambia(valor: String) {
        uiState = uiState.copy(contrasena = valor, errorContrasena = null)
    }

    fun ingresar(onExito: (Int) -> Unit) {
        val correo = uiState.correo.trim()
        val contrasena = uiState.contrasena

        val errorCorreo = validarCorreo(correo)
        val errorContrasena = validarContrasena(contrasena)

        if (errorCorreo != null || errorContrasena != null) {
            uiState = uiState.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            return
        }

        uiState = uiState.copy(enviando = true)

        viewModelScope.launch {
            repositorio.asegurarUsuariosDePrueba()

            val usuario = repositorio.obtenerPorCorreo(correo)

            uiState = when {
                usuario == null -> uiState.copy(
                    enviando = false,
                    errorCorreo = "No existe una cuenta con este correo."
                )

                usuario.contrasena != contrasena -> uiState.copy(
                    enviando = false,
                    errorContrasena = "La contraseña no es correcta."
                )

                else -> uiState.copy(enviando = false)
            }

            if (usuario != null && usuario.contrasena == contrasena) {
                onExito(usuario.id)
            }
        }
    }

    private fun validarCorreo(correo: String): String? {
        if (correo.isBlank()) return "Ingresa tu correo institucional."
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            return "El correo no tiene un formato válido."
        }
        return null
    }

    private fun validarContrasena(contrasena: String): String? {
        if (contrasena.isBlank()) return "Ingresa tu contraseña."
        return null
    }
}
