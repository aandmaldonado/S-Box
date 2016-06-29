package sbox.detection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
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
import sbox.utils.Utils;

public class demo_video {

    public static void main(String[] args) {
        try {
            CvHaarClassifierCascade classifierFrontalFace = null;
            CvHaarClassifierCascade classifierFrontalMouth = null;
            Mat frame = new Mat();
            VideoCapture cap = new VideoCapture("C:\\prueba\\perspectiva1\\prueba_faceRecorder_28-06-2016_17.26.22.2622_3.avi");
            Utils utils = new Utils();
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            utils.loadHaarClassifier("C:\\Users\\aandmaldonado\\Downloads\\haarcascade_frontalface_default.xml");
            classifierFrontalFace = utils.getClassifier();

            utils.loadHaarClassifier("C:\\Users\\aandmaldonado\\Downloads\\haarcascade_smile.xml");
            classifierFrontalMouth = utils.getClassifier();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss.ms");
            System.out.println("INICIO: "+dateFormat.format(new Date()));
            while (cap.grab()) {
                if (cap.retrieve(frame)) {
                    CvMemStorage storage = CvMemStorage.create();
                    IplImage grayImage = IplImage.create(converter.convertToIplImage(converter.convert(frame)).width(), converter.convertToIplImage(converter.convert(frame)).height(), IPL_DEPTH_8U, 1);
                    cvCvtColor(converter.convertToIplImage(converter.convert(frame)), grayImage, CV_BGR2GRAY);
                    CvSeq faces = cvHaarDetectObjects(grayImage, classifierFrontalFace, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
                    CvSeq mouth = cvHaarDetectObjects(grayImage, classifierFrontalMouth, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
                    cvClearMemStorage(storage);
                    int totalFaces = faces.total();
                    int totalMouth = mouth.total();
                    for (int i = 0; i < totalFaces; i++) {
                        CvRect cvRectFace = new CvRect(cvGetSeqElem(faces, i));
                        int xFace = cvRectFace.x(), yFace = cvRectFace.y(), wFace = cvRectFace.width(), hFace = cvRectFace.height();
                        cvRectangle(converter.convertToIplImage(converter.convert(frame)), cvPoint(cvRectFace.x(), cvRectFace.y()), cvPoint(xFace + wFace, yFace + hFace), CvScalar.RED, 1, CV_AA, 0);

                        for (int j = 0; j < totalMouth; j++) {
                            CvRect cvRectMouth = new CvRect(cvGetSeqElem(mouth, j));
                            int xMouth = cvRectMouth.x(), yMouth = cvRectMouth.y(), wMouth = cvRectMouth.width(), hMouth = cvRectMouth.height();

                            if (yMouth > yFace + hFace * 3 / 5 && yMouth + hMouth < yFace + hFace && Math.abs((xMouth + wMouth / 2)) - (xFace + wFace / 2) < wFace / 10) {
//                                cvRectangle(converter.convertToIplImage(converter.convert(frame)), cvPoint(xMouth, yMouth), cvPoint(xMouth + wMouth, yMouth + hMouth), CvScalar.GREEN, 1, CV_AA, 0);
//                                cvSaveImage("path", converter.convertToIplImage(converter.convert(frame)));
                                System.out.println("Frame con sonrisa detectada: " + cap.get(CV_CAP_PROP_POS_MSEC));
                            } else {
                                System.out.println("Frame sin sonrisa detectada");
                            }
                        }
                    }

                }
            }
            System.out.println("FIN: "+dateFormat.format(new Date()));
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
