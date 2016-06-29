/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.URIReferenceException;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import sbox.detection.VideoDetection;
import sbox.detection.WebcamAndMicrophoneCapture;

/**
 *
 * @author AlvaRock
 */
public class NewMain2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            VideoDetection a = new VideoDetection("C:\\prueba\\perspectiva1");
            try {
                a.startCamera(new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi"), new File("C:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml"), new File("C:\\opencv\\sources\\data\\haarcascades\\haarcascade_smile.xml"));
            } catch (IOException | URISyntaxException | URIReferenceException ex) {
                Logger.getLogger(NewMain2.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FrameGrabber.Exception | FrameRecorder.Exception ex) {
            Logger.getLogger(NewMain2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
