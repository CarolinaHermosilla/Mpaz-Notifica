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

/**
 * Lógica de la pantalla Historial.
 *
 * Consulta en la base local las comunicaciones que ya se enviaron y cuenta
 * cuántos apoderados recibió cada una. La pantalla solo muestra ese estado.
 */
class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ComunicacionRepositorio(
        AppDatabase.getDatabase(application).comunicacionDao()
    )

    var uiState by mutableStateOf(HistorialUiState())
        private set

    /**
     * Carga las comunicaciones enviadas. Se llama cada vez que se abre la
     * pantalla, para que el listado esté al día después de enviar una nueva.
     */
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
