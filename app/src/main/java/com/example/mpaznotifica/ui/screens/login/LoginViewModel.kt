package com.example.mpaznotifica.ui.screens.login

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla de Login.
 *
 * Toda la validación vive acá y no en la pantalla: la vista solamente muestra
 * el estado y avisa cuando el usuario escribe o presiona un botón.
 *
 * Las cuentas se descargan de ms-usuarios y se guardan en SQLite. La validación
 * del correo y la contraseña se hace contra SQLite, como pide la rúbrica.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    var uiState by mutableStateOf(LoginUiState())
        private set

    init {
        // Se descargan las cuentas del servidor: ya no se crean en la aplicación.
        viewModelScope.launch {
            try {
                repositorio.sincronizarCuentas()
            } catch (error: Exception) {
                // Sin red se usan las cuentas que ya estaban guardadas de antes.
                uiState = uiState.copy(
                    aviso = "No se pudo conectar. Se usarán las cuentas guardadas."
                )
            }
        }
    }

    fun onCorreoCambia(valor: String) {
        uiState = uiState.copy(correo = valor, errorCorreo = null)
    }

    fun onContrasenaCambia(valor: String) {
        uiState = uiState.copy(contrasena = valor, errorContrasena = null)
    }

    fun onAvisoMostrado() {
        uiState = uiState.copy(aviso = null)
    }

    /**
     * Valida los campos y, si están correctos, revisa el usuario en la base local.
     * Solo si las credenciales coinciden se llama a [onExito], entregándole el id
     * del usuario que entró: ese id viaja en la ruta de navegación.
     */
    fun ingresar(onExito: (Int) -> Unit) {
        val correo = uiState.correo.trim()
        val contrasena = uiState.contrasena

        val errorCorreo = validarCorreo(correo)
        val errorContrasena = validarContrasena(contrasena)

        if (errorCorreo != null || errorContrasena != null) {
            uiState = uiState.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            return
        }

        uiState = uiState.copy(enviando = true, aviso = null)

        viewModelScope.launch {
            // Se intenta traer lo último del servidor; si falla, se valida con lo guardado.
            try {
                repositorio.sincronizarCuentas()
            } catch (error: Exception) {
                uiState = uiState.copy(aviso = ErroresRed.mensaje(error))
            }

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
                // Se guarda la sesión para poder volver a entrar y para cerrar sesión.
                SesionPreferencias.guardar(
                    getApplication(),
                    usuario.id,
                    usuario.nombre,
                    usuario.perfil,
                    usuario.curso
                )
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
