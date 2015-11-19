package com.parrot.bebopflyingbanana.video;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

/**
 * Created by root on 6/27/15.
 */
public class FaceDetection {
    public static int IMAGE_WIDTH = 640;
    public static int IMAGE_HEIGHT = 368;
    public static int SUBSAMPLING_FACTOR = 1;

    private android.content.Context context;
    private static File classifierFile;
    private IplImage image;
    private IplImage grayImage;
    private IplImage smallImage;
    private static CvHaarClassifierCascade classifier;
    private CvMemStorage storage;
    private CvSeq faces;
    private OpenCVFrameConverter.ToIplImage converterToIpl;
    private OpenCVFrameConverter.ToMat converterToMat;
    private CascadeClassifier cascade;

    private static String TAG = "FaceDetection";

    public FaceDetection(android.content.Context context) {
        this.context = context;

        // Load the classifier file from Java resources.
        try {
            //InputStream input = context.getAssets().open("haarcascade_frontalface_default.xml");
            InputStream input = context.getAssets().open("lbpcascade_frontalface.xml");

            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BebopFlyingBanana";
            File dir = new File(file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            classifierFile = new File(dir, "classifier.xml");
            OutputStream output = new FileOutputStream(classifierFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            input.close();
            output.close();

        } catch (IOException e) {
            Log.e(TAG, "Could not extract the classifier file from Java resource.");
            e.printStackTrace();
        }

        /*
        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);
        if (!classifierFile.exists()) {
            Log.e(TAG, "Could not extract the classifier file from Java resource.");
        }
        classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
        classifierFile.delete();
        if (classifier.isNull()) {
            Log.e(TAG, "Could not load the classifier file.");
        }
        */

        cascade = new CascadeClassifier(classifierFile.getAbsolutePath());

        storage = CvMemStorage.create();
        //converterToIpl = new OpenCVFrameConverter.ToIplImage();
        converterToMat = new OpenCVFrameConverter.ToMat();
    }

    //public CvSeq detect(Frame frame) {
    public Rect detect(Frame frame) {
        /*
        image = converterToIpl.convert(frame);
        grayImage = IplImage.create(IMAGE_WIDTH, IMAGE_HEIGHT, IPL_DEPTH_8U, 1);
        smallImage = IplImage.create(IMAGE_WIDTH / SUBSAMPLING_FACTOR, IMAGE_HEIGHT / SUBSAMPLING_FACTOR, IPL_DEPTH_8U, 1);
        cvClearMemStorage(storage);
        cvCvtColor(image, grayImage, CV_BGR2GRAY);
        cvResize(grayImage, smallImage, CV_INTER_AREA);
        //faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        */

        Mat image = converterToMat.convert(frame);
        Mat grayImage = new Mat();
        cvtColor(image, grayImage, COLOR_BGR2GRAY);
        Rect faces = new Rect();
        cascade.detectMultiScale(grayImage, faces, 1.1, 2, CV_HAAR_DO_CANNY_PRUNING, new Size(0, 0), new Size(grayImage.size().width(), grayImage.size().height()));

        return faces;
    }


}
