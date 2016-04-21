package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

    /* Activity's methods */

    /*
    Called when the activity is first created.
    This is where you should do all of your normal static set up: create views,
    bind data to lists, etc. This method also provides you with a Bundle containing
    the activity's previously frozen state, if there was one. 
    Always followed by onStart().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Notice: this is how each activity will get the robot and connect it to the robot face
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        robot = Robot.getInstance((RobotFace)findViewById(R.id.face), this);
    }

    /*
    Called after your activity has been stopped, prior to it being started again.
    Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        Log.i(TAG, "Restarting Main Activity ...");
        super.onRestart();
    }

    /*
    Called when the activity is becoming visible to the user.
    Followed by onResume() if the activity comes to the foreground, or onStop()
    if it becomes hidden.
     */
    @Override
    protected void onStart() {
        Log.i(TAG, "Starting Main Activity ...");
        super.onStart();
    }

    View left = null;
    View right = null;
    View promptContainer = null;

    /*
    Called when the activity will start interacting with the user. At this point
    your activity is at the top of the activity stack, with user input going to it.
    Always followed by onPause().
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "Resuming Main Activity ...");
        super.onResume();

        left = findViewById(R.id.leftCard);
        right = findViewById(R.id.rightCard);
        robot.setCards(left, right);

        promptContainer = findViewById(R.id.prompt_container);
        robot.setPromptContainer(promptContainer);

        robot.startup();
        App.FbConnect();
    }

    /*
    Called when the system is about to start resuming a previous activity.
    This is typically used to commit unsaved changes to persistent data, stop
    animations and other things that may be consuming CPU, etc. Implementations
    of this method must be very quick because the next activity will not be
    resumed until this method returns.
    Followed by either onResume() if the activity returns back to the front, or
    onStop() if it becomes invisible to the user.
     */
    @Override
    protected void onPause() {
        Log.i(TAG, "Pausing Main Activity ...");
        super.onPause();

        App.FbDisconnect();
        robot.shutdown();
    }

    /* Called when the activity is no longer visible to the user, because another
       activity has been resumed and is covering this one. This may happen either
       because a new activity is being started, an existing one is being brought
       in front of this one, or this one is being destroyed. Followed by either
       onRestart() if this activity is coming back to interact with the user, or
       onDestroy() if this activity is going away.
     */
    @Override
    protected void onStop() {
        Log.i(TAG, "Stopping Main Activity ...");
        super.onStop();
    }

    /*
    The final call you receive before your activity is destroyed. This can happen
    either because the activity is finishing (someone called finish() on it, or
    because the system is temporarily destroying this instance of the activity
    to save space. You can distinguish between these two scenarios with the
    isFinishing() method.
     */
    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying Main Activity ...");
        super.onDestroy();
    }

    boolean mIsOpen = false;

    /* Touch events */
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "Screen touched ");
                // TODO: log to Firebase?

                if(!robot.isPromptOpen()) {
                    PromptData pd = new PromptData();
                    pd.type = PromptTypes.SELECT;
                    pd.srcText = "apple";
                    pd.options.add(new PromptData.Option(1, "apple").setDrawable("apple"));
                    pd.options.add(new PromptData.Option(2, "banana").setDrawable("banana"));
                    pd.options.add(new PromptData.Option(3, "girl").setDrawable("girl"));

                    Prompt p = new SelectPrompt();
                    p.setData(pd);

                    robot.setPrompt(p, "generic");
                } else {
                    robot.hidePrompt();
                }

                break;
            default:
                break;
        }
        return true;
    }
}
