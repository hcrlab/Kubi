package uw.hcrlab.kubi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This is an empty activity which never actually renders to the screen. It is simply intended
 * to show the splash screen while waiting for the Android system to start up this application.
 */
public class SplashScreen extends Activity {

    /**
     * Starts the Setup Activity immediately
     *
     * @param savedInstanceState Saved bundle from previous creation of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
