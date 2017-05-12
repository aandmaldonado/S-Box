package sbox.activityrender;

import static org.monte.media.AudioFormatKeys.ChannelsKey;
import static org.monte.media.AudioFormatKeys.SampleRateKey;
import static org.monte.media.AudioFormatKeys.SampleSizeInBitsKey;
import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.HeightKey;
import static org.monte.media.VideoFormatKeys.QualityKey;
import static org.monte.media.VideoFormatKeys.WidthKey;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.Mixer;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_MJPG;
import org.monte.media.math.Rational;
import sbox.facerecorder.WebcamAndMicrophoneCapture;

public class initScreenRecorder extends javax.swing.JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 2428698729310732777L;
    private final static Logger log = Logger.getLogger("sbox");
    private ScreenRecorder screenRecorder = null;
    private File movieFolder;
    private String videoScreen = "";

    private static class AreaItem {

        private String title;
        /**
         * Area or null for entire screen.
         */
        private Dimension inputDimension;
        /**
         * null if same value as input dimension.
         */
        private Dimension outputDimension;
        /**
         * SwingConstants.CENTER, .NORTH_WEST, SOUTH_WEST.
         */
        private int alignment;
        private Point location;

        public AreaItem(String title, Dimension dim, int alignment) {
            this(title, dim, null, alignment, new Point(0, 0));
        }

        public AreaItem(String title, Dimension inputDim, Dimension outputDim, int alignment, Point location) {
            this.title = title;
            this.inputDimension = inputDim;
            this.outputDimension = outputDim;
            this.alignment = alignment;
            this.location = location;
        }

        @Override
        public String toString() {
            return title;
        }

        public Rectangle getBounds(GraphicsConfiguration cfg) {
            Rectangle areaRect = null;
            if (inputDimension != null) {
                areaRect = new Rectangle(0, 0, inputDimension.width, inputDimension.height);
            }
            outputDimension = outputDimension;
            Rectangle screenBounds = cfg.getBounds();
            if (areaRect == null) {
                areaRect = (Rectangle) screenBounds.clone();
            }
            switch (alignment) {
                case SwingConstants.CENTER:
                    areaRect.x = screenBounds.x + (screenBounds.width - areaRect.width) / 2;
                    areaRect.y = screenBounds.y + (screenBounds.height - areaRect.height) / 2;
                    break;
                case SwingConstants.NORTH_WEST:
                    areaRect.x = screenBounds.x;
                    areaRect.y = screenBounds.y;
                    break;
                case SwingConstants.SOUTH_WEST:
                    areaRect.x = screenBounds.x;
                    areaRect.y = screenBounds.y + screenBounds.height - areaRect.height;
                    break;
                default:
                    break;
            }
            areaRect.translate(location.x, location.y);

            areaRect = areaRect.intersection(screenBounds);
            return areaRect;

        }
    }

    private static class AudioSourceItem {

        private String title;
        private Mixer.Info mixerInfo;
        private boolean isEnabled;

        public AudioSourceItem(String title, Mixer.Info mixerInfo) {
            this(title, mixerInfo, true);
        }

        public AudioSourceItem(String title, Mixer.Info mixerInfo, boolean isEnabled) {
            this.title = title;
            this.mixerInfo = mixerInfo;
            this.isEnabled = isEnabled;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public void start(String path, String nombreProyecto, int experimentos) throws IOException, AWTException {
        GraphicsConfiguration cfg = getGraphicsConfiguration();
        Rectangle areaRect = null;
        Dimension outputDimension = null;
        String mimeType, videoFormatName, compressorName, crsr;

        mimeType = MIME_AVI;
//        videoFormatName = ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
        videoFormatName = ENCODING_AVI_MJPG;
//        compressorName = COMPRESSOR_NAME_AVI_TECHSMITH_SCREEN_CAPTURE;
        compressorName = ENCODING_AVI_MJPG;
        crsr = ScreenRecorder.ENCODING_WHITE_CURSOR;

        float quality = 1.0f;
//        float quality = 0.5f;
//        int bitDepth = 16;
        int bitDepth = 24;
        int audioRate = 22050;
        int audioChannels = 1;
        int audioBitsPerSample = 8;

        int mouseRate = 20;
        int screenRate = 20;
//        int screenRate = 30;

        AreaItem item = new AreaItem("Entire Screen", null, SwingConstants.NORTH_WEST);
        areaRect = item.getBounds(cfg);
        outputDimension = item.outputDimension;
        if (outputDimension == null) {
            outputDimension = areaRect.getSize();
        }

        AudioSourceItem asi = new AudioSourceItem("None", null, false);

        movieFolder = new File(path);
        screenRecorder = new ScreenRecorder(cfg, areaRect,
                // the file format:
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, mimeType),
                //
                // the output format for screen capture:
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, videoFormatName, CompressorNameKey,
                        compressorName, WidthKey, outputDimension.width, HeightKey, outputDimension.height, DepthKey,
                        bitDepth, FrameRateKey, Rational.valueOf(screenRate), QualityKey, quality, KeyFrameIntervalKey,
                        (int) (screenRate * 60) // one keyframe per minute is
                // enough
                ),
                //
                // the output format for mouse capture:
                crsr == null ? null
                        : new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, crsr, FrameRateKey,
                                Rational.valueOf(mouseRate)),
                //
                // the output format for audio capture:
                !asi.isEnabled ? null
                        : new Format(MediaTypeKey, MediaType.AUDIO,
                                // EncodingKey, audioFormatName,
                                SampleRateKey, Rational.valueOf(audioRate), SampleSizeInBitsKey, audioBitsPerSample,
                                ChannelsKey, audioChannels),
                //
                // the storage location of the movie
                movieFolder);
        ScreenRecorder.nombreProyecto = nombreProyecto;
        ScreenRecorder.experimentos = experimentos;

        screenRecorder.start();

    }

    public static void main(String[] args) throws IOException, AWTException {

        initScreenRecorder init = new initScreenRecorder();
        init.start("", "", 0);
    }

    public String stop() throws IOException {
        videoScreen = screenRecorder.stop();
        return videoScreen;
    }
}
