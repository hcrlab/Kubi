package uw.hcrlab.kubi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.screen.RobotFace;

public class DebugActivity extends Activity {
    private static String TAG = MainActivity.class.getSimpleName();

    /* Activity's Properties */
    private Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);

        WebView eye_area = (WebView) findViewById(R.id.webview_eyes);
        eye_area.loadUrl("file:///android_asset/eyes.html");
    }

}

