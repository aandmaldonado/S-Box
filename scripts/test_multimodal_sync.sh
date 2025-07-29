#!/bin/bash

echo "=== Prueba de Sincronización Multimodal S-Box ==="
echo ""

echo "1. Compilando el proyecto..."
cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando prueba de sincronización multimodal..."
    echo "   - Presiona 'Iniciar Sincronización' para comenzar"
    echo "   - Observa el panel de visualización de sincronización"
    echo "   - Verifica que todos los componentes estén sincronizados"
    echo "   - Presiona 'Detener Sincronización' para finalizar"
    echo "   - Presiona 'Guardar Archivos' para guardar los archivos sincronizados"
    echo "   - Los archivos se guardarán en la carpeta 'recordings'"
    echo ""
    echo "3. Componentes que se sincronizan:"
    echo "   🎥 Video: Captura de cámara (640x480, 30 FPS)"
    echo "   🖥️  Pantalla: Grabación de pantalla (MP4, H.264)"
    echo "   🎤 Audio: Captura de micrófono (WAV, 44.1kHz)"
    echo ""
    echo "4. Indicadores de sincronización:"
    echo "   🟢 Verde: Sincronizado (drift < 50ms)"
    echo "   🟡 Amarillo: Advertencia (drift 50-100ms)"
    echo "   🔴 Rojo: No sincronizado (drift > 100ms)"
    echo ""
    ./gradlew :sbox:runMultimodalSyncTest
else
    echo "❌ Error en la compilación"
    exit 1
fi 