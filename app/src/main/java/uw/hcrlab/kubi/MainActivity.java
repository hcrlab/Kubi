package uw.hcrlab.kubi;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.revolverobotics.kubiapi.KubiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import trash.KubiCallback;
import trash.OldRobotFace;
import uw.hcrlab.kubi.speech.SpeechUtils;


public class MainActivity extends ASR {
    private String TAG = MainActivity.class.getSimpleName();

    /* Activity's Properties */

    private MainThread mainThread;
    private OldRobotFace robotFace;
    private KubiManager kubiManager;

    /* ASR's Properties */

    // The ID of the bot to use for the chatbot, can be changed
    // you can also make a new bot by creating an account in pandorabots.com and making a new chatbot robot
    private String PANDORA_BOT_ID = "b9581e5f6e343f72";
    private TTS tts;
    private Bot bot;
    // Map containing key = simple questions and value = how the robot responds
    private Map<String, String> simpleResponses;


    /* Activity's methods */

    /*
    Called when the activity is first created.
    This is where you should do all of your normal static set up: create views, bind data to lists, etc.
    This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
    Always followed by onStart().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");
        super.onCreate(savedInstanceState);
        setup();
    }

    /*
    Called after your activity has been stopped, prior to it being started again.
    Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        Log.i(TAG, "Restarting Main Activity ...");
        super.onRestart();
        //TODO: implement this
        /* get the information that has been saved from onStop() */
    }

    /*
    Called when the activity is becoming visible to the user.
    Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
     */
    @Override
    protected void onStart() {
        Log.i(TAG, "Starting Main Activity ...");
        super.onStart();
        //TODO: implement this
        /* get the information that has been saved from onCreate() or onRestart() */
    }

    /*
    Called when the activity will start interacting with the user. At this point your activity is at
    the top of the activity stack, with user input going to it.
    Always followed by onPause().
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "Resuming Main Activity ...");
        super.onResume();
        //TODO: implement this
        /* get the information that has been saved from onPause() */
        restartMainThread();
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
        Log.i(TAG, "Pausing Main Activity ...");
        super.onPause();
        //TODO: implement this
        /* saving ... */

        /* destroying MainThread */
        destroyMainThread();
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
        Log.i(TAG, "Stopping Main Activity ...");
        super.onStop();
        //TODO: implement this
        /* saving ... */

        /* destroying MainThread */
        destroyMainThread();
    }

    /*
    The final call you receive before your activity is destroyed. This can happen either because
    the activity is finishing (someone called finish() on it, or because the system is temporarily
    destroying this instance of the activity to save space. You can distinguish between these two
    scenarios with the isFinishing() method.
     */
    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying Main Activity ...");
        super.onDestroy();
        //TODO: implement this
        /* saving ... */

        /* destroying MainThread */
        destroyMainThread();
        tts.shutdown();
    }

    /* Setting up the Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "Creating Option Menu ...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                // TODO: modify this
                break;
            default:
                break;
        }
        return true;
    }

    /* ASR's methods */

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        String speechInput = nBestList.get(0);
        Log.i(TAG, "Speech input: " + speechInput);
        String response = SpeechUtils.getResponse(speechInput);
        if (response == null) {
            response = simpleResponses.get(speechInput);
        }
        try {
            if(response != null){
                Log.i(TAG, "Saying : " + response);
                tts.speak(response, "EN");
            }  else {
                Log.i(TAG, "Default response");
                bot.initiateQuery(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not response to sppech input: " + speechInput);
            e.printStackTrace();
        }
    }

    @Override
    public void processAsrReadyForSpeech() {
        Log.i(TAG, "Listening to user's speech.");
        Toast.makeText(this, "I'm listening.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void processAsrError(int errorCode) {
        String errorMessage = SpeechUtils.getErrorMessage(errorCode);
        try {
            if (errorMessage != null) {
                tts.speak(errorMessage,"EN");
            }
        } catch (Exception e) {
            Log.e(TAG, "English not available for TTS, default language used instead");
        }

        // If there is an error, shows feedback to the user and writes it in the log
        Log.e(TAG, "Error: "+ errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void setup() {
        Log.i(TAG, "Initializing local variables ...");
        /* initialize screen */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        robotFace = (OldRobotFace) findViewById(R.id.face);

        /* initialize speech recognizer */
        createRecognizer(getApplicationContext());

        /* Set up text to speech capability, using the library in TTSLib */
        tts = TTS.getInstance(this);

        /*
         Parse the simple questions and responses that the robot is able to perform.
         These responses are defined under res/values/arrays.xml
          */
        this.simpleResponses = new HashMap<String, String>();
        String[] stringArray = getResources().getStringArray(R.array.promptsAndResponses);
        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            simpleResponses.put(splitResult[0], splitResult[1]);
        }

        /* A chat bot web service that the user can optionally use to answer responses */
        bot = new Bot(this, PANDORA_BOT_ID, this.tts);

        /* Manager that manages the Kubi's actions */
        kubiManager = new KubiManager(new KubiCallback(), true);

        /* Start the Main Thread */
        mainThread = new MainThread(robotFace, kubiManager, this);
    }

    private void destroyMainThread() {
        Log.i(TAG, "Shutting down Main Thread ...");
        boolean retry = true;
        while (retry) {
            try {
                mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    private void restartMainThread() {
        if (mainThread.isAlive()) {
            mainThread.interrupt();
        }
        mainThread = new MainThread(robotFace, kubiManager, this);
        mainThread.start();
    }
}
