package com.example.mpaznotifica.data.sesion

import android.content.Context

/**
 * Guarda la sesión de la persona que entró, para que la aplicación sepa quién
 * es al volver a abrirla y pueda cerrar sesión de verdad.
 *
 * También guarda el id del borrador que se está redactando, porque la pantalla
 * de Destinatarios lo necesita para enviarlo y así no hay que cambiar la navegación.
 */
object SesionPreferencias {

    private const val ARCHIVO = "sesion_mpaz"

    private fun prefs(context: Context) =
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    fun guardar(context: Context, usuarioId: Int, nombre: String, perfil: String, curso: String) {
        prefs(context).edit()
            .putInt("usuarioId", usuarioId)
            .putString("nombre", nombre)
            .putString("perfil", perfil)
            .putString("curso", curso)
            .apply()
    }

    fun usuarioId(context: Context): Int = prefs(context).getInt("usuarioId", 0)

    fun haySesion(context: Context): Boolean = usuarioId(context) > 0

    fun guardarBorradorId(context: Context, id: Int) {
        prefs(context).edit().putInt("borradorId", id).apply()
    }

    fun borradorId(context: Context): Int = prefs(context).getInt("borradorId", 0)

    /** Borra la sesión y el borrador pendiente. */
    fun cerrar(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
