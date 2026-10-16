package com.example.mpaznotifica.data.model

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity

/**
 * Un grupo junto con sus integrantes.
 *
 * Room no puede llenar esta clase solo, así que el repositorio la arma juntando
 * el grupo con la lista de apoderados que le corresponden.
 */
data class GrupoConIntegrantes(
    val grupo: GrupoEntity,
    val integrantes: List<ApoderadoEntity>
) {
    val cantidadDeIntegrantes: Int
        get() = integrantes.size
}
