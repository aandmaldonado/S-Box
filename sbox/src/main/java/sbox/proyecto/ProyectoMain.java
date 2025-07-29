package sbox.proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lombok.extern.slf4j.Slf4j;

// Importaciones de Bytedeco/JavaCV
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.IplImage;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

// Importar ScreenRecorder y AudioCapture
import sbox.activityrender.ScreenRecorder;
import sbox.facerecorder.AudioCapture;
import sbox.sync.MultimodalSynchronizer;

/**
 * Aplicación principal S-Box
 * @author amaldonado
 */
@Slf4j
public class ProyectoMain extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Variables estáticas para PerspectivaCliente
    public static boolean ScreenGo = false;
    public static boolean ScreenStop = false;
    
    // Variables para captura de video
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToIplImage converter;
    private Java2DFrameConverter java2DConverter;
    private IplImage grabbedImage = null;
    private IplImage grayImage = null;
    private boolean isCapturing = false;
    private Thread captureThread;
    private BufferedImage currentFrame = null;
    
    // Variables para grabación de pantalla
    private ScreenRecorder screenRecorder;
    private boolean isScreenRecording = false;
    
    // Variables para detección de sonrisas
    private boolean isSmileDetectionEnabled = false;
    private int faceCount = 0;
    private int smileCount = 0;
    private int mouthCount = 0;
    
    // Variables para captura de audio
    private AudioCapture audioCapture;
    private boolean isAudioRecording = false;
    private Timer audioUpdateTimer;
    
    // Variables para sincronización multimodal
    private MultimodalSynchronizer synchronizer;
    private boolean isSynchronized = false;
    private Timer syncUpdateTimer;
    
    private JPanel mainPanel;
    private JButton btnIniciarCamara;
    private JButton btnDetenerCamara;
    private JButton btnIniciarGrabacion;
    private JButton btnDetenerGrabacion;
    private JButton btnToggleDeteccion;
    private JButton btnToggleAudio;
    private JButton btnSincronizacion;
    private JLabel lblEstado;
    private JLabel lblStats;
    private JLabel lblAudioStats;
    private JLabel lblSyncStats;
    private VideoPanel videoPanel;
    private VolumeMeter volumeMeter;
    private SyncStatusPanel syncStatusPanel;
    
    public ProyectoMain() {
        initComponents();
        setupLayout();
        setupWindowListener();
        initScreenRecorder();
        initAudioCapture();
        initSynchronizer();
    }
    
    private void initComponents() {
        setTitle("S-Box - Sistema de Grabación, Detección, Audio y Sincronización");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnIniciarCamara = new JButton("Iniciar Cámara");
        btnDetenerCamara = new JButton("Detener Cámara");
        btnIniciarGrabacion = new JButton("Iniciar Grabación");
        btnDetenerGrabacion = new JButton("Detener Grabación");
        btnToggleDeteccion = new JButton("Activar Detección");
        btnToggleAudio = new JButton("Activar Audio");
        btnSincronizacion = new JButton("Iniciar Sincronización");
        lblEstado = new JLabel("Estado: Listo");
        lblStats = new JLabel("Estadísticas: -");
        lblAudioStats = new JLabel("Audio: -");
        lblSyncStats = new JLabel("Sincronización: -");
        
        // Configurar botones
        btnIniciarCamara.setFont(new Font("Arial", Font.BOLD, 14));
        btnDetenerCamara.setFont(new Font("Arial", Font.BOLD, 14));
        btnIniciarGrabacion.setFont(new Font("Arial", Font.BOLD, 14));
        btnDetenerGrabacion.setFont(new Font("Arial", Font.BOLD, 14));
        btnToggleDeteccion.setFont(new Font("Arial", Font.BOLD, 14));
        btnToggleAudio.setFont(new Font("Arial", Font.BOLD, 14));
        btnSincronizacion.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStats.setFont(new Font("Arial", Font.PLAIN, 12));
        lblAudioStats.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSyncStats.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Agregar listeners
        btnIniciarCamara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarCamara();
            }
        });
        
        btnDetenerCamara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerCamara();
            }
        });
        
        btnIniciarGrabacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarGrabacion();
            }
        });
        
        btnDetenerGrabacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerGrabacion();
            }
        });
        
        btnToggleDeteccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDeteccion();
            }
        });
        
        btnToggleAudio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAudio();
            }
        });
        
        btnSincronizacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSincronizacion();
            }
        });
        
        controlPanel.add(btnIniciarCamara);
        controlPanel.add(btnDetenerCamara);
        controlPanel.add(btnIniciarGrabacion);
        controlPanel.add(btnDetenerGrabacion);
        controlPanel.add(btnToggleDeteccion);
        controlPanel.add(btnToggleAudio);
        controlPanel.add(btnSincronizacion);
        controlPanel.add(lblEstado);
        controlPanel.add(lblStats);
        controlPanel.add(lblAudioStats);
        controlPanel.add(lblSyncStats);
        
        // Panel de video
        videoPanel = new VideoPanel();
        videoPanel.setPreferredSize(new Dimension(640, 480));
        
        // Medidor de volumen
        volumeMeter = new VolumeMeter();
        volumeMeter.setPreferredSize(new Dimension(200, 100));
        
        // Panel de estado de sincronización
        syncStatusPanel = new SyncStatusPanel();
        syncStatusPanel.setPreferredSize(new Dimension(200, 150));
        
        // Panel derecho para controles
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(volumeMeter, BorderLayout.NORTH);
        rightPanel.add(syncStatusPanel, BorderLayout.CENTER);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(videoPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                detenerCamara();
                detenerGrabacion();
                detenerAudio();
                detenerSincronizacion();
            }
        });
    }
    
    private void initScreenRecorder() {
        screenRecorder = new ScreenRecorder();
        ScreenRecorder.nombreProyecto = "sbox_main";
        ScreenRecorder.experimentos = 1;
    }
    
    private void initAudioCapture() {
        audioCapture = new AudioCapture();
    }
    
    private void initSynchronizer() {
        synchronizer = new MultimodalSynchronizer();
        
        // Configurar callbacks de sincronización
        synchronizer.setStatusCallback(new MultimodalSynchronizer.SyncStatusCallback() {
            @Override
            public void onSyncStatusChanged(boolean isSynchronized, long timestamp, long drift) {
                SwingUtilities.invokeLater(() -> {
                    syncStatusPanel.setSyncStatus(isSynchronized, timestamp, drift);
                    syncStatusPanel.repaint();
                    
                    String status = isSynchronized ? "Sincronizado" : "No sincronizado";
                    lblSyncStats.setText(String.format("Sincronización: %s | Drift: %dms | Tiempo: %ds", 
                        status, drift, timestamp / 1000));
                });
            }
            
            @Override
            public void onFrameCaptured(long frameNumber, long timestamp) {
                // Actualizar estadísticas de frames si es necesario
            }
        });
        
        synchronizer.setErrorCallback(new MultimodalSynchronizer.SyncErrorCallback() {
            @Override
            public void onSyncError(String error, long timestamp) {
                SwingUtilities.invokeLater(() -> {
                    lblSyncStats.setText("Error: " + error);
                    log.error("Error de sincronización: {}", error);
                });
            }
        });
    }
    
    private void iniciarCamara() {
        log.info("Iniciando cámara...");
        lblEstado.setText("Estado: Iniciando cámara...");
        
        try {
            // Inicializar grabber - usar OpenCVFrameGrabber para compatibilidad con macOS
            grabber = new OpenCVFrameGrabber(0); // Cámara 0
            grabber.start();
            
            // Inicializar convertidores
            converter = new OpenCVFrameConverter.ToIplImage();
            java2DConverter = new Java2DFrameConverter();
            
            // Crear imágenes
            grabbedImage = IplImage.create(640, 480, IPL_DEPTH_8U, 3);
            grayImage = IplImage.create(640, 480, IPL_DEPTH_8U, 1);
            
            isCapturing = true;
            lblEstado.setText("Estado: Cámara iniciada");
            
            // Iniciar thread de captura
            captureThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isCapturing) {
                        try {
                            Frame frame = grabber.grab();
                            if (frame != null) {
                                // Convertir frame a IplImage
                                IplImage image = converter.convert(frame);
                                if (image != null) {
                                    // Redimensionar si es necesario
                                    if (image.width() != 640 || image.height() != 480) {
                                        IplImage resized = IplImage.create(640, 480, IPL_DEPTH_8U, 3);
                                        cvResize(image, resized);
                                        image = resized;
                                    }
                                    
                                    // Copiar a grabbedImage
                                    cvCopy(image, grabbedImage);
                                    
                                    // Convertir a escala de grises
                                    cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                                    
                                    // Realizar detección de sonrisas si está habilitada
                                    if (isSmileDetectionEnabled) {
                                        performSmileDetection(grabbedImage, grayImage);
                                    }
                                    
                                    // Convertir a BufferedImage para mostrar
                                    Frame displayFrame = converter.convert(grabbedImage);
                                    BufferedImage bufferedImage = java2DConverter.convert(displayFrame);
                                    
                                    // Actualizar UI en EDT
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentFrame = bufferedImage;
                                            videoPanel.setFrame(currentFrame);
                                            videoPanel.repaint();
                                            
                                            // Actualizar estadísticas si la detección está activa
                                            if (isSmileDetectionEnabled) {
                                                lblStats.setText(String.format("Caras: %d | Sonrisas: %d | Bocas: %d", 
                                                    faceCount, smileCount, mouthCount));
                                            }
                                        }
                                    });
                                }
                            }
                            Thread.sleep(33); // ~30 FPS
                        } catch (Exception e) {
                            log.error("Error en captura de cámara", e);
                            break;
                        }
                    }
                }
            });
            captureThread.start();
            
        } catch (Exception e) {
            log.error("Error al iniciar cámara", e);
            lblEstado.setText("Estado: Error al iniciar cámara - " + e.getMessage());
        }
    }
    
    private void detenerCamara() {
        log.info("Deteniendo cámara...");
        isCapturing = false;
        
        if (captureThread != null) {
            try {
                captureThread.join(1000); // Esperar máximo 1 segundo
            } catch (InterruptedException e) {
                log.warn("Interrupción al detener thread de cámara", e);
            }
        }
        
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception e) {
                log.error("Error al detener grabber", e);
            }
        }
        
        if (grabbedImage != null) {
            grabbedImage.release();
            grabbedImage = null;
        }
        if (grayImage != null) {
            grayImage.release();
            grayImage = null;
        }
        
        currentFrame = null;
        videoPanel.setFrame(null);
        videoPanel.repaint();
        
        lblEstado.setText("Estado: Cámara detenida");
        lblStats.setText("Estadísticas: -");
    }
    
    private void iniciarGrabacion() {
        log.info("Iniciando grabación...");
        
        if (isScreenRecording) {
            lblEstado.setText("Estado: Grabación ya está activa");
            return;
        }
        
        try {
            screenRecorder.start();
            isScreenRecording = true;
            lblEstado.setText("Estado: Grabando pantalla...");
            
            // Actualizar estado de variables estáticas
            ScreenGo = true;
            ScreenStop = false;
            
        } catch (Exception e) {
            log.error("Error al iniciar grabación", e);
            lblEstado.setText("Estado: Error al iniciar grabación - " + e.getMessage());
        }
    }
    
    private void detenerGrabacion() {
        log.info("Deteniendo grabación...");
        
        if (!isScreenRecording) {
            lblEstado.setText("Estado: No hay grabación activa");
            return;
        }
        
        try {
            String outputFile = screenRecorder.stop();
            isScreenRecording = false;
            lblEstado.setText("Estado: Grabación detenida - " + outputFile);
            
            // Actualizar estado de variables estáticas
            ScreenGo = false;
            ScreenStop = true;
            
        } catch (Exception e) {
            log.error("Error al detener grabación", e);
            lblEstado.setText("Estado: Error al detener grabación - " + e.getMessage());
        }
    }
    
    private void toggleDeteccion() {
        if (isSmileDetectionEnabled) {
            isSmileDetectionEnabled = false;
            btnToggleDeteccion.setText("Activar Detección");
            lblEstado.setText("Estado: Detección desactivada");
            lblStats.setText("Estadísticas: -");
            log.info("Detección de sonrisas desactivada");
        } else {
            isSmileDetectionEnabled = true;
            btnToggleDeteccion.setText("Desactivar Detección");
            lblEstado.setText("Estado: Detección activada");
            log.info("Detección de sonrisas activada");
        }
    }
    
    private void toggleAudio() {
        if (isAudioRecording) {
            detenerAudio();
        } else {
            iniciarAudio();
        }
    }
    
    private void iniciarAudio() {
        try {
            audioCapture.startCapture();
            isAudioRecording = true;
            btnToggleAudio.setText("Detener Audio");
            lblEstado.setText("Estado: Audio activado");
            
            // Iniciar timer para actualizar medidor de volumen
            audioUpdateTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateAudioUI();
                }
            });
            audioUpdateTimer.start();
            
            log.info("Captura de audio iniciada");
            
        } catch (Exception e) {
            log.error("Error al iniciar audio", e);
            lblEstado.setText("Estado: Error al iniciar audio - " + e.getMessage());
        }
    }
    
    private void detenerAudio() {
        if (audioCapture != null && audioCapture.isCapturing()) {
            audioCapture.stopCapture();
            isAudioRecording = false;
            btnToggleAudio.setText("Activar Audio");
            lblEstado.setText("Estado: Audio detenido");
            
            if (audioUpdateTimer != null) {
                audioUpdateTimer.stop();
            }
            
            log.info("Captura de audio detenida");
        }
    }
    
    private void updateAudioUI() {
        if (audioCapture != null && audioCapture.isCapturing()) {
            double volume = audioCapture.getCurrentVolume();
            volumeMeter.setVolume(volume);
            volumeMeter.repaint();
            
            long duration = audioCapture.getCaptureDuration();
            long bytes = audioCapture.getBytesCaptured();
            lblAudioStats.setText(String.format("Audio: %ds | %.1f dB | %d bytes", 
                duration / 1000, volume, bytes));
        }
    }
    
    private void toggleSincronizacion() {
        if (isSynchronized) {
            detenerSincronizacion();
        } else {
            iniciarSincronizacion();
        }
    }
    
    private void iniciarSincronizacion() {
        try {
            synchronizer.startSynchronization();
            isSynchronized = true;
            btnSincronizacion.setText("Detener Sincronización");
            lblEstado.setText("Estado: Sincronización activada");
            
            // Iniciar timer para actualizar estadísticas de sincronización
            syncUpdateTimer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateSyncUI();
                }
            });
            syncUpdateTimer.start();
            
            log.info("Sincronización multimodal iniciada");
            
        } catch (Exception e) {
            log.error("Error al iniciar sincronización", e);
            lblEstado.setText("Estado: Error al iniciar sincronización - " + e.getMessage());
        }
    }
    
    private void detenerSincronizacion() {
        if (synchronizer != null && synchronizer.isSynchronized()) {
            try {
                synchronizer.stopSynchronization();
                isSynchronized = false;
                btnSincronizacion.setText("Iniciar Sincronización");
                lblEstado.setText("Estado: Sincronización detenida");
                
                if (syncUpdateTimer != null) {
                    syncUpdateTimer.stop();
                }
                
                // Guardar archivos sincronizados
                try {
                    MultimodalSynchronizer.SyncResult result = synchronizer.saveSynchronizedFiles("sbox_sync");
                    log.info("Archivos sincronizados guardados - Video: {}, Pantalla: {}, Audio: {}", 
                        result.getVideoFile().getName(), result.getScreenFile().getName(), 
                        result.getAudioFile().getName());
                } catch (IOException e) {
                    log.error("Error al guardar archivos sincronizados", e);
                }
                
                log.info("Sincronización multimodal detenida");
                
            } catch (Exception e) {
                log.error("Error al detener sincronización", e);
                lblEstado.setText("Estado: Error al detener sincronización - " + e.getMessage());
            }
        }
    }
    
    private void updateSyncUI() {
        if (synchronizer != null && synchronizer.isSynchronized()) {
            long totalFrames = synchronizer.getTotalFrames();
            long syncErrors = synchronizer.getSyncErrors();
            long averageDrift = synchronizer.getAverageDrift();
            
            lblSyncStats.setText(String.format("Sincronización: Activa | Frames: %d | Errores: %d | Drift: %dms", 
                totalFrames, syncErrors, averageDrift));
        }
    }
    
    private void performSmileDetection(IplImage colorImage, IplImage grayImage) {
        try {
            // TODO: Implementar detección de sonrisas usando los clasificadores Haar
            // Por ahora, simulamos la detección
            faceCount = 1; // Simular una cara detectada
            smileCount = (int)(Math.random() * 2); // Simular 0 o 1 sonrisa
            mouthCount = 1; // Simular una boca detectada
            
        } catch (Exception e) {
            log.error("Error en detección de sonrisas", e);
        }
    }
    
    // Panel personalizado para mostrar video
    private class VideoPanel extends JPanel {
        private BufferedImage frame = null;
        
        public void setFrame(BufferedImage frame) {
            this.frame = frame;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            if (frame != null) {
                // Dibujar el frame centrado
                int x = (getWidth() - frame.getWidth()) / 2;
                int y = (getHeight() - frame.getHeight()) / 2;
                g2d.drawImage(frame, x, y, null);
            } else {
                // Dibujar fondo negro con texto
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                String text = "Área de Video";
                int x = (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(text, x, y);
            }
        }
    }
    
    // Panel personalizado para medidor de volumen
    private class VolumeMeter extends JPanel {
        private double volume = -60.0; // dB
        
        public void setVolume(double volume) {
            this.volume = volume;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            int width = getWidth();
            int height = getHeight();
            
            // Dibujar fondo
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            
            // Dibujar barra de volumen
            double normalizedVolume = Math.max(0, Math.min(1, (volume + 60) / 60.0));
            int barHeight = (int) (height * normalizedVolume);
            
            // Color basado en el volumen
            if (normalizedVolume < 0.3) {
                g2d.setColor(Color.GREEN);
            } else if (normalizedVolume < 0.7) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.RED);
            }
            
            g2d.fillRect(10, height - barHeight - 10, width - 20, barHeight);
            
            // Dibujar texto del volumen
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String volumeText = String.format("%.1f dB", volume);
            int textWidth = g2d.getFontMetrics().stringWidth(volumeText);
            g2d.drawString(volumeText, (width - textWidth) / 2, height - 5);
        }
    }
    
    // Panel personalizado para estado de sincronización
    private class SyncStatusPanel extends JPanel {
        private boolean isSynchronized = false;
        private long timestamp = 0;
        private long drift = 0;
        
        public void setSyncStatus(boolean isSynchronized, long timestamp, long drift) {
            this.isSynchronized = isSynchronized;
            this.timestamp = timestamp;
            this.drift = drift;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            int width = getWidth();
            int height = getHeight();
            
            // Dibujar fondo
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            
            // Dibujar indicador de sincronización
            g2d.setColor(isSynchronized ? Color.GREEN : Color.RED);
            g2d.fillOval(10, 10, 20, 20);
            
            // Dibujar texto de estado
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String statusText = isSynchronized ? "Sincronizado" : "No Sincronizado";
            g2d.drawString(statusText, 40, 25);
            
            // Dibujar información de tiempo
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(String.format("Tiempo: %ds", timestamp / 1000), 10, 45);
            g2d.drawString(String.format("Drift: %dms", drift), 10, 60);
            
            // Dibujar indicador de drift
            double normalizedDrift = Math.max(0, Math.min(1, Math.abs(drift) / 50.0));
            int barWidth = (int) (width * normalizedDrift);
            
            if (normalizedDrift < 0.3) {
                g2d.setColor(Color.GREEN);
            } else if (normalizedDrift < 0.7) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.RED);
            }
            
            g2d.fillRect(10, 70, barWidth, 10);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProyectoMain().setVisible(true);
            }
        });
    }
}
