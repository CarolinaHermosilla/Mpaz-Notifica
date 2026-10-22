package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.ApoderadoDao
import com.example.mpaznotifica.data.local.dao.GrupoDao
import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity
import com.example.mpaznotifica.data.model.GrupoConIntegrantes

class GrupoRepositorio(
    private val grupoDao: GrupoDao,
    private val apoderadoDao: ApoderadoDao
) {

    suspend fun obtenerApoderados(): List<ApoderadoEntity> {
        return apoderadoDao.obtenerTodos()
    }

    suspend fun obtenerGruposConIntegrantes(): List<GrupoConIntegrantes> {
        return grupoDao.obtenerGrupos().map { grupo ->
            GrupoConIntegrantes(
                grupo = grupo,
                integrantes = grupoDao.obtenerIntegrantes(grupo.id)
            )
        }
    }

    suspend fun eliminarGrupo(grupoId: Int) {
        grupoDao.borrarIntegrantesDeGrupo(grupoId)
        grupoDao.borrarGrupo(grupoId)
    }

    suspend fun existeGrupoConNombre(nombre: String): Boolean {
        return grupoDao.contarGruposConNombre(nombre.trim()) > 0
    }

    suspend fun crearGrupo(
        nombre: String,
        descripcion: String,
        idsApoderados: List<Int>
    ): Long {
        val idGrupo = grupoDao.insertarGrupo(
            GrupoEntity(
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                creadoPorTaller = false
            )
        )

        grupoDao.insertarIntegrantes(
            idsApoderados.map { idApoderado ->
                GrupoApoderadoEntity(
                    grupoId = idGrupo.toInt(),
                    apoderadoId = idApoderado
                )
            }
        )

        return idGrupo
    }

    suspend fun asegurarDatosDePrueba() {
        val idsPorNombre = asegurarApoderados()

        if (grupoDao.contarGrupos() > 0) return

        GRUPOS_DE_PRUEBA.forEach { grupoDePrueba ->
            val idGrupo = grupoDao.insertarGrupo(
                GrupoEntity(
                    nombre = grupoDePrueba.nombre,
                    descripcion = grupoDePrueba.descripcion,
                    creadoPorTaller = grupoDePrueba.deTaller
                )
            )

            val integrantes = grupoDePrueba.integrantes.mapNotNull { nombre ->
                idsPorNombre[nombre]?.let { idApoderado ->
                    GrupoApoderadoEntity(
                        grupoId = idGrupo.toInt(),
                        apoderadoId = idApoderado
                    )
                }
            }

            grupoDao.insertarIntegrantes(integrantes)
        }
    }

    private suspend fun asegurarApoderados(): Map<String, Int> {
        if (apoderadoDao.contarApoderados() == 0) {
            APODERADOS_DE_PRUEBA.forEach { apoderado -> apoderadoDao.insertar(apoderado) }
        }
        return apoderadoDao.obtenerTodos().associate { it.nombre to it.id }
    }

    private data class GrupoDePrueba(
        val nombre: String,
        val descripcion: String,
        val deTaller: Boolean,
        val integrantes: List<String>
    )

    companion object {
        private const val CURSO = UsuarioRepositorio.CURSO_PROFESOR

        const val TALLER_AJEDREZ = "Taller de Ajedrez"
        const val TALLER_TEATRO = "Taller de Teatro"

        private val APODERADOS_DE_PRUEBA = listOf(
            ApoderadoEntity(
                nombre = "Rocío Alarcón Vega",
                estudiante = "Estudiante A. Alarcón",
                curso = CURSO
            ),
            ApoderadoEntity(
                nombre = "Marcelo Bustos Pinto",
                estudiante = "Estudiante B. Bustos",
                curso = CURSO,
                taller = TALLER_AJEDREZ
            ),
            ApoderadoEntity(
                nombre = "Paula Cárdenas Muñoz",
                estudiante = "Estudiante C. Cárdenas",
                curso = CURSO
            ),
            ApoderadoEntity(
                nombre = "Iván Donoso Rivas",
                estudiante = "Estudiante D. Donoso",
                curso = CURSO,
                taller = TALLER_TEATRO
            ),
            ApoderadoEntity(
                nombre = "Carolina Espinoza Lagos",
                estudiante = "Estudiante E. Espinoza",
                curso = CURSO
            ),
            ApoderadoEntity(
                nombre = "Héctor Fuentes Navarro",
                estudiante = "Estudiante F. Fuentes",
                curso = CURSO,
                taller = TALLER_AJEDREZ
            ),
            ApoderadoEntity(
                nombre = "Daniela Guzmán Órdenes",
                estudiante = "Estudiante G. Guzmán",
                curso = CURSO
            ),
            ApoderadoEntity(
                nombre = "Rodrigo Herrera Salas",
                estudiante = "Estudiante H. Herrera",
                curso = CURSO,
                taller = TALLER_TEATRO
            )
        )

        private val GRUPOS_DE_PRUEBA = listOf(
            GrupoDePrueba(
                nombre = "Directiva del curso",
                descripcion = "Apoderados que integran la directiva del curso.",
                deTaller = false,
                integrantes = listOf(
                    "Carolina Espinoza Lagos",
                    "Paula Cárdenas Muñoz",
                    "Rocío Alarcón Vega"
                )
            ),
            GrupoDePrueba(
                nombre = TALLER_TEATRO,
                descripcion = "Apoderados de los estudiantes que participan del Taller de Teatro.",
                deTaller = true,
                integrantes = listOf(
                    "Iván Donoso Rivas",
                    "Rodrigo Herrera Salas"
                )
            ),
            GrupoDePrueba(
                nombre = TALLER_AJEDREZ,
                descripcion = "Apoderados de los estudiantes que participan del Taller de Ajedrez.",
                deTaller = true,
                integrantes = listOf(
                    "Héctor Fuentes Navarro",
                    "Marcelo Bustos Pinto"
                )
            )
        )
    }
}
