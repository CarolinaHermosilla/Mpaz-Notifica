package com.example.mpaznotifica.ui.screens.comunicacion

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MediaArchivo {

    private const val CARPETA = "evidencias"

    fun uriParaCamara(context: Context): Uri {
        val carpeta = File(context.cacheDir, CARPETA).apply { mkdirs() }
        val archivo = File(carpeta, nombreArchivo())
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.archivos",
            archivo
        )
    }

    private fun nombreArchivo(): String {
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "evidencia_$marca.jpg"
    }

    fun fechaHoraActual(): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
    }

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
