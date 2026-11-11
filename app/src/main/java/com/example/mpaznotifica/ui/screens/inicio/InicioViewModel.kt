package com.example.mpaznotifica.ui.screens.inicio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.remoto.ClienteRetrofit
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Inicio.
 *
 * La cabecera sale de la cuenta que inició sesión, guardada en SQLite. Los
 * contadores y las comunicaciones recientes ahora se piden a los microservicios:
 * las enviadas a ms-historial, los borradores a ms-mensajeria y los apoderados
 * a ms-usuarios. Si el servidor no responde, los contadores quedan en cero y se
 * muestra un aviso, sin que la pantalla se caiga.
 */
class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)

    private val usuarioRepositorio = UsuarioRepositorio(baseDatos.usuarioDao())
    private val comunicacionRepositorio = ComunicacionRepositorio(baseDatos.comunicacionDao())
    private val grupoRepositorio = GrupoRepositorio()

    var uiState by mutableStateOf(InicioUiState())
        private set

    /**
     * Se vuelve a cargar todo cada vez que la pantalla aparece, para que los
     * contadores y las comunicaciones recientes queden al día después de enviar.
     * Si se cambia de cuenta, también se refresca el encabezado.
     */
    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)

            val usuario = usuarioRepositorio.obtenerPorId(usuarioId)

            uiState = uiState.copy(
                saludo = if (usuario != null) "Hola, ${primerNombre(usuario.nombre)}" else "",
                rolYCurso = if (usuario == null) {
                    ""
                } else if (esInspectoria(usuario.perfil)) {
                    usuario.perfil
                } else {
                    "Profesor/a jefe de ${usuario.curso}"
                },
                curso = usuario?.curso.orEmpty(),
                iniciales = if (usuario != null) inicialesDe(usuario.nombre) else "",
                textoAlcance = if (usuario == null) {
                    ""
                } else if (esInspectoria(usuario.perfil)) {
                    TEXTO_ALCANCE_INSPECTORIA
                } else {
                    "Solo puedes enviar comunicaciones a los apoderados de ${usuario.curso}. " +
                        "Las comunicaciones institucionales y disciplinarias corresponden a " +
                        "Inspectoría General."
                }
            )

            try {
                val envios = comunicacionRepositorio.obtenerEnvios(usuarioId)
                val misMensajes = ClienteRetrofit.mensajeria.porRemitente(usuarioId.toLong())
                val apoderados = grupoRepositorio.obtenerApoderados()

                uiState = uiState.copy(
                    cargando = false,
                    borradores = misMensajes.count { it.estado == "Borrador" },
                    enviadas = envios.size,
                    apoderados = apoderados.size,
                    comunicacionesRecientes = envios.take(MAXIMO_RECIENTES).map { envio ->
                        ComunicacionResumen(
                            titulo = envio.titulo,
                            tipo = envio.tipo,
                            fecha = ErroresRed.formatearFecha(envio.fecha),
                            destinatarios = textoDestinatarios(envio.cantidadDestinatarios),
                            enviada = true
                        )
                    }
                )
            } catch (error: Exception) {
                // Sin servidor los contadores quedan en cero y se avisa, sin caerse.
                uiState = uiState.copy(
                    cargando = false,
                    borradores = 0,
                    enviadas = 0,
                    apoderados = 0,
                    comunicacionesRecientes = emptyList(),
                    aviso = ErroresRed.mensaje(error)
                )
            }
        }
    }

    fun onAvisoMostrado() {
        uiState = uiState.copy(aviso = null)
    }

    private fun esInspectoria(perfil: String): Boolean {
        return perfil == UsuarioRepositorio.PERFIL_INSPECTORIA
    }

    private fun primerNombre(nombreCompleto: String): String {
        return nombreCompleto.trim().split(" ").firstOrNull().orEmpty()
    }

    private fun inicialesDe(nombreCompleto: String): String {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> ""
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }

    private fun textoDestinatarios(cantidad: Int): String {
        return when (cantidad) {
            0 -> "Sin destinatarios registrados"
            1 -> "1 apoderado"
            else -> "$cantidad apoderados"
        }
    }

    /** Cierra la sesión guardada y avisa a la navegación. */
    fun cerrarSesion(onListo: () -> Unit) {
        SesionPreferencias.cerrar(getApplication())
        onListo()
    }

    companion object {
        private const val MAXIMO_RECIENTES = 3

        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar comunicaciones a todo el colegio, a un curso, a un apoderado " +
                "o a los apoderados de un taller."
    }
}
