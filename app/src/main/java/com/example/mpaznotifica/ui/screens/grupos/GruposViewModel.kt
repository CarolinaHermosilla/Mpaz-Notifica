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

class GruposViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)
    private val repositorio = GrupoRepositorio(
        grupoDao = baseDatos.grupoDao(),
        apoderadoDao = baseDatos.apoderadoDao()
    )

    var uiState by mutableStateOf(GruposUiState())
        private set

    fun cargarGrupos() {
        viewModelScope.launch {
            repositorio.asegurarDatosDePrueba()
            uiState = GruposUiState(
                grupos = repositorio.obtenerGruposConIntegrantes(),
                cargando = false
            )
        }
    }

    fun eliminarGrupo(grupoId: Int) {
        viewModelScope.launch {
            repositorio.eliminarGrupo(grupoId)
            uiState = uiState.copy(grupos = repositorio.obtenerGruposConIntegrantes())
        }
    }
}
