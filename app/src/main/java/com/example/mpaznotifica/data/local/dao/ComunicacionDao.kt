package com.example.mpaznotifica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mpaznotifica.data.local.entity.ComunicacionDestinatarioEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity

@Dao
interface ComunicacionDao {

    @Insert
    suspend fun insertar(comunicacion: ComunicacionEntity): Long

    @Query("SELECT * FROM comunicaciones WHERE estado = :estado ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaPorEstado(estado: String): ComunicacionEntity?

    @Query("SELECT * FROM comunicaciones WHERE estado = :estado ORDER BY id DESC")
    suspend fun obtenerPorEstado(estado: String): List<ComunicacionEntity>

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

    @Query(
        "SELECT comunicacionId AS comunicacionId, COUNT(*) AS cantidad " +
            "FROM comunicacion_destinatario GROUP BY comunicacionId"
    )
    suspend fun contarDestinatariosAgrupados(): List<ConteoDestinatarios>

    data class ConteoDestinatarios(
        val comunicacionId: Int,
        val cantidad: Int
    )
}
