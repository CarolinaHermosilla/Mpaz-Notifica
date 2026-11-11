package com.example.mpaznotifica.data.remoto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Direcciones de los tres microservicios del backend. */
interface ApiUsuarios {

    @GET("api/usuarios")
    suspend fun cuentas(): List<UsuarioDto>

    @GET("api/apoderados")
    suspend fun apoderados(): List<ApoderadoDto>

    @GET("api/grupos")
    suspend fun grupos(): List<GrupoDto>

    @POST("api/grupos")
    suspend fun crearGrupo(@Body cuerpo: NuevoGrupoDto): GrupoDto

    @DELETE("api/grupos/{id}")
    suspend fun eliminarGrupo(@Path("id") id: Long): Response<Unit>
}

interface ApiMensajeria {

    @POST("api/mensajes")
    suspend fun crearBorrador(@Body cuerpo: NuevoMensajeDto): MensajeDto

    @PUT("api/mensajes/{id}/enviar")
    suspend fun enviar(@Path("id") id: Long, @Body cuerpo: EnvioDto): MensajeDto

    @GET("api/mensajes")
    suspend fun porRemitente(@Query("remitenteId") remitenteId: Long): List<MensajeDto>
}

interface ApiHistorial {

    @GET("api/historial")
    suspend fun registros(): List<RegistroDto>
}
