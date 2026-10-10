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

/**
 * Lógica de la pantalla de Login.
 *
 * Toda la validación vive acá y no en la pantalla: la vista solamente muestra
 * el estado y avisa cuando el usuario escribe o presiona un botón.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    var uiState by mutableStateOf(LoginUiState())
        private set

    /** Aviso que se muestra abajo en pantalla (por ejemplo, al tocar Inspectoría General). */
    var aviso by mutableStateOf<String?>(null)
        private set

    init {
        // Se deja cargado el usuario de prueba en la base de datos local la primera vez.
        viewModelScope.launch {
            repositorio.asegurarUsuarioDePrueba()
        }
    }

    fun onCorreoCambia(valor: String) {
        uiState = uiState.copy(correo = valor, errorCorreo = null)
    }

    fun onContrasenaCambia(valor: String) {
        uiState = uiState.copy(contrasena = valor, errorContrasena = null)
    }

    /**
     * El perfil de Inspectoría General todavía no está habilitado: al tocarlo
     * se avisa y no se cambia la selección.
     */
    fun onPerfilSeleccionado(perfil: String) {
        if (perfil == LoginUiState.PERFIL_INSPECTORIA) {
            aviso = "El perfil de Inspectoría General se habilita en una etapa posterior."
            return
        }
        uiState = uiState.copy(perfilSeleccionado = perfil)
    }

    fun onAvisoMostrado() {
        aviso = null
    }

    /**
     * Valida los campos y, si están correctos, revisa el usuario en la base de datos local.
     * Solo si las credenciales coinciden se llama a [onExito].
     */
    fun ingresar(onExito: () -> Unit) {
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
                onExito()
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
