package sbox.sync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import sbox.facerecorder.AudioCapture;

import java.awt.image.BufferedImage;

/**
 * Sincronizador multimodal para alinear cámara, pantalla y audio
 * @author amaldonado
 */
@Slf4j
public class MultimodalSynchronizer {
    
    // Estados de sincronización
    private AtomicBoolean isSynchronized = new AtomicBoolean(false);
    private AtomicLong startTimestamp = new AtomicLong(0);
    private AtomicLong currentTimestamp = new AtomicLong(0);
    
    // Componentes de captura
    private VideoCaptureManager videoCapture;
    private SimpleScreenRecorder screenRecorder;
    private AudioCapture audioCapture;
    
    // Configuración de sincronización
    private static final long SYNC_INTERVAL_MS = 100; // 10 FPS para sincronización
    private static final long MAX_SYNC_DRIFT_MS = 50; // Máximo drift permitido
    
    // Estadísticas de sincronización
    private long totalFrames = 0;
    private long syncErrors = 0;
    private long averageDrift = 0;
    
    // Callbacks para notificaciones
    private SyncStatusCallback statusCallback;
    private SyncErrorCallback errorCallback;
    
    public interface SyncStatusCallback {
        void onSyncStatusChanged(boolean isSynchronized, long timestamp, long drift);
        void onFrameCaptured(long frameNumber, long timestamp);
    }
    
    public interface SyncErrorCallback {
        void onSyncError(String error, long timestamp);
    }
    
    public MultimodalSynchronizer() {
        this.videoCapture = new VideoCaptureManager();
        this.screenRecorder = new SimpleScreenRecorder();
        this.audioCapture = new AudioCapture();
    }
    
    /**
     * Inicia la sincronización multimodal
     */
    public void startSynchronization() {
        if (isSynchronized.get()) {
            log.warn("La sincronización ya está activa");
            return;
        }
        
        try {
            log.info("Iniciando sincronización multimodal...");
            
            // Inicializar timestamp de inicio
            startTimestamp.set(System.currentTimeMillis());
            currentTimestamp.set(startTimestamp.get());
            
            // Iniciar componentes
            videoCapture.startCapture();
            screenRecorder.start();
            audioCapture.startCapture();
            
            // Marcar como sincronizado
            isSynchronized.set(true);
            
            // Iniciar thread de sincronización
            startSyncThread();
            
            log.info("Sincronización multimodal iniciada - Timestamp: {}", startTimestamp.get());
            
        } catch (Exception e) {
            log.error("Error al iniciar sincronización multimodal", e);
            if (errorCallback != null) {
                errorCallback.onSyncError("Error al iniciar sincronización: " + e.getMessage(), 
                    System.currentTimeMillis());
            }
        }
    }
    
    /**
     * Detiene la sincronización multimodal
     */
    public void stopSynchronization() {
        if (!isSynchronized.get()) {
            log.warn("La sincronización no está activa");
            return;
        }
        
        try {
            log.info("Deteniendo sincronización multimodal...");
            
            // Detener componentes
            videoCapture.stopCapture();
            screenRecorder.stop();
            audioCapture.stopCapture();
            
            // Marcar como no sincronizado
            isSynchronized.set(false);
            
            // Calcular estadísticas finales
            calculateFinalStats();
            
            log.info("Sincronización multimodal detenida - Frames: {}, Errores: {}, Drift Promedio: {}ms", 
                totalFrames, syncErrors, averageDrift);
            
        } catch (Exception e) {
            log.error("Error al detener sincronización multimodal", e);
            if (errorCallback != null) {
                errorCallback.onSyncError("Error al detener sincronización: " + e.getMessage(), 
                    System.currentTimeMillis());
            }
        }
    }
    
    /**
     * Thread principal de sincronización
     */
    private void startSyncThread() {
        Thread syncThread = new Thread(() -> {
            long lastSyncTime = System.currentTimeMillis();
            
            while (isSynchronized.get()) {
                try {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - startTimestamp.get();
                    
                    // Actualizar timestamp actual
                    currentTimestamp.set(currentTime);
                    
                    // Verificar sincronización de componentes
                    boolean videoInSync = videoCapture.isInSync(elapsedTime);
                    boolean screenInSync = screenRecorder.isRecording();
                    boolean audioInSync = audioCapture.isCapturing();
                    
                    // Calcular drift
                    long drift = calculateDrift(elapsedTime);
                    
                    // Verificar si todo está sincronizado
                    boolean allInSync = videoInSync && screenInSync && audioInSync && 
                                       Math.abs(drift) <= MAX_SYNC_DRIFT_MS;
                    
                    // Actualizar estadísticas
                    totalFrames++;
                    if (!allInSync) {
                        syncErrors++;
                    }
                    
                    // Notificar cambio de estado
                    if (statusCallback != null) {
                        statusCallback.onSyncStatusChanged(allInSync, elapsedTime, drift);
                        statusCallback.onFrameCaptured(totalFrames, elapsedTime);
                    }
                    
                    // Esperar hasta el próximo intervalo de sincronización
                    long sleepTime = SYNC_INTERVAL_MS - (System.currentTimeMillis() - currentTime);
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    
                } catch (InterruptedException e) {
                    log.warn("Thread de sincronización interrumpido", e);
                    break;
                } catch (Exception e) {
                    log.error("Error en thread de sincronización", e);
                    if (errorCallback != null) {
                        errorCallback.onSyncError("Error de sincronización: " + e.getMessage(), 
                            System.currentTimeMillis());
                    }
                }
            }
        });
        
        syncThread.setName("MultimodalSyncThread");
        syncThread.setDaemon(true);
        syncThread.start();
    }
    
    /**
     * Calcula el drift de sincronización
     */
    private long calculateDrift(long elapsedTime) {
        // Obtener timestamps de cada componente
        long videoTimestamp = videoCapture.getCurrentTimestamp();
        long screenTimestamp = screenRecorder.getCurrentTimestamp();
        long audioTimestamp = audioCapture.getCaptureDuration();
        
        // Calcular drift promedio
        long avgTimestamp = (videoTimestamp + screenTimestamp + audioTimestamp) / 3;
        long drift = elapsedTime - avgTimestamp;
        
        return drift;
    }
    
    /**
     * Calcula estadísticas finales
     */
    private void calculateFinalStats() {
        if (totalFrames > 0) {
            averageDrift = Math.abs(averageDrift) / totalFrames;
        }
    }
    
    /**
     * Guarda todos los archivos sincronizados
     */
    public SyncResult saveSynchronizedFiles(String baseFilename) throws IOException {
        if (isSynchronized.get()) {
            throw new IllegalStateException("La sincronización debe estar detenida para guardar archivos");
        }
        
        try {
            // Crear directorio si no existe
            File outputDir = new File("recordings");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Guardar archivos con timestamp común
            long timestamp = startTimestamp.get();
            String timestampStr = String.valueOf(timestamp);
            
            // Guardar video
            File videoFile = videoCapture.saveToFile(baseFilename + "_video_" + timestampStr + ".mp4");
            
            // Guardar frames de pantalla como imágenes
            File screenFile = saveScreenFrames(baseFilename + "_screen_" + timestampStr);
            
            // Guardar audio
            File audioFile = audioCapture.saveToFile(baseFilename + "_audio_" + timestampStr + ".wav");
            
            // Crear archivo de metadatos de sincronización
            File metadataFile = createSyncMetadata(baseFilename + "_sync_" + timestampStr + ".json", 
                timestamp, totalFrames, syncErrors, averageDrift);
            
            log.info("Archivos sincronizados guardados - Video: {}, Pantalla: {}, Audio: {}, Metadata: {}", 
                videoFile.getName(), screenFile.getName(), audioFile.getName(), metadataFile.getName());
            
            return new SyncResult(videoFile, screenFile, audioFile, metadataFile, 
                timestamp, totalFrames, syncErrors, averageDrift);
            
        } catch (Exception e) {
            log.error("Error al guardar archivos sincronizados", e);
            throw new IOException("No se pudieron guardar los archivos sincronizados", e);
        }
    }
    
    /**
     * Guarda frames de pantalla como imágenes
     */
    private File saveScreenFrames(String baseFilename) throws IOException {
        try {
            // Crear directorio para frames
            File framesDir = new File("recordings", baseFilename + "_frames");
            if (!framesDir.exists()) {
                framesDir.mkdirs();
            }
            
            // Guardar algunos frames representativos
            ArrayList<BufferedImage> frames = screenRecorder.getFrameBuffer();
            int totalFrames = frames.size();
            
            // Guardar frame inicial, medio y final
            if (totalFrames > 0) {
                screenRecorder.saveFrameAsImage(0, baseFilename + "_frame_001.png");
                if (totalFrames > 1) {
                    screenRecorder.saveFrameAsImage(totalFrames / 2, baseFilename + "_frame_mid.png");
                    screenRecorder.saveFrameAsImage(totalFrames - 1, baseFilename + "_frame_end.png");
                }
            }
            
            // Crear archivo de información de frames
            File infoFile = new File("recordings", baseFilename + "_info.txt");
            String info = String.format("Total Frames: %d\nDuration: %dms\nFPS: %.1f\nBuffer Size: %d", 
                totalFrames, screenRecorder.getCurrentTimestamp(), 
                totalFrames > 0 ? (totalFrames * 1000.0) / screenRecorder.getCurrentTimestamp() : 0,
                frames.size());
            
            java.nio.file.Files.write(infoFile.toPath(), info.getBytes());
            
            return infoFile;
            
        } catch (Exception e) {
            log.error("Error al guardar frames de pantalla", e);
            throw new IOException("No se pudieron guardar los frames de pantalla", e);
        }
    }
    
    /**
     * Crea archivo de metadatos de sincronización
     */
    private File createSyncMetadata(String filename, long startTime, long frames, long errors, long avgDrift) 
            throws IOException {
        File metadataFile = new File("recordings", filename);
        
        String metadata = String.format(
            "{\n" +
            "  \"sync_info\": {\n" +
            "    \"start_timestamp\": %d,\n" +
            "    \"total_frames\": %d,\n" +
            "    \"sync_errors\": %d,\n" +
            "    \"average_drift_ms\": %d,\n" +
            "    \"sync_interval_ms\": %d,\n" +
            "    \"max_drift_ms\": %d\n" +
            "  },\n" +
            "  \"components\": {\n" +
            "    \"video\": {\n" +
            "      \"enabled\": true,\n" +
            "      \"format\": \"MP4\",\n" +
            "      \"resolution\": \"640x480\",\n" +
            "      \"fps\": 30\n" +
            "    },\n" +
            "    \"screen\": {\n" +
            "      \"enabled\": true,\n" +
            "      \"format\": \"PNG\",\n" +
            "      \"storage\": \"memory_buffer\"\n" +
            "    },\n" +
            "    \"audio\": {\n" +
            "      \"enabled\": true,\n" +
            "      \"format\": \"WAV\",\n" +
            "      \"sample_rate\": 44100,\n" +
            "      \"bits\": 16,\n" +
            "      \"channels\": 1\n" +
            "    }\n" +
            "  }\n" +
            "}",
            startTime, frames, errors, avgDrift, SYNC_INTERVAL_MS, MAX_SYNC_DRIFT_MS
        );
        
        java.nio.file.Files.write(metadataFile.toPath(), metadata.getBytes());
        return metadataFile;
    }
    
    // Getters y Setters
    
    public boolean isSynchronized() {
        return isSynchronized.get();
    }
    
    public long getStartTimestamp() {
        return startTimestamp.get();
    }
    
    public long getCurrentTimestamp() {
        return currentTimestamp.get();
    }
    
    public long getTotalFrames() {
        return totalFrames;
    }
    
    public long getSyncErrors() {
        return syncErrors;
    }
    
    public long getAverageDrift() {
        return averageDrift;
    }
    
    public void setStatusCallback(SyncStatusCallback callback) {
        this.statusCallback = callback;
    }
    
    public void setErrorCallback(SyncErrorCallback callback) {
        this.errorCallback = callback;
    }
    
    public VideoCaptureManager getVideoCapture() {
        return videoCapture;
    }
    
    public SimpleScreenRecorder getScreenRecorder() {
        return screenRecorder;
    }
    
    public AudioCapture getAudioCapture() {
        return audioCapture;
    }
    
    /**
     * Resultado de la sincronización
     */
    public static class SyncResult {
        private final File videoFile;
        private final File screenFile;
        private final File audioFile;
        private final File metadataFile;
        private final long startTimestamp;
        private final long totalFrames;
        private final long syncErrors;
        private final long averageDrift;
        
        public SyncResult(File videoFile, File screenFile, File audioFile, File metadataFile,
                         long startTimestamp, long totalFrames, long syncErrors, long averageDrift) {
            this.videoFile = videoFile;
            this.screenFile = screenFile;
            this.audioFile = audioFile;
            this.metadataFile = metadataFile;
            this.startTimestamp = startTimestamp;
            this.totalFrames = totalFrames;
            this.syncErrors = syncErrors;
            this.averageDrift = averageDrift;
        }
        
        // Getters
        public File getVideoFile() { return videoFile; }
        public File getScreenFile() { return screenFile; }
        public File getAudioFile() { return audioFile; }
        public File getMetadataFile() { return metadataFile; }
        public long getStartTimestamp() { return startTimestamp; }
        public long getTotalFrames() { return totalFrames; }
        public long getSyncErrors() { return syncErrors; }
        public long getAverageDrift() { return averageDrift; }
    }
} 