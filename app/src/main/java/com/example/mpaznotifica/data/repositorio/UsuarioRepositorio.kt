package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.UsuarioEntity

/**
 * Repositorio de usuarios. Es la capa que queda entre la interfaz y la base de datos:
 * las pantallas nunca consultan Room directamente, siempre pasan por aquí.
 */
class UsuarioRepositorio(private val usuarioDao: UsuarioDao) {

    /** Busca un usuario por su correo. Devuelve null si no existe. */
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    /**
     * Deja cargado el usuario de prueba la primera vez que se abre la aplicación.
     * Los datos son ficticios, como exige el requerimiento.
     */
    suspend fun asegurarUsuarioDePrueba() {
        if (usuarioDao.contarUsuarios() == 0) {
            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = "Andrea Fuentes",
                    correo = CORREO_PRUEBA,
                    contrasena = CONTRASENA_PRUEBA,
                    perfil = PERFIL_PROFESOR,
                    curso = "5.° Básico B"
                )
            )
        }
    }

    companion object {
        const val CORREO_PRUEBA = "profesor.jefe@ejemplo.cl"
        const val CONTRASENA_PRUEBA = "123456"
        const val PERFIL_PROFESOR = "Profesor o profesora jefe"
        const val PERFIL_INSPECTORIA = "Inspectoría General"
    }
}
