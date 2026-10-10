package com.example.mpaznotifica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.UsuarioEntity

/**
 * Base de datos local del proyecto (Room, que por dentro usa SQLite).
 * Se irán agregando las tablas de grupos, apoderados y comunicaciones
 * a medida que se desarrollen las pantallas.
 */
@Database(
    entities = [UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_notifica.db"
                ).build().also { instancia = it }
            }
        }
    }
}
