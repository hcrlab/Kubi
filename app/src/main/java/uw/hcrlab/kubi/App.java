package uw.hcrlab.kubi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
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
import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.HashMap;

import uw.hcrlab.kubi.lesson.ResultsDisplayHelper;
import uw.hcrlab.kubi.robot.Robot;

/**
 * Created by Alexander on 4/9/2015.
 */
public class App extends Application implements Firebase.AuthResultHandler, IKubiManagerDelegate {
    public static String TAG = App.class.getSimpleName();

    public static final int DEVICE_SETUP_CODE = 1;

    private static Context mContext;
    private static Boolean mIsWizardMode = true;
    private static Firebase fb;
    private static String deviceID;
    private static String deviceName;
    private static String participant;
    private static boolean isConnected = false;

    private HttpProxyCacheServer proxy;

    private static HashMap<String, String> mAudioURLs;

    private KubiManager kubiManager;
    Handler connectionHandler = new Handler();
    private int numAttempts = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        mAudioURLs = new HashMap<>();

        Firebase.setAndroidContext(this);
        fb = new Firebase("https://hcrkubi.firebaseio.com");
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        initImageLoader(getApplicationContext());

        ResultsDisplayHelper.init(this);

        connectToKubi();
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
        if(isConnected) {
            return;
        }

        isConnected = true;

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
        if(!isConnected) {
            return;
        }

        isConnected = false;

        fb.child("devices").child(deviceID).child("connected").setValue(false);
    }

    public static void setParticipant(String p) {
        participant = p;
    }

    public static Context getContext(){
        return mContext;
    }

    public static Boolean InWizardMode() {
        return mIsWizardMode;
    }

    public static Firebase getFirebase() {
        return fb.child("devices").child(deviceID).child("participants").child(participant);
    }

    public static void getParticipants(ValueEventListener listener) {
        fb.child("participants").addListenerForSingleValueEvent(listener);
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

    public void loadAudio(String language) {
        fb.child("audio").child(language).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if(snap.hasChildren()) {
                    mAudioURLs.clear();

                    for(DataSnapshot audio : snap.getChildren()) {
                        Log.d(TAG, "Got audio URL: " + audio.getValue(String.class));
                        if(!mAudioURLs.containsKey(audio.getKey())) {
                            mAudioURLs.put(audio.getKey(), (String) audio.getValue());
                        }
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

    public void connectToKubi() {
        if(kubiManager == null) {
            kubiManager = new KubiManager(this, true);
            kubiManager.findAllKubis();
        }
    }

    public void disconnectFromKubi() {
        if(kubiManager != null) {
            kubiManager.disconnect();
            kubiManager = null;
        }
    }

    public KubiManager getKubiManager() {
        return kubiManager;
    }

    private Runnable retry = new Runnable() {
        @Override
        public void run() {
            if(numAttempts < 9) {
                numAttempts += 1;
                Robot.replaceCurrentToast("Attempt " + (numAttempts + 1) + " to connect to kubi base...");
                kubiManager.findAllKubis();
            } else {
                connectionHandler.removeCallbacks(retry);
                Robot.replaceCurrentToast("Max attempts exceeded. Could not connect to a Kubi robot!");
                Log.d(TAG, "Could not connect to a Kubi!");
            }
        }
    };

    /*
    Handles retry logic for connecting to Kubi via bluetooth.
    If we're within the time limit for retrying, wait the right amount of time then retry.
    Callbacks detecting a failure to connect should call this method directly.
    */
    private void attemptKubiConnect() {
        if(numAttempts > 9 || kubiManager.getKubi() != null) {
            Robot.replaceCurrentToast("Kubi already connected!");
            return;
        }

        connectionHandler.removeCallbacks(retry);
        connectionHandler.postDelayed(retry, 3000);
    }

    /* IKubiManagerDelegate methods */

    @Override
    public void kubiDeviceFound(KubiManager manager, KubiSearchResult result) {
        Log.i(TAG, "A kubi device was found");
        connectionHandler.removeCallbacks(retry);
        // Attempt to connect to the kubi
        manager.connectToKubi(result);
    }

    @Override
    public void kubiManagerFailed(KubiManager manager, int reason) {
        Log.i(TAG, "Kubi Manager Failed: " + reason);
        attemptKubiConnect();  // engage retry logic
    }

    @Override
    public void kubiManagerStatusChanged(KubiManager manager, int oldStatus, int newStatus) {
        // When the Kubi has successfully connected, nod as a sign of success
        if (newStatus == KubiManager.STATUS_CONNECTED && oldStatus == KubiManager.STATUS_CONNECTING) {
            connectionHandler.removeCallbacks(retry);

            Kubi kubi = manager.getKubi();
            kubi.performGesture(Kubi.GESTURE_NOD);

            Robot.replaceCurrentToast("Successfully connected to Kubi base");
        }
    }

    @Override
    public void kubiScanComplete(KubiManager manager, ArrayList<KubiSearchResult> result) {
        Log.i(TAG, "Kubi scan completed");
        Log.i(TAG, "Size of result is " + result.size());
        if(result.size() > 0) {
            connectionHandler.removeCallbacks(retry);
            manager.stopFinding();
            // Attempt to connect to the kubi
            manager.connectToKubi(result.get(0));
        } else {
            Log.e(TAG, "No Kubi's detected... Retrying scan...");
            attemptKubiConnect();  // engage retry logic
        }
    }
}
