package sbox.proyecto;

import com.sun.jna.NativeLibrary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import sbox.detection.VideoDetection;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class Reproductor extends javax.swing.JFrame {

    private EmbeddedMediaPlayerComponent player;
    private File file;
    private final String ruta = System.getProperty("user.dir") + "\\lib";
    private final File vlcInstallPath = new File(ruta);
    private final static Logger log = Logger.getLogger("sbox");

    //bandera para controlar la reproduccion de video y el cambio en el avance de video
    private boolean band = true;

    public Reproductor() {
        NativeLibrary.addSearchPath("libvlc", vlcInstallPath.getAbsolutePath());
    }

    /**
     * Creates new form Reproductor
     *
     * @param f
     */
    public Reproductor(File f) {
        file = f;
        NativeLibrary.addSearchPath("libvlc", vlcInstallPath.getAbsolutePath());
        initComponents();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);//centrar en pantalla
        player = new EmbeddedMediaPlayerComponent();
        //se añade reproductor 
        jPanel2.add(player);
        player.setSize(jPanel2.getSize());
        player.setVisible(true);
        //slider control progreso
        sldProgress.setMinimum(0);
        sldProgress.setMaximum(100);
        sldProgress.setValue(0);
        sldProgress.setEnabled(false);

        //Control de reproduccion
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (file != null) {
                    if ("Iniciar".equals(btnPlay.getText())) {
                        player.getMediaPlayer().playMedia(file.getAbsolutePath());
                        sldProgress.setEnabled(true);
                        btnPlay.setText("Pausar");
                    } else if ("Pausar".equals(btnPlay.getText())) {
                        player.getMediaPlayer().setPause(player.getMediaPlayer().isPlaying() ? true : false);
                        btnPlay.setText("Reanudar");
                    } else if ("Reanudar".equals(btnPlay.getText())) {
                        player.getMediaPlayer().setPause(player.getMediaPlayer().isPlaying() ? true : false);
                        btnPlay.setText("Pausar");
                    }
                }
            }
        });

        //Control detener reproduccion
        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                player.getMediaPlayer().stop();
                sldProgress.setValue(0);
                sldProgress.setEnabled(false);
                btnPlay.setText("Iniciar");
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                player.getMediaPlayer().stop();
                sldProgress.setValue(0);
                sldProgress.setEnabled(false);
            }
        });

        //Listener de reproductor para mostrar el progreso en la reproduccion del video 
        player.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void positionChanged(MediaPlayer mp, float pos) {
                if (band) {
                    int value = Math.min(100, Math.round(pos * 100.0f));
                    sldProgress.setValue(value);
                }
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {

            }

        });

        //Listener para el slider progress
        sldProgress.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                band = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                band = true;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        //Control para cambiar a posicion de reproduccion
        sldProgress.addChangeListener(new ChangeListener() {

            @Override
            public synchronized void stateChanged(ChangeEvent e) {
                if (!band) {
                    Object source = e.getSource();
                    float np = ((JSlider) source).getValue() / 100f;
                    player.getMediaPlayer().setPosition(np);
                }

            }
        });

    }//end: constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        sldProgress = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        btnPlay = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(sldProgress);

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        btnPlay.setText("Iniciar");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(btnPlay, gridBagConstraints);

        btnStop.setText("Detener");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(btnStop, gridBagConstraints);

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 300));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnStopActionPerformed

//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Reproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Reproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Reproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Reproductor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Reproductor().setVisible(true);
//            }
//        });
//    }
    /**
     *
     * @param videoMaster
     * @param videoSec
     * @param ini
     * @param fin
     * @return
     */
    public boolean cutVideo(File videoMaster, File videoSec, String ini, String fin) {
        player = new EmbeddedMediaPlayerComponent();
        boolean resp = false;
        initComponents();
        jPanel2.add(player);
        player.setSize(jPanel2.getSize());
        player.setVisible(false);
        String[] opts = {":start-time=" + ini, ":stop-time=" + fin, ":sout=#transcode{vcodec=h264,venc=x264{cfr=16},fps=30,scale=1,acodec=mp4a,ab=160,channels=2,samplerate=44100}:file{dst=" + videoSec.getAbsolutePath() + "}"};
        resp = player.getMediaPlayer().playMedia(videoMaster.getAbsolutePath(), opts);
        player.getMediaPlayer().start();
        player.getMediaPlayer().mute(true);
        while (player.getMediaPlayer().isPlaying()) {
            System.out.println("Esperando secuencia...");
        }
//        player.getMediaPlayer().release();
        log.info("Se ha generado secuencia: " + videoSec.getName());
        return resp;
    }

    public void stop() {
        player.getMediaPlayer().release();
    }

//    public String getDuration(File file) {
//        player = new EmbeddedMediaPlayerComponent();
//        initComponents();
//        jPanel2.add(player);
//        player.setSize(jPanel2.getSize());
//        player.setVisible(true);
//        player.getMediaPlayer().playMedia(file.getAbsolutePath());
////        player.getMediaPlayer().setVolume(0);
//        player.getMediaPlayer().mute(true);
//        player.getMediaPlayer().parseMedia();
//        player.getMediaPlayer().start();
//        long milisegundos = player.getMediaPlayer().getLength();
//        long hora = milisegundos / 3600000;
//        long restohora = milisegundos % 3600000;
//        long minuto = restohora / 60000;
//        long restominuto = restohora % 60000;
//        long segundo = restominuto / 1000;
////        long restosegundo = restominuto % 1000;
//        return hora + ":" + minuto + ":" + segundo;
//    }
    public String getFps(File file) {
        player = new EmbeddedMediaPlayerComponent();
        initComponents();
        jPanel2.add(player);
        player.setSize(jPanel2.getSize());
        player.setVisible(true);
        player.getMediaPlayer().playMedia(file.getAbsolutePath());
        player.getMediaPlayer().mute(true);
        player.getMediaPlayer().parseMedia();
        player.getMediaPlayer().start();
        long fps = (long) player.getMediaPlayer().getFps();
//        player.getMediaPlayer().release();
        return String.valueOf(fps);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSlider sldProgress;
    // End of variables declaration//GEN-END:variables

}
