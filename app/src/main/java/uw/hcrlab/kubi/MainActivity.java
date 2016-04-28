package uw.hcrlab.kubi;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.List;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.screen.RobotFace;


public class MainActivity extends FragmentActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    /* Activity's Properties */
    private Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Notice: this is how each activity will get the robot and connect it to the robot face
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        RobotFace face = (RobotFace) findViewById(R.id.face);
        View promptContainer = findViewById(R.id.prompt_container);
        View hintContainer = findViewById(R.id.hint_container);
        View leftCard = findViewById(R.id.leftCard);
        View rightCard = findViewById(R.id.rightCard);

        // Initialize robot with UI components
        robot = Robot.getInstance(this, face, promptContainer, hintContainer, leftCard, rightCard);
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

                if(robot.isHintOpen()) {
                    robot.hideHint();
                } else {
                    robot.showHint("Some hint text");
                }

                break;
            default:
                break;
        }
        return true;
    }
}
