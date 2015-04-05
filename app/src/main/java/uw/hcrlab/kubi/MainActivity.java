package uw.hcrlab.kubi;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.revolverobotics.kubiapi.KubiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.screen.OldRobotFace;


public class MainActivity extends ASR {
    private String TAG = OldKubiDemoActivity.class.getSimpleName();

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

        /* the main loop of the app */
        mainThread = new MainThread(robotFace, kubiManager, this);
    }

    /*
    Called after your activity has been stopped, prior to it being started again.
    Always followed by onStart()
     */
    @Override
    protected void onRestart() {
        //TODO: implement this
    }

    /*
    Called when the activity is becoming visible to the user.
    Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
     */
    @Override
    protected void onStart() {
        //TODO: implement this
    }

    /*
    Called when the activity will start interacting with the user. At this point your activity is at
    the top of the activity stack, with user input going to it.
    Always followed by onPause().
     */
    @Override
    protected void onResume() {
        //TODO: implement this
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
        //TODO: implement this
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
        //TODO: implement this
    }

    /*
    The final call you receive before your activity is destroyed. This can happen either because
    the activity is finishing (someone called finish() on it, or because the system is temporarily
    destroying this instance of the activity to save space. You can distinguish between these two
    scenarios with the isFinishing() method.
     */
    @Override
    protected void onDestroy() {
        //TODO: implement this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ASR's methods */

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {

    }

    @Override
    public void processAsrReadyForSpeech() {

    }

    @Override
    public void processAsrError(int errorCode) {

    }
}
