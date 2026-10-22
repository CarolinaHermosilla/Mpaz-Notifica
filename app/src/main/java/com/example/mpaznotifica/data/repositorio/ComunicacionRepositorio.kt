package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.ComunicacionDao
import com.example.mpaznotifica.data.local.entity.ComunicacionDestinatarioEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComunicacionRepositorio(private val comunicacionDao: ComunicacionDao) {

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

    suspend fun obtenerBorradorPendiente(): ComunicacionEntity? {
        return comunicacionDao.obtenerUltimaPorEstado(ComunicacionEntity.ESTADO_BORRADOR)
    }

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

    suspend fun contarPorEstado(estado: String): Int {
        return comunicacionDao.contarPorEstado(estado)
    }

    suspend fun obtenerComunicacionesEnviadas(): List<ComunicacionEntity> {
        return comunicacionDao.obtenerPorEstado(ComunicacionEntity.ESTADO_ENVIADA)
    }

    suspend fun contarDestinatariosPorComunicacion(): Map<Int, Int> {
        return comunicacionDao.contarDestinatariosAgrupados()
            .associate { conteo -> conteo.comunicacionId to conteo.cantidad }
    }

    private fun fechaActual(): String {
        val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL"))
        return formato.format(Date())
    }

    companion object {
        val CANALES = listOf("Correo electrónico", "SMS", "WhatsApp")
    }
}
