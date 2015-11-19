package com.parrot.bebopflyingbanana.photo;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.parrot.arsdk.ardatatransfer.ARDATATRANSFER_ERROR_ENUM;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMedia;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloader;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloaderCompletionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by root on 7/7/15.
 */
public class ARDataTransferMediasDownloaderCompletionCallBack implements ARDataTransferMediasDownloaderCompletionListener {
    private ARDataTransferMediasDownloader mediasDownloader;
    private Context context;
    private Button photoBt;
    private static int totalNoOfMedia;
    private static int noOfMedia = 1;

    private static String TAG = "ARDataTransferMediasDownloaderCompletionCallBack";

    public ARDataTransferMediasDownloaderCompletionCallBack(Context context,
                                                            ARDataTransferMediasDownloader mediasDownloader,
                                                            Button photoBt,
                                                            int totalNoOfMedia) {
        this.context = context;
        this.mediasDownloader = mediasDownloader;
        this.photoBt = photoBt;
        this.totalNoOfMedia = totalNoOfMedia;
    }

    @Override
    public void didMediaComplete(Object arg, ARDataTransferMedia media, ARDATATRANSFER_ERROR_ENUM error) {
        Log.i(TAG, "Media Complete: " + media.getName() + " - " + error.toString());
        Log.i(TAG, "Listed Length: " + media.getSize());
        Log.i(TAG, "Actual Length: " + media.getThumbnail().length);

        noOfMedia++;
        if (error == ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK) {
            try {
                File photo = new File(media.getFilePath());
                FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());
                fos.write(media.getThumbnail());
                fos.close();
                Log.i(TAG, "Success writing file");
            } catch (IOException e) {
                Log.e(TAG, "Cannot save media.");
                e.printStackTrace();
            }

            mediasDownloader.deleteMedia(media);
            Log.i(TAG, "Send delete command");

            /*
            if (noOfMedia > totalNoOfMedia) {
                Log.i(TAG, "Last file");
                noOfMedia = 1;
                totalNoOfMedia = 0;

                photoBt.setEnabled(true);
                photoBt.setTag(null);

                Log.i(TAG, "Last file end");
                return;
            }

            Toast.makeText(context, "Downloading photo (" + noOfMedia + "/" + noOfMedia + ")", Toast.LENGTH_LONG);
            Log.i(TAG, "Ended receive");
            */
        } else {
            Log.e(TAG, error.toString());
        }
    }
}
