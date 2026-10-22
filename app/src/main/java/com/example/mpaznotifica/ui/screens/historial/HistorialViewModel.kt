package com.example.mpaznotifica.ui.screens.historial

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import kotlinx.coroutines.launch

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ComunicacionRepositorio(
        AppDatabase.getDatabase(application).comunicacionDao()
    )

    var uiState by mutableStateOf(HistorialUiState())
        private set

    fun cargar() {
        viewModelScope.launch {
            val enviadas = repositorio.obtenerComunicacionesEnviadas()
            val cantidades = repositorio.contarDestinatariosPorComunicacion()

            uiState = HistorialUiState(
                cargando = false,
                comunicaciones = enviadas.map { comunicacion ->
                    ComunicacionHistorial(
                        titulo = comunicacion.titulo,
                        tipo = comunicacion.tipo,
                        fecha = comunicacion.fecha,
                        canal = comunicacion.canal,
                        destinatarios = cantidades[comunicacion.id] ?: 0
                    )
                }
            )
        }
    }
}
