package com.parrot.bebopflyingbanana;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.bebopflyingbanana.video.ARFrame;
import com.parrot.bebopflyingbanana.video.StreamImageView;

import java.io.Serializable;

public class PilotingActivity extends Activity implements DeviceControllerListener, Serializable {
    private static String TAG = PilotingActivity.class.getSimpleName();
    public static String EXTRA_DEVICE_SERVICE = "pilotingActivity.extra.device.service";

    public static DeviceController deviceController;
    public ARDiscoveryDeviceService service;

    private Button emergencyBt;

    private Button gazUpBt;
    private Button gazDownBt;
    private Button yawLeftBt;
    private Button yawRightBt;

    private Button forwardBt;
    private Button backBt;
    private Button rollLeftBt;
    private Button rollRightBt;

    private TextView rollText;
    private TextView yawText;
    private TextView batteryLabel;
    private Button photoBt;
    private ToggleButton toggleAuto;
    private ToggleButton toggleLanding;
    private StreamImageView streamImageView;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_piloting);
            rollText = (TextView) findViewById(R.id.textView2);
            yawText = (TextView) findViewById(R.id.textView3);

            emergencyBt = (Button) findViewById(R.id.emergencyBt);
            emergencyBt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (deviceController != null) {
                        deviceController.sendEmergency();
                    }
                }
            });

            gazUpBt = (Button) findViewById(R.id.gazUpBt);
            gazUpBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setGaz((byte) 50);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setGaz((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            gazDownBt = (Button) findViewById(R.id.gazDownBt);
            gazDownBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setGaz((byte) -50);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setGaz((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            yawLeftBt = (Button) findViewById(R.id.yawLeftBt);
            yawLeftBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setYaw((byte) -50);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setYaw((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            yawRightBt = (Button) findViewById(R.id.yawRightBt);
            yawRightBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setYaw((byte) 50);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setYaw((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            forwardBt = (Button) findViewById(R.id.forwardBt);
            forwardBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setPitch((byte) 50);
                                deviceController.setFlag((byte) 1);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setPitch((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            backBt = (Button) findViewById(R.id.backBt);
            backBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setPitch((byte) -50);
                                deviceController.setFlag((byte) 1);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setPitch((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            rollLeftBt = (Button) findViewById(R.id.rollLeftBt);
            rollLeftBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setRoll((byte) -50);
                                deviceController.setFlag((byte) 1);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setRoll((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            rollRightBt = (Button) findViewById(R.id.rollRightBt);
            rollRightBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            if (deviceController != null) {
                                deviceController.setRoll((byte) 50);
                                deviceController.setFlag((byte) 1);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.setRoll((byte) 0);
                                deviceController.setFlag((byte) 0);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            photoBt = (Button) findViewById(R.id.photoBt);
            photoBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            if (deviceController != null) {
                                deviceController.takePhoto(photoBt);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            /*
            photoBt = (Button) findViewById(R.id.photoBt);
            photoBt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            v.setPressed(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                            photoBt.setEnabled(false);
                            if (deviceController != null)
                            {
                                deviceController.takePhoto(photoBt);
                            }
                            break;

                        default:

                            break;
                    }

                    return true;
                }
            });
            */

            toggleAuto = (ToggleButton) findViewById(R.id.togglebuttonauto);
            toggleLanding = (ToggleButton) findViewById(R.id.togglebuttonlanding);
            batteryLabel = (TextView) findViewById(R.id.batteryLabel);
            streamImageView = (StreamImageView) findViewById(R.id.streamImageView);

            Intent intent = getIntent();
            service = intent.getParcelableExtra(EXTRA_DEVICE_SERVICE);

            deviceController = new DeviceController(this, service);
            deviceController.setListener(this);

            streamImageView.setDeviceController(deviceController);
        }

        catch (Exception e) {
            deviceController.sendLanding();
            e.printStackTrace();
        }
    }

    public void onToggleLanding(View view) {
        try {
            boolean on = ((ToggleButton) view).isChecked();
            if (on) {
                // take off
                deviceController.sendTakeoff();
            } else {
                // landing
                deviceController.sendLanding();
            }
        }
        catch (Exception e) {
            deviceController.sendLanding();
            e.printStackTrace();
        }
    }

    public void onToggleAuto(View view) throws InterruptedException {
        try {
            boolean on = ((ToggleButton) view).isChecked();
            if (on) {
                // Auto
                photoBt.setVisibility(View.INVISIBLE);
                forwardBt.setVisibility(View.INVISIBLE);
                backBt.setVisibility(View.INVISIBLE);
                rollLeftBt.setVisibility(View.INVISIBLE);
                rollRightBt.setVisibility(View.INVISIBLE);
                gazUpBt.setVisibility(View.INVISIBLE);
                gazDownBt.setVisibility(View.INVISIBLE);
                yawLeftBt.setVisibility(View.INVISIBLE);
                yawRightBt.setVisibility(View.INVISIBLE);
                toggleLanding.setVisibility(View.INVISIBLE);
                yawText.setVisibility(View.INVISIBLE);
                rollText.setVisibility(View.INVISIBLE);
                streamImageView.enableAuto();

            } else {
                // Manual
                streamImageView.disableAuto();
                photoBt.setVisibility(View.VISIBLE);
                forwardBt.setVisibility(View.VISIBLE);
                backBt.setVisibility(View.VISIBLE);
                rollLeftBt.setVisibility(View.VISIBLE);
                rollRightBt.setVisibility(View.VISIBLE);
                gazUpBt.setVisibility(View.VISIBLE);
                gazDownBt.setVisibility(View.VISIBLE);
                yawLeftBt.setVisibility(View.VISIBLE);
                yawRightBt.setVisibility(View.VISIBLE);
                toggleLanding.setVisibility(View.VISIBLE);
                yawText.setVisibility(View.VISIBLE);
                rollText.setVisibility(View.VISIBLE);
            }
        }

        catch (Exception e) {
            deviceController.sendLanding();
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (deviceController != null) {

            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    Looper.prepare();
                    boolean failed = false;final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PilotingActivity.this);
                    // set title
                    alertDialogBuilder.setTitle("Connecting ...");

                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    failed = deviceController.start();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            //alertDialog.hide();
                            alertDialog.dismiss();
                        }
                    });

                    if (failed)
                    {
                        finish();
                    }
                }
            }).start();
        }
    }

    private void stopDeviceController() {
        try {
            if (deviceController != null) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PilotingActivity.this);

                // set title
                alertDialogBuilder.setTitle("Disconnecting ...");

                // show it
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // create alert dialog
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deviceController.stop();
                                deviceController = null;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //alertDialog.hide();
                                        alertDialog.dismiss();
                                        finish();
                                    }
                                });

                            }
                        }).start();
                    }
                });
                //alertDialog.show();

            }
        }
        catch (Exception e) {
            deviceController.sendLanding();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop()
    {
        if (deviceController != null)
        {
            deviceController.stop();
            deviceController = null;
        }

        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        stopDeviceController();
    }

    @Override
    public void onDisconnect() {
        stopDeviceController();
    }

    @Override
    public void onUpdateBattery(final byte percent)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryLabel.setText(String.format("%d%%", percent));
            }
        });

    }

    @Override
    public void onFlyingStateChanged(final ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM state)
    {
        // on the UI thread, disable and enable buttons according to flying state
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (state) {
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
//                        takeoffBt.setEnabled(true);
//                        landingBt.setEnabled(false);
                        break;
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
//                        takeoffBt.setEnabled(false);
//                        landingBt.setEnabled(true);
                        break;
                    default:
                        // in all other cases, take of and landing are not enabled
//                        takeoffBt.setEnabled(false);
//                        landingBt.setEnabled(false);
                        break;
                }
            }
        });
    }

    @Override
    public void onUpdateStream(final ARFrame frame)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                streamImageView.setARFrame(frame);
                streamImageView.setImageBitmap(frame.bitmap);
                streamImageView.invalidate();
            }
        });
    }
}