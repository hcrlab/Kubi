package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import uw.hcrlab.kubi.robot.Robot;


public class MainActivity extends FragmentActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    /* Activity's Properties */
    private Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Initialize robot with UI components
        robot = Robot.Factory.create(this, R.id.main_eyes, R.id.prompt_placeholder, R.id.thought_bubble, R.id.leftCard, R.id.rightCard);
    }

    @Override
    protected void onResume() {
        super.onResume();

        robot.startup();
        App.FbConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        App.FbDisconnect();
        robot.shutdown();
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
}
