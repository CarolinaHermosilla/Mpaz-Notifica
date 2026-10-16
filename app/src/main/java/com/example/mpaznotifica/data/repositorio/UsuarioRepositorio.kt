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

    /** Busca un usuario por su id. Es lo que permite saber quién inició sesión. */
    suspend fun obtenerPorId(id: Int): UsuarioEntity? {
        return usuarioDao.obtenerPorId(id)
    }

    /**
     * Deja cargadas las cuentas de prueba la primera vez que se abre la aplicación.
     *
     * El rol es un atributo de cada cuenta (campo "perfil"): el usuario nunca lo elige
     * en el Login. En producción el rol se asigna al crear la cuenta, con el código
     * que la escuela envía por SMS.
     *
     * Los datos son ficticios, como exige el requerimiento.
     */
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
                    // Inspectoría General no está amarrada a un curso: su alcance es todo el colegio.
                    curso = "Todo el colegio"
                )
            )
        }
    }

    companion object {
        // Cuentas de prueba que se muestran en la pantalla de Login
        const val CORREO_PROFESOR = "profesor.jefe@ejemplo.cl"
        const val CORREO_INSPECTOR = "inspector.general@ejemplo.cl"
        const val CONTRASENA_PRUEBA = "123456"

        const val CURSO_PROFESOR = "5.º Básico B"
        const val ALCANCE_INSPECTORIA = "Todo el colegio"

        // Roles del sistema. Para agregar uno nuevo (por ejemplo dirección o coordinación)
        // basta con sumar su nombre aquí y su código de activación en la etapa de registro.
        const val PERFIL_PROFESOR = "Profesor o profesora jefe"
        const val PERFIL_INSPECTORIA = "Inspectoría General"
    }
}
