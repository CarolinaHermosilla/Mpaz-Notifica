package com.example.mpaznotifica.ui.screens.comunicacion

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import kotlinx.coroutines.launch

class ComunicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    private val comunicacionRepositorio = ComunicacionRepositorio(
        AppDatabase.getDatabase(application).comunicacionDao()
    )

    var uiState by mutableStateOf(ComunicacionUiState())
        private set

    private var usuarioCargado: Int? = null

    fun cargarUsuario(usuarioId: Int) {
        if (usuarioCargado == usuarioId) return
        usuarioCargado = usuarioId

        viewModelScope.launch {
            val usuario = repositorio.obtenerPorId(usuarioId) ?: return@launch
            val esInspectoria = usuario.perfil == UsuarioRepositorio.PERFIL_INSPECTORIA

            uiState = uiState.copy(
                firma = usuario.nombre,
                curso = usuario.curso,
                rol = usuario.perfil,
                esInspectoria = esInspectoria
            )
        }
    }

    var uriFotoPendiente: Uri? = null
        private set


    fun onTipoSeleccionado(tipo: String) {
        uiState = uiState.copy(tipoSeleccionado = tipo)
    }

    fun onTituloCambia(valor: String) {
        uiState = uiState.copy(titulo = valor, errorTitulo = null)
    }

    fun onMensajeCambia(valor: String) {
        uiState = uiState.copy(mensaje = valor, errorMensaje = null)
    }

    fun onAvisoMostrado() {
        uiState = uiState.copy(aviso = null)
    }


    fun onPermisoCamaraConcedido() {
        uriFotoPendiente = try {
            MediaArchivo.uriParaCamara(getApplication())
        } catch (e: Exception) {
            uiState = uiState.copy(
                aviso = "No se pudo preparar el archivo para la foto."
            )
            null
        }
    }

    fun onPermisoCamaraRechazado() {
        uriFotoPendiente = null
        uiState = uiState.copy(
            pedirPermisoCamara = false,
            aviso = "Sin el permiso de cámara no se puede tomar la foto de evidencia."
        )
    }

    fun pedirPermisoDeCamara() {
        uiState = uiState.copy(pedirPermisoCamara = true)
    }

    fun onPermisoCamaraSolicitado() {
        uiState = uiState.copy(pedirPermisoCamara = false)
    }

    fun onFotoTomada(exitoso: Boolean) {
        val uri = uriFotoPendiente
        uriFotoPendiente = null

        if (!exitoso || uri == null) {
            uiState = uiState.copy(aviso = "No se tomó ninguna foto.")
            return
        }

        agregarAdjunto(
            Adjunto(
                uri = uri,
                nombre = uri.lastPathSegment.orEmpty().ifBlank { "evidencia.jpg" },
                origen = OrigenAdjunto.CAMARA,
                fechaHora = MediaArchivo.fechaHoraActual()
            )
        )
    }


    fun onImagenDeGaleriaElegida(uri: Uri?) {
        if (uri == null) {
            return
        }

        agregarAdjunto(
            Adjunto(
                uri = uri,
                nombre = MediaArchivo.nombreDesdeUri(getApplication(), uri),
                origen = OrigenAdjunto.GALERIA,
                fechaHora = MediaArchivo.fechaHoraActual()
            )
        )
    }

    private fun agregarAdjunto(adjunto: Adjunto) {
        uiState = uiState.copy(adjuntos = uiState.adjuntos + adjunto)
    }

    fun quitarAdjunto(adjunto: Adjunto) {
        uiState = uiState.copy(adjuntos = uiState.adjuntos.filterNot { it.uri == adjunto.uri })
    }


    fun abrirVistaPrevia() {
        uiState = uiState.copy(mostrarVistaPrevia = true)
    }

    fun cerrarVistaPrevia() {
        uiState = uiState.copy(mostrarVistaPrevia = false)
    }

    fun continuar(usuarioId: Int, onContinuar: () -> Unit) {
        val titulo = uiState.titulo.trim()
        val mensaje = uiState.mensaje.trim()

        val errorTitulo = if (titulo.isEmpty()) "Escribe el título de la comunicación." else null
        val errorMensaje = if (mensaje.isEmpty()) "Escribe el mensaje de la comunicación." else null

        if (errorTitulo != null || errorMensaje != null) {
            uiState = uiState.copy(errorTitulo = errorTitulo, errorMensaje = errorMensaje)
            return
        }

        uiState = uiState.copy(mostrarVistaPrevia = false)

        viewModelScope.launch {
            try {
                // El borrador se crea en ms-mensajeria: su id es la llave para enviarlo.
                val borrador = comunicacionRepositorio.crearBorrador(
                    titulo = titulo,
                    mensaje = mensaje,
                    tipo = uiState.tipoSeleccionado,
                    adjuntos = uiState.adjuntos.map { it.nombre },
                    remitenteId = usuarioId,
                    remitenteNombre = uiState.firma
                )

                // Se guarda el id para que Destinatarios lo encuentre sin cambiar la navegación.
                SesionPreferencias.guardarBorradorId(getApplication(), borrador.id.toInt())
                onContinuar()
            } catch (error: Exception) {
                uiState = uiState.copy(
                    aviso = ErroresRed.mensaje(error)
                )
            }
        }
    }
}
