package com.example.mpaznotifica.ui.screens.historial

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.remoto.ErroresRed
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla Historial.
 *
 * Junta los envíos de ms-historial y de ms-mensajeria, porque los dos servicios
 * saben qué comunicaciones quedaron enviadas. Se descartan los repetidos, así
 * ninguna comunicación se pierde del listado.
 */
class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ComunicacionRepositorio(
        AppDatabase.getDatabase(application).comunicacionDao()
    )

    var uiState by mutableStateOf(HistorialUiState())
        private set

    fun cargar() {
        viewModelScope.launch {
            val usuarioId = SesionPreferencias.usuarioId(getApplication())

            try {
                val envios = repositorio.obtenerEnvios(usuarioId)

                uiState = HistorialUiState(
                    cargando = false,
                    comunicaciones = envios.map { envio ->
                        ComunicacionHistorial(
                            titulo = envio.titulo,
                            tipo = envio.tipo,
                            fecha = ErroresRed.formatearFecha(envio.fecha),
                            canal = envio.canal,
                            destinatarios = envio.cantidadDestinatarios
                        )
                    }
                )
            } catch (error: Exception) {
                uiState = HistorialUiState(
                    cargando = false,
                    error = ErroresRed.mensaje(error)
                )
            }
        }
    }
}
