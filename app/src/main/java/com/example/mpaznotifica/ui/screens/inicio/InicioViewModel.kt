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

/**
 * Lógica de la pantalla Inicio.
 *
 * La cabecera (saludo, rol, curso e iniciales) se arma a partir de la cuenta que
 * inició sesión: el Login entrega el id del usuario en la ruta y acá se consulta
 * en la base de datos local. Nadie elige su rol en ninguna pantalla.
 *
 * Los contadores y las comunicaciones recientes también salen de la base local:
 * los borradores y las enviadas de la tabla de comunicaciones, y el total de
 * apoderados de la tabla de apoderados.
 *
 * Nota de coordinación (Carolina): el nombre del campo "rolYCurso" reemplaza al
 * antiguo "nombreProfesor", que estaba fijo en el código.
 */
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

    /**
     * Carga los datos de la cuenta que inició sesión, los tres contadores y la
     * lista de comunicaciones recientes. Se vuelve a llamar cada vez que se entra
     * a la pantalla, para que los totales queden al día después de enviar.
     */
    fun cargar(usuarioId: Int) {
        viewModelScope.launch {
            // Los apoderados del curso son datos de ejemplo que se cargan solos.
            // Se aseguran acá para que el contador de apoderados nunca muestre cero.
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

    /**
     * Las últimas comunicaciones enviadas, con su tipo, su fecha y cuántos
     * apoderados las recibieron. Son las que se ven en "Comunicaciones recientes".
     */
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

    /** Primer nombre de la persona, para el saludo. */
    private fun primerNombre(nombreCompleto: String): String {
        return nombreCompleto.trim().split(" ").firstOrNull().orEmpty()
    }

    /** Toma la primera letra del nombre y del apellido, por ejemplo "Andrea Muñoz" -> "AM". */
    private fun inicialesDe(nombreCompleto: String): String {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> ""
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }

    /** Cómo se muestra la cantidad de apoderados que recibieron una comunicación. */
    private fun textoDestinatarios(cantidad: Int): String {
        return when (cantidad) {
            0 -> "Sin destinatarios registrados"
            1 -> "1 apoderado"
            else -> "$cantidad apoderados"
        }
    }

    /** Acción del botón "Cerrar sesión" del encabezado. */
    fun cerrarSesion(onListo: () -> Unit) {
        onListo()
    }

    companion object {
        /** Cuántas comunicaciones recientes se muestran en la pantalla Inicio. */
        private const val MAXIMO_RECIENTES = 3

        /** Alcance de los envíos cuando la cuenta es de Inspectoría General. */
        const val TEXTO_ALCANCE_INSPECTORIA =
            "Puedes enviar comunicaciones a todo el colegio, a un curso, a un apoderado " +
                "o a los apoderados de un taller."
    }
}
