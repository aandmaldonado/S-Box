package sbox.detection;

import java.io.File;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvLoad;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.xml.crypto.URIReferenceException;

import org.bytedeco.javacpp.avcodec;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class VideoDetection extends JFrame {

    private static final long serialVersionUID = 1653464046270736821L;
    private CanvasFrame canvasFrame = null;
    private String localPath = "";
    final private static int FRAME_RATE = 30;
    final private static int GOP_LENGTH_IN_FRAMES = 60;
    private OpenCVFrameGrabber grabber = null;
    private OpenCVFrameConverter.ToIplImage converter = null;
    private IplImage grabbedImage = null;
    private FFmpegFrameRecorder recorder = null;
    private static long startTime = 0;
    private static long videoTS = 0;
    private static CvHaarClassifierCascade classifierFrontalFace = null;
    private static CvHaarClassifierCascade classifierFrontalFaceSmile = null;
    private CvHaarClassifierCascade classifier = null;

    private String nombreVideo = "";

    public VideoDetection(String pathVideoDetection) throws Exception {
        localPath = pathVideoDetection;
    }

    public void startCamera(File videoInput, File classifier, File classifier2)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, IOException, URISyntaxException,
            URIReferenceException, MalformedURLException {
        System.out.println("inicio");
        int captureWidth = 1366;
        int captureHeight = 768;
        grabber = new OpenCVFrameGrabber(videoInput);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        grabber.start();
        converter = new OpenCVFrameConverter.ToIplImage();
        grabbedImage = converter.convert(grabber.grab());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss.ms");
        dateFormat.format(new Date());
        nombreVideo = "videoDetection_" + dateFormat.format(new Date());
        recorder = new FFmpegFrameRecorder(localPath + "\\" + nombreVideo + ".avi", captureWidth, captureHeight, 2);
        recorder.setInterleaved(true);
        recorder.setVideoOption("tune", "zerolatency");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "28");
        recorder.setVideoBitrate(2000000);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("avi");
        recorder.setFrameRate(FRAME_RATE);
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);
        recorder.start();
        canvasFrame = new CanvasFrame("", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        canvasFrame.setAlwaysOnTop(true);
        canvasFrame.pack();
        canvasFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        canvasFrame.setVisible(false);
        canvasFrame.setLocation(450, 0);
        CvMemStorage storage = CvMemStorage.create();

//        loadClassifier(classifier);
//        classifierFrontalFace = getClassifier();
//
//        loadClassifier(classifier2);
//        classifierFrontalFaceSmile = getClassifier();
        while (null !=(grabbedImage = converter.convert(grabber.grab()))) {
            cvClearMemStorage(storage);

//            IplImage grayImage = IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
//
//            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
//
//            CvSeq faces = cvHaarDetectObjects(grayImage, classifierFrontalFace, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
//
//            CvSeq smile = cvHaarDetectObjects(grayImage, classifierFrontalFaceSmile, storage, 1.5, 3, CV_HAAR_DO_CANNY_PRUNING);
//
//            cvClearMemStorage(storage);
//
//            int totalFaces = faces.total();
//
//            int totalSmile = smile.total();
//
//            for (int i = 0; i < totalFaces; i++) {
//                CvRect cvRectFace = new CvRect(cvGetSeqElem(faces, i));
//                int xFace = cvRectFace.x(), yFace = cvRectFace.y(), wFace = cvRectFace.width(), hFace = cvRectFace.height();
//                cvRectangle(grabbedImage, cvPoint(cvRectFace.x(), cvRectFace.y()), cvPoint(xFace + wFace, yFace + hFace), CvScalar.RED, 1, CV_AA, 0);
//                for (int j = 0; j < totalSmile; j++) {
//                    CvRect cvRectMouth = new CvRect(cvGetSeqElem(smile, j));
//                    int xMouth = cvRectMouth.x(), yMouth = cvRectMouth.y(), wMouth = cvRectMouth.width(), hMouth = cvRectMouth.height();
//
//                    if (yMouth > yFace + hFace * 3 / 5 && yMouth + hMouth < yFace + hFace && Math.abs((xMouth + wMouth / 2)) - (xFace + wFace / 2) < wFace / 10) {
//                        cvRectangle(grabbedImage, cvPoint(xMouth, yMouth), cvPoint(xMouth + wMouth, yMouth + hMouth), CvScalar.GREEN, 1, CV_AA, 0);
//                    }
//                }
//            }

            canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            if (videoTS > recorder.getTimestamp()) {

                recorder.setTimestamp(videoTS);
            }
            recorder.record(converter.convert(grabbedImage));
        }
        stopCamera();
    }

    public void stopCamera() throws org.bytedeco.javacv.FrameRecorder.Exception, Exception {
        canvasFrame.dispose();
        recorder.stop();
        System.out.println("fin");
    }

    private CvHaarClassifierCascade loadClassifier(File file) throws IOException {
        if (file == null || file.length() <= 0) {
            throw new IOException("Could not extract file Classifier.");
        }
        Loader.load(opencv_objdetect.class);
        setClassifier(new CvHaarClassifierCascade(cvLoad(file.getAbsolutePath())));
        if (getClassifier().isNull()) {
            throw new IOException(
                    "Could not extract content file the Classifier.");
        }

        return getClassifier();
    }

    public CvHaarClassifierCascade getClassifier() {
        return classifier;
    }

    public void setClassifier(CvHaarClassifierCascade classifier) {
        this.classifier = classifier;
    }

}
