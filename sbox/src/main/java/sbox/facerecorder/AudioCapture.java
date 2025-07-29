package sbox.facerecorder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase para captura de audio desde micrófono
 * @author amaldonado
 */
@Slf4j
public class AudioCapture {
    
    // Configuración de audio
    private static final float SAMPLE_RATE = 44100.0f;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1; // Mono
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    
    // Variables de captura
    private TargetDataLine audioLine;
    private AudioFormat audioFormat;
    private Thread captureThread;
    private AtomicBoolean isCapturing;
    private ByteArrayOutputStream audioBuffer;
    
    // Variables de sincronización
    private AtomicLong startTimestamp = new AtomicLong(0);
    private AtomicLong currentTimestamp = new AtomicLong(0);
    private AtomicLong sampleCount = new AtomicLong(0);
    
    // Estadísticas
    private long bytesCaptured;
    private double currentVolume;
    
    public AudioCapture() {
        this.isCapturing = new AtomicBoolean(false);
        this.audioBuffer = new ByteArrayOutputStream();
        this.audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
    }
    
    /**
     * Inicia la captura de audio
     */
    public void startCapture() {
        if (isCapturing.get()) {
            log.warn("La captura de audio ya está activa");
            return;
        }
        
        try {
            // Configurar línea de audio
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            
            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException("Formato de audio no soportado");
            }
            
            // Obtener línea de audio
            audioLine = (TargetDataLine) AudioSystem.getLine(info);
            audioLine.open(audioFormat);
            audioLine.start();
            
            // Reiniciar buffer y estadísticas
            audioBuffer.reset();
            bytesCaptured = 0;
            startTimestamp.set(System.currentTimeMillis());
            currentTimestamp.set(startTimestamp.get());
            sampleCount.set(0);
            isCapturing.set(true);
            
            log.info("Captura de audio iniciada - Sample Rate: {}Hz, Bits: {}, Canales: {}", 
                SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS);
            
            // Iniciar thread de captura
            captureThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    captureAudio();
                }
            });
            captureThread.start();
            
        } catch (Exception e) {
            log.error("Error al iniciar captura de audio", e);
            throw new RuntimeException("No se pudo iniciar la captura de audio", e);
        }
    }
    
    /**
     * Detiene la captura de audio
     */
    public void stopCapture() {
        if (!isCapturing.get()) {
            log.warn("La captura de audio no está activa");
            return;
        }
        
        isCapturing.set(false);
        
        if (captureThread != null) {
            try {
                captureThread.join(2000); // Esperar máximo 2 segundos
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de audio", e);
            }
        }
        
        if (audioLine != null) {
            audioLine.stop();
            audioLine.close();
            audioLine = null;
        }
        
        long duration = System.currentTimeMillis() - startTimestamp.get();
        log.info("Captura de audio detenida - Duración: {}ms, Bytes: {}, Muestras: {}", 
            duration, bytesCaptured, sampleCount.get());
    }
    
    /**
     * Captura audio en un thread separado
     */
    private void captureAudio() {
        byte[] buffer = new byte[4096];
        
        while (isCapturing.get()) {
            try {
                int bytesRead = audioLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    audioBuffer.write(buffer, 0, bytesRead);
                    bytesCaptured += bytesRead;
                    
                    // Actualizar timestamp y contador de muestras
                    currentTimestamp.set(System.currentTimeMillis());
                    long samples = bytesRead / 2; // 16 bits = 2 bytes por muestra
                    sampleCount.addAndGet(samples);
                    
                    // Calcular volumen actual (RMS)
                    calculateVolume(buffer, bytesRead);
                }
            } catch (Exception e) {
                log.error("Error en captura de audio", e);
                break;
            }
        }
    }
    
    /**
     * Calcula el volumen actual del audio
     */
    private void calculateVolume(byte[] buffer, int bytesRead) {
        if (bytesRead < 2) return;
        
        double sum = 0;
        int samples = bytesRead / 2; // 16 bits = 2 bytes por muestra
        
        for (int i = 0; i < bytesRead; i += 2) {
            if (i + 1 < bytesRead) {
                // Convertir bytes a muestra de 16 bits
                short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
                sum += sample * sample;
            }
        }
        
        // Calcular RMS (Root Mean Square)
        double rms = Math.sqrt(sum / samples);
        currentVolume = 20 * Math.log10(rms / 32768.0); // Normalizar a dB
    }
    
    /**
     * Guarda el audio capturado en un archivo WAV
     */
    public File saveToFile(String filename) throws IOException {
        if (audioBuffer.size() == 0) {
            throw new IOException("No hay audio capturado para guardar");
        }
        
        try {
            // Crear directorio si no existe
            File outputDir = new File("recordings");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            File outputFile = new File(outputDir, filename);
            
            // Convertir buffer a AudioInputStream
            byte[] audioData = audioBuffer.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
            
            // Guardar como WAV
            AudioSystem.write(ais, javax.sound.sampled.AudioFileFormat.Type.WAVE, outputFile);
            
            log.info("Audio guardado en: {}", outputFile.getAbsolutePath());
            return outputFile;
            
        } catch (Exception e) {
            log.error("Error al guardar audio", e);
            throw new IOException("No se pudo guardar el archivo de audio", e);
        }
    }
    
    /**
     * Obtiene el audio capturado como array de bytes
     */
    public byte[] getAudioData() {
        return audioBuffer.toByteArray();
    }
    
    /**
     * Obtiene el volumen actual en dB
     */
    public double getCurrentVolume() {
        return currentVolume;
    }
    
    /**
     * Obtiene la duración de la captura en milisegundos
     */
    public long getCaptureDuration() {
        if (startTimestamp.get() == 0) return 0;
        return System.currentTimeMillis() - startTimestamp.get();
    }
    
    /**
     * Obtiene el número de bytes capturados
     */
    public long getBytesCaptured() {
        return bytesCaptured;
    }
    
    /**
     * Verifica si la captura está activa
     */
    public boolean isCapturing() {
        return isCapturing.get();
    }
    
    /**
     * Obtiene el timestamp de inicio de la captura
     */
    public long getStartTimestamp() {
        return startTimestamp.get();
    }
    
    /**
     * Obtiene el timestamp actual de la captura
     */
    public long getCurrentTimestamp() {
        return currentTimestamp.get();
    }
    
    /**
     * Obtiene el número de muestras capturadas
     */
    public long getSampleCount() {
        return sampleCount.get();
    }
    
    /**
     * Verifica si la captura está sincronizada
     */
    public boolean isInSync(long expectedTimestamp) {
        if (!isCapturing.get()) {
            return false;
        }
        
        long currentTime = currentTimestamp.get();
        long elapsedTime = currentTime - startTimestamp.get();
        long drift = Math.abs(elapsedTime - expectedTimestamp);
        
        // Considerar sincronizado si el drift es menor a 50ms
        return drift <= 50;
    }
    
    /**
     * Obtiene información de los mixers disponibles
     */
    public static void printAvailableMixers() {
        log.info("=== Mixers Disponibles ===");
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        
        for (int i = 0; i < mixerInfos.length; i++) {
            Mixer.Info info = mixerInfos[i];
            log.info("Mixer {}: {} - {}", i, info.getName(), info.getDescription());
            
            try {
                Mixer mixer = AudioSystem.getMixer(info);
                javax.sound.sampled.Line.Info[] dataLineInfos = mixer.getTargetLineInfo();
                
                for (javax.sound.sampled.Line.Info lineInfo : dataLineInfos) {
                    if (lineInfo instanceof DataLine.Info && 
                        ((DataLine.Info) lineInfo).getLineClass().equals(TargetDataLine.class)) {
                        log.info("  - TargetDataLine: {}", lineInfo);
                    }
                }
            } catch (Exception e) {
                log.warn("Error al obtener información del mixer {}", info.getName(), e);
            }
        }
    }
    
    /**
     * Obtiene el formato de audio configurado
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
    
    /**
     * Limpia el buffer de audio
     */
    public void clearBuffer() {
        audioBuffer.reset();
        bytesCaptured = 0;
        sampleCount.set(0);
    }
} 