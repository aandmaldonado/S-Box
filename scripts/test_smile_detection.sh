#!/bin/bash

echo "=== Prueba de Detección de Sonrisas S-Box ==="
echo ""

echo "1. Compilando el proyecto..."
cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando detección de sonrisas..."
    echo "   - Presiona 'Iniciar Detección' para comenzar"
    echo "   - Sonríe frente a la cámara para probar"
    echo "   - Observa los rectángulos de detección:"
    echo "     🟢 Verde: Cara detectada"
    echo "     🔴 Rojo: Sonrisa detectada"
    echo "     🔵 Azul: Boca detectada"
    echo "   - Presiona 'Detener Detección' para finalizar"
    echo ""
    ./gradlew :sbox:runSmileDetection
else
    echo "❌ Error en la compilación"
    exit 1
fi 