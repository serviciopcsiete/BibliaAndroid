# CHANGELOG

## 1.1.2 - 2026-09-17

- Eliminados los botones Anterior / Siguiente del lector.
- Restaurada la navegación principal mediante swipe horizontal cuando está activado `Cambio de hoja`.
- El `HorizontalPager` pagina el contenido del capítulo y, al superar la última hoja, continúa al capítulo siguiente.
- Al deslizar hacia atrás desde la primera hoja, abre el capítulo anterior.
- La transición funciona también entre libros: Génesis 50 → Éxodo 1, Malaquías 4 → Mateo 1, etc.
- El modo `Cambio de hoja` conserva la animación 3D tipo libro; al desactivarlo se usa lectura vertical.

## 1.1.1

- Corregida la Biblioteca: al seleccionar un libro ya no se abre forzosamente el capítulo 1.
- Añadido selector visual de todos los capítulos de cada libro.
- Añadida navegación Anterior / Siguiente dentro del lector.
- La navegación cruza correctamente entre libros (por ejemplo, Génesis 50 → Éxodo 1).
- Se conserva íntegro el corpus RVR1960 proporcionado por el usuario.


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

## 1.2.0
- RVR1960 queda como unica version visible y activa de la aplicacion.
- Biblioteca reorganizada por secciones canonicas: Pentateuco, Historicos, Poeticos y sapienciales, Profetas mayores, Profetas menores, Evangelios, Historia, Cartas paulinas, Cartas generales y Profecia.
- Se elimina el conteo de capitulos de la lista principal de libros.
- La ficha de informacion de cada libro incorpora acceso directo a todos sus capitulos.
- El lector agrega selector rapido de capitulos mediante boton flotante y toque sobre el titulo.
- El modo vertical mantiene scroll por capitulo; el modo Cambio de hoja mantiene swipe horizontal.
- Inicio incorpora Texto del dia, calculado por fecha y obtenido del corpus RVR1960.
- Interfaz reforzada con iconografia Material.
