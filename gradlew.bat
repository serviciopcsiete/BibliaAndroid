@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (gradle %*) else (echo Gradle 9.6 no esta instalado. Abre el proyecto con Android Studio o instala Gradle 9.6. & exit /b 1)
