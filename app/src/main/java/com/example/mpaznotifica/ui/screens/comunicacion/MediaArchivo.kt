package com.example.mpaznotifica.ui.screens.comunicacion

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Recurso nativo Cámara: guarda en una carpeta propia de la aplicación la foto
 * que toma la aplicación de la cámara del teléfono.
 *
 * Esto es necesario porque la imagen no se puede escribir directamente en la
 * galería del usuario: se le entrega a la cámara una dirección propia
 * (a través del FileProvider declarado en el manifiesto) y la cámara escribe ahí.
 */
object MediaArchivo {

    /** Subcarpeta, dentro de la carpeta temporal, donde se guardan las fotos. */
    private const val CARPETA = "evidencias"

    /**
     * Dirección de una dirección de archivo (content://) para que la cámara
     * escriba la foto que va a tomar.
     */
    fun uriParaCamara(context: Context): Uri {
        val carpeta = File(context.cacheDir, CARPETA).apply { mkdirs() }
        val archivo = File(carpeta, nombreArchivo())
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.archivos",
            archivo
        )
    }

    /** Nombre del archivo según el momento en que se toma la foto. */
    private fun nombreArchivo(): String {
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "evidencia_$marca.jpg"
    }

    /**
     * Fecha y hora en texto, para mostrarla en la vista previa.
     */
    fun fechaHoraActual(): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
    }

    /**
     * Nombre visible de una imagen elegida en la galería.
     * Si no se puede leer, se devuelve un nombre genérico.
     */
    fun nombreDesdeUri(context: Context, uri: Uri): String {
        val desconocido = "imagen_seleccionada.jpg"
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val indice = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (indice >= 0 && cursor.moveToFirst()) {
                    cursor.getString(indice) ?: desconocido
                } else {
                    desconocido
                }
            } ?: desconocido
        } catch (e: Exception) {
            desconocido
        }
    }
}
