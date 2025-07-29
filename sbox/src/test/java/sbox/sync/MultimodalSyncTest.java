package sbox.sync;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lombok.extern.slf4j.Slf4j;

/**
 * Prueba de sincronización multimodal
 * @author amaldonado
 */
@Slf4j
public class MultimodalSyncTest extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Sincronizador
    private MultimodalSynchronizer synchronizer;
    private boolean isSynchronized = false;
    private Timer updateTimer;
    
    // UI Components
    private JPanel mainPanel;
    private JButton btnStartSync;
    private JButton btnStopSync;
    private JButton btnSaveFiles;
    private JLabel lblStatus;
    private JLabel lblStats;
    private JLabel lblVideoStats;
    private JLabel lblScreenStats;
    private JLabel lblAudioStats;
    private SyncVisualizationPanel syncPanel;
    
    public MultimodalSyncTest() {
        initComponents();
        setupLayout();
        setupWindowListener();
        initSynchronizer();
    }
    
    private void initComponents() {
        setTitle("Prueba de Sincronización Multimodal - S-Box");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnStartSync = new JButton("Iniciar Sincronización");
        btnStopSync = new JButton("Detener Sincronización");
        btnSaveFiles = new JButton("Guardar Archivos");
        lblStatus = new JLabel("Estado: Listo");
        lblStats = new JLabel("Estadísticas: -");
        lblVideoStats = new JLabel("Video: -");
        lblScreenStats = new JLabel("Pantalla: -");
        lblAudioStats = new JLabel("Audio: -");
        
        // Configurar botones
        btnStartSync.setFont(new Font("Arial", Font.BOLD, 14));
        btnStopSync.setFont(new Font("Arial", Font.BOLD, 14));
        btnSaveFiles.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStats.setFont(new Font("Arial", Font.PLAIN, 12));
        lblVideoStats.setFont(new Font("Arial", Font.PLAIN, 12));
        lblScreenStats.setFont(new Font("Arial", Font.PLAIN, 12));
        lblAudioStats.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Configurar estado inicial de botones
        btnStopSync.setEnabled(false);
        btnSaveFiles.setEnabled(false);
        
        // Agregar listeners
        btnStartSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSynchronization();
            }
        });
        
        btnStopSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSynchronization();
            }
        });
        
        btnSaveFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSynchronizedFiles();
            }
        });
        
        controlPanel.add(btnStartSync);
        controlPanel.add(btnStopSync);
        controlPanel.add(btnSaveFiles);
        controlPanel.add(lblStatus);
        controlPanel.add(lblStats);
        controlPanel.add(lblVideoStats);
        controlPanel.add(lblScreenStats);
        controlPanel.add(lblAudioStats);
        
        // Panel de visualización de sincronización
        syncPanel = new SyncVisualizationPanel();
        syncPanel.setPreferredSize(new Dimension(600, 400));
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(syncPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (synchronizer != null && synchronizer.isSynchronized()) {
                    synchronizer.stopSynchronization();
                }
            }
        });
    }
    
    private void initSynchronizer() {
        synchronizer = new MultimodalSynchronizer();
        
        // Configurar callbacks de sincronización
        synchronizer.setStatusCallback(new MultimodalSynchronizer.SyncStatusCallback() {
            @Override
            public void onSyncStatusChanged(boolean isSynchronized, long timestamp, long drift) {
                SwingUtilities.invokeLater(() -> {
                    syncPanel.setSyncStatus(isSynchronized, timestamp, drift);
                    syncPanel.repaint();
                    
                    String status = isSynchronized ? "Sincronizado" : "No sincronizado";
                    lblStatus.setText(String.format("Estado: %s | Drift: %dms | Tiempo: %ds", 
                        status, drift, timestamp / 1000));
                });
            }
            
            @Override
            public void onFrameCaptured(long frameNumber, long timestamp) {
                SwingUtilities.invokeLater(() -> {
                    syncPanel.setFrameInfo(frameNumber, timestamp);
                    syncPanel.repaint();
                });
            }
        });
        
        synchronizer.setErrorCallback(new MultimodalSynchronizer.SyncErrorCallback() {
            @Override
            public void onSyncError(String error, long timestamp) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Error: " + error);
                    log.error("Error de sincronización: {}", error);
                });
            }
        });
    }
    
    private void startSynchronization() {
        try {
            log.info("Iniciando prueba de sincronización multimodal...");
            
            synchronizer.startSynchronization();
            isSynchronized = true;
            
            btnStartSync.setEnabled(false);
            btnStopSync.setEnabled(true);
            btnSaveFiles.setEnabled(false);
            
            lblStatus.setText("Estado: Sincronización iniciada");
            
            // Iniciar timer para actualizar estadísticas
            updateTimer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateStats();
                }
            });
            updateTimer.start();
            
            log.info("Sincronización multimodal iniciada");
            
        } catch (Exception e) {
            log.error("Error al iniciar sincronización", e);
            lblStatus.setText("Estado: Error al iniciar sincronización - " + e.getMessage());
        }
    }
    
    private void stopSynchronization() {
        try {
            log.info("Deteniendo sincronización multimodal...");
            
            synchronizer.stopSynchronization();
            isSynchronized = false;
            
            btnStartSync.setEnabled(true);
            btnStopSync.setEnabled(false);
            btnSaveFiles.setEnabled(true);
            
            if (updateTimer != null) {
                updateTimer.stop();
            }
            
            lblStatus.setText("Estado: Sincronización detenida");
            
            log.info("Sincronización multimodal detenida");
            
        } catch (Exception e) {
            log.error("Error al detener sincronización", e);
            lblStatus.setText("Estado: Error al detener sincronización - " + e.getMessage());
        }
    }
    
    private void saveSynchronizedFiles() {
        try {
            log.info("Guardando archivos sincronizados...");
            
            MultimodalSynchronizer.SyncResult result = synchronizer.saveSynchronizedFiles("test_sync");
            
            lblStatus.setText(String.format("Estado: Archivos guardados - Video: %s, Pantalla: %s, Audio: %s", 
                result.getVideoFile().getName(), result.getScreenFile().getName(), 
                result.getAudioFile().getName()));
            
            log.info("Archivos sincronizados guardados exitosamente");
            
        } catch (IOException e) {
            log.error("Error al guardar archivos sincronizados", e);
            lblStatus.setText("Estado: Error al guardar archivos - " + e.getMessage());
        }
    }
    
    private void updateStats() {
        if (synchronizer != null) {
            // Estadísticas generales
            long totalFrames = synchronizer.getTotalFrames();
            long syncErrors = synchronizer.getSyncErrors();
            long averageDrift = synchronizer.getAverageDrift();
            
            lblStats.setText(String.format("Frames: %d | Errores: %d | Drift Promedio: %dms", 
                totalFrames, syncErrors, averageDrift));
            
            // Estadísticas de video
            if (synchronizer.getVideoCapture() != null) {
                long videoFrames = synchronizer.getVideoCapture().getFrameCount();
                boolean videoInSync = synchronizer.getVideoCapture().isInSync(
                    System.currentTimeMillis() - synchronizer.getStartTimestamp());
                lblVideoStats.setText(String.format("Video: %d frames | %s", 
                    videoFrames, videoInSync ? "Sincronizado" : "No sincronizado"));
            }
            
            // Estadísticas de pantalla
            if (synchronizer.getScreenRecorder() != null) {
                long screenFrames = synchronizer.getScreenRecorder().getFrameCount();
                boolean screenInSync = synchronizer.getScreenRecorder().isInSync(
                    System.currentTimeMillis() - synchronizer.getStartTimestamp());
                lblScreenStats.setText(String.format("Pantalla: %d frames | %s", 
                    screenFrames, screenInSync ? "Sincronizado" : "No sincronizado"));
            }
            
            // Estadísticas de audio
            if (synchronizer.getAudioCapture() != null) {
                long audioSamples = synchronizer.getAudioCapture().getSampleCount();
                boolean audioInSync = synchronizer.getAudioCapture().isInSync(
                    System.currentTimeMillis() - synchronizer.getStartTimestamp());
                lblAudioStats.setText(String.format("Audio: %d muestras | %s", 
                    audioSamples, audioInSync ? "Sincronizado" : "No sincronizado"));
            }
        }
    }
    
    // Panel de visualización de sincronización
    private class SyncVisualizationPanel extends JPanel {
        private boolean isSynchronized = false;
        private long timestamp = 0;
        private long drift = 0;
        private long frameNumber = 0;
        private long frameTimestamp = 0;
        
        public void setSyncStatus(boolean isSynchronized, long timestamp, long drift) {
            this.isSynchronized = isSynchronized;
            this.timestamp = timestamp;
            this.drift = drift;
        }
        
        public void setFrameInfo(long frameNumber, long frameTimestamp) {
            this.frameNumber = frameNumber;
            this.frameTimestamp = frameTimestamp;
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
            
            // Dibujar título
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            String title = "Visualización de Sincronización Multimodal";
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 30);
            
            // Dibujar indicador de estado
            g2d.setColor(isSynchronized ? Color.GREEN : Color.RED);
            g2d.fillOval(20, 50, 30, 30);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String statusText = isSynchronized ? "SINCRONIZADO" : "NO SINCRONIZADO";
            g2d.drawString(statusText, 60, 70);
            
            // Dibujar información de tiempo
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(String.format("Tiempo Total: %ds", timestamp / 1000), 20, 100);
            g2d.drawString(String.format("Drift Actual: %dms", drift), 20, 120);
            g2d.drawString(String.format("Frame: %d", frameNumber), 20, 140);
            g2d.drawString(String.format("Timestamp Frame: %dms", frameTimestamp), 20, 160);
            
            // Dibujar gráfico de drift
            drawDriftGraph(g2d, width, height);
            
            // Dibujar indicadores de componentes
            drawComponentIndicators(g2d, width, height);
        }
        
        private void drawDriftGraph(Graphics2D g2d, int width, int height) {
            int graphX = 20;
            int graphY = 200;
            int graphWidth = width - 40;
            int graphHeight = 100;
            
            // Dibujar área del gráfico
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(graphX, graphY, graphWidth, graphHeight);
            
            // Dibujar línea central (drift = 0)
            g2d.setColor(Color.WHITE);
            g2d.drawLine(graphX, graphY + graphHeight / 2, graphX + graphWidth, graphY + graphHeight / 2);
            
            // Dibujar línea de drift actual
            double normalizedDrift = Math.max(-1, Math.min(1, drift / 50.0));
            int driftY = graphY + graphHeight / 2 - (int) (normalizedDrift * graphHeight / 2);
            
            if (Math.abs(drift) <= 50) {
                g2d.setColor(Color.GREEN);
            } else if (Math.abs(drift) <= 100) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.RED);
            }
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Drift: " + drift + "ms", graphX, graphY - 10);
            g2d.drawLine(graphX, driftY, graphX + graphWidth, driftY);
        }
        
        private void drawComponentIndicators(Graphics2D g2d, int width, int height) {
            int startY = 350;
            int indicatorSize = 20;
            int spacing = 30;
            
            // Video
            g2d.setColor(Color.BLUE);
            g2d.fillOval(20, startY, indicatorSize, indicatorSize);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Video", 50, startY + 15);
            
            // Pantalla
            g2d.setColor(Color.GREEN);
            g2d.fillOval(20, startY + spacing, indicatorSize, indicatorSize);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Pantalla", 50, startY + spacing + 15);
            
            // Audio
            g2d.setColor(Color.RED);
            g2d.fillOval(20, startY + spacing * 2, indicatorSize, indicatorSize);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Audio", 50, startY + spacing * 2 + 15);
            
            // Estado de sincronización
            g2d.setColor(isSynchronized ? Color.GREEN : Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String syncText = isSynchronized ? "✓ SINCRONIZADO" : "✗ NO SINCRONIZADO";
            g2d.drawString(syncText, 150, startY + spacing + 15);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MultimodalSyncTest().setVisible(true);
            }
        });
    }
} 