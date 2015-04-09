package uw.hcrlab.kubi;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
public class Robot implements IKubiManagerDelegate {
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

        //Setup the Robot instance with the new face
        setup(face, context);
    }

    /**
     * Gets the singleton instance of the Robot object. Note that after calling this method, the
     * robot.start() method must be called, or the RobotFace will never be drawn.
     *
     * @param face A RobotFace view that can be drawn to
     * @param context The current activity
     * @return The Robot singleton
     */
    public static Robot getInstance(RobotFace face, Context context) {
        if (robotInstance == null) {
            //Create the singleton instance
            robotInstance = new Robot(face, context);

        } else {
            //Shutdown resources tied to the previous robot face to allow them to be recreated
            robotInstance.shutdown();
            robotInstance.tts.shutdown();

            //Setup the Robot instance with the new face
            robotInstance.setup(face, context);
        }

        return robotInstance;
    }

    /**
     * Handles the setup actions which must occur every time a new face is passed in
     * @param face The RobotFace view for the current Activity
     * @param context The current activity
     */
    private void setup(RobotFace face, Context context) {
        robotFace = face;
        robotFace.setOnTouchListener(faceListener);

        tts = TTS.getInstance(context);
        thread = new MainThread(robotFace, kubiManager);
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

    private View.OnTouchListener faceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };
}
