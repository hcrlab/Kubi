package uw.hcrlab.kubi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import uw.hcrlab.kubi.robot.Action;
import uw.hcrlab.kubi.robot.FaceAction;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.screen.RobotFace;


public class LessonActivity extends Activity {
    private String TAG = LessonActivity.class.getSimpleName();

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
        Log.i(TAG, "Creating Lesson Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // this is how each activity will get the robot and connect it to the robot face
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_lesson);
        robot = Robot.getInstance((RobotFace)findViewById(R.id.face), this);
    }

    /*
    Called after your activity has been stopped, prior to it being started again.
    Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        Log.i(TAG, "Restarting Lesson Activity ...");
        super.onRestart();
    }

    /*
    Called when the activity is becoming visible to the user.
    Followed by onResume() if the activity comes to the foreground, or onStop()
    if it becomes hidden.
     */
    @Override
    protected void onStart() {
        Log.i(TAG, "Starting Lesson Activity ...");
        super.onStart();
    }

    /*
    Called when the activity will start interacting with the user. At this point
    your activity is at the top of the activity stack, with user input going to it.
    Always followed by onPause().
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "Resuming Lesson Activity ...");
        super.onResume();

        Button backButton = (Button) findViewById(R.id.backButton);
        Button settingButton = (Button) findViewById(R.id.settingsButton);
        initializeButtons(backButton, settingButton);
        
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
        Log.i(TAG, "Pausing Lesson Activity ...");
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
        Log.i(TAG, "Stopping Lesson Activity ...");
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
        Log.i(TAG, "Destroying Lesson Activity ...");
        super.onDestroy();
    }

    /* Setting up the Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "Creating Option Menu ...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Called onOptionsItemSelected; selected item: " + item);
        /*
         Handle action bar item clicks here. The action bar will automatically
         handle clicks on the Home/Up button, so long as you specify a parent
         activity in AndroidManifest.xml.
          */
        int id = item.getItemId();

        // TODO: modify this if to map with options
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Touch events */
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "Screen touched ");
                // TODO: send to Firebase
                // demo();

                break;
            default:
                break;
        }
        return true;
    }

    private void demo() {
        Timer timer = new Timer();

        TimerTask yesnod = new TimerTask() {
            @Override
            public void run() {
                robot.say("Yes", "EN");
                robot.perform(Action.NOD);
            }
        };
        timer.schedule(yesnod, 2000);

        TimerTask noshake = new TimerTask() {
            @Override
            public void run() {
                robot.say("No", "EN");
                robot.perform(Action.SHAKE);
            }
        };
        timer.schedule(noshake, 8000);

        TimerTask sleep = new TimerTask() {
            @Override
            public void run() {
                robot.act(FaceAction.SLEEP);
                robot.perform(Action.SLEEP);
            }
        };
        timer.schedule(sleep, 14000);

        TimerTask wake = new TimerTask() {
            @Override
            public void run() {
                robot.act(FaceAction.WAKE);
                robot.perform(Action.WAKE);
            }
        };
        timer.schedule(wake, 20000);

        TimerTask lookaround = new TimerTask() {
            @Override
            public void run() {
                robot.perform(Action.LOOK_AROUND);
            }
        };
        timer.schedule(lookaround, 26000);
        TimerTask lookaround2 = new TimerTask() {
            @Override
            public void run() {
                robot.perform(Action.LOOK_AROUND);
            }
        };
        timer.schedule(lookaround2, 28000);
        TimerTask lookaround3 = new TimerTask() {
            @Override
            public void run() {
                robot.perform(Action.LOOK_AROUND);
            }
        };
        timer.schedule(lookaround3, 30000);
    }

    private void initializeButtons(Button backButton, Button settingButton) {
        final Intent mainActivityIntent = new Intent(this, MainActivity.class);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                robot.shutdown();
                startActivity(mainActivityIntent);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: implement this
            }
        });
    }
}
