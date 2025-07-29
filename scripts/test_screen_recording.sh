#!/bin/bash

echo "=== Prueba de Grabación de Pantalla S-Box ==="
echo ""

echo "1. Compilando el proyecto..."
cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando prueba de grabación de pantalla..."
    echo "   - Presiona 'Iniciar Grabación' para comenzar"
    echo "   - Mueve el mouse y abre algunas ventanas"
    echo "   - Presiona 'Detener Grabación' para finalizar"
    echo "   - El video se guardará en la carpeta 'recordings'"
    echo ""
    ./gradlew :sbox:runScreenRecordingTest
else
    echo "❌ Error en la compilación"
    exit 1
fi 