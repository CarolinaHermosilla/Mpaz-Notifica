package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.data.remoto.ApiUsuarios
import com.example.mpaznotifica.data.remoto.ApoderadoDto
import com.example.mpaznotifica.data.remoto.ClienteRetrofit
import com.example.mpaznotifica.data.remoto.GrupoDto
import com.example.mpaznotifica.data.remoto.NuevoGrupoDto

/**
 * Repositorio de grupos y apoderados.
 *
 * Los apoderados y los grupos ya no se inventan en la aplicación: se descargan de
 * ms-usuarios. Los grupos se crean y se eliminan en el servidor, y después se
 * vuelve a leer la lista para que quede igual a lo que guardó el servidor.
 *
 * El servidor devuelve los integrantes de un grupo como nombres, no como ids; por
 * eso se buscan en la lista de apoderados por nombre (funciona porque no hay
 * nombres repetidos).
 */
class GrupoRepositorio(
    private val api: ApiUsuarios = ClienteRetrofit.usuarios
) {

    /** Todos los apoderados del curso, según el servidor. */
    suspend fun obtenerApoderados(): List<ApoderadoEntity> {
        return api.apoderados().map { dto -> aApoderado(dto) }
    }

    /** Los grupos del curso con sus integrantes, según el servidor. */
    suspend fun obtenerGruposConIntegrantes(): List<GrupoConIntegrantes> {
        val apoderados = obtenerApoderados()
        return api.grupos().map { dto -> aGrupo(dto, apoderados) }
    }

    /** Crea un grupo en el servidor y devuelve el grupo creado. */
    suspend fun crearGrupo(
        nombre: String,
        descripcion: String,
        idsApoderados: List<Int>
    ): GrupoDto {
        return api.crearGrupo(
            NuevoGrupoDto(
                nombre = nombre.trim(),
                descripcion = descripcion.trim().takeIf { it.isNotBlank() },
                integrantes = idsApoderados.map { it.toLong() }
            )
        )
    }

    /** Elimina un grupo en el servidor. */
    suspend fun eliminarGrupo(grupoId: Int) {
        api.eliminarGrupo(grupoId.toLong())
    }

    private fun aApoderado(dto: ApoderadoDto): ApoderadoEntity {
        return ApoderadoEntity(
            id = dto.id.toInt(),
            nombre = dto.nombre,
            estudiante = dto.estudiante,
            curso = dto.curso,
            taller = dto.taller
        )
    }

    private fun aGrupo(dto: GrupoDto, apoderados: List<ApoderadoEntity>): GrupoConIntegrantes {
        val integrantes = dto.integrantes.mapNotNull { nombre ->
            apoderados.firstOrNull { it.nombre == nombre }
        }

        return GrupoConIntegrantes(
            grupo = GrupoEntity(
                id = dto.id.toInt(),
                nombre = dto.nombre,
                descripcion = dto.descripcion.orEmpty(),
                creadoPorTaller = dto.creadoPorTaller
            ),
            integrantes = integrantes
        )
    }
}
