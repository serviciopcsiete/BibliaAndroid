# CHANGELOG

## 1.1.0 - 2026-09-17

### Añadido
- Integración offline completa del dataset RVR1960 proporcionado para pruebas.
- RVR1960 como versión bíblica predeterminada.
- Parser compatible con el formato rico del JSON: libros, capítulos, versículos, encabezados, etiquetas y secciones.
- Renderizado de `heading1`, `label` y `section1` en el lector.
- Conservación de saltos de línea en pasajes poéticos.
- Normalización de capítulos duplicados mediante `chapter_usfm`.
- Fusión de fragmentos repetidos del mismo número de versículo para búsqueda.
- Identificación de la versión al compartir un versículo.

### Corregido
- Import incorrecto de `weight` en `Screens.kt`.
- Mensaje del lector cuando un capítulo no dispone de contenido.

## 1.0.1 - 2026-09-15
- Primera compilación APK exitosa en Codemagic tras correcciones Kotlin/Compose/AGP.

## 1.0.0 - 2026-09-15
- Estructura inicial de la aplicación Android.
