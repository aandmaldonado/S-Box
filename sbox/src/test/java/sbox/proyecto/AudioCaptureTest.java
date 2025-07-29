package sbox.proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lombok.extern.slf4j.Slf4j;

import sbox.facerecorder.AudioCapture;

/**
 * Prueba de captura de audio para S-Box
 * @author amaldonado
 */
@Slf4j
public class AudioCaptureTest extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private AudioCapture audioCapture;
    private boolean isRecording = false;
    
    private JPanel mainPanel;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnSave;
    private JLabel lblStatus;
    private JLabel lblStats;
    private VolumeMeter volumeMeter;
    private Timer updateTimer;
    
    public AudioCaptureTest() {
        initComponents();
        setupLayout();
        setupWindowListener();
        initAudioCapture();
    }
    
    private void initComponents() {
        setTitle("S-Box - Prueba de Captura de Audio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnStart = new JButton("Iniciar Captura");
        btnStop = new JButton("Detener Captura");
        btnSave = new JButton("Guardar Audio");
        lblStatus = new JLabel("Estado: Listo");
        lblStats = new JLabel("Estadísticas: -");
        
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStop.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStats.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Agregar listeners
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRecording();
            }
        });
        
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAudio();
            }
        });
        
        controlPanel.add(btnStart);
        controlPanel.add(btnStop);
        controlPanel.add(btnSave);
        controlPanel.add(lblStatus);
        controlPanel.add(lblStats);
        
        // Medidor de volumen
        volumeMeter = new VolumeMeter();
        volumeMeter.setPreferredSize(new Dimension(400, 100));
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información"));
        infoPanel.setLayout(new BorderLayout());
        
        JLabel infoLabel = new JLabel(
            "<html>" +
            "<b>Instrucciones:</b><br>" +
            "1. Haz clic en 'Iniciar Captura'<br>" +
            "2. Habla o haz ruido para ver el medidor de volumen<br>" +
            "3. Haz clic en 'Detener Captura'<br>" +
            "4. Haz clic en 'Guardar Audio' para guardar como WAV<br><br>" +
            "<b>Formato:</b> WAV (44.1kHz, 16-bit, Mono)<br>" +
            "<b>Ubicación:</b> Carpeta 'recordings'" +
            "</html>"
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(volumeMeter, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupLayout() {
        // Configuración adicional del layout si es necesaria
    }
    
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isRecording) {
                    stopRecording();
                }
                if (updateTimer != null) {
                    updateTimer.stop();
                }
            }
        });
    }
    
    private void initAudioCapture() {
        audioCapture = new AudioCapture();
        
        // Mostrar mixers disponibles
        AudioCapture.printAvailableMixers();
    }
    
    private void startRecording() {
        if (isRecording) {
            lblStatus.setText("Estado: Grabación ya está activa");
            return;
        }
        
        try {
            log.info("Iniciando captura de audio...");
            audioCapture.startCapture();
            isRecording = true;
            lblStatus.setText("Estado: Grabando audio...");
            
            // Iniciar timer para actualizar UI
            updateTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateUI();
                }
            });
            updateTimer.start();
            
        } catch (Exception e) {
            log.error("Error al iniciar grabación", e);
            lblStatus.setText("Estado: Error al iniciar grabación - " + e.getMessage());
        }
    }
    
    private void stopRecording() {
        if (!isRecording) {
            lblStatus.setText("Estado: No hay grabación activa");
            return;
        }
        
        try {
            log.info("Deteniendo captura de audio...");
            audioCapture.stopCapture();
            isRecording = false;
            lblStatus.setText("Estado: Grabación detenida");
            
            if (updateTimer != null) {
                updateTimer.stop();
            }
            
            // Actualizar estadísticas finales
            updateStats();
            
        } catch (Exception e) {
            log.error("Error al detener grabación", e);
            lblStatus.setText("Estado: Error al detener grabación - " + e.getMessage());
        }
    }
    
    private void saveAudio() {
        if (isRecording) {
            lblStatus.setText("Estado: Detén la grabación primero");
            return;
        }
        
        try {
            String filename = "audio_" + System.currentTimeMillis() + ".wav";
            File savedFile = audioCapture.saveToFile(filename);
            lblStatus.setText("Estado: Audio guardado - " + savedFile.getName());
            
            // Mostrar información del archivo
            long fileSize = savedFile.length();
            lblStats.setText(String.format("Archivo: %s (%.2f MB)", 
                savedFile.getName(), fileSize / (1024.0 * 1024.0)));
            
        } catch (Exception e) {
            log.error("Error al guardar audio", e);
            lblStatus.setText("Estado: Error al guardar audio - " + e.getMessage());
        }
    }
    
    private void updateUI() {
        if (audioCapture != null && audioCapture.isCapturing()) {
            // Actualizar medidor de volumen
            double volume = audioCapture.getCurrentVolume();
            volumeMeter.setVolume(volume);
            volumeMeter.repaint();
            
            // Actualizar estadísticas
            updateStats();
        }
    }
    
    private void updateStats() {
        if (audioCapture != null) {
            long duration = audioCapture.getCaptureDuration();
            long bytes = audioCapture.getBytesCaptured();
            double volume = audioCapture.getCurrentVolume();
            
            lblStats.setText(String.format("Duración: %ds | Bytes: %d | Volumen: %.1f dB", 
                duration / 1000, bytes, volume));
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
            
            // Dibujar escala
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 10; i++) {
                int x = (width * i) / 10;
                int db = -60 + (i * 6);
                g2d.drawLine(x, height - 20, x, height - 15);
                g2d.drawString(String.valueOf(db), x - 10, height - 5);
            }
            
            // Dibujar barra de volumen
            double normalizedVolume = Math.max(0, Math.min(1, (volume + 60) / 60.0));
            int barWidth = (int) (width * normalizedVolume);
            
            // Color basado en el volumen
            if (normalizedVolume < 0.3) {
                g2d.setColor(Color.GREEN);
            } else if (normalizedVolume < 0.7) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.RED);
            }
            
            g2d.fillRect(0, 10, barWidth, height - 30);
            
            // Dibujar texto del volumen
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String volumeText = String.format("%.1f dB", volume);
            int textWidth = g2d.getFontMetrics().stringWidth(volumeText);
            g2d.drawString(volumeText, (width - textWidth) / 2, height / 2);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AudioCaptureTest().setVisible(true);
            }
        });
    }
} 