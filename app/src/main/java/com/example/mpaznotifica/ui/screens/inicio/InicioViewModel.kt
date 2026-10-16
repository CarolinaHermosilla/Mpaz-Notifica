package com.example.mpaznotifica.ui.screens.inicio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Inicio.
 *
 * La cabecera (saludo, rol, curso e iniciales) se arma a partir de la cuenta que
 * inició sesión: el Login entrega el id del usuario en la ruta y acá se consulta
 * en la base de datos local. Nadie elige su rol en ninguna pantalla.
 *
 * Nota de coordinación (Carolina): el nombre del campo "rolYCurso" reemplaza al
 * antiguo "nombreProfesor", que estaba fijo en el código.
 */
class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    var uiState by mutableStateOf(InicioUiState())
        private set

    // Evita volver a consultar la base cada vez que la pantalla se redibuja.
    private var usuarioCargado: Int? = null

    /** Carga los datos de la cuenta que inició sesión. */
    fun cargarUsuario(usuarioId: Int) {
        if (usuarioCargado == usuarioId) return
        usuarioCargado = usuarioId

        viewModelScope.launch {
            val usuario = repositorio.obtenerPorId(usuarioId) ?: return@launch
            val esInspectoria = usuario.perfil == UsuarioRepositorio.PERFIL_INSPECTORIA

            uiState = uiState.copy(
                saludo = "Hola, ${usuario.nombre.trim().split(" ").first()}",
                rolYCurso = if (esInspectoria) {
                    usuario.perfil
                } else {
                    "Profesor/a jefe de ${usuario.curso}"
                },
                curso = usuario.curso,
                iniciales = inicialesDe(usuario.nombre),
                textoAlcance = if (esInspectoria) {
                    TEXTO_ALCANCE_INSPECTORIA
                } else {
                    "Solo puedes enviar comunicaciones a los apoderados de ${usuario.curso}. " +
                        "Las comunicaciones institucionales y disciplinarias corresponden a " +
                        "Inspectoría General."
                }
            )
        }
    }

    // ---------------------------------------------------------------------
    // Punto de conexión pendiente con la base de datos local:
    // los contadores (Borradores, Enviadas, Apoderados) y la lista de
    // "Comunicaciones recientes" se cargarán desde los DAO que defina el otro
    // integrante del equipo, en una etapa posterior.
    // ---------------------------------------------------------------------

    /** Acción del botón "Cerrar sesión" del encabezado. */
    fun cerrarSesion(onListo: () -> Unit) {
        onListo()
    }

    /** Toma la primera letra del nombre y del apellido, por ejemplo "Andrea Muñoz" -> "AM". */
    private fun inicialesDe(nombreCompleto: String): String {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> ""
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }

    companion object {
        /** Alcance de los envíos cuando la cuenta es de Inspectoría General. */
        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar comunicaciones a todo el colegio, a un curso, a un apoderado " +
                "o a los apoderados de un taller."
    }
}
