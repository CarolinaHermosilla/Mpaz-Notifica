package com.example.mpaznotifica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity

@Dao
interface ApoderadoDao {

    /** Todos los apoderados del curso, ordenados por nombre. */
    @Query("SELECT * FROM apoderados ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<ApoderadoEntity>

    @Query("SELECT COUNT(*) FROM apoderados")
    suspend fun contarApoderados(): Int

    /** Devuelve el id que le asignó la base de datos al apoderado nuevo. */
    @Insert
    suspend fun insertar(apoderado: ApoderadoEntity): Long
}
