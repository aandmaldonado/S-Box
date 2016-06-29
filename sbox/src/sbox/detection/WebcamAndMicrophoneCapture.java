package sbox.detection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class WebcamAndMicrophoneCapture {

    final private int FRAME_RATE = 30;
    final private int GOP_LENGTH_IN_FRAMES = 60;

    private long startTime = 0;
    private long videoTS = 0;

    private OpenCVFrameConverter.ToIplImage converter = null;
    private opencv_core.IplImage grabbedImage = null;
    private OpenCVFrameGrabber grabber = null;
    private FFmpegFrameRecorder recorder = null;
    private CanvasFrame cFrame = null;
    private String localPath = "";
    private String videoFace = "";
    private final static Logger log = Logger.getLogger(WebcamAndMicrophoneCapture.class.getName());

    public WebcamAndMicrophoneCapture(String pathVideoDetection) {
        localPath = pathVideoDetection;
    }

    public void startCamera(File videoInput, File classifier, File classifier2) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception {
        int captureWidth = 1366;
        int captureHeight = 768;

        grabber = new OpenCVFrameGrabber(videoInput);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        grabber.start();

        converter = new OpenCVFrameConverter.ToIplImage();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss.ms");
        dateFormat.format(new Date());
        videoFace = "videoDetection_" + dateFormat.format(new Date());
        recorder = new FFmpegFrameRecorder(localPath + videoFace + ".avi", captureWidth, captureHeight, 2);
        recorder.setInterleaved(true);
        recorder.setVideoOption("tune", "zerolatency");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "28");
        recorder.setVideoBitrate(2000000);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("avi");
        recorder.setFrameRate(FRAME_RATE);
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);
        recorder.setAudioBitrate(192000);
        recorder.setSampleRate(44100);
        recorder.setAudioChannels(2);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.start();
        cFrame = new CanvasFrame("", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        cFrame.setAlwaysOnTop(true);
        cFrame.pack();
//        Frame capturedFrame = null;
        cFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        cFrame.setVisible(false);
        cFrame.setLocation(450, 0);
//        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        while (true) {
//                cvClearMemStorage(storage);
            grabbedImage = converter.convert(grabber.grab());
            cFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            if (videoTS > recorder.getTimestamp()) {
                recorder.setTimestamp(videoTS);
            }
            if (grabbedImage != null) {
                recorder.record(converter.convert(grabbedImage));
            } else {
                recorder.stop();
                cFrame.dispose();
            }

        }

    }

    public void stopCamera() throws org.bytedeco.javacv.FrameRecorder.Exception, FrameGrabber.Exception {
//        grabber.stop();
//        recorder.stop();
        cFrame.dispose();
    }

}
