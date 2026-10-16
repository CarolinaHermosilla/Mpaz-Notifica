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
 * Lógica de la pantalla de creación de grupos.
 *
 * Las validaciones están acá y no en la pantalla: el nombre es obligatorio, hay
 * que marcar al menos un apoderado y no se permite repetir el nombre de un grupo
 * que ya existe.
 */
class NuevoGrupoViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)
    private val repositorio = GrupoRepositorio(
        grupoDao = baseDatos.grupoDao(),
        apoderadoDao = baseDatos.apoderadoDao()
    )

    var uiState by mutableStateOf(NuevoGrupoUiState())
        private set

    /** Carga los apoderados del curso y los grupos que ya existen. */
    fun cargar() {
        viewModelScope.launch {
            repositorio.asegurarDatosDePrueba()
            uiState = uiState.copy(
                apoderados = repositorio.obtenerApoderados(),
                gruposExistentes = repositorio.obtenerGruposConIntegrantes(),
                cargando = false
            )
        }
    }

    fun onNombreCambia(valor: String) {
        uiState = uiState.copy(nombre = valor, errorNombre = null)
    }

    fun onDescripcionCambia(valor: String) {
        uiState = uiState.copy(descripcion = valor)
    }

    fun onApoderadoMarcado(idApoderado: Int, marcado: Boolean) {
        val seleccionados = if (marcado) {
            uiState.idsSeleccionados + idApoderado
        } else {
            uiState.idsSeleccionados - idApoderado
        }
        uiState = uiState.copy(idsSeleccionados = seleccionados, errorIntegrantes = null)
    }

    /**
     * Valida el formulario y guarda el grupo.
     * Solo si todo está correcto se llama a [onExito].
     */
    fun guardar(onExito: () -> Unit) {
        val nombre = uiState.nombre.trim()

        if (nombre.isBlank()) {
            uiState = uiState.copy(errorNombre = "Escribe un nombre para el grupo.")
            return
        }

        if (uiState.idsSeleccionados.isEmpty()) {
            uiState = uiState.copy(
                errorIntegrantes = "Marca al menos un apoderado para formar el grupo."
            )
            return
        }

        uiState = uiState.copy(guardando = true)

        viewModelScope.launch {
            // No se permite crear dos grupos con el mismo nombre.
            if (repositorio.existeGrupoConNombre(nombre)) {
                uiState = uiState.copy(
                    guardando = false,
                    errorNombre = "Ya existe un grupo con ese nombre."
                )
                return@launch
            }

            repositorio.crearGrupo(
                nombre = nombre,
                descripcion = uiState.descripcion,
                idsApoderados = uiState.idsSeleccionados.toList()
            )

            uiState = uiState.copy(guardando = false)
            onExito()
        }
    }
}
