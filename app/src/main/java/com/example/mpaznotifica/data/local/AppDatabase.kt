package com.example.mpaznotifica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpaznotifica.data.local.dao.ApoderadoDao
import com.example.mpaznotifica.data.local.dao.ComunicacionDao
import com.example.mpaznotifica.data.local.dao.GrupoDao
import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionDestinatarioEntity
import com.example.mpaznotifica.data.local.entity.ComunicacionEntity
import com.example.mpaznotifica.data.local.entity.GrupoApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity
import com.example.mpaznotifica.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ApoderadoEntity::class,
        GrupoEntity::class,
        GrupoApoderadoEntity::class,
        ComunicacionEntity::class,
        ComunicacionDestinatarioEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun apoderadoDao(): ApoderadoDao

    abstract fun grupoDao(): GrupoDao

    abstract fun comunicacionDao(): ComunicacionDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_notifica.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
