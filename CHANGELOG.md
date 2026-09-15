# Changelog

## [1.0.0] - 2026-09-15
### Added
- Primera versión funcional del proyecto Android.
- Navegación principal Inicio/Biblia/Planes/Temas/Ajustes.
- Lector offline, modo página y scroll.
- Soporte multiversión y conversor USFM.
- Base Room para favoritos, notas, resaltados, historial y progreso.
- Planes anual y cronológico de 365 días.
- Temas por necesidad.
- Configuración persistente mediante DataStore.
- Soporte de tema claro, sepia y oscuro.
- Compartir versículos.

## 1.0.1
- Migración a Kotlin integrado de AGP 9.4.
- Eliminado `org.jetbrains.kotlin.android` incompatible con el nuevo DSL de AGP 9.
- Corregidos errores de compilación Compose en `BibleApp.kt`, `ReaderScreen.kt`, `Screens.kt` y `Theme.kt`.
- Añadidos argumentos KSP para exportar esquemas de Room.
- Añadido `codemagic.yaml` para compilación Debug con Gradle 9.6.0 y JDK 17.
- Incrementados `versionCode` a 2 y `versionName` a 1.0.1.
