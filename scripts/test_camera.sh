#!/bin/bash

echo "=== Prueba de Cámara S-Box ==="
echo ""

echo "1. Compilando el proyecto..."
cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando prueba de cámara..."
    echo "   - Presiona 'Iniciar Cámara' para comenzar"
    echo "   - Presiona 'Detener Cámara' para finalizar"
    echo ""
    ./gradlew :sbox:runCameraTest
else
    echo "❌ Error en la compilación"
    exit 1
fi 