package com.parrot.bebopflyingbanana.photo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.parrot.arsdk.ardatatransfer.ARDataTransferMedia;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloaderAvailableMediaListener;

import java.io.File;

/**
 * Created by root on 7/7/15.
 */
public class ARDataTransferMediasDownloaderAvailableMediaCallBack implements ARDataTransferMediasDownloaderAvailableMediaListener {
    private Context context;
    private String file_path;
    private File dir;
    private static String TAG = "ARDataTransferMediasDownloaderAvailableMediaCallBack";

    public ARDataTransferMediasDownloaderAvailableMediaCallBack(Context context) {
        this.context = context;

        file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BebopFlyingBanana";
        dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void didMediaAvailable(Object arg, ARDataTransferMedia media, int index) {
        Log.i(TAG, "Date: " + media.getDate());
        Log.i(TAG, "File Path: " + media.getFilePath());
        Log.i(TAG, "Name: " + media.getName());
        Log.i(TAG, "UUID: " + media.getUUID());
        Log.i(TAG, "Product: " + media.getProduct());
        Log.i(TAG, "Product Value: " + media.getProductValue());
        Log.i(TAG, "Size: " + media.getSize());

        /*
        try {
            File photo = new File(media.getFilePath());
            FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());
            fos.write(media.getThumbnail());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Cannot save media.");
            e.printStackTrace();
        }

        Toast.makeText(context, "Downloaded!", Toast.LENGTH_LONG);
        */
    }

}
