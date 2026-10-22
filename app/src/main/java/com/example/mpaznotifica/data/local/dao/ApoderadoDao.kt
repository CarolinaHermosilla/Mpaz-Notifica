package com.example.mpaznotifica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity

@Dao
interface ApoderadoDao {

    @Query("SELECT * FROM apoderados ORDER BY estudiante ASC")
    suspend fun obtenerTodos(): List<ApoderadoEntity>

    @Query("SELECT COUNT(*) FROM apoderados")
    suspend fun contarApoderados(): Int

    @Insert
    suspend fun insertar(apoderado: ApoderadoEntity): Long
}
