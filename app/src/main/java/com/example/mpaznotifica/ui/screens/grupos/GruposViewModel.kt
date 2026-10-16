package com.example.mpaznotifica.ui.screens.grupos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Grupos.
 *
 * La pantalla nunca consulta la base de datos directamente: pide los grupos al
 * repositorio y muestra lo que llega en el estado.
 */
class GruposViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)
    private val repositorio = GrupoRepositorio(
        grupoDao = baseDatos.grupoDao(),
        apoderadoDao = baseDatos.apoderadoDao()
    )

    var uiState by mutableStateOf(GruposUiState())
        private set

    /**
     * Carga los grupos del curso. La primera vez deja además los datos de prueba
     * (los apoderados y los tres grupos de ejemplo).
     */
    fun cargarGrupos() {
        viewModelScope.launch {
            repositorio.asegurarDatosDePrueba()
            uiState = GruposUiState(
                grupos = repositorio.obtenerGruposConIntegrantes(),
                cargando = false
            )
        }
    }

    /** Elimina un grupo y vuelve a cargar la lista. */
    fun eliminarGrupo(grupoId: Int) {
        viewModelScope.launch {
            repositorio.eliminarGrupo(grupoId)
            uiState = uiState.copy(grupos = repositorio.obtenerGruposConIntegrantes())
        }
    }
}
