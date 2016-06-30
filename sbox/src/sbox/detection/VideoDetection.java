package sbox.detection;

import java.io.File;
import static org.bytedeco.javacpp.opencv_core.cvLoad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import org.bytedeco.javacpp.opencv_highgui.VideoCapture;
import static org.bytedeco.javacpp.opencv_highgui.CV_CAP_PROP_POS_MSEC;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class VideoDetection {

    private VideoCapture cap;
    private CvHaarClassifierCascade classifierFrontalFace;
    private CvHaarClassifierCascade classifierFrontalFaceSmile;
    private OpenCVFrameConverter.ToIplImage converter;
    private Mat frame;
    private final SimpleDateFormat dateFormat;
    private final static Logger log = Logger.getLogger(VideoDetection.class.getName());

    public VideoDetection() {
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss.ms");

    }

    public List<TimeDetection> getDetection(File videoMaster, File faceDetect, File smileDetect) {
        TimeDetection times = null;
        List<TimeDetection> listTime = new ArrayList<TimeDetection>();
        frame = new Mat();
        cap = new VideoCapture(videoMaster.getAbsolutePath());
        classifierFrontalFace = new CvHaarClassifierCascade(cvLoad(faceDetect.getAbsolutePath()));
        classifierFrontalFaceSmile = new CvHaarClassifierCascade(cvLoad(smileDetect.getAbsolutePath()));
        converter = new OpenCVFrameConverter.ToIplImage();
        CvSeq faces, mouth;
        CvRect cvRectFace, cvRectMouth;
        CvMemStorage storage;
        IplImage grayImage;
        int duracion = 0, totalFaces = 0, totalMouth = 0, xFace = 0, yFace = 0, wFace = 0, hFace = 0, xMouth = 0, yMouth = 0, wMouth = 0, hMouth = 0, timeSmileMs = 0, timeSmileMm = 0, startTime = 0, stopTime = 0;
        long milisegundos = 0, hora = 0, restohora = 0, minuto = 0, restominuto = 0, segundo = 0;
        log.info("Inicio proceso detección de sonrisas: " + dateFormat.format(new Date()));
        log.info("Video MASTER: " + videoMaster.getAbsolutePath());
        duracion = (int) getDuration(cap);
        milisegundos = getDuration(cap);
        hora = milisegundos / 3600000;
        restohora = milisegundos % 3600000;
        minuto = restohora / 60000;
        restominuto = restohora % 60000;
        segundo = restominuto / 1000;
        log.info("Duracion video: " + hora + ":" + minuto + ":" + segundo);
        while (cap.grab()) {
            if (cap.retrieve(frame)) {
                storage = CvMemStorage.create();
                grayImage = IplImage.create(converter.convertToIplImage(converter.convert(frame)).width(), converter.convertToIplImage(converter.convert(frame)).height(), IPL_DEPTH_8U, 1);
                cvCvtColor(converter.convertToIplImage(converter.convert(frame)), grayImage, CV_BGR2GRAY);
                faces = cvHaarDetectObjects(grayImage, classifierFrontalFace, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
                mouth = cvHaarDetectObjects(grayImage, classifierFrontalFaceSmile, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
                cvClearMemStorage(storage);
                totalFaces = faces.total();
                totalMouth = mouth.total();
                for (int i = 0; i < totalFaces; i++) {
                    cvRectFace = new CvRect(cvGetSeqElem(faces, i));
                    xFace = cvRectFace.x();
                    yFace = cvRectFace.y();
                    wFace = cvRectFace.width();
                    hFace = cvRectFace.height();
                    cvRectangle(converter.convertToIplImage(converter.convert(frame)), cvPoint(cvRectFace.x(), cvRectFace.y()), cvPoint(xFace + wFace, yFace + hFace), CvScalar.RED, 1, CV_AA, 0);
                    for (int j = 0; j < totalMouth; j++) {
                        cvRectMouth = new CvRect(cvGetSeqElem(mouth, j));
                        xMouth = cvRectMouth.x();
                        yMouth = cvRectMouth.y();
                        wMouth = cvRectMouth.width();
                        hMouth = cvRectMouth.height();
                        if (yMouth > yFace + hFace * 3 / 5 && yMouth + hMouth < yFace + hFace && Math.abs((xMouth + wMouth / 2)) - (xFace + wFace / 2) < wFace / 10) {
                            log.info("Frame con sonrisa detectada: " + cap.get(CV_CAP_PROP_POS_MSEC));
                            timeSmileMs = (int) cap.get(CV_CAP_PROP_POS_MSEC);

                            if (timeSmileMs < 1000) {
                                timeSmileMm = 0;
                            } else {
                                timeSmileMm = timeSmileMs / 1000;
                            }

                            if (timeSmileMm < 6) {
                                startTime = 0;
                                stopTime = 10;
                            } else if (timeSmileMm > (duracion - 5)) {//95
                                startTime = duracion - 10;
                                stopTime = duracion;
                            } else {
                                startTime = timeSmileMm - 5;
                                stopTime = timeSmileMm + 5;
                            }

                            times = new TimeDetection();
                            times.setStartTime(String.valueOf(startTime));
                            times.setStopTime(String.valueOf(stopTime));
                            listTime.add(times);
                        } else {
                            log.info("Frame sin sonrisa detectada");
                        }
                    }
                }
            }
        }
        log.info("Fin proceso detección de sonrisas: " + dateFormat.format(new Date()));
        return listTime;
    }

    private long getDuration(VideoCapture cap) {
        long duracion = 0;
        while (cap.grab()) {
            if (cap.retrieve(frame)) {
                duracion = (long) cap.get(CV_CAP_PROP_POS_MSEC);
            }
        }
        return duracion;
    }

}
