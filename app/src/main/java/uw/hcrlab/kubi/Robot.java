package uw.hcrlab.kubi;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import sandra.libs.tts.TTS;
import trash.KubiCallback;
import uw.hcrlab.kubi.screen.RobotFace;

/**
 * Created by kimyen on 4/7/15.
 */
public class Robot implements IKubiManagerDelegate, Observer {
    public static String TAG = Robot.class.getSimpleName();

    private static Robot robotInstance = null;
    private RobotFace robotFace;
    private KubiManager kubiManager;
    private TTS tts;

    private MainThread thread;

    /**
     *  This class implements the Singleton pattern. Note that only the tts engine and RobotFace
     *  are updated when getInstance() is called.
     */

    private Robot(RobotFace face, Context context){
        //Only one copy of this ever
        kubiManager = new KubiManager(this, true);

        //These will update every time getInstance is called
        robotFace = face;
        tts = TTS.getInstance(context);
        thread = new MainThread(robotFace, kubiManager);
    }

    public static Robot getInstance(RobotFace face, Context context) {
        if (robotInstance == null) {
            //Create the singleton instance
            robotInstance = new Robot(face, context);

        } else {
            //Update the tts engine with the new context, and update the reference to the current face view
            robotInstance.tts.shutdown(); //Required to update the context
            robotInstance.tts = TTS.getInstance(context);
            robotInstance.robotFace = face;

            //Note that we want to keep the same KubiManager, so we don't update it
        }

        return robotInstance;
    }

    public void start() {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        thread = new MainThread(robotFace, kubiManager);
        thread.start();
    }

    public void shutdown() {
        Log.i(TAG, "Shutting down Main Thread ...");
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    public void say(String msg) {
        try {
            tts.speak(msg, "EN");
        } catch (Exception e) {
            Log.e(TAG, "English not available for TTS, default language used instead");
        }
    }

    /* IKubiManagerDelegate methods */

    @Override
    public void kubiDeviceFound(KubiManager manager, KubiSearchResult result) {
        Log.i(TAG, "A kubi device was found");
        // Attempt to connect to the kubi
        manager.connectToKubi(result);
    }

    @Override
    public void kubiManagerFailed(KubiManager manager, int reason) {
        Log.i(TAG, "Failed. Reason: " + reason);
        if (reason == KubiManager.FAIL_CONNECTION_LOST || reason == KubiManager.FAIL_DISTANCE) {
            manager.findAllKubis();
        }
    }

    @Override
    public void kubiManagerStatusChanged(KubiManager manager, int oldStatus, int newStatus) {
        // When the Kubi has successfully connected, nod as a sign of success
        if (newStatus == KubiManager.STATUS_CONNECTED && oldStatus == KubiManager.STATUS_CONNECTING) {
            Kubi kubi = manager.getKubi();
            kubi.performGesture(Kubi.GESTURE_NOD);
        }
    }

    @Override
    public void kubiScanComplete(KubiManager manager, ArrayList<KubiSearchResult> result) {
        Log.i(TAG, "Kubi scan completed");
        Log.i(TAG, "Size of result is " + result.size());
        if(result.size() > 0) {
            manager.stopFinding();
            // Attempt to connect to the kubi
            manager.connectToKubi(result.get(0));
        }
    }

    /* update on onTouchEvent from RobotFace */

    @Override
    public void update(Observable observable, Object o) {

    }
}
