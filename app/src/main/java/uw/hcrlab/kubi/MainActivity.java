package uw.hcrlab.kubi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import uw.hcrlab.kubi.robot.PermissionsManager;
import uw.hcrlab.kubi.robot.Robot;


public class MainActivity extends FragmentActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private static final int DEVICE_SETUP_CODE = 1;
    private boolean authenticated = false;

    /* Activity's Properties */
    private Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // make sure we have the permissions needed to connect bluetooth
        PermissionsManager.requestPermissionsDialogIfNecessary(this);

        // Initialize robot with UI components
        robot = Robot.Factory.create(this, R.id.main_eyes, R.id.prompt, R.id.thought_bubble, R.id.leftCard, R.id.rightCard);

        App app = (App) getApplication();
        authenticated = app.authenticate();

        if(!authenticated) {
            startActivityForResult(new Intent(this, SetupActivity.class), DEVICE_SETUP_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DEVICE_SETUP_CODE) {
            robot.startup();
            App.FbConnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(authenticated) {
            robot.startup();
            App.FbConnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(authenticated) {
            App.FbDisconnect();
            robot.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying Main Activity ...");
        super.onDestroy();

        robot.cleanup();
    }

    /* Touch events */
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "Screen touched ");
                // TODO: log to Firebase?

//                Log.d(TAG, "Starting eye animation");
//
//                ((Eyes)findViewById(R.id.main_eyes)).start(R.raw.look_down);
//
//                if(robot.isHintOpen()) {
//                    robot.hideHint();
//                } else {
//                    robot.showHint("Some hint text");
//                }

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        PermissionsManager.onRequestPermissionsResult(
                this, requestCode, permissions, grantResults);
    }

}
