package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import uw.hcrlab.kubi.robot.Eyes;
import uw.hcrlab.kubi.robot.Robot;


public class MainActivity extends FragmentActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    public static final String TEACHING_LANGUAGE = "TEACHING_LANGUAGE";
    public static final String PARTICIPANT_ID = "PARTICIPANT_ID";

    private boolean shouldDisconnect = true;

    /* Activity's Properties */
    private Robot robot;

    private String language;
    private String participant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Indicate what the current language is!
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            participant = extras.getString(PARTICIPANT_ID);
            language = extras.getString(TEACHING_LANGUAGE);

            if(participant == null) {
                throw new NullPointerException("You must pass a participant ID to this activity!");
            }

            if(language != null) {
                View flag = findViewById(R.id.main_flag);

                if(language.equals("swedish")) {
                    flag.setBackground(getDrawable(R.drawable.swedish));
                } else {
                    flag.setBackground(getDrawable(R.drawable.dutch));
                }
            }
        }

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

        if(shouldDisconnect) {
            App.FbDisconnect();
        }

        shouldDisconnect = true;

        robot.shutdown();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        shouldDisconnect = false;
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

                Log.d(TAG, "Starting eye animation");

                ((Eyes)findViewById(R.id.main_eyes)).look(Eyes.Look.LOOK_DOWN_LEFT);

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
