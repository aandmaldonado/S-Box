/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import com.sun.jna.NativeLibrary;
import java.awt.Color;
import java.io.File;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 *
 * @author Álvaro Andrés Maldonado Pinto
 */
public class ReproductorSec extends javax.swing.JFrame {

    private EmbeddedMediaPlayerComponent playerFaceRecorder, playerActivityRender, playerCanalExt;
    private final String ruta = System.getProperty("user.dir") + "\\lib";
    private final File vlcInstallPath = new File(ruta);
    private File faceRecorder, activityRender, canalExt;

    /**
     * Creates new form ReproductorSec
     *
     * @param faceRecorder
     * @param activityRender
     * @param canalExt
     */
    public ReproductorSec(File faceRecorder, File activityRender, File canalExt) {
        this.faceRecorder = faceRecorder;
        this.activityRender = activityRender;
        this.canalExt = canalExt;
        NativeLibrary.addSearchPath("libvlc", vlcInstallPath.getAbsolutePath());
        initComponents();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);//centrar en pantalla
        playerFaceRecorder = new EmbeddedMediaPlayerComponent();
        playerActivityRender = new EmbeddedMediaPlayerComponent();
        playerCanalExt = new EmbeddedMediaPlayerComponent();
        //se añade reproductor 
        faceRecorderPanel.add(playerFaceRecorder);
        playerFaceRecorder.setSize(faceRecorderPanel.getSize());
        playerFaceRecorder.setVisible(true);

        activityRenderPanel.add(playerActivityRender);
        playerActivityRender.setSize(activityRenderPanel.getSize());
        playerActivityRender.setVisible(true);

        canalExtPanel.add(playerCanalExt);
        playerCanalExt.setSize(canalExtPanel.getSize());
        playerCanalExt.setVisible(true);

        if (null != faceRecorder) {
            labelPathFR.setText(faceRecorder.getAbsolutePath());
        } else {
            labelPathFR.setText("Perspectiva no configurada para este experimento");
            faceRecorderPanel.setBackground(Color.BLACK);
        }

        if (null != activityRender) {
            labelPathAR.setText(activityRender.getAbsolutePath());
        } else {
            labelPathAR.setText("Perspectiva no configurada para este experimento");
            activityRenderPanel.setBackground(Color.BLACK);
        }

        if (null != canalExt) {
            labelPathCE.setText(canalExt.getAbsolutePath());
        } else {
            labelPathCE.setText("Perspectiva no configurada para este experimento");
            canalExtPanel.setBackground(Color.BLACK);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        faceRecorderPanel = new javax.swing.JPanel();
        activityRenderPanel = new javax.swing.JPanel();
        canalExtPanel = new javax.swing.JPanel();
        labelFaceRecorder = new javax.swing.JLabel();
        labelActRender = new javax.swing.JLabel();
        labelCanalExt = new javax.swing.JLabel();
        labelPathFR = new javax.swing.JLabel();
        labelPathAR = new javax.swing.JLabel();
        labelPathCE = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        iniButton = new javax.swing.JButton();
        PauseButton = new javax.swing.JButton();
        detButton = new javax.swing.JButton();
        repeatToggleButton = new javax.swing.JToggleButton();

        setResizable(false);

        faceRecorderPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        faceRecorderPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout faceRecorderPanelLayout = new javax.swing.GroupLayout(faceRecorderPanel);
        faceRecorderPanel.setLayout(faceRecorderPanelLayout);
        faceRecorderPanelLayout.setHorizontalGroup(
            faceRecorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        faceRecorderPanelLayout.setVerticalGroup(
            faceRecorderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );

        activityRenderPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        activityRenderPanel.setPreferredSize(new java.awt.Dimension(425, 425));

        javax.swing.GroupLayout activityRenderPanelLayout = new javax.swing.GroupLayout(activityRenderPanel);
        activityRenderPanel.setLayout(activityRenderPanelLayout);
        activityRenderPanelLayout.setHorizontalGroup(
            activityRenderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        activityRenderPanelLayout.setVerticalGroup(
            activityRenderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );

        canalExtPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        canalExtPanel.setPreferredSize(new java.awt.Dimension(425, 425));

        javax.swing.GroupLayout canalExtPanelLayout = new javax.swing.GroupLayout(canalExtPanel);
        canalExtPanel.setLayout(canalExtPanelLayout);
        canalExtPanelLayout.setHorizontalGroup(
            canalExtPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        canalExtPanelLayout.setVerticalGroup(
            canalExtPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );

        labelFaceRecorder.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelFaceRecorder.setText("FaceRecorder");

        labelActRender.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelActRender.setText("ActivityRender");

        labelCanalExt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelCanalExt.setText("Perspectiva Externa");

        labelPathFR.setText("jLabel1");

        labelPathAR.setText("jLabel2");

        labelPathCE.setText("jLabel3");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        iniButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        iniButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/1468668624_audio-video-outline-play.png"))); // NOI18N
        iniButton.setBorder(null);
        iniButton.setBorderPainted(false);
        iniButton.setOpaque(false);
        iniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniButtonActionPerformed(evt);
            }
        });

        PauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/1468668628_audio-video-outline-pause.png"))); // NOI18N
        PauseButton.setBorder(null);
        PauseButton.setBorderPainted(false);
        PauseButton.setOpaque(false);
        PauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PauseButtonActionPerformed(evt);
            }
        });

        detButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        detButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/1468668622_audio-video-outline-stop.png"))); // NOI18N
        detButton.setBorder(null);
        detButton.setBorderPainted(false);
        detButton.setOpaque(false);
        detButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detButtonActionPerformed(evt);
            }
        });

        repeatToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/1468668617_audio-video-outline-replay.png"))); // NOI18N
        repeatToggleButton.setBorder(null);
        repeatToggleButton.setBorderPainted(false);
        repeatToggleButton.setOpaque(false);
        repeatToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repeatToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iniButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PauseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repeatToggleButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(repeatToggleButton)
                    .addComponent(detButton)
                    .addComponent(PauseButton)
                    .addComponent(iniButton))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(faceRecorderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFaceRecorder)
                    .addComponent(labelPathFR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(activityRenderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelActRender)
                    .addComponent(labelPathAR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPathCE)
                    .addComponent(labelCanalExt)
                    .addComponent(canalExtPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(369, 369, 369))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelFaceRecorder)
                    .addComponent(labelActRender)
                    .addComponent(labelCanalExt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(canalExtPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(activityRenderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(faceRecorderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPathFR)
                    .addComponent(labelPathAR)
                    .addComponent(labelPathCE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniButtonActionPerformed
        // TODO add your handling code here:
        if (null != faceRecorder) {
            playerFaceRecorder.getMediaPlayer().playMedia(faceRecorder.getAbsolutePath());
        }
        if (null != activityRender) {
            playerActivityRender.getMediaPlayer().playMedia(activityRender.getAbsolutePath());
        }
        if (null != canalExt) {
            playerCanalExt.getMediaPlayer().playMedia(canalExt.getAbsolutePath());
        }
        
        iniButtonActionPerformed(evt);
    }//GEN-LAST:event_iniButtonActionPerformed

    private void detButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detButtonActionPerformed
        // TODO add your handling code here:
        if (null != faceRecorder) {
            playerFaceRecorder.getMediaPlayer().stop();
        }
        if (null != activityRender) {
            playerActivityRender.getMediaPlayer().stop();
        }
        if (null != canalExt) {
            playerCanalExt.getMediaPlayer().stop();
        }
    }//GEN-LAST:event_detButtonActionPerformed

    private void PauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PauseButtonActionPerformed
        // TODO add your handling code here:
        if (null != faceRecorder) {
            playerFaceRecorder.getMediaPlayer().setPause(playerFaceRecorder.getMediaPlayer().isPlaying() ? true : false);
        }
        if (null != activityRender) {
            playerActivityRender.getMediaPlayer().setPause(playerActivityRender.getMediaPlayer().isPlaying() ? true : false);
        }
        if (null != canalExt) {
            playerCanalExt.getMediaPlayer().setPause(playerCanalExt.getMediaPlayer().isPlaying() ? true : false);
        }
    }//GEN-LAST:event_PauseButtonActionPerformed

    private void repeatToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repeatToggleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_repeatToggleButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReproductorSec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReproductorSec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReproductorSec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReproductorSec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReproductorSec(new File("C:\\prueba\\videoDetect\\videoDetect__1.avi"), new File("C:\\prueba\\videoDetect\\videoDetect__2.avi"), new File("C:\\prueba\\videoDetect\\videoDetect__3.avi")).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton PauseButton;
    private javax.swing.JPanel activityRenderPanel;
    private javax.swing.JPanel canalExtPanel;
    private javax.swing.JButton detButton;
    private javax.swing.JPanel faceRecorderPanel;
    private javax.swing.JButton iniButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelActRender;
    private javax.swing.JLabel labelCanalExt;
    private javax.swing.JLabel labelFaceRecorder;
    private javax.swing.JLabel labelPathAR;
    private javax.swing.JLabel labelPathCE;
    private javax.swing.JLabel labelPathFR;
    private javax.swing.JToggleButton repeatToggleButton;
    // End of variables declaration//GEN-END:variables
}
