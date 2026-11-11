package com.example.mpaznotifica.data.remoto

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Conexión con los tres microservicios del backend.
 *
 * Los tiempos de espera están en 60 segundos porque el plan gratuito de Render
 * duerme los servicios a los 15 minutos sin uso, y el primer llamado puede tardar
 * hasta 50 segundos en despertarlos. Con los 10 segundos que trae Android por
 * defecto, la aplicación mostraría un error de red que en realidad significa
 * "el servicio se estaba despertando".
 */
object ClienteRetrofit {

    private const val URL_USUARIOS = "https://mpaz-usuarios.onrender.com/"
    private const val URL_MENSAJERIA = "https://mpaz-mensajeria.onrender.com/"
    private const val URL_HISTORIAL = "https://mpaz-historial.onrender.com/"

    private fun constructor(url: String): Retrofit {
        val registro = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cliente = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(registro)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarios: ApiUsuarios by lazy {
        constructor(URL_USUARIOS).create(ApiUsuarios::class.java)
    }

    val mensajeria: ApiMensajeria by lazy {
        constructor(URL_MENSAJERIA).create(ApiMensajeria::class.java)
    }

    val historial: ApiHistorial by lazy {
        constructor(URL_HISTORIAL).create(ApiHistorial::class.java)
    }
}
