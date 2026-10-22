package com.example.mpaznotifica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity

@Dao
interface GrupoDao {

    @Query("SELECT * FROM grupos ORDER BY id DESC")
    suspend fun obtenerGrupos(): List<GrupoEntity>

    @Query(
        "SELECT a.* FROM apoderados a " +
            "INNER JOIN grupo_apoderado ga ON ga.apoderadoId = a.id " +
            "WHERE ga.grupoId = :grupoId ORDER BY a.nombre ASC"
    )
    suspend fun obtenerIntegrantes(grupoId: Int): List<ApoderadoEntity>

    @Query("SELECT COUNT(*) FROM grupos")
    suspend fun contarGrupos(): Int

    @Query("SELECT COUNT(*) FROM grupos WHERE LOWER(nombre) = LOWER(:nombre)")
    suspend fun contarGruposConNombre(nombre: String): Int

    @Insert
    suspend fun insertarGrupo(grupo: GrupoEntity): Long

    @Insert
    suspend fun insertarIntegrantes(integrantes: List<GrupoApoderadoEntity>)

    @Query("DELETE FROM grupo_apoderado WHERE grupoId = :grupoId")
    suspend fun borrarIntegrantesDeGrupo(grupoId: Int)

    @Query("DELETE FROM grupos WHERE id = :grupoId")
    suspend fun borrarGrupo(grupoId: Int)
}
