# MPAZ Notifica

Aplicación móvil Android para la **Escuela Escritora Marcela Paz**. Su objetivo es apoyar el envío
y el registro de las comunicaciones que la escuela dirige a los apoderados (citaciones, avisos,
actividades, talleres y autorizaciones), reemplazando parte del proceso en papel.

Proyecto académico de la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** (Duoc UC).
Es un MVP: todos los datos que usa la aplicación son **ficticios**.

## Integrantes

- Carolina Hermosilla
- Pedro Toledo

## Tecnologías

- Kotlin
- Jetpack Compose con Material Design 3
- Arquitectura MVVM (ViewModel + estado)
- Persistencia local con Room (SQLite)
- Navegación con Navigation Compose
- Android Studio

## Funcionalidades

| # | Componente | Responsable | Estado |
|---|------------|-------------|--------|
| 1 | Login | Carolina | En desarrollo |
| 2 | Inicio | Pedro | Pendiente |
| 3 | Comunicación (cámara y galería) | Pedro | Pendiente |
| 4 | Selección de apoderados (vibración y animación de envío) | Carolina | En desarrollo |
| 5 | Selección y creación de grupos | Carolina | En desarrollo |
| 6 | Historial de comunicaciones | Pedro | Pendiente |

## Cómo ejecutar el proyecto

1. Clonar o descargar el repositorio.
2. Abrir la carpeta del proyecto con **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias (la primera vez requiere conexión a internet).
4. Verificar que el archivo `local.properties` apunte al SDK de Android de tu computador.
5. Elegir un emulador o un teléfono con Android (mínimo Android 6.0 / API 23) y presionar **Run**.

## Credenciales de prueba

La aplicación no tiene autenticación real: no existe servidor ni usuarios verdaderos.

- Correo: `profesor.jefe@ejemplo.cl`
- Contraseña: `123456`

## Estructura del proyecto

```
app/src/main/java/com/example/mpaznotifica/
├── MainActivity.kt
├── data/local/            Base de datos Room (entidades, DAO y AppDatabase)
└── ui/
    ├── components/        Componentes reutilizables (barra superior, etc.)
    ├── navigation/        Rutas y navegación entre pantallas
    ├── screens/           Una carpeta por pantalla
    └── theme/             Colores y tipografía (tomados del mockup)
```

## Estado del avance

Primera entrega: estructura del proyecto, tema visual, navegación y persistencia local.
Las pantallas se están desarrollando una por una.
