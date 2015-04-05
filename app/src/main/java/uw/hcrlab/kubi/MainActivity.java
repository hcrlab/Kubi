package uw.hcrlab.kubi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.revolverobotics.kubiapi.KubiManager;

import java.util.ArrayList;
import java.util.Map;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.view.OldRobotFace;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
