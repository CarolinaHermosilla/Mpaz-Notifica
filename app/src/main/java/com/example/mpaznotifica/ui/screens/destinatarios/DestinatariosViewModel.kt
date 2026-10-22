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
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DestinatariosViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)
    private val grupoRepositorio = GrupoRepositorio(
        grupoDao = baseDatos.grupoDao(),
        apoderadoDao = baseDatos.apoderadoDao()
    )
    private val comunicacionRepositorio = ComunicacionRepositorio(baseDatos.comunicacionDao())
    private val usuarioRepositorio = UsuarioRepositorio(baseDatos.usuarioDao())

    var uiState by mutableStateOf(DestinatariosUiState())
        private set

    private var borradorId: Int? = null

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            grupoRepositorio.asegurarDatosDePrueba()

            val usuario = usuarioRepositorio.obtenerPorId(usuarioId)
            val borrador = comunicacionRepositorio.obtenerBorradorPendiente()
            borradorId = borrador?.id

            uiState = uiState.copy(
                esInspectoria = usuario?.perfil == UsuarioRepositorio.PERFIL_INSPECTORIA,
                curso = usuario?.curso.orEmpty(),
                apoderados = grupoRepositorio.obtenerApoderados(),
                grupos = grupoRepositorio.obtenerGruposConIntegrantes(),
                tituloComunicacion = borrador?.titulo.orEmpty(),
                tipoComunicacion = borrador?.tipo.orEmpty(),
                hayBorrador = borrador != null,
                cargando = false
            )
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
            delay(1500L)

            Vibracion.vibrar(getApplication(), 500L)

            comunicacionRepositorio.enviarComunicacion(
                comunicacionId = idBorrador,
                canal = uiState.canalSeleccionado,
                idsApoderados = uiState.idsSeleccionados.toList()
            )

            uiState = uiState.copy(enviando = false)
            onExito()
        }
    }
}
