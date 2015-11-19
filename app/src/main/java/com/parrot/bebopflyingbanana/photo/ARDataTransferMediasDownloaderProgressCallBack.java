package com.parrot.bebopflyingbanana.photo;

import com.parrot.arsdk.ardatatransfer.ARDataTransferMedia;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloaderProgressListener;

/**
 * Created by root on 7/7/15.
 */
public class ARDataTransferMediasDownloaderProgressCallBack implements ARDataTransferMediasDownloaderProgressListener {
    public static String TAG = "ARDataTransferMediasDownloaderProgressCallBack";

    @Override
    public void didMediaProgress(Object arg, ARDataTransferMedia media, float percent) {
        //Log.i(TAG, "Media Progress: " + media.getName() + " - " + percent);
    }
}
