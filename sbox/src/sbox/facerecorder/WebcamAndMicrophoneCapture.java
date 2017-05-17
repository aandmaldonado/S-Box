package sbox.facerecorder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import sbox.proyecto.ProyectoMain;

public class WebcamAndMicrophoneCapture {

    final private int WEBCAM_DEVICE_INDEX = 0;
    final private int AUDIO_DEVICE_INDEX = 4;

    final private int FRAME_RATE = 20;
    final private int GOP_LENGTH_IN_FRAMES = 40;

    private long startTime = 0;
    private long videoTS = 0;

//    private StringBuilder cronometro = new StringBuilder();
//    private Timer t;
//    private int h, m, s, cs;

    private OpenCVFrameConverter.ToIplImage converter = null;
    private opencv_core.IplImage grabbedImage = null;
    private opencv_core.IplImage grayImage = null;
    private OpenCVFrameGrabber grabber = null;
    private FFmpegFrameRecorder recorder = null;
    private CanvasFrame cFrame = null;
    private boolean running = true;
    private String videoFace = "";
    private final static Logger log = Logger.getLogger("sbox");

    public WebcamAndMicrophoneCapture() {

    }

    public void startCamera(String path, String nombreProyecto, int experimentos) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception {
//    public static void main(String[] args) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception {
        int captureWidth = 1366;
        int captureHeight = 768;

        // The available FrameGrabber classes include OpenCVFrameGrabber
        // (opencv_videoio),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        grabber.start();

        converter = new OpenCVFrameConverter.ToIplImage();

        grabbedImage = converter.convert(grabber.grab());

        // org.bytedeco.javacv.FFmpegFrameRecorder.FFmpegFrameRecorder(String
        // filename, int imageWidth, int imageHeight, int audioChannels)
        // For each param, we're passing in...
        // filename = either a path to a local file we wish to create, or an
        // RTMP url to an FMS / Wowza server
        // imageWidth = width we specified for the grabber
        // imageHeight = height we specified for the grabber
        // audioChannels = 2, because we like stereo
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'_'HH.mm.ss.ms");
        dateFormat.format(new Date());
        videoFace = nombreProyecto + "_faceRecorder_" + dateFormat.format(new Date()) + "_" + String.valueOf(experimentos);
        recorder = new FFmpegFrameRecorder(path + videoFace + ".avi", captureWidth, captureHeight, 2);
        recorder.setInterleaved(true);

        // decrease "startup" latency in FFMPEG (see:
        // https://trac.ffmpeg.org/wiki/StreamingGuide)
        recorder.setVideoOption("tune", "zerolatency");
        // tradeoff between quality and encode speed
        // possible values are ultrafast,superfast, veryfast, faster, fast,
        // medium, slow, slower, veryslow
        // ultrafast offers us the least amount of compression (lower encoder
        // CPU) at the cost of a larger stream size
        // at the other end, veryslow provides the best compression (high
        // encoder CPU) while lowering the stream size
        // (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("preset", "ultrafast");
        // Constant Rate Factor (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("crf", "28");
        // 2000 kb/s, reasonable "sane" area for 720
        recorder.setVideoBitrate(2000000);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("avi");
        // FPS (frames per second)
        recorder.setFrameRate(FRAME_RATE);
        // Key frame interval, in our case every 2 seconds -> 30 (fps) * 2 = 60
        // (gop length)
        recorder.setGopSize(GOP_LENGTH_IN_FRAMES);

        // We don't want variable bitrate audio
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        // 192 Kbps
        recorder.setAudioBitrate(192000);
        recorder.setSampleRate(44100);
        recorder.setAudioChannels(2);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

        // Jack 'n coke... do it...
        recorder.start();
        ProyectoMain.ScreenGo = true;
        log.info("FaceRecorder: Inicio de grabación");
        // Thread for audio capture, this could be in a nested private class if
        // you prefer...
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Pick a format...
                // NOTE: It is better to enumerate the formats that the system
                // supports,
                // because getLine() can error out with any particular format...
                // For us: 44.1 sample rate, 16 bits, stereo, signed, little
                // endian
                AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

                // Get TargetDataLine with that format
                Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
                Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                try {
                    // Open and start capturing audio
                    // It's possible to have more control over the chosen audio
                    // device with this line:
                    // TargetDataLine line =
                    // (TargetDataLine)mixer.getLine(dataLineInfo);
                    final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                    line.open(audioFormat);
                    line.start();

                    final int sampleRate = (int) audioFormat.getSampleRate();
                    final int numChannels = audioFormat.getChannels();

                    // Let's initialize our audio buffer...
                    int audioBufferSize = sampleRate * numChannels;
                    final byte[] audioBytes = new byte[audioBufferSize];

                    // Using a ScheduledThreadPoolExecutor vs a while loop with
                    // a Thread.sleep will allow
                    // us to get around some OS specific timing issues, and keep
                    // to a more precise
                    // clock as the fixed rate accounts for garbage collection
                    // time, etc
                    // a similar approach could be used for the webcam capture
                    // as well, if you wish
                    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                    exec.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Read from the line... non-blocking
                                int nBytesRead = line.read(audioBytes, 0, line.available());

                                // Since we specified 16 bits in the
                                // AudioFormat,
                                // we need to convert our read byte[] to short[]
                                // (see source from
                                // FFmpegFrameRecorder.recordSamples for
                                // AV_SAMPLE_FMT_S16)
                                // Let's initialize our short[] array
                                int nSamplesRead = nBytesRead / 2;
                                short[] samples = new short[nSamplesRead];

                                // Let's wrap our short[] into a ShortBuffer and
                                // pass it to recordSamples
                                ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                                ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                                // recorder is instance of
                                // org.bytedeco.javacv.FFmpegFrameRecorder
                                if (running) {
                                    recorder.recordSamples(sampleRate, numChannels, sBuff);
                                }
                            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                                log.error(e);
                            }
                        }
                    }, 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
                } catch (LineUnavailableException e1) {
                    log.error(e1);
                }
            }
        }).start();
        // A really nice hardware accelerated component for our preview...
        cFrame = new CanvasFrame("", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        cFrame.setAlwaysOnTop(true);
        cFrame.pack();
//        Frame capturedFrame = null;
        cFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        cFrame.setVisible(false);
        cFrame.setLocation(450, 0);
        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
//        initReloj();
//        t.start();
        // While we are capturing...
//        while ((capturedFrame = grabber.grab()) != null) {
        while ((grabbedImage = converter.convert(grabber.grab())) != null) {
            grayImage = opencv_core.IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            cvClearMemStorage(storage);
//            opencv_core.CvFont mCvFont = new opencv_core.CvFont();
//            cvInitFont(mCvFont, CV_FONT_HERSHEY_COMPLEX_SMALL, 0.5f, 1.0f, 0, 1, 8);
//            int x = 400;
//            int y = 450;
//            cvPutText(grabbedImage, initReloj(), cvPoint(x, y), mCvFont, opencv_core.CvScalar.RED);
//            if (cFrame.isVisible()) {
            // Show our frame in the preview
//                cFrame.showImage(capturedFrame);
//                cFrame.showImage(converter.convert(grabbedImage));
//                
//            }
            cFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
            // Let's define our start time...
            // This needs to be initialized as close to when we'll use it as
            // possible,
            // as the delta from assignment to computed time could be too high
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            // Create timestamp for this frame
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            // Check for AV drift
            if (videoTS > recorder.getTimestamp()) {
//                System.out.println("Lip-flap correction: " + videoTS + " : " + recorder.getTimestamp() + " -> "
//                        + (videoTS - recorder.getTimestamp()));

                // We tell the recorder to write this frame at this timestamp
                recorder.setTimestamp(videoTS);
            }

            // Send the frame to the org.bytedeco.javacv.FFmpegFrameRecorder
            recorder.record(converter.convert(grayImage));
//            recorder.record(converter.convert(grabbedImage));

        }

//        cFrame.dispose();
//        recorder.stop();
//        grabber.stop();
    }

    public String stopCamera() throws org.bytedeco.javacv.FrameRecorder.Exception, FrameGrabber.Exception {
        running = false;
        grabber.stop();
        recorder.stop();
        cFrame.dispose();
//        if (t.isRunning()) {
//            t.stop();
//        }
        log.info("FaceRecorder: Fin de grabación");
        return videoFace;
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
//                        .append(h).append(":")
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
