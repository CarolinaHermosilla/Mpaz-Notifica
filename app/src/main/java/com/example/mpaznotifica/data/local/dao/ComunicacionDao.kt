package com.example.mpaznotifica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mpaznotifica.data.local.entity.ComunicacionDestinatarioEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity

@Dao
interface ComunicacionDao {

    /** Guarda la comunicación redactada (todavía como borrador). Devuelve su id. */
    @Insert
    suspend fun insertar(comunicacion: ComunicacionEntity): Long

    /** La última comunicación que quedó en ese estado. */
    @Query("SELECT * FROM comunicaciones WHERE estado = :estado ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaPorEstado(estado: String): ComunicacionEntity?

    /**
     * Todas las comunicaciones que quedaron en ese estado, de la más nueva a la
     * más antigua. Se usa para el Historial de comunicaciones enviadas.
     */
    @Query("SELECT * FROM comunicaciones WHERE estado = :estado ORDER BY id DESC")
    suspend fun obtenerPorEstado(estado: String): List<ComunicacionEntity>

    /** Marca la comunicación como enviada, con su canal y su fecha. */
    @Query(
        "UPDATE comunicaciones SET canal = :canal, fecha = :fecha, estado = :estado " +
            "WHERE id = :comunicacionId"
    )
    suspend fun marcarComoEnviada(
        comunicacionId: Int,
        canal: String,
        fecha: String,
        estado: String
    )

    @Insert
    suspend fun insertarDestinatarios(destinatarios: List<ComunicacionDestinatarioEntity>)

    @Query("SELECT COUNT(*) FROM comunicaciones WHERE estado = :estado")
    suspend fun contarPorEstado(estado: String): Int

    @Query("SELECT COUNT(*) FROM comunicacion_destinatario WHERE comunicacionId = :comunicacionId")
    suspend fun contarDestinatarios(comunicacionId: Int): Int

    /**
     * Cuántos apoderados recibieron cada comunicación, de una sola vez.
     * Devuelve una fila por comunicación que tenga destinatarios.
     */
    @Query(
        "SELECT comunicacionId AS comunicacionId, COUNT(*) AS cantidad " +
            "FROM comunicacion_destinatario GROUP BY comunicacionId"
    )
    suspend fun contarDestinatariosAgrupados(): List<ConteoDestinatarios>

    /** Cantidad de apoderados que recibieron una comunicación. */
    data class ConteoDestinatarios(
        val comunicacionId: Int,
        val cantidad: Int
    )
}
