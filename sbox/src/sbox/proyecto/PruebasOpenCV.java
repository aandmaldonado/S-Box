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
import static org.bytedeco.javacpp.opencv_highgui.CV_CAP_PROP_FPS;
import static org.bytedeco.javacpp.opencv_highgui.CV_CAP_PROP_FRAME_COUNT;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author aandmaldonado
 */
public class PruebasOpenCV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi
        String sFile = "prueba_faceRecorder_28-06-2016_21.48.09.489_1.avi";
        File fFile = new File(sFile);
        //video capture
        opencv_highgui.VideoCapture cap = new opencv_highgui.VideoCapture(sFile);
        cap.grab();
        System.out.println("opencv:");
        System.out.println(cap.get(CV_CAP_PROP_FPS ));
        System.out.println(cap.get(CV_CAP_PROP_FRAME_COUNT ));
        
        //OpenCVFrameGrabber
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(fFile);
        try {
            grabber.start();
            System.out.println("OpenCVFrameGrabber:");
            System.out.println(grabber.getFrameRate());
            System.out.println(grabber.getLengthInFrames());
            System.out.println(grabber.getSampleRate());
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(PruebasOpenCV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
