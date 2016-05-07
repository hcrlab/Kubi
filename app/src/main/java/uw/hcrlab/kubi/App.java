package uw.hcrlab.kubi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;

/**
 * Created by Alexander on 4/9/2015.
 */
public class App extends Application implements Firebase.AuthResultHandler {
    public static String TAG = App.class.getSimpleName();

    private static Context mContext;
    private static Boolean mIsWizardMode = true;
    private static Firebase fb;
    private static String deviceID;
    private static String deviceName;

    private HttpProxyCacheServer proxy;

    private static HashMap<String, String> mAudioURLs;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Firebase.setAndroidContext(this);

        mAudioURLs = new HashMap<>();

        fb = new Firebase("https://hcrkubi.firebaseio.com");

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        initImageLoader(getApplicationContext());
    }

    public void saveCredentials(String username, String password, String name) {
        SharedPreferences pref = this.getSharedPreferences("uw.hcrlab.kubi.settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("device_name", name);

        editor.commit();
    }

    public boolean authenticate(Firebase.AuthResultHandler callback) {
        if(fb.getAuth() != null) {
            loadAudio();
            return true;
        }

        // Get stored values
        SharedPreferences pref = this.getSharedPreferences("uw.hcrlab.kubi.settings", MODE_PRIVATE);
        String username = pref.getString("username", null);
        String password = pref.getString("password", null);
        deviceName = pref.getString("device_name", "Default Device Name");

        if(username != null && password != null && deviceName != null) {
            if(callback == null) {
                fb.authWithPassword(username, password, this);
            } else {
                fb.authWithPassword(username, password, callback);
            }
            return true;
        }

        return false;
    }

    public boolean authenticate() {
        return authenticate(null);
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(config);
    }

    public static void FbConnect() {
        fb.child("devices").child(deviceID).child("connected").setValue(true);

        // Check for the device name and set it if need be
        Firebase ref = fb.child("devices").child(deviceID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if(!snap.child("name").exists() && deviceName != null) {
                    snap.child("name").getRef().setValue(deviceName);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Couldn't get device info!");
            }
        });
    }

    public static void FbDisconnect() {
        fb.child("devices").child(deviceID).child("connected").setValue(false);
    }

    public static Context getContext(){
        return mContext;
    }

    public static Boolean InWizardMode() {
        return mIsWizardMode;
    }

    public static Firebase getFirebase() {
        return fb.child("devices").child(deviceID);
    }

    @Override
    public void onAuthenticated(AuthData authData) {
        Log.i(TAG, "Authenticated with Firebase!");

        loadAudio();
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        Log.e(TAG, "Firebase Authentication Error!");
        Log.e(TAG, firebaseError.toString());
    }

    private void loadAudio() {
        fb.child("audio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if(snap.hasChildren()) {
                    for(DataSnapshot audio : snap.getChildren()) {
                        Log.d(TAG, "Got audio URL: " + audio.getValue(String.class));
                        mAudioURLs.put(audio.getKey(), (String) audio.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Error getting the audio links from Firebase!");
            }
        });
    }

    public static String getAudioURL(String text) {
        if(mAudioURLs.containsKey(text)) {
            return mAudioURLs.get(text);
        }

        return null;
    }
}
