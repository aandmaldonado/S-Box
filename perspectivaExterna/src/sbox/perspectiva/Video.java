package sbox.perspectiva;

import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.xml.crypto.URIReferenceException;

import org.apache.log4j.Logger;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.CV_FONT_HERSHEY_COMPLEX_SMALL;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvInitFont;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPutText;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Video extends JFrame {

    private static final long serialVersionUID = 1653464046270736821L;
    private CanvasFrame canvasFrame = null;
    private Boolean isVisibleDefault = null;
    private String localPath = "";
    //Grabacion
    final private static int FRAME_RATE = 20;
    final private static int GOP_LENGTH_IN_FRAMES = 40;
    private OpenCVFrameGrabber grabber = null;
    private OpenCVFrameConverter.ToIplImage converter = null;
    private IplImage grabbedImage = null;
    private IplImage grayImage = null;
    private FFmpegFrameRecorder recorder = null;
    private static long startTime = 0;
    private static long videoTS = 0;
    //Cronometro    
//    private StringBuilder cronometro = new StringBuilder();
//    private Timer t;
//    private int h, m, s, cs;

    private String nombreVideo = "";

    public Boolean getIsVisibleDefault() {
        return isVisibleDefault;
    }

    public void setIsVisibleDefault(Boolean isVisibleDefault) {
        this.isVisibleDefault = isVisibleDefault;
    }

    public Video(String pathFaceRecorder) throws Exception {
        localPath = pathFaceRecorder;
    }

    public void startCamera(int decive)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, IOException, URISyntaxException,
            URIReferenceException, MalformedURLException {

        int captureWidth = 1366;
        int captureHeight = 768;
        grabber = new OpenCVFrameGrabber(decive);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        grabber.start();
        converter = new OpenCVFrameConverter.ToIplImage();
        grabbedImage = converter.convert(grabber.grab());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss.ms");
        dateFormat.format(new Date());
        nombreVideo = "canalExterno_" + dateFormat.format(new Date());
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
        PerspectivaServidor.grab = true;
        canvasFrame = new CanvasFrame("", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        canvasFrame.setAlwaysOnTop(true);
        canvasFrame.pack();
        canvasFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        canvasFrame.setVisible(false);
        canvasFrame.setLocation(450, 0);
        CvMemStorage storage = CvMemStorage.create();
        isVisibleDefault = true;
//        initReloj();
//        t.start();
        while (isVisibleDefault && (grabbedImage = converter.convert(grabber.grab())) != null) {
            grayImage = opencv_core.IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            cvClearMemStorage(storage);
//            opencv_core.CvFont mCvFont = new opencv_core.CvFont();
//            cvInitFont(mCvFont, CV_FONT_HERSHEY_COMPLEX_SMALL, 0.5f, 1.0f, 0, 1, 8);
//            int x = 400;
//            int y = 450;
//            cvPutText(grabbedImage, initReloj(), cvPoint(x, y), mCvFont, opencv_core.CvScalar.RED);
            canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            if (videoTS > recorder.getTimestamp()) {
                /**
                 * We tell the recorder to write this frame at this timestamp
                 */
                recorder.setTimestamp(videoTS);
            }
                        recorder.record(converter.convert(grayImage));
//            recorder.record(converter.convert(grabbedImage));
        }
    }

    public String stopCamera() throws org.bytedeco.javacv.FrameRecorder.Exception, Exception {
        canvasFrame.dispose();
        recorder.stop();
        grabber.stop();
        PerspectivaServidor.grab = false;
//        if (t.isRunning()) {
//            t.stop();
//        }

        return nombreVideo;
    }

//    public String initReloj() {
//        Date ahora = new Date();
//        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
//        final String date = formateador.format(ahora);
//        t = new Timer(10, new java.awt.event.ActionListener() {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent ae) {
//                ++cs;
//                if (cs == 100) {
//                    cs = 0;
//                    ++s;
//                }
//                if (s == 60) {
//                    s = 0;
//                    ++m;
//                }
//                if (m == 60) {
//                    m = 0;
//                    ++h;
//                }
//                cronometro = new StringBuilder();
//                cronometro.append(date)
//                        .append(" ")
//                        .append((h <= 9 ? "0" : ""))
//                        .append(h)
//                        .append(":")
//                        .append((m <= 9 ? "0" : ""))
//                        .append(m)
//                        .append(":")
//                        .append((s <= 9 ? "0" : ""))
//                        .append(s)
//                        .append(":")
//                        .append((cs <= 9 ? "0" : ""))
//                        .append(cs);
//            }
//        });
//        return cronometro.toString();
//    }
}
