package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.ComunicacionDao
import com.example.mpaznotifica.data.local.entity.ComunicacionDestinatarioEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio de comunicaciones.
 *
 * Se encarga de dos momentos del flujo:
 *  1. Guardar el borrador cuando se sale de la pantalla de Comunicación.
 *  2. Marcar la comunicación como enviada cuando se confirman los destinatarios.
 */
class ComunicacionRepositorio(private val comunicacionDao: ComunicacionDao) {

    /** Guarda la comunicación redactada como borrador. Devuelve su id. */
    suspend fun guardarBorrador(
        titulo: String,
        mensaje: String,
        tipo: String,
        adjuntos: List<String>,
        usuarioId: Int
    ): Long {
        return comunicacionDao.insertar(
            ComunicacionEntity(
                titulo = titulo.trim(),
                mensaje = mensaje.trim(),
                tipo = tipo,
                adjuntos = adjuntos.joinToString(", "),
                estado = ComunicacionEntity.ESTADO_BORRADOR,
                usuarioId = usuarioId
            )
        )
    }

    /** El último borrador pendiente, que es el que se está por enviar. */
    suspend fun obtenerBorradorPendiente(): ComunicacionEntity? {
        return comunicacionDao.obtenerUltimaPorEstado(ComunicacionEntity.ESTADO_BORRADOR)
    }

    /**
     * Marca el borrador como enviado y guarda quiénes lo recibieron.
     */
    suspend fun enviarComunicacion(
        comunicacionId: Int,
        canal: String,
        idsApoderados: List<Int>
    ) {
        comunicacionDao.marcarComoEnviada(
            comunicacionId = comunicacionId,
            canal = canal,
            fecha = fechaActual(),
            estado = ComunicacionEntity.ESTADO_ENVIADA
        )

        comunicacionDao.insertarDestinatarios(
            idsApoderados.map { idApoderado ->
                ComunicacionDestinatarioEntity(
                    comunicacionId = comunicacionId,
                    apoderadoId = idApoderado
                )
            }
        )
    }

    /** Cuántas comunicaciones hay en un estado ("Borrador" o "Enviada"). */
    suspend fun contarPorEstado(estado: String): Int {
        return comunicacionDao.contarPorEstado(estado)
    }

    /**
     * Las comunicaciones que ya se enviaron, de la más nueva a la más antigua.
     * Es lo que muestra la pantalla de Historial.
     */
    suspend fun obtenerComunicacionesEnviadas(): List<ComunicacionEntity> {
        return comunicacionDao.obtenerPorEstado(ComunicacionEntity.ESTADO_ENVIADA)
    }

    /**
     * Cuántos apoderados recibió cada comunicación, en una sola consulta.
     * Se devuelve como un mapa (id de la comunicación -> cantidad) para poder
     * armar el listado sin preguntar una vez por cada fila.
     */
    suspend fun contarDestinatariosPorComunicacion(): Map<Int, Int> {
        return comunicacionDao.contarDestinatariosAgrupados()
            .associate { conteo -> conteo.comunicacionId to conteo.cantidad }
    }

    /** Fecha y hora actual, en el mismo formato que se muestra en pantalla. */
    private fun fechaActual(): String {
        val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL"))
        return formato.format(Date())
    }

    companion object {
        /** Canales que se pueden elegir en la pantalla de Destinatarios. */
        val CANALES = listOf("Correo electrónico", "SMS", "WhatsApp")
    }
}
