package uw.hcrlab.kubi;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;

import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.screen.RobotFace;

/**
 * Created by kimyen on 4/7/15.
 */
public class Robot implements IKubiManagerDelegate {
    public static String TAG = Robot.class.getSimpleName();

    private static Robot robotInstance = null;

    private RobotThread thread;
    private RobotFace robotFace;
    private KubiManager kubiManager;

    //TODO: only public temporarily
    public TTS tts;

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
     * robot.startup() method must be called, or the RobotFace will never be drawn.
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
     * Handles the setup actions which must occur every time a new face is passed in.
     *
     * @param face The RobotFace view for the current Activity
     * @param context The current activity
     */
    private void setup(RobotFace face, Context context) {
        robotFace = face;
        robotFace.setOnTouchListener(faceListener);

        tts = TTS.getInstance(context);

        //TODO: Move the pandora bot over here
        //bot = new Bot(this, PANDORA_BOT_ID, this.tts);

        thread = new RobotThread(robotFace, kubiManager);
    }

    /**
     * Starts the robot by starting the RobotThread if it has not already been started.
     */
    public void startup() {
        if (thread.isAlive()) {
            Log.i(TAG, "Robot already started ...");
            return;
        }

        thread.start();
    }

    /**
     * Stops the RobotThread
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down Main Thread ...");

        while (true) {
            try {
                thread.join();
                return;
            } catch (InterruptedException e) {
                Log.e(TAG, "Robot thread didn't join. Trying again.");
            }
        }
    }

    /**
     * Generates text-to-speech for the provided message.
     *
     * @param msg Message to speak
     */
    public void say(String msg) {
        try {
            tts.speak(msg, "EN");
        } catch (Exception e) {
            Log.e(TAG, "English not available for TTS, default language used instead");
        }
    }

    /**
     * Touch listener for the RobotFace
     */
    private View.OnTouchListener faceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG, "RobotFace touch occured!");
            return false;
        }
    };

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
}
