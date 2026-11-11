package com.example.mpaznotifica.data.repositorio

import com.example.mpaznotifica.data.local.dao.ComunicacionDao
import com.example.mpaznotifica.data.remoto.ApiHistorial
import com.example.mpaznotifica.data.remoto.ApiMensajeria
import com.example.mpaznotifica.data.remoto.ClienteRetrofit
import com.example.mpaznotifica.data.remoto.DestinatarioDto
import com.example.mpaznotifica.data.remoto.EnvioDto
import com.example.mpaznotifica.data.remoto.EnvioUnificado
import com.example.mpaznotifica.data.remoto.MensajeDto
import com.example.mpaznotifica.data.remoto.NuevoMensajeDto
import com.example.mpaznotifica.data.remoto.NuevoRegistroDto
import com.example.mpaznotifica.data.remoto.RegistroDto

/**
 * Repositorio de comunicaciones.
 *
 * Las comunicaciones ya no viven en el teléfono: el borrador se crea en
 * ms-mensajeria, el envío se confirma ahí mismo y el listado de lo enviado se
 * pide a ms-historial. Room se mantiene como caché de la sesión y de las cuentas.
 */
class ComunicacionRepositorio(
    private val comunicacionDao: ComunicacionDao,
    private val mensajeria: ApiMensajeria = ClienteRetrofit.mensajeria,
    private val historial: ApiHistorial = ClienteRetrofit.historial
) {

    /** Crea el borrador en el servidor. Devuelve la comunicación con su id. */
    suspend fun crearBorrador(
        titulo: String,
        mensaje: String,
        tipo: String,
        adjuntos: List<String>,
        remitenteId: Int,
        remitenteNombre: String
    ): MensajeDto {
        return mensajeria.crearBorrador(
            NuevoMensajeDto(
                titulo = titulo.trim(),
                cuerpo = mensaje.trim(),
                tipo = tipo,
                remitenteId = remitenteId.toLong(),
                remitenteNombre = remitenteNombre,
                adjuntos = adjuntos.joinToString(", ").takeIf { it.isNotBlank() }
            )
        )
    }

    /**
     * Envía la comunicación con su canal y sus destinatarios.
     *
     * Primero se confirma el envío en ms-mensajeria (que la deja como "Enviada")
     * y después se registra el envío en ms-historial. Son dos servicios distintos:
     * ms-mensajeria no avisa por su cuenta al historial, así que la aplicación
     * hace el registro con los datos que devolvió el envío.
     */
    suspend fun enviarComunicacion(
        mensajeId: Long,
        canal: String,
        remitenteId: Int,
        remitenteNombre: String,
        destinatarios: List<Pair<Int, String>>
    ): MensajeDto {
        val enviado = mensajeria.enviar(
            mensajeId,
            EnvioDto(
                canal = canal,
                destinatarios = destinatarios.map { (id, nombre) ->
                    DestinatarioDto(apoderadoId = id.toLong(), nombre = nombre)
                }
            )
        )

        // El registro del historial es aparte: si falla, el envío igual quedó hecho.
        runCatching {
            historial.registrar(
                NuevoRegistroDto(
                    mensajeId = enviado.id,
                    titulo = enviado.titulo,
                    tipo = enviado.tipo,
                    canal = enviado.canal ?: canal,
                    remitenteId = remitenteId.toLong(),
                    remitenteNombre = remitenteNombre,
                    destinatarios = destinatarios.map { it.second }
                )
            )
        }

        return enviado
    }

    /** Comunicaciones del usuario, según ms-mensajeria (incluye los borradores). */
    suspend fun comunicacionesDe(remitenteId: Int): List<MensajeDto> {
        return mensajeria.porRemitente(remitenteId.toLong())
    }

    /** Historial del usuario, de lo más nuevo a lo más antiguo. */
    suspend fun obtenerEnviadas(remitenteId: Int): List<RegistroDto> {
        return historial.registros(remitenteId.toLong())
    }

    /**
     * Los envíos vistos desde los dos servicios.
     *
     * ms-historial es la fuente principal, pero ms-mensajeria también sabe qué
     * mensajes quedaron con estado "Enviada". Se juntan los dos y se descartan
     * los repetidos por id de mensaje: así el Historial no pierde una comunicación
     * si el registro del historial no alcanzó a quedar.
     */
    suspend fun obtenerEnvios(remitenteId: Int): List<EnvioUnificado> {
        val combinados = LinkedHashMap<Long, EnvioUnificado>()

        // Primero los registros del historial del usuario, que traen sus destinatarios.
        runCatching { historial.registros(remitenteId.toLong()) }
            .getOrDefault(emptyList())
            .forEach { registro ->
                combinados[registro.mensajeId] = EnvioUnificado(
                    mensajeId = registro.mensajeId,
                    titulo = registro.titulo,
                    tipo = registro.tipo,
                    canal = registro.canal,
                    fecha = registro.fecha,
                    cantidadDestinatarios = registro.cantidadDestinatarios
                )
            }

        // Después los enviados de mensajería, que cubren lo que falte en el historial.
        runCatching { mensajeria.porRemitente(remitenteId.toLong()) }
            .getOrDefault(emptyList())
            .filter { it.estado == ESTADO_ENVIADA }
            .forEach { mensaje ->
                if (!combinados.containsKey(mensaje.id)) {
                    combinados[mensaje.id] = EnvioUnificado(
                        mensajeId = mensaje.id,
                        titulo = mensaje.titulo,
                        tipo = mensaje.tipo,
                        canal = mensaje.canal.orEmpty(),
                        fecha = mensaje.fecha.orEmpty(),
                        cantidadDestinatarios = mensaje.cantidadDestinatarios.toInt()
                    )
                }
            }

        return combinados.values
            .sortedByDescending { it.mensajeId }
    }

    companion object {
        /** Canales que acepta el servidor, escritos exactamente igual. */
        val CANALES = listOf("Correo electrónico", "SMS", "WhatsApp")

        private const val ESTADO_ENVIADA = "Enviada"
    }
}
