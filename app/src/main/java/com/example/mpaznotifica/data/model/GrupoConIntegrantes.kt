package com.example.mpaznotifica.data.model

import com.example.mpaznotifica.data.local.entity.ApoderadoEntity
import com.example.mpaznotifica.data.local.entity.GrupoEntity

data class GrupoConIntegrantes(
    val grupo: GrupoEntity,
    val integrantes: List<ApoderadoEntity>
) {
    val cantidadDeIntegrantes: Int
        get() = integrantes.size
}
