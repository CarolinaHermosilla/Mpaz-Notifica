package com.example.mpaznotifica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpaznotifica.data.local.dao.ApoderadoDao
import com.example.mpaznotifica.data.local.dao.GrupoDao
import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity
import com.example.mpaznotifica.data.local.entity.UsuarioEntity

/**
 * Base de datos local del proyecto (Room, que por dentro usa SQLite).
 *
 * Tablas actuales:
 *  - usuarios: las cuentas que inician sesión.
 *  - apoderados: los destinatarios de las comunicaciones.
 *  - grupos: los grupos de apoderados.
 *  - grupo_apoderado: qué apoderados forman cada grupo.
 *
 * La tabla de comunicaciones se agrega cuando se desarrolle esa parte.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        ApoderadoEntity::class,
        GrupoEntity::class,
        GrupoApoderadoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun apoderadoDao(): ApoderadoDao

    abstract fun grupoDao(): GrupoDao

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
                    // Proyecto académico: al cambiar las tablas se vuelve a crear la
                    // base local y se cargan otra vez los datos de prueba.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
