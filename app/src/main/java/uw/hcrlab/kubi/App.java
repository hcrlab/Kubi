package uw.hcrlab.kubi;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import sandra.libs.vpa.vpalib.Bot;

/**
 * Created by Alexander on 4/9/2015.
 */
public class App extends Application implements Firebase.AuthResultHandler {
    public static String TAG = App.class.getSimpleName();

    private static Context mContext;
    private static Boolean mIsWizardMode = true;
    private static Firebase fb;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Firebase.setAndroidContext(this);

        fb = new Firebase("https://hcrkubi.firebaseio.com");
        fb.authWithPassword("hcr@cs.uw.edu", "hcrpass", this);
    }

    public static Context getContext(){
        return mContext;
    }

    public static Boolean InWizardMode() {
        return mIsWizardMode;
    }

    public static Firebase getFirebase() {
        return fb;
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
