/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sbox.proyecto;

import java.io.File;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvInitFont;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPutText;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 *
 * @author aandmaldonado
 */
public class PruebasOpenCV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File source = new File("C:\\jkehkehvt_activityRender_08-11-2016_20.21.56.2156_1.avi");
        File alineado = new File(source.getParent() + "\\Alineado");
        opencv_core.Mat mat = new opencv_core.Mat();
        Frame frame = new Frame();
        opencv_core.IplImage iplImage = null;
        int captureWidth = 800, captureHeight = 600;
        long startTime = 0, videoTS;
        OpenCVFrameConverter.ToIplImage converter = null;
        opencv_highgui.VideoCapture cap = null;
        FFmpegFrameRecorder recorder = null;
        opencv_core.CvMemStorage storage;
        opencv_core.CvFont mCvFont = new opencv_core.CvFont();
        cvInitFont(mCvFont, opencv_core.CV_FONT_HERSHEY_TRIPLEX, 0.5f, 1.0f, 0, 1, 8);
        try {
            cap = new opencv_highgui.VideoCapture(source.getAbsolutePath());
            converter = new OpenCVFrameConverter.ToIplImage();
            if (!alineado.exists()) {
                alineado.mkdirs();
            }
            recorder = new FFmpegFrameRecorder(alineado.getAbsolutePath() + "\\" + source.getName(), captureWidth, captureHeight, 2);
            recorder.setInterleaved(true);
            recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "28");
            recorder.setVideoBitrate(2000000);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("avi");
            recorder.setFrameRate(30);
            recorder.setGopSize(60);
            recorder.start();
            int frameCount = 0;
            int sec =0;
            while (cap.grab()) {
                if (cap.retrieve(mat)) {
                    frameCount++;
                    frame = converter.convert(mat);
                    iplImage = converter.convert(frame);
                    storage = opencv_core.CvMemStorage.create();
                    cvClearMemStorage(storage);
                    int x = 950;
                    int y = 650;
                    if (frameCount==30) {
                        frameCount=0;
                        sec++;
                        System.out.println("sec: "+sec);
                        cvPutText(iplImage, "PRUEBA", cvPoint(x, y), mCvFont, opencv_core.CvScalar.BLACK);
                    }
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    videoTS = 1000 * (System.currentTimeMillis() - startTime);

                    if (videoTS > recorder.getTimestamp()) {
                        recorder.setTimestamp(videoTS);
                    }
                    recorder.record(converter.convert(iplImage));
                }
            }
            recorder.stop();
            cap.release();
        } catch (FrameRecorder.Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                recorder.stop();
                cap.release();
            } catch (FrameRecorder.Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
