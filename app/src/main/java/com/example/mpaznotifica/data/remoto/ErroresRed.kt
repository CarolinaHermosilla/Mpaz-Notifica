package com.example.mpaznotifica.data.remoto

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Traduce los errores de la conexión a un mensaje que entienda el usuario.
 *
 * El backend responde con { "fecha": "...", "estado": 400, "mensaje": "..." }
 * cuando algo está mal (400 datos inválidos, 404 no existe). Ese campo "mensaje"
 * ya viene escrito para la persona final, así que se muestra tal cual.
 */
object ErroresRed {

    private const val SIN_CONEXION = "No se pudo conectar. Revisa tu conexión."
    private const val ERROR_SERVIDOR = "Ocurrió un problema en el servidor. Intenta nuevamente."
    private const val NO_EXISTE = "Ese registro ya no existe."

    /** Mensaje para mostrar cuando falla una llamada al backend. */
    fun mensaje(error: Throwable): String {
        return when (error) {
            is HttpException -> mensajeDeCodigo(error)
            is IOException -> SIN_CONEXION
            else -> SIN_CONEXION
        }
    }

    private fun mensajeDeCodigo(error: HttpException): String {
        if (error.code() == 404) return NO_EXISTE
        if (error.code() == 400) {
            return campoMensaje(error) ?: "Los datos enviados no son válidos."
        }
        return ERROR_SERVIDOR
    }

    /** Lee el campo "mensaje" del cuerpo de error del servidor, si viene. */
    private fun campoMensaje(error: HttpException): String? {
        return try {
            val cuerpo = error.response()?.errorBody()?.string() ?: return null
            val json = Gson().fromJson(cuerpo, JsonObject::class.java)
            json.get("mensaje")?.asString?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convierte la fecha del servidor al formato de la aplicación.
     *
     * No todos los servicios usan el mismo formato: ms-historial devuelve
     * ISO-8601 ("2026-11-11T17:25:55.813817") y ms-mensajeria devuelve
     * "dd/MM/yyyy HH:mm:ss". Se prueban los dos, y si no calza ninguno se
     * muestra la fecha tal como llegó.
     */
    fun formatearFecha(iso: String?): String {
        if (iso.isNullOrBlank()) return ""

        val salida = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val formatosDeEntrada = listOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm"
        )

        val valor = iso.substringBefore(".").substringBefore("Z").trim()

        formatosDeEntrada.forEach { patron ->
            try {
                val entrada = SimpleDateFormat(patron, Locale.getDefault())
                val fecha = entrada.parse(valor)
                if (fecha != null) return salida.format(fecha)
            } catch (error: Exception) {
                // Se prueba el siguiente formato.
            }
        }

        return iso
    }
}
