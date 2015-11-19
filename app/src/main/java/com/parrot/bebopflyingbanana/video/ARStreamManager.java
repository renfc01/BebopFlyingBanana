package com.parrot.bebopflyingbanana.video;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.media.FaceDetector;
import android.util.Log;
import android.graphics.Bitmap;

import com.parrot.arsdk.arsal.ARNativeData;
import com.parrot.arsdk.arstream.ARStreamReader;
import com.parrot.arsdk.arnetwork.ARNetworkManager;
import com.parrot.arsdk.arstream.ARStreamReaderListener;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacv.OpenCVFrameConverter;


/**
 * Created by root on 5/27/15.
 */
public class ARStreamManager {

    public ARStreamReader streamReader;
    public Thread videoRxThread;
    public Thread videoTxThread;
    public ARNativeData data;
    public ARStreamReaderListener listener;
    private android.content.Context context;
    private FaceDetection faceDetect;
    private OpenCVFrameConverter.ToIplImage converterToIpl;
    private IplImage ipl;
    private IplImage ipl2;
    public static BlockingQueue<ARFrame> frameQueue;
    public static int IMAGE_WIDTH = 640;
    public static int IMAGE_HEIGHT = 368;
    public FaceDetector faceDetector;

    public static int success = 0;

    private static String TAG = "ARStreamManager";

    public ARStreamManager ()
    {

    }

    public ARStreamManager (android.content.Context context,
                            ARNetworkManager netManager,
                            int iobufferD2cArstreamData,
                            int iobufferC2dArstreamAck,
                            int videoFragmentSize,
                            int videoMaxAckInterval)
    {
        frameQueue = new LinkedBlockingQueue<ARFrame>();
        this.context = context;
        this.data = new ARNativeData(42000);
        this.listener = new ARStreamReaderCallBack(frameQueue);
        this.streamReader = new ARStreamReader(netManager, iobufferD2cArstreamData,
                iobufferC2dArstreamAck, data, listener, videoFragmentSize, videoMaxAckInterval);
        this.faceDetect = new FaceDetection(context);
        converterToIpl = new OpenCVFrameConverter.ToIplImage();

        this.faceDetector = new FaceDetector(IMAGE_WIDTH, IMAGE_HEIGHT, 100);
    }

    public void startStream()
    {
        /* Create and start videoTx and videoRx threads */
        videoRxThread = new Thread (streamReader.getDataRunnable());
        videoRxThread.start();
        videoTxThread = new Thread (streamReader.getAckRunnable());
        videoTxThread.start();
    }

    public static long decodeTotal = 0;
    public static long bitmapTotal = 0;
    public static long faceTotal = 0;
    public static long runTimes = 0;

    public ARFrame getFrameWithTimeout(int video_receive_timeout)
    {

        if (frameQueue.size() == 0) {
            try {
                Thread.sleep(video_receive_timeout);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted Exception");
            }
            return null;
        }

        runTimes++;

        ARFrame f = frameQueue.poll();

        //Log.i(TAG, "(3) Starting decode");
        // Decoded frame
        long decodeStart = System.currentTimeMillis();
        f.frame = f.decodeFromVideo();
        long decodeEnd = System.currentTimeMillis();
        decodeTotal += (decodeEnd - decodeStart);
        //Log.i(TAG, "Decode average: " + decodeTotal/(runTimes));

        if (f.frame != null)
        {
            // Convert to bitmap
            //Log.i(TAG, "(5) Convert to bitmap");
            long bitmapStart = System.currentTimeMillis();
            /*
            AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
            f.bitmap = converterToBitmap.convert(f.frame);
            */
            ipl = converterToIpl.convertToIplImage(f.frame);
            ipl2 = IplImage.create(ipl.width(), ipl.height(), IPL_DEPTH_8U, 4);
            cvCvtColor(ipl, ipl2, CV_BGR2RGBA);
            f.bitmap = Bitmap.createBitmap(ipl2.width(), ipl2.height(), Bitmap.Config.ARGB_8888);
            f.bitmap.copyPixelsFromBuffer(ipl2.getByteBuffer());
            long bitmapEnd = System.currentTimeMillis();
            bitmapTotal += (bitmapEnd - bitmapStart);
            //Log.i(TAG, "Bitmap average: " + bitmapTotal/(runTimes));


            // Detect faces
            long faceStart = System.currentTimeMillis();
            //Log.i(TAG, "(4) Starting face detection");
            f.faces = faceDetect.detect(f.frame);
            long faceEnd = System.currentTimeMillis();
            faceTotal += (faceEnd - faceStart);
            //Log.i(TAG, "Face average: " + faceTotal/(runTimes));
        } else {
            Log.i(TAG, f.frameNo + ": failed");
            return null;
        }

        return f;
    }

    public void freeFrame(ARFrame frame)
    {
        //TODO
    }

    public void stopStream()
    {
        streamReader.stop();
    }
}