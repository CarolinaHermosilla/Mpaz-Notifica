package com.example.mpaznotifica.ui.screens.grupos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Grupos.
 *
 * El listado y la eliminación se hacen contra ms-usuarios: después de eliminar se
 * vuelve a leer la lista del servidor, para que quede igual a lo que quedó guardado.
 */
class GruposViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = GrupoRepositorio()

    var uiState by mutableStateOf(GruposUiState())
        private set

    fun cargarGrupos() {
        viewModelScope.launch {
            try {
                uiState = GruposUiState(
                    grupos = repositorio.obtenerGruposConIntegrantes(),
                    cargando = false
                )
            } catch (error: Exception) {
                uiState = GruposUiState(
                    cargando = false,
                    error = ErroresRed.mensaje(error)
                )
            }
        }
    }

    fun eliminarGrupo(grupoId: Int) {
        viewModelScope.launch {
            try {
                repositorio.eliminarGrupo(grupoId)
                uiState = uiState.copy(
                    grupos = repositorio.obtenerGruposConIntegrantes(),
                    error = null
                )
            } catch (error: Exception) {
                uiState = uiState.copy(error = ErroresRed.mensaje(error))
            }
        }
    }

    fun onErrorMostrado() {
        uiState = uiState.copy(error = null)
    }
}
