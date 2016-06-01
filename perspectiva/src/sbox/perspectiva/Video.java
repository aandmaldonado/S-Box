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

import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvInitFont;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvPutText;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
//import org.lightcouch.CouchDbClient;
//import sbox.utils.Idioma;
//import sbox.utils.LoggerUtils;
//import sbox.utils.SesionTO;

/**
 * Web service implementation
 *
 * @author Arturo Mendoza
 * @version 1.0
 * @since 01-07-2014<br>
 * <p>
 * Libraries used
 * </p>
 * @see - log4j it is possible to enable logging at runtime without modifying
 * the application binary. {@link Logger} <br>
 * - OpenCv {@link OpenCVFrameGrabber} <br>
 * - CvHaarClassifierCascade {@link CvHaarClassifierCascade} <br>
 */
public class Video extends JFrame {

    private static final long serialVersionUID = 1653464046270736821L;
    final private static int FRAME_RATE = 30;
    final private static int GOP_LENGTH_IN_FRAMES = 60;

    private OpenCVFrameGrabber grabber = null;
    private OpenCVFrameConverter.ToIplImage converter = null;
    private CanvasFrame canvasFrame = null;
    private IplImage grabbedImage = null;
    private FFmpegFrameRecorder recorder = null;
//	private Configuration config = null;
    private Boolean isVisible = null;
    private Boolean isVisibleDefault = null;
    private String localPath = "";
    private StringBuilder cronometro = new StringBuilder();
    private Timer t;
    private int h, m, s, cs;

//	private SesionTO sesion;
//	private CouchDbClient client;
    public Boolean getIsVisibleDefault() {
        return isVisibleDefault;
    }

    public void setIsVisibleDefault(Boolean isVisibleDefault) {
        this.isVisibleDefault = isVisibleDefault;
    }

    private static long startTime = 0;
    private static long videoTS = 0;

    final static Logger logger = Logger.getLogger(Video.class);

//	public static void main(String[] args) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, IOException {
//		Video video = new Video("C:\\pruebaface\\perspectiva1\\");
//		try {
//			BasicConfigurator.configure();
//			video.startCamera("DEMO", "SONRISA DEMO", "Nombre DEMO");
//		} catch (URIReferenceException | URISyntaxException | MalformedURLException e) {
//			logger.error("Error al abrir URI" + e.getMessage());
//		}
//	}
    public Video(String pathFaceRecorder) throws Exception {
        localPath = pathFaceRecorder;
    }

    public void startCamera(int decive)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, IOException, URISyntaxException,
            URIReferenceException, MalformedURLException {

//          LoggerUtils loggerUtils = new LoggerUtils();
//          loggerUtils.load(config.getString("pathGlobal"), sessionId, config.getString("synchronizer.ruta.log"),
//				config.getString("pathLog4j"), "synchronizer.log", "synchronizer.html", tipoProyecto);
//          logger.info("*********** Inicio de Recorder Video ***************** ");
//          logger.info("*********** " + sessionId + " **************************** ");
        videoTS = 0;
        startTime = 0;
//        File framePath = new File(localPath + "\\frame\\");
//        if (!framePath.exists()) {
//            framePath.mkdir();
//        }
        int captureWidth = 800;
        int captureHeight = 600;
        grabber = new OpenCVFrameGrabber(decive);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        grabber.start();
        converter = new OpenCVFrameConverter.ToIplImage();
        grabbedImage = converter.convert(grabber.grab());
        recorder = new FFmpegFrameRecorder(localPath + "\\canalExterno.avi", captureWidth, captureHeight, 2);
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
        isVisible = false;
        canvasFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        canvasFrame.setVisible(isVisible);
        canvasFrame.setLocation(450, 0);
        CvMemStorage storage = CvMemStorage.create();
        isVisibleDefault = true;
        initReloj();
        t.start();
        while (isVisibleDefault && (grabbedImage = converter.convert(grabber.grab())) != null) {
            cvClearMemStorage(storage);
            opencv_core.CvFont mCvFont = new opencv_core.CvFont();
            cvInitFont(mCvFont, CV_FONT_HERSHEY_COMPLEX_SMALL, 0.5f, 1.0f, 0, 1, 8);
            int x = 400;
            int y = 450;
            cvPutText(grabbedImage, initReloj(), cvPoint(x, y), mCvFont, opencv_core.CvScalar.RED);
            canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
//                cvFlip(grabbedImage, grabbedImage, 1);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            long timeLocal = System.currentTimeMillis();
//            cvSaveImage(framePath.getPath() + "\\" + timeLocal + ".jpg", grabbedImage);
            /**
             * Create timestamp for this frame
             */
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            if (videoTS > recorder.getTimestamp()) {
                /**
                 * We tell the recorder to write this frame at this timestamp
                 */
                recorder.setTimestamp(videoTS);
            }
            recorder.record(converter.convert(grabbedImage));
        }
    }

    public void stopCamera() throws org.bytedeco.javacv.FrameRecorder.Exception, Exception {
        canvasFrame.dispose();
        recorder.stop();
        grabber.stop();
        if (t.isRunning()) {
            t.stop();
        }
    }

    public String initReloj() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        final String date = formateador.format(ahora);
        t = new Timer(10, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                ++cs;
                if (cs == 100) {
                    cs = 0;
                    ++s;
                }
                if (s == 60) {
                    s = 0;
                    ++m;
                }
                if (m == 60) {
                    m = 0;
                    ++h;
                }
                cronometro = new StringBuilder();
                cronometro.append(date)
                        .append(" ")
                        .append((h <= 9 ? "0" : ""))
                        .append(h)
                        .append(":")
                        .append((m <= 9 ? "0" : ""))
                        .append(m)
                        .append(":")
                        .append((s <= 9 ? "0" : ""))
                        .append(s)
                        .append(":")
                        .append((cs <= 9 ? "0" : ""))
                        .append(cs);
            }
        });
        return cronometro.toString();
    }
}
