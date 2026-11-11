package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.UsuarioDao
import com.example.mpaznotifica.data.local.entity.UsuarioEntity
import com.example.mpaznotifica.data.remoto.ClienteRetrofit
import com.example.mpaznotifica.data.remoto.ApiUsuarios

/**
 * Repositorio de usuarios. Es la capa que queda entre la interfaz y la base de datos:
 * las pantallas nunca consultan Room directamente, siempre pasan por aquí.
 *
 * Las cuentas ya no se inventan en la aplicación: se descargan de ms-usuarios y se
 * dejan guardadas en SQLite. El login sigue validando contra SQLite, como pide la
 * rúbrica, pero nadie puede crearse una cuenta ni un rol por su cuenta.
 */
class UsuarioRepositorio(
    private val usuarioDao: UsuarioDao,
    private val api: ApiUsuarios = ClienteRetrofit.usuarios
) {

    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    suspend fun obtenerPorId(id: Int): UsuarioEntity? {
        return usuarioDao.obtenerPorId(id)
    }

    /**
     * Trae las cuentas del servidor y las deja guardadas en SQLite.
     *
     * El mapeo es la parte delicada: el servidor devuelve el rol como
     * "PROFESOR_JEFE" o "INSPECTORIA", pero toda la aplicación compara contra los
     * textos "Profesor o profesora jefe" e "Inspectoría General" (campo perfil).
     * Además, Inspectoría General no tiene curso en el servidor (viene nulo), así
     * que se guarda "Todo el colegio", que es lo que esperan las pantallas.
     */
    suspend fun sincronizarCuentas() {
        val cuentas = api.cuentas()

        cuentas.forEach { dto ->
            val esInspectoria = dto.rol == "INSPECTORIA"

            usuarioDao.guardar(
                UsuarioEntity(
                    // Se usa el id del servidor, para que la sesión apunte a la misma cuenta.
                    id = dto.id.toInt(),
                    nombre = dto.nombre,
                    correo = dto.correo,
                    contrasena = dto.contrasena,
                    perfil = if (esInspectoria) PERFIL_INSPECTORIA else PERFIL_PROFESOR,
                    curso = if (esInspectoria) ALCANCE_INSPECTORIA else (dto.curso ?: CURSO_PROFESOR)
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

        const val PERFIL_PROFESOR = "Profesor o profesora jefe"
        const val PERFIL_INSPECTORIA = "Inspectoría General"
    }
}
