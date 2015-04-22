package uw.hcrlab.kubi;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.UUID;

import sandra.libs.vpa.vpalib.Bot;

/**
 * Created by Alexander on 4/9/2015.
 */
public class App extends Application implements Firebase.AuthResultHandler {
    public static String TAG = App.class.getSimpleName();

    private static Context mContext;
    private static Boolean mIsWizardMode = true;
    private static Firebase fb;
    private static String deviceID;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Firebase.setAndroidContext(this);

        fb = new Firebase("https://hcrkubi.firebaseio.com");
        fb.authWithPassword("hcr@cs.uw.edu", "hcrpass", this);

        deviceID = Build.MANUFACTURER + " " + Build.MODEL;;
    }

    public static void FbConnect() {
        fb.child(deviceID).child("connected").setValue(true);
    }

    public static void FbDisconnect() {
        fb.child(deviceID).child("connected").setValue(false);
    }

    public static Context getContext(){
        return mContext;
    }

    public static Boolean InWizardMode() {
        return mIsWizardMode;
    }

    public static Firebase getFirebase() {
        return fb.child(deviceID.toString());
    }

    @Override
    public void onAuthenticated(AuthData authData) {
        Log.i(TAG, "Authenticated with Firebase!");
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        Log.e(TAG, "Firebase Authentication Error!");
        Log.e(TAG, firebaseError.toString());
    }
}
