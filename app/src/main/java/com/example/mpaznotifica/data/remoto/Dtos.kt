package com.example.mpaznotifica.data.remoto

/**
 * Clases que reciben el JSON de los tres microservicios.
 *
 * Los nombres de las propiedades tienen que ser idénticos a los del JSON;
 * si cambia uno, el campo llega nulo. Las fechas vienen como texto ISO-8601,
 * así que se reciben como String y se les cambia el formato al mostrarlas.
 */

// ---------- ms-usuarios ----------

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    /** "PROFESOR_JEFE" o "INSPECTORIA". */
    val rol: String,
    /** En Inspectoría General viene nulo. */
    val curso: String?,
    val activo: Boolean = true
)

data class ApoderadoDto(
    val id: Long,
    val nombre: String,
    val estudiante: String,
    val curso: String,
    val taller: String?
)

data class GrupoDto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val creadoPorTaller: Boolean,
    val cantidadIntegrantes: Int = 0,
    /** Nombres de los integrantes: el servicio no entrega sus ids. */
    val integrantes: List<String> = emptyList()
)

data class NuevoGrupoDto(
    val nombre: String,
    val descripcion: String?,
    /** Ids de los apoderados que forman el grupo. */
    val integrantes: List<Long>
)

// ---------- ms-mensajeria ----------

data class NuevoMensajeDto(
    val titulo: String,
    val cuerpo: String,
    val tipo: String,
    val remitenteId: Long,
    val remitenteNombre: String,
    val adjuntos: String?
)

data class DestinatarioDto(
    val apoderadoId: Long,
    val nombre: String
)

data class EnvioDto(
    val canal: String,
    val destinatarios: List<DestinatarioDto>
)

data class MensajeDto(
    val id: Long,
    val titulo: String,
    val cuerpo: String,
    val tipo: String,
    val canal: String?,
    /** "Borrador" o "Enviada". */
    val estado: String,
    /** Viene nulo mientras la comunicación es borrador. */
    val fecha: String?,
    val remitenteId: Long,
    val remitenteNombre: String,
    val adjuntos: String?,
    val cantidadDestinatarios: Long = 0
)

// ---------- ms-historial ----------

data class RegistroDto(    val id: Long,
    val mensajeId: Long,
    val titulo: String,
    val tipo: String,
    val canal: String,
    val fecha: String,
    val remitenteId: Long,
    val remitenteNombre: String,
    val cantidadDestinatarios: Int = 0,
    val destinatarios: List<String> = emptyList()
)

/**
 * Cuerpo con el que la aplicación registra un envío en ms-historial.
 *
 * No se manda la fecha ni la cantidad de destinatarios: el servidor pone la
 * fecha del momento y cuenta los nombres de la lista.
 */
data class NuevoRegistroDto(
    val mensajeId: Long,
    val titulo: String,
    val tipo: String,
    val canal: String,
    val remitenteId: Long,
    val remitenteNombre: String,
    val destinatarios: List<String>
)
