package uw.hcrlab.kubi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import uw.hcrlab.kubi.screen.RobotFace;


public class ConversationActivity extends Activity {
    private String TAG = ConversationActivity.class.getSimpleName();

    /* Activity's Properties */
    private Robot robot;

    /* Activity's methods */

    /*
    Called when the activity is first created.
    This is where you should do all of your normal static set up: create views, bind data to lists, etc.
    This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
    Always followed by onStart().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Conversation Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Notice: this is how each activity will get the robot and connect it to the robot face
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_conversation);
        robot = Robot.getInstance((RobotFace)findViewById(R.id.face), this);
    }

    /*
    Called after your activity has been stopped, prior to it being started again.
    Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        Log.i(TAG, "Restarting Conversation Activity ...");
        super.onRestart();
    }

    /*
    Called when the activity is becoming visible to the user.
    Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
     */
    @Override
    protected void onStart() {
        Log.i(TAG, "Starting Conversation Activity ...");
        super.onStart();
    }

    /*
    Called when the activity will start interacting with the user. At this point your activity is at
    the top of the activity stack, with user input going to it.
    Always followed by onPause().
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "Resuming Conversation Activity ...");
        super.onResume();

        robot.startup();
    }

    /*
    Called when the system is about to start resuming a previous activity.
    This is typically used to commit unsaved changes to persistent data, stop animations and other things
    that may be consuming CPU, etc. Implementations of this method must be very quick because the next activity
    will not be resumed until this method returns.
    Followed by either onResume() if the activity returns back to the front, or onStop() if it becomes invisible to the user.
     */
    @Override
    protected void onPause() {
        Log.i(TAG, "Pausing Conversation Activity ...");
        super.onPause();

        robot.shutdown();
    }

    /*
    Called when the activity is no longer visible to the user, because another activity has been resumed
    and is covering this one. This may happen either because a new activity is being started,
    an existing one is being brought in front of this one, or this one is being destroyed.
    Followed by either onRestart() if this activity is coming back to interact with the user,
    or onDestroy() if this activity is going away.
     */
    @Override
    protected void onStop() {
        Log.i(TAG, "Stopping Conversation Activity ...");
        super.onStop();
    }

    /*
    The final call you receive before your activity is destroyed. This can happen either because
    the activity is finishing (someone called finish() on it, or because the system is temporarily
    destroying this instance of the activity to save space. You can distinguish between these two
    scenarios with the isFinishing() method.
     */
    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying Conversation Activity ...");
        super.onDestroy();
    }

    /* Setting up the Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "Creating Option Menu ...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Called onOptionsItemSelected; selected item: " + item);
        /*
         Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
         so long as you specify a parent activity in AndroidManifest.xml.
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
                robot.listen();
                break;
            default:
                break;
        }
        return true;
    }
}
