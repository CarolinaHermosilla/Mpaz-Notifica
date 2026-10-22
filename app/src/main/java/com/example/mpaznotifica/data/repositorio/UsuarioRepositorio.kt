package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.UsuarioEntity

class UsuarioRepositorio(private val usuarioDao: UsuarioDao) {

    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    suspend fun obtenerPorId(id: Int): UsuarioEntity? {
        return usuarioDao.obtenerPorId(id)
    }

    suspend fun asegurarUsuariosDePrueba() {
        if (usuarioDao.obtenerPorCorreo(CORREO_PROFESOR) == null) {
            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = "Andrea Muñoz",
                    correo = CORREO_PROFESOR,
                    contrasena = CONTRASENA_PRUEBA,
                    perfil = PERFIL_PROFESOR,
                    curso = CURSO_PROFESOR
                )
            )
        }

        if (usuarioDao.obtenerPorCorreo(CORREO_INSPECTOR) == null) {
            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = "Mario Gutierrez",
                    correo = CORREO_INSPECTOR,
                    contrasena = CONTRASENA_PRUEBA,
                    perfil = PERFIL_INSPECTORIA,
                    curso = "Todo el colegio"
                )
            )
        }
    }

    companion object {
        const val CORREO_PROFESOR = "profesor.jefe@ejemplo.cl"
        const val CORREO_INSPECTOR = "inspector.general@ejemplo.cl"
        const val CONTRASENA_PRUEBA = "123456"

        const val CURSO_PROFESOR = "5.º Básico B"
        const val ALCANCE_INSPECTORIA = "Todo el colegio"

        const val PERFIL_PROFESOR = "Profesor o profesora jefe"
        const val PERFIL_INSPECTORIA = "Inspectoría General"
    }
}
