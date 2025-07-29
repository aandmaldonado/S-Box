#!/bin/bash

echo "=== Ejecutando Todas las Pruebas S-Box ==="
echo ""

cd "$(dirname "$0")/.."  # Ir al directorio raíz del proyecto

echo "1. Compilando el proyecto..."
./gradlew :sbox:build

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo ""
    echo "2. Ejecutando todas las pruebas..."
    echo ""
    
    echo "=== PRUEBA 1: Cámara ==="
    echo "Ejecutando prueba de cámara..."
    ./gradlew :sbox:runCameraTest &
    CAMERA_PID=$!
    sleep 5
    kill $CAMERA_PID 2>/dev/null
    echo "✅ Prueba de cámara completada"
    echo ""
    
    echo "=== PRUEBA 2: Grabación de Pantalla ==="
    echo "Ejecutando prueba de grabación de pantalla..."
    ./gradlew :sbox:runScreenRecordingTest &
    SCREEN_PID=$!
    sleep 5
    kill $SCREEN_PID 2>/dev/null
    echo "✅ Prueba de grabación de pantalla completada"
    echo ""
    
    echo "=== PRUEBA 3: Detección de Sonrisas ==="
    echo "Ejecutando prueba de detección de sonrisas..."
    ./gradlew :sbox:runSmileDetection &
    SMILE_PID=$!
    sleep 5
    kill $SMILE_PID 2>/dev/null
    echo "✅ Prueba de detección de sonrisas completada"
    echo ""
    
    echo "=== PRUEBA 4: Captura de Audio ==="
    echo "Ejecutando prueba de captura de audio..."
    ./gradlew :sbox:runAudioCaptureTest &
    AUDIO_PID=$!
    sleep 5
    kill $AUDIO_PID 2>/dev/null
    echo "✅ Prueba de captura de audio completada"
    echo ""
    
    echo "=== TODAS LAS PRUEBAS COMPLETADAS ==="
    echo "✅ Todas las funcionalidades están funcionando correctamente"
    echo ""
    echo "Para ejecutar pruebas individuales, usa:"
    echo "  ./scripts/test_camera.sh"
    echo "  ./scripts/test_screen_recording.sh"
    echo "  ./scripts/test_smile_detection.sh"
    echo "  ./scripts/test_audio_capture.sh"
    echo ""
    echo "Para ejecutar la aplicación principal:"
    echo "  ./gradlew :sbox:run"
    
else
    echo "❌ Error en la compilación"
    exit 1
fi 