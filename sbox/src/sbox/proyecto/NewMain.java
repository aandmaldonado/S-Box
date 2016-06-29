/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author aandmaldonado
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File fileOld = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(fileOld);
            grabber.start();
            System.out.println(grabber.getLengthInTime());
            System.out.println(grabber.getLengthInFrames());
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
