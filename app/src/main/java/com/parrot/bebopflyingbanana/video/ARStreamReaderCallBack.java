package com.parrot.bebopflyingbanana.video;

import com.parrot.arsdk.arsal.ARNativeData;
import com.parrot.arsdk.arstream.ARSTREAM_READER_CAUSE_ENUM;
import com.parrot.arsdk.arstream.ARStreamReaderListener;

import java.util.concurrent.BlockingQueue;

class ARStreamReaderCallBack implements ARStreamReaderListener
{
    private static String TAG = "ARStreamReaderCallBack";
    public static BlockingQueue<ARFrame> frameQueue;
    public static int count = 1;
    public static int drop = 1;

    public ARStreamReaderCallBack() { }

    public ARStreamReaderCallBack(BlockingQueue<ARFrame> frameQueue) {
        this.frameQueue = frameQueue;
    }


    /*** This method will be called by the system ***/
    @Override
    public ARNativeData didUpdateFrameStatus(ARSTREAM_READER_CAUSE_ENUM cause,
                                             ARNativeData currentFrame,
                                             boolean isFlushFrame,
                                             int nbSkippedFrames,
                                             int newBufferCapacity) {
        //Log.i(TAG, "didUpdateFrameStatus");
        //Log.i(TAG, "ARSTREAM_READER_CAUSE_ENUM: " + cause);
        //Log.i(TAG, "ARNativeData: " + currentFrame);
        //Log.i(TAG, "isFlushFrame: " + isFlushFrame);
        //Log.i(TAG, "nbSkippedFrames: " + nbSkippedFrames);
        //Log.i(TAG, "newBufferCapacity: " + newBufferCapacity);
        //Log.i(TAG, "frames received: " + count);
        //Log.i(TAG, "Dropped frames" + drop + "/" + count);

        switch (cause)
        {
            case ARSTREAM_READER_CAUSE_FRAME_COMPLETE:
                ARFrame freeFrame = new ARFrame(currentFrame.getByteData(), currentFrame.getDataSize(), isFlushFrame, count++);

                /*** I-Frame ***/
                if (isFlushFrame) {
                    drop += frameQueue.size();
                    frameQueue.clear();
                }

                frameQueue.offer(freeFrame);

                return currentFrame;

            case ARSTREAM_READER_CAUSE_FRAME_TOO_SMALL:
                /* This case should not happen, as we've allocated a frame pointer to the maximum possible size. */
                ARNativeData enlargedFrame = new ARNativeData(newBufferCapacity);
                return enlargedFrame;

            case ARSTREAM_READER_CAUSE_COPY_COMPLETE:
                /* Same as before ... but return value are ignored, so we just do nothing */
                return null;

            case ARSTREAM_READER_CAUSE_CANCEL:
                /* Same as before ... but return value are ignored, so we just do nothing */
                return null;

            default:
                return null;
        }
    }
}