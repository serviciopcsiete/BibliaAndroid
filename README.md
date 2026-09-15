# Biblia Android — V1.0.0

Aplicación Android nativa en **Kotlin + Jetpack Compose + Room** orientada a lectura bíblica offline.

## Funciones implementadas

- Biblioteca de 66 libros y selector por testamento.
- Lector por capítulos.
- Modo página con animación 3D tipo cambio de hoja y modo scroll.
- Arquitectura multiversión (RV1909 / WEB).
- Búsqueda de texto.
- Favoritos, notas, subrayados y historial en Room.
- Información introductoria para los 66 libros.
- Plan de lectura anual de 365 días.
- Plan cronológico de 365 días.
- Textos por necesidades/temas.
- Progreso de planes persistente.
- Configuración de versión, tamaño de fuente, tema claro/sepia/oscuro y modo de lectura.
- Compartir versículos mediante el Share Sheet de Android.
- Todo el contenido de usuario permanece local.

## Importante: corpus bíblico

El ZIP incluye **datos demostrativos mínimos** para poder ejecutar la interfaz inmediatamente. Por restricciones del entorno de generación no se descargaron binarios externos dentro del proyecto. Para obtener la V1 con los textos completos, descarga los paquetes USFM oficiales y conviértelos:

```bash
python tools/import_usfm.py spaRV1909_usfm.zip rv1909
python tools/import_usfm.py engwebp_usfm.zip web
```

Las URLs oficiales están en `tools/README.md` y las licencias en `LICENSES.md`.

## Abrir

1. Android Studio Quail 4 (2026.1.4) o posterior.
2. Abrir esta carpeta.
3. JDK 17.
4. SDK Android API 37 / Build Tools 36+.
5. Sincronizar Gradle.
6. Ejecutar `app`.

El proyecto usa AGP 9.4.0, Kotlin 2.3.21, Compose BOM 2026.08.00, Material 3 1.4.0 y Room 2.8.5.

## Datos y privacidad

La V1 no requiere cuenta, servidor, analítica ni permisos sensibles. Los favoritos, notas, historial y progreso se guardan localmente.

## Próxima iteración recomendada

- Resolver referencias (`Jn 3:16`) directamente en búsqueda.
- Fichas históricas académicamente revisadas para cada libro.
- Page curl físico con shader opcional.
- Widget de versículo diario.
- Backup/sincronización opcional.
