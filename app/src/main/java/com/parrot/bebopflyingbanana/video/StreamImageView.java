package com.parrot.bebopflyingbanana.video;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.bebopflyingbanana.DeviceController;
import com.parrot.bebopflyingbanana.PilotingActivity;
import com.parrot.bebopflyingbanana.R;

import java.util.Timer;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by root on 6/27/15.
 */
public class StreamImageView extends ImageView {
    private Paint paint;
    private float scaleX, scaleY, scale;
    private float offsetX = 0, offsetY = 0;
    private Point size;
    private int upExceed = 0, downExceed = 0, leftExceed = 0, rightExceed = 0, iteration = 0;
    private int action = 0;
    /*
     * 0 : none
     * 1 : backwards
     * 2 : left
     * 3 : right
     * 4 : up
     * 5 : down
     */
    private int confirm = 0;
    private boolean takingPhoto = false;
    private long startTime;
    public static int IMAGE_WIDTH = 640;
    public static int IMAGE_HEIGHT = 368;

    public static String EXTRA_DEVICE_SERVICE = "pilotingActivity.extra.device.service";

    public static DeviceController deviceController;

    public void setDeviceController(DeviceController deviceController) {
        this.deviceController = deviceController;
    }

    public ARFrame frame;

    public static String TAG = "StreamImageView";

    public StreamImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        size = new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        scaleX = (float) size.x / IMAGE_WIDTH;
        scaleY = (float) size.y / IMAGE_HEIGHT;
        if (scaleX > scaleY) {
            scale = scaleX;
            offsetX = 0;
            offsetY = ((IMAGE_HEIGHT * scale) - size.y) / 2;
        } else {
            scale = scaleY;
            offsetY = 0;
            offsetX = ((IMAGE_WIDTH * scale) - size.x) / 2;
        }

        startTime = System.currentTimeMillis();
    }

    public static boolean autoMode = false;

    public void enableAuto() {
        autoMode = true;
    }

    public void disableAuto() {
        autoMode = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //static boundary rectangle
        canvas.drawRect(size.x / 10, size.y / 10, (size.x / 10) * 9, (size.y / 10) * 8, paint);

        if (autoMode == false) {
            if ((frame != null) && (frame.faces != null)) {
                int upExceed = 0, downExceed = 0, leftExceed = 0, rightExceed = 0;
                for (int i = 0; i < frame.faces.capacity(); i++) {          //draw and count faces out of frame
                    int xx = frame.faces.position(i).x();
                    int yy = frame.faces.position(i).y();
                    int ww = frame.faces.position(i).width();
                    int hh = frame.faces.position(i).height();
                    canvas.drawRect((xx - offsetX) * scale,
                            (yy - offsetY) * scale,
                            (xx - offsetX + ww) * scale,
                            (yy - offsetY + hh) * scale,
                            paint);
                }
            }
        } else {
            boolean leftFaceExceed = false, rightFaceExceed = false, upFaceExceed = false, downFaceExceed = false;
            if ((frame != null) && (frame.faces != null)) {

                leftFaceExceed = rightFaceExceed = upFaceExceed = downFaceExceed = false;

                for (int i = 0; i < frame.faces.capacity(); i++) {          //draw and count faces out of frame
                    int xx = frame.faces.position(i).x();
                    int yy = frame.faces.position(i).y();
                    int ww = frame.faces.position(i).width();
                    int hh = frame.faces.position(i).height();
                    canvas.drawRect((xx - offsetX) * scale,
                            (yy - offsetY) * scale,
                            (xx - offsetX + ww) * scale,
                            (yy - offsetY + hh) * scale,
                            paint);

                    //count faces out of frame
                    if (((xx - offsetX) * scale) < (size.x / 10)) {
                        leftFaceExceed = true;
                        //Log.i(TAG, "leftExceed");
                    }

                    if (((xx - offsetX + ww) * scale) > ((size.x / 10) * 9)) {
                        rightFaceExceed = true;
                        //Log.i(TAG, "rightExceed");
                    }

                    if (((yy - offsetY) * scale) < (size.y / 10)) {
                        upFaceExceed = true;
                        //Log.i(TAG, "upExceed");
                    }

                    if (((yy - offsetY + hh) * scale) > ((size.y / 10) * 8)) {
                        downFaceExceed = true;
                        //Log.i(TAG, "downExceed");
                    }
                }

                if (leftFaceExceed == true) {
                    leftExceed++;
                }
                if (rightFaceExceed == true) {
                    rightExceed++;
                }
                if (upFaceExceed == true) {
                    upExceed++;
                }
                if (downFaceExceed == true) {
                    downExceed++;
                }

                iteration++;
                long currentTime = System.currentTimeMillis();

                if (takingPhoto == false) {
                    Drawable d;
                    switch (action) {
                        case 0:
                            confirm++;
                            break;
                        case 1:
                            confirm = 0;
                            d = getResources().getDrawable(R.drawable.chevronback);
                            d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                    (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) + (d.getIntrinsicHeight() / 2));
                            d.draw(canvas);
                            break;
                        case 2:
                            confirm = 0;
                            d = getResources().getDrawable(R.drawable.chevronleft);
                            d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                    (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) + (d.getIntrinsicHeight() / 2));
                            d.draw(canvas);
                            break;
                        case 3:
                            confirm = 0;
                            d = getResources().getDrawable(R.drawable.chevronright);
                            d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                    (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) + (d.getIntrinsicHeight() / 2));
                            d.draw(canvas);
                            break;
                        case 4:
                            confirm = 0;
                            d = getResources().getDrawable(R.drawable.chevronup);
                            d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                    (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) + (d.getIntrinsicHeight() / 2));
                            d.draw(canvas);
                            break;
                        case 5:
                            confirm = 0;
                            d = getResources().getDrawable(R.drawable.chevrondown);
                            d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                    (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                    (size.y / 2) + (d.getIntrinsicHeight() / 2));
                            d.draw(canvas);
                            break;
                    }
                }

                if ((currentTime - startTime) > 3000) {
                    Log.i(TAG, leftExceed + "-" + rightExceed + "-" + upExceed + "-" + downExceed + "-" + iteration);

                    if (confirm == 3) {
                        takingPhoto = true;
                    }

                    if (takingPhoto == true) {
                        Drawable d;
                        switch (confirm) {
                            case 0:
                                takingPhoto = false;
                                if (deviceController != null) {
                                    ColorDrawable cd = new ColorDrawable(Color.WHITE);
                                    deviceController.takePhoto(null);
                                    cd.draw(canvas);

                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    autoMode = false;
                                    upExceed = downExceed = leftExceed = rightExceed = iteration = 0;
                                    action = 0;
                                    confirm = 0;
                                    //Log.i(TAG, "No movement needed");
                                }
                                break;
                            case 1:
                                d = getResources().getDrawable(R.drawable.one);
                                d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                        (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) + (d.getIntrinsicHeight() / 2));
                                d.draw(canvas);
                                takingPhoto = false;
                                confirm--;
                                break;
                            case 2:
                                d = getResources().getDrawable(R.drawable.two);
                                d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                        (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) + (d.getIntrinsicHeight() / 2));
                                d.draw(canvas);
                                confirm--;
                                break;
                            case 3:
                                d = getResources().getDrawable(R.drawable.three);
                                d.setBounds((size.x / 2) - (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) - (d.getIntrinsicHeight() / 2),
                                        (size.x / 2) + (d.getIntrinsicWidth() / 2),
                                        (size.y / 2) + (d.getIntrinsicHeight() / 2));
                                d.draw(canvas);
                                confirm--;
                                break;
                        }
                    } else if (iteration > 1) {        //after all faces are drawn, execute the most popular movement
                        action = 0;
                        if ((((double) leftExceed / iteration) > 0.5) && (((double) rightExceed / iteration) > 0.5)) {
                            // Move backwards
                            Log.i(TAG, "Move backwards");
                            action = 1;
                            if (deviceController != null) {
                                deviceController.setPitch((byte) -50);
                                deviceController.setFlag((byte) 1);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setPitch((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                        } else if (((double) leftExceed / iteration) > 0.5) {
                            // Turn left
                            Log.i(TAG, "Turn left");
                            action = 2;
                            if (deviceController != null) {
                                deviceController.setYaw((byte) 50);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setYaw((byte) 0);
                            }
                        } else if (((double) rightExceed / iteration) > 0.5) {
                            // Turn right
                            Log.i(TAG, "Turn right");
                            action = 3;
                            if (deviceController != null) {
                                deviceController.setYaw((byte) -50);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setYaw((byte) 0);
                            }
                        } else if ((((double) downExceed / iteration) > 0.5) && (((double) upExceed / iteration) > 0.5)) {
                            // Move backwards
                            Log.i(TAG, "Move backwards");
                            action = 1;
                            if (deviceController != null) {
                                deviceController.setPitch((byte) -50);
                                deviceController.setFlag((byte) 1);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setPitch((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                        } else if (((double) upExceed / iteration) > 0.5) {
                            // Move upwards
                            Log.i(TAG, "Move upwards");
                            action = 4;
                            if (deviceController != null) {
                                deviceController.setGaz((byte) 50);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setGaz((byte) 0);
                            }
                        } else if (((double) downExceed / iteration) > 0.5) {
                            // Move downwards
                            Log.i(TAG, "Move downwards");
                            action = 5;
                            if (deviceController != null) {
                                deviceController.setGaz((byte) -50);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                deviceController.setGaz((byte) 0);
                            }
                        }

                        leftExceed = rightExceed = upExceed = downExceed = iteration = 0;
                        startTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    public void setARFrame(ARFrame frame) {
        this.frame = frame;
    }
}