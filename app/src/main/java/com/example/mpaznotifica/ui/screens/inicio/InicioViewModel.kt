package com.example.mpaznotifica.ui.screens.inicio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaznotifica.data.local.AppDatabase
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity
import com.example.mpaznotifica.data.repositorio.ComunicacionRepositorio
import com.example.mpaznotifica.data.repositorio.GrupoRepositorio
import com.example.mpaznotifica.data.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = AppDatabase.getDatabase(application)

    private val usuarioRepositorio = UsuarioRepositorio(baseDatos.usuarioDao())
    private val comunicacionRepositorio = ComunicacionRepositorio(baseDatos.comunicacionDao())
    private val grupoRepositorio = GrupoRepositorio(
        grupoDao = baseDatos.grupoDao(),
        apoderadoDao = baseDatos.apoderadoDao()
    )

    var uiState by mutableStateOf(InicioUiState())
        private set

    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            grupoRepositorio.asegurarDatosDePrueba()

            val usuario = usuarioRepositorio.obtenerPorId(usuarioId)

            val borradores = comunicacionRepositorio
                .contarPorEstado(ComunicacionEntity.ESTADO_BORRADOR)
            val enviadas = comunicacionRepositorio
                .contarPorEstado(ComunicacionEntity.ESTADO_ENVIADA)
            val apoderados = grupoRepositorio.obtenerApoderados().size

            uiState = uiState.copy(
                cargando = false,
                saludo = if (usuario != null) "Hola, ${primerNombre(usuario.nombre)}" else "",
                rolYCurso = if (usuario == null) {
                    ""
                } else if (esInspectoria(usuario.perfil)) {
                    usuario.perfil
                } else {
                    "Profesor/a jefe de ${usuario.curso}"
                },
                curso = usuario?.curso.orEmpty(),
                iniciales = if (usuario != null) inicialesDe(usuario.nombre) else "",
                textoAlcance = if (usuario == null) {
                    ""
                } else if (esInspectoria(usuario.perfil)) {
                    TEXTO_ALCANCE_INSPECTORIA
                } else {
                    "Solo puedes enviar comunicaciones a los apoderados de ${usuario.curso}. " +
                        "Las comunicaciones institucionales y disciplinarias corresponden a " +
                        "Inspectoría General."
                },
                borradores = borradores,
                enviadas = enviadas,
                apoderados = apoderados,
                comunicacionesRecientes = comunicacionesRecientes()
            )
        }
    }

    private suspend fun comunicacionesRecientes(): List<ComunicacionResumen> {
        val enviadas = comunicacionRepositorio.obtenerComunicacionesEnviadas()
        val cantidades = comunicacionRepositorio.contarDestinatariosPorComunicacion()

        return enviadas.take(MAXIMO_RECIENTES).map { comunicacion ->
            ComunicacionResumen(
                titulo = comunicacion.titulo,
                tipo = comunicacion.tipo,
                fecha = comunicacion.fecha,
                destinatarios = textoDestinatarios(cantidades[comunicacion.id] ?: 0),
                enviada = true
            )
        }
    }

    private fun esInspectoria(perfil: String): Boolean {
        return perfil == UsuarioRepositorio.PERFIL_INSPECTORIA
    }

    private fun primerNombre(nombreCompleto: String): String {
        return nombreCompleto.trim().split(" ").firstOrNull().orEmpty()
    }

    private fun inicialesDe(nombreCompleto: String): String {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> ""
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }

    private fun textoDestinatarios(cantidad: Int): String {
        return when (cantidad) {
            0 -> "Sin destinatarios registrados"
            1 -> "1 apoderado"
            else -> "$cantidad apoderados"
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        onListo()
    }

    companion object {
        private const val MAXIMO_RECIENTES = 3

        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar comunicaciones a todo el colegio, a un curso, a un apoderado " +
                "o a los apoderados de un taller."
    }
}
