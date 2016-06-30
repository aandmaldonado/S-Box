/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import sbox.detection.CutVideo;
import sbox.detection.TimeDetection;
import sbox.detection.VideoDetection;
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss.ms");
        VideoDetection vd = new VideoDetection();
        Reproductor r = new Reproductor();
        File videoMaster = new File("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
        File faceDetect = new File("C:\\Users\\aandmaldonado\\Documents\\GitHub\\S-Box\\sbox\\src\\resources\\haarcascades\\haarcascade_frontalface_default.xml");
        File smileDetect = new File("C:\\Users\\aandmaldonado\\Documents\\GitHub\\S-Box\\sbox\\src\\resources\\haarcascades\\haarcascade_mcs_mouth.xml");
        String fileNew = "C:\\prueba\\videoDetect\\videoDetect_";
        List<TimeDetection> list = vd.getDetection(videoMaster, faceDetect, smileDetect);
        System.out.println("videos por generar: " + list.size());
        int i = 0;
        System.out.println("Inicio proceso cortador de vídeos: " + dateFormat.format(new Date()));
        for (TimeDetection t : list) {
            i++;
            r.cutVideo(videoMaster, new File(fileNew + "_" + i + ".avi"), t.getStartTime(), t.getStopTime());
        }
        System.out.println("Fin proceso cortador de vídeos: " + dateFormat.format(new Date()));
        System.out.println("videos generados: " + i);

//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect1.avi"), "0", "10");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect2.avi"), "1", "11");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect3.avi"), "2", "12");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect4.avi"), "3", "13");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect5.avi"), "4", "14");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect6.avi"), "5", "15");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect7.avi"), "8", "18");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect8.avi"), "9", "19");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect9.avi"), "10", "20");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect10.avi"), "11", "21");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect11.avi"), "12", "22");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect12.avi"), "13", "23");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect13.avi"), "14", "24");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect14.avi"), "15", "25");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect15.avi"), "17", "27");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect16.avi"), "16", "26");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect17.avi"), "19", "29");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect18.avi"), "18", "28");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect19.avi"), "21", "31");
//        r.cutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect20.avi"), "20", "30");

//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect1.avi"), "0", "10");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect2.avi"), "1", "11");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect3.avi"), "2", "12");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect4.avi"), "3", "13");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect5.avi"), "4", "14");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect6.avi"), "5", "15");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect7.avi"), "8", "18");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect8.avi"), "9", "19");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect9.avi"), "10", "20");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect10.avi"), "11", "21");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect11.avi"), "12", "22");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect12.avi"), "13", "23");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect13.avi"), "14", "24");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect14.avi"), "15", "25");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect15.avi"), "17", "27");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect16.avi"), "16", "26");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect17.avi"), "19", "29");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect18.avi"), "18", "28");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect19.avi"), "21", "31");
//        cutVideo.start();
//        cutVideo = new CutVideo(videoMaster, new File("C:\\prueba\\perspectiva1\\videoDetect20.avi"), "20", "30");
//        cutVideo.start();
    }

}
