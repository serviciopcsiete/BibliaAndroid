#!/bin/sh
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; fi
echo "Gradle 9.6 no está instalado. Abre el proyecto con Android Studio o instala Gradle 9.6." >&2
exit 1
