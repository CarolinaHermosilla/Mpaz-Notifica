package com.example.mpaznotifica.ui.screens.destinatarios

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.data.nativo.Vibracion
import com.example.mpaznotifica.data.remoto.ClienteRetrofit
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Destinatarios.
 *
 * Los apoderados y los grupos ahora vienen de ms-usuarios. El envío se confirma
 * en ms-mensajeria con su canal y sus destinatarios; al terminar el teléfono vibra
 * (recurso nativo), el círculo de carga se ve al menos 1,5 segundos y se limpia
 * el borrador pendiente de la sesión.
 */
class DestinatariosViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)

    private val grupoRepositorio = GrupoRepositorio()
    private val comunicacionRepositorio = ComunicacionRepositorio(baseDatos.comunicacionDao())
    private val usuarioRepositorio = UsuarioRepositorio(baseDatos.usuarioDao())

    var uiState by mutableStateOf(DestinatariosUiState())
        private set

    private var borradorId: Int? = null

    /** Quién envía: se usa para firmar el registro del historial. */
    private var remitenteId: Int = 0
    private var remitenteNombre: String = ""

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val usuario = usuarioRepositorio.obtenerPorId(usuarioId)
                remitenteId = usuarioId
                remitenteNombre = usuario?.nombre.orEmpty()
                val apoderados = grupoRepositorio.obtenerApoderados()
                val grupos = grupoRepositorio.obtenerGruposConIntegrantes()

                val idBorrador = SesionPreferencias.borradorId(getApplication())
                borradorId = idBorrador.takeIf { it > 0 }

                // Se busca el borrador en el servidor para mostrar su título y su tipo.
                val borrador = if (idBorrador > 0) {
                    ClienteRetrofit.mensajeria
                        .porRemitente(usuarioId.toLong())
                        .firstOrNull { it.id.toInt() == idBorrador }
                } else {
                    null
                }

                uiState = uiState.copy(
                    esInspectoria = usuario?.perfil == UsuarioRepositorio.PERFIL_INSPECTORIA,
                    curso = usuario?.curso.orEmpty(),
                    apoderados = apoderados,
                    grupos = grupos,
                    tituloComunicacion = borrador?.titulo.orEmpty(),
                    tipoComunicacion = borrador?.tipo.orEmpty(),
                    hayBorrador = borrador != null,
                    cargando = false
                )
            } catch (error: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    errorDestinatarios = ErroresRed.mensaje(error)
                )
            }
        }
    }

    fun onTodoElCurso() {
        uiState = uiState.copy(
            idsSeleccionados = uiState.apoderados.map { it.id }.toSet(),
            errorDestinatarios = null
        )
    }

    fun onElegirApoderados() {
        uiState = uiState.copy(
            idsSeleccionados = emptySet(),
            errorDestinatarios = null
        )
    }

    fun onGrupoTocado(grupo: GrupoConIntegrantes) {
        val idsDelGrupo = grupo.integrantes.map { it.id }.toSet()
        val yaEstabanTodos = idsDelGrupo.isNotEmpty() &&
            idsDelGrupo.all { uiState.idsSeleccionados.contains(it) }

        val seleccionados = if (yaEstabanTodos) {
            uiState.idsSeleccionados - idsDelGrupo
        } else {
            uiState.idsSeleccionados + idsDelGrupo
        }

        uiState = uiState.copy(idsSeleccionados = seleccionados, errorDestinatarios = null)
    }

    fun onApoderadoMarcado(idApoderado: Int, marcado: Boolean) {
        val seleccionados = if (marcado) {
            uiState.idsSeleccionados + idApoderado
        } else {
            uiState.idsSeleccionados - idApoderado
        }
        uiState = uiState.copy(idsSeleccionados = seleccionados, errorDestinatarios = null)
    }

    fun onCanalSeleccionado(canal: String) {
        uiState = uiState.copy(canalSeleccionado = canal)
    }

    fun enviar(onExito: () -> Unit) {
        if (uiState.idsSeleccionados.isEmpty()) {
            uiState = uiState.copy(
                errorDestinatarios = "Marca al menos un apoderado para enviar la comunicación."
            )
            return
        }

        val idBorrador = borradorId
        if (idBorrador == null) {
            uiState = uiState.copy(
                errorDestinatarios = "No hay una comunicación redactada. Vuelve al paso anterior."
            )
            return
        }

        uiState = uiState.copy(enviando = true, errorDestinatarios = null)

        viewModelScope.launch {
            try {
                val destinatarios = uiState.apoderados
                    .filter { it.id in uiState.idsSeleccionados }
                    .map { it.id to it.nombre }

                val inicio = System.currentTimeMillis()
                comunicacionRepositorio.enviarComunicacion(
                    mensajeId = idBorrador.toLong(),
                    canal = uiState.canalSeleccionado,
                    remitenteId = remitenteId,
                    remitenteNombre = remitenteNombre,
                    destinatarios = destinatarios
                )

                // El círculo de carga se ve al menos 1,5 segundos, como antes.
                val transcurrido = System.currentTimeMillis() - inicio
                if (transcurrido < 1500) delay(1500 - transcurrido)

                // Vibración: recurso nativo, al confirmarse el envío.
                Vibracion.vibrar(getApplication(), 500L)

                SesionPreferencias.guardarBorradorId(getApplication(), 0)
                uiState = uiState.copy(enviando = false)
                onExito()
            } catch (error: Exception) {
                uiState = uiState.copy(
                    enviando = false,
                    errorDestinatarios = ErroresRed.mensaje(error)
                )
            }
        }
    }
}
