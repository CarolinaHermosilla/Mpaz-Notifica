package com.example.mpaznotifica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpaznotifica.data.sesion.SesionPreferencias
import com.example.mpaznotifica.ui.screens.comunicacion.PantallaComunicacion
import com.example.mpaznotifica.ui.screens.destinatarios.PantallaDestinatarios
import com.example.mpaznotifica.ui.screens.grupos.PantallaGrupos
import com.example.mpaznotifica.ui.screens.grupos.PantallaNuevoGrupo
import com.example.mpaznotifica.ui.screens.historial.PantallaHistorial
import com.example.mpaznotifica.ui.screens.inicio.PantallaInicio
import com.example.mpaznotifica.ui.screens.login.PantallaLogin

object Rutas {
    const val LOGIN = "login"

    const val INICIO = "inicio/{usuarioId}"
    const val ARG_USUARIO_ID = "usuarioId"

    const val COMUNICACION = "comunicacion/{usuarioId}"

    fun comunicacion(usuarioId: Int) = "comunicacion/$usuarioId"

    const val DESTINATARIOS = "destinatarios/{usuarioId}"

    fun destinatarios(usuarioId: Int) = "destinatarios/$usuarioId"

    const val GRUPOS = "grupos"
    const val NUEVO_GRUPO = "nuevo_grupo"
    const val HISTORIAL = "historial"

    const val AVISO_GRUPO_GUARDADO = "aviso_grupo_guardado"

    fun inicio(usuarioId: Int) = "inicio/$usuarioId"
}

/**
 * Navegación general de la aplicación.
 *
 * Si la persona ya había iniciado sesión, la aplicación arranca en Inicio en vez
 * del Login. Al cerrar sesión se borra la sesión guardada y se limpia la pila,
 * para que el botón "atrás" no vuelva a Inicio.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val arranque = remember {
        if (SesionPreferencias.haySesion(context)) {
            Rutas.inicio(SesionPreferencias.usuarioId(context))
        } else {
            Rutas.LOGIN
        }
    }

    NavHost(
        navController = navController,
        startDestination = arranque
    ) {
        composable(Rutas.LOGIN) {
            PantallaLogin(
                onIngresar = { usuarioId ->
                    navController.navigate(Rutas.inicio(usuarioId)) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.GRUPOS) { entrada ->
            val aviso = entrada.savedStateHandle.get<String>(Rutas.AVISO_GRUPO_GUARDADO)

            PantallaGrupos(
                onVolver = { navController.popBackStack() },
                onNuevoGrupo = { navController.navigate(Rutas.NUEVO_GRUPO) },
                aviso = aviso,
                onAvisoMostrado = {
                    entrada.savedStateHandle.remove<String>(Rutas.AVISO_GRUPO_GUARDADO)
                }
            )
        }

        composable(Rutas.NUEVO_GRUPO) {
            PantallaNuevoGrupo(
                onVolver = { navController.popBackStack() },
                onGrupoGuardado = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Rutas.AVISO_GRUPO_GUARDADO, "Grupo guardado")
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Rutas.DESTINATARIOS,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaDestinatarios(
                usuarioId = usuarioId,
                onVolver = { navController.popBackStack() },
                onComunicacionEnviada = {
                    navController.navigate(Rutas.HISTORIAL) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        composable(
            route = Rutas.INICIO,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaInicio(
                usuarioId = usuarioId,
                onNuevaComunicacion = { navController.navigate(Rutas.comunicacion(usuarioId)) },
                onVerGrupos = { navController.navigate(Rutas.GRUPOS) },
                onVerHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.COMUNICACION,
            arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0

            PantallaComunicacion(
                usuarioId = usuarioId,
                onVolver = { navController.popBackStack() },
                onContinuar = { navController.navigate(Rutas.destinatarios(usuarioId)) }
            )
        }

        composable(Rutas.HISTORIAL) {
            PantallaHistorial(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
