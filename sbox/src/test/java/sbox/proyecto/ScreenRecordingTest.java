package sbox.proyecto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

import lombok.extern.slf4j.Slf4j;

import sbox.activityrender.ScreenRecorder;

/**
 * Prueba de grabación de pantalla para S-Box
 * @author amaldonado
 */
@Slf4j
public class ScreenRecordingTest extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private ScreenRecorder screenRecorder;
    private boolean isRecording = false;
    
    private JPanel mainPanel;
    private JButton btnStart;
    private JButton btnStop;
    private JLabel lblStatus;
    private JLabel lblStats;
    
    public ScreenRecordingTest() {
        initComponents();
        setupLayout();
        setupWindowListener();
        initScreenRecorder();
    }
    
    private void initComponents() {
        setTitle("S-Box - Prueba de Grabación de Pantalla");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        btnStart = new JButton("Iniciar Grabación");
        btnStop = new JButton("Detener Grabación");
        lblStatus = new JLabel("Estado: Listo");
        lblStats = new JLabel("Estadísticas: -");
        
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStop.setFont(new Font("Arial", Font.BOLD, 14));
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
        
        controlPanel.add(btnStart);
        controlPanel.add(btnStop);
        controlPanel.add(lblStatus);
        controlPanel.add(lblStats);
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información"));
        infoPanel.setLayout(new BorderLayout());
        
        JLabel infoLabel = new JLabel(
            "<html>" +
            "<b>Instrucciones:</b><br>" +
            "1. Haz clic en 'Iniciar Grabación'<br>" +
            "2. Mueve el mouse y abre algunas ventanas<br>" +
            "3. Haz clic en 'Detener Grabación'<br>" +
            "4. El video se guardará en la carpeta 'recordings'<br><br>" +
            "<b>Formato:</b> MP4 (H.264)<br>" +
            "<b>Frame Rate:</b> 30 FPS<br>" +
            "<b>Resolución:</b> Pantalla completa" +
            "</html>"
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
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
            }
        });
    }
    
    private void initScreenRecorder() {
        screenRecorder = new ScreenRecorder();
        ScreenRecorder.nombreProyecto = "screen_test";
        ScreenRecorder.experimentos = 1;
    }
    
    private void startRecording() {
        if (isRecording) {
            lblStatus.setText("Estado: Grabación ya está activa");
            return;
        }
        
        try {
            log.info("Iniciando grabación de pantalla...");
            screenRecorder.start();
            isRecording = true;
            lblStatus.setText("Estado: Grabando pantalla...");
            
            // Iniciar thread para actualizar estadísticas
            startStatsThread();
            
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
            log.info("Deteniendo grabación de pantalla...");
            String outputFile = screenRecorder.stop();
            isRecording = false;
            lblStatus.setText("Estado: Grabación detenida");
            lblStats.setText("Archivo: " + outputFile);
            
            // Mostrar información del archivo
            File file = new File(outputFile);
            if (file.exists()) {
                long fileSize = file.length();
                lblStats.setText(String.format("Archivo: %s (%.2f MB)", 
                    file.getName(), fileSize / (1024.0 * 1024.0)));
            }
            
        } catch (Exception e) {
            log.error("Error al detener grabación", e);
            lblStatus.setText("Estado: Error al detener grabación - " + e.getMessage());
        }
    }
    
    private void startStatsThread() {
        Thread statsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording) {
                    try {
                        String stats = screenRecorder.getRecordingStats();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblStats.setText(stats);
                            }
                        });
                        Thread.sleep(1000); // Actualizar cada segundo
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        statsThread.setDaemon(true);
        statsThread.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ScreenRecordingTest().setVisible(true);
            }
        });
    }
} 