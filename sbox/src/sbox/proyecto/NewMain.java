/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sbox.detection.TimeDetection;
import sbox.detection.VideoDetection;

/**
 *
 * @author aandmaldonado
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        long ini = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss.ms");
        VideoDetection vd = new VideoDetection();
        Reproductor r = new Reproductor();
        File videoMaster = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
        File faceDetect = new File("C:\\Users\\aandmaldonado\\Documents\\GitHub\\S-Box\\sbox\\src\\resources\\haarcascades\\haarcascade_frontalface_default.xml");
        File smileDetect = new File("C:\\Users\\aandmaldonado\\Documents\\GitHub\\S-Box\\sbox\\src\\resources\\haarcascades\\haarcascade_mcs_mouth.xml");
        String fileNew = "C:\\prueba\\videoDetect\\videoDetect_";
//        Map<Integer, TimeDetection> map = vd.getDetection(videoMaster, faceDetect, smileDetect);
//        System.out.println("videos por generar: " + map.size());
//        int i = 0;
//        System.out.println("Inicio proceso cortador de vídeos: " + dateFormat.format(new Date()));
//        for (Integer key : map.keySet()) {
//            i++;
//            r.cutVideo(videoMaster, new File(fileNew + "_" + i + ".avi"), map.get(key).getStartTime(), map.get(key).getStopTime());
//        }
//        System.out.println("Fin proceso cortador de vídeos: " + dateFormat.format(new Date()));
//        System.out.println("videos generados: " + i);
        Thread t;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Reproductor r = new Reproductor();
                File videoMaster = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
                r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect1.avi"), "0", "10");
            }
        });
        t.start();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Reproductor r = new Reproductor();
                File videoMaster = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
                r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect2.avi"), "11", "21");
            }
        });
        t.start();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Reproductor r = new Reproductor();
                File videoMaster = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
                r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect3.avi"), "22", "31");
            }
        });
        t.start();
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect1.avi"), "0", "10");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect2.avi"), "11", "21");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect3.avi"), "22", "31");
        long fin = System.currentTimeMillis();
        long d = fin - ini;
        System.out.println("duración: " + vd.getTimeDetect(d));

    }

}
