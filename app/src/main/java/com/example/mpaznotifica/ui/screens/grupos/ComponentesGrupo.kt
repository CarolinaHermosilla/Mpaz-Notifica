package com.example.mpaznotifica.ui.screens.grupos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpaznotifica.data.model.GrupoConIntegrantes
import com.example.mpaznotifica.ui.theme.AzulClaroInfo
import com.example.mpaznotifica.ui.theme.AzulInstitucional
import com.example.mpaznotifica.ui.theme.AzulTitulo
import com.example.mpaznotifica.ui.theme.Blanco
import com.example.mpaznotifica.ui.theme.TextoPrincipal
import com.example.mpaznotifica.ui.theme.TextoSecundario

/**
 * Tarjeta blanca con los datos de un grupo: avatar, nombre, cantidad de
 * integrantes, la marca de "creado por el taller", su descripción y los nombres
 * de sus integrantes.
 *
 * Se usa en el listado de grupos y también en la pantalla de creación, así que
 * vive en su propio archivo para no repetir el diseño. Si [onClick] es null, la
 * tarjeta solo se muestra (no se puede tocar).
 */
@Composable
fun TarjetaGrupo(
    grupo: GrupoConIntegrantes,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Blanco)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AzulClaroInfo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = AzulInstitucional,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.size(12.dp))

            Column {
                Text(
                    text = grupo.grupo.nombre,
                    color = TextoPrincipal,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "${grupo.cantidadDeIntegrantes} " +
                            if (grupo.cantidadDeIntegrantes == 1) "integrante" else "integrantes",
                        color = AzulTitulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (grupo.grupo.creadoPorTaller) {
                        Text(
                            text = " · creado por el taller",
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (grupo.grupo.descripcion.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = grupo.grupo.descripcion,
                color = TextoSecundario,
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        grupo.integrantes.forEach { integrante ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = AzulInstitucional,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = integrante.nombre,
                    color = TextoPrincipal,
                    fontSize = 15.sp
                )
            }
        }
    }
}
