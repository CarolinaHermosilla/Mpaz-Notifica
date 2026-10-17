# Registro de ramas borradas — MPAZ Notifica

Documento interno del equipo. Se guarda para poder **recuperar** las ramas
`pedro/inicio` y `pedro/comunicacion` si algún día hiciera falta, ya que borrar
una rama en Git no deja una copia automática.

Fecha del borrado: 16-10-2026

## Por qué se pueden borrar sin riesgo

Los dos commits **ya están dentro de la rama `main`**, así que el código no se
pierde. Se borró solo el nombre de la rama. Comprobación hecha antes de borrar:

- `git merge pedro/comunicacion` sobre `main` → `Already up to date`
- `git diff main pedro/comunicacion` → solo mostraba lo que `main` tiene de más
  (el trabajo de Carolina), nada propio fuera.

## Datos de cada rama

### pedro/inicio

- Commit completo: `6fb51fefef75b4527635cc54643d1d0930105e4a`
- Mensaje: `feat(inicio): agrega saludo, contadores, accesos y comunicaciones recientes`
- Autor: KriegDev <setnamekrieg@gmail.com>
- Fecha: 2026-10-10 16:10:16 -0300
- Se fusionó en `main` con el commit `c226f7c` (Merge pull request #1 de pedro/inicio)
- Archivos:
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/inicio/InicioUiState.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/inicio/InicioViewModel.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/inicio/PantallaInicio.kt`

### pedro/comunicacion

- Commit completo: `28dce9f80f3e56ca76ed4464c749631a1a1bc551`
- Mensaje: `feat(comunicacion): formulario con vista previa, camara y galeria para evidencias`
- Autor: KriegDev <setnamekrieg@gmail.com>
- Fecha: 2026-10-10 16:13:49 -0300
- Se fusionó en `main` con el commit `9fcbb0c` (Merge pull request #2 de pedro/comunicacion)
- Archivos:
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/comunicacion/Adjunto.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/comunicacion/ComunicacionUiState.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/comunicacion/ComunicacionViewModel.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/comunicacion/MediaArchivo.kt`
  - `app/src/main/java/com/example/mpaznotifica/ui/screens/comunicacion/PantallaComunicacion.kt`
  - `app/src/main/res/xml/file_paths_res.xml`
  - También tocó `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml` y
    `gradle/libs.versions.toml` (permisos de cámara, FileProvider y la librería Coil)

## Cómo recuperar una rama borrada

Como los commits siguen en `main`, basta con volver a crear la rama apuntando al
commit. Desde la carpeta del proyecto:

```bash
# Recrear la rama en tu computador
git branch pedro/inicio 6fb51fefef75b4527635cc54643d1d0930105e4a
git branch pedro/comunicacion 28dce9f80f3e56ca76ed4464c749631a1a1bc551

# Y si hace falta subirla otra vez al repositorio remoto
git push -u origin pedro/inicio
git push -u origin pedro/comunicacion
```

También se pueden revisar los commits directamente, aunque la rama ya no exista:

```bash
git show 6fb51fefef75b4527635cc54643d1d0930105e4a
git show 28dce9f80f3e56ca76ed4464c749631a1a1bc551
```

Además, en el repositorio de GitHub los dos commits quedaron registrados dentro de
los pull request #1 y #2, así que el historial del cambio también se puede
consultar desde la página del repositorio.
