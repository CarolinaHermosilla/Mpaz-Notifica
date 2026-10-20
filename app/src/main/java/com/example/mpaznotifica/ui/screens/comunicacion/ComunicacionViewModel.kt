package com.example.mpaznotifica.ui.screens.comunicacion

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Comunicación.
 *
 * Acá vive el recurso nativo Cámara (a través de [MediaArchivo]) y Galería, y
 * también las validaciones: los campos se revisan al apretar "Continuar", no en
 * la pantalla. La navegación la sigue administrando AppNavigation.
 *
 * Cambio de coordinación (Carolina): la firma de la vista previa se carga desde
 * la cuenta que inició sesión, usando el id que llega en la ruta de navegación.
 */
class ComunicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepositorio(
        AppDatabase.getDatabase(application).usuarioDao()
    )

    /** Guarda el borrador de la comunicación y después lo marca como enviado. */
    private val comunicacionRepositorio = ComunicacionRepositorio(
        AppDatabase.getDatabase(application).comunicacionDao()
    )

    var uiState by mutableStateOf(ComunicacionUiState())
        private set

    // Evita volver a consultar la base cada vez que la pantalla se redibuja.
    private var usuarioCargado: Int? = null

    /**
     * Deja la firma de la vista previa con el nombre de quien inició sesión,
     * para que la comunicación la firme su autor y no siempre la misma persona.
     * Además guarda el curso y el rol, que son los que definen a quiénes llega
     * la comunicación (una cuenta de Inspectoría General no está amarrada a un curso).
     */
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

    /**
     * Dirección donde la aplicación de la cámara va a escribir la foto.
     * Se guarda acá y se le entrega al lanzador de la cámara desde la pantalla.
     */
    var uriFotoPendiente: Uri? = null
        private set

    // ---------------------------------------------------------------------
    // Campos del formulario
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Recurso nativo Cámara
    // ---------------------------------------------------------------------

    /**
     * Prepara la dirección del archivo donde la cámara va a guardar la foto.
     * Si no se puede, deja un aviso en vez de cerrar la aplicación.
     */
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

    /** El permiso de cámara fue rechazado. */
    fun onPermisoCamaraRechazado() {
        uriFotoPendiente = null
        uiState = uiState.copy(
            pedirPermisoCamara = false,
            aviso = "Sin el permiso de cámara no se puede tomar la foto de evidencia."
        )
    }

    /** Marca que hay que pedir el permiso de cámara. */
    fun pedirPermisoDeCamara() {
        uiState = uiState.copy(pedirPermisoCamara = true)
    }

    /** La pantalla ya pidió el permiso: se apaga la marca para no pedirlo de nuevo. */
    fun onPermisoCamaraSolicitado() {
        uiState = uiState.copy(pedirPermisoCamara = false)
    }

    /**
     * Guarda la foto que tomó la cámara.
     *
     * @param exitoso true si la cámara avisó que la foto quedó guardada.
     */
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

    // ---------------------------------------------------------------------
    // Recurso nativo Galería
    // ---------------------------------------------------------------------

    /** Guarda la imagen que se eligió en la galería del teléfono. */
    fun onImagenDeGaleriaElegida(uri: Uri?) {
        if (uri == null) {
            // El profesor cerró la galería sin elegir nada: no es un error.
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

    /** Saca una imagen de la lista de adjuntos. */
    fun quitarAdjunto(adjunto: Adjunto) {
        uiState = uiState.copy(adjuntos = uiState.adjuntos.filterNot { it.uri == adjunto.uri })
    }

    // ---------------------------------------------------------------------
    // Vista previa y avance al paso siguiente
    // ---------------------------------------------------------------------

    fun abrirVistaPrevia() {
        uiState = uiState.copy(mostrarVistaPrevia = true)
    }

    fun cerrarVistaPrevia() {
        uiState = uiState.copy(mostrarVistaPrevia = false)
    }

    /**
     * Valida el formulario y, si está correcto, avanza a la pantalla de
     * Destinatarios, que es la que sigue en el flujo.
     *
     * Cambio de coordinación (Carolina): antes de avanzar se guarda el borrador
     * de la comunicación en la base local. Así la pantalla de Destinatarios lo
     * toma, le agrega los destinatarios y el canal, y lo deja como enviada.
     */
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
            comunicacionRepositorio.guardarBorrador(
                titulo = titulo,
                mensaje = mensaje,
                tipo = uiState.tipoSeleccionado,
                adjuntos = uiState.adjuntos.map { it.nombre },
                usuarioId = usuarioId
            )
            onContinuar()
        }
    }
}
