package uw.hcrlab.kubi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import uw.hcrlab.kubi.robot.PermissionsManager;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
