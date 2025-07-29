#!/bin/bash

echo "=== Prueba de Captura de Audio S-Box ==="
echo ""

echo "1. Compilando el proyecto..."
cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando prueba de captura de audio..."
    echo "   - Presiona 'Iniciar Captura' para comenzar"
    echo "   - Habla o haz ruido para ver el medidor de volumen"
    echo "   - Observa el medidor de volumen en tiempo real"
    echo "   - Presiona 'Detener Captura' para finalizar"
    echo "   - Presiona 'Guardar Audio' para guardar como WAV"
    echo "   - El archivo se guardará en la carpeta 'recordings'"
    echo ""
    ./gradlew :sbox:runAudioCaptureTest
else
    echo "❌ Error en la compilación"
    exit 1
fi 