package uw.hcrlab.kubi;

import android.util.Log;

import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;

import java.util.Random;

import uw.hcrlab.kubi.screen.OldRobotFace;

/**
 * Created by kimyen on 4/5/15.
 */
public class MainThread extends Thread {
    /* injected from MainActivity */
    private final OldRobotFace robotFace;
    private final KubiManager kubiManager;
    private final MainActivity activity;

    /* Class variables */
    private static final String TAG = MainThread.class.getSimpleName();
    private boolean isRunning;

    /* Idle behavior periods */

    // the different between real time and calculated time to perform an action
    private final long EPSILON = 100;
    // sleep after 11 minutes
    private final long SLEEP_TIME = 11 * 60 * 1000;
    // blink after 5 seconds
    private final long BLINK_TIME = 5 * 1000;
    // look around every 3 minutes
    private long BORING_TIME = 3 * 60 * 1000;

    private Random random = new Random();

    private long nextSleepTime = System.currentTimeMillis() + SLEEP_TIME + random.nextInt(10) * 60 * 1000;
    private long nextBlinkTime = System.currentTimeMillis() + BLINK_TIME + random.nextInt(10) * 1000;
    private long nextBoringTime = System.currentTimeMillis() + BORING_TIME + random.nextInt(60) * 1000;


    public MainThread(OldRobotFace robotFace, KubiManager kubiManager, MainActivity mainActivity) {
        super();
        this.robotFace = robotFace;
        this.kubiManager = kubiManager;
        this.activity = mainActivity;
        this.isRunning = true;
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting the main loop");

        robotFace.showAction(OldRobotFace.Action.WAKE);
        robotFace.setEmotion(OldRobotFace.Emotion.NORMAL);

        while (isRunning) {
            try {
                synchronized (robotFace) {
                    if (Math.abs(System.currentTimeMillis() - nextSleepTime) < EPSILON) {
                        Log.i(TAG, "Sleep at " + System.currentTimeMillis());
                        robotFace.showAction(OldRobotFace.Action.SLEEP);
                        kubiFaceDown();
                        nextSleepTime = 0;
                        isRunning = false;
                    }

                    if (Math.abs(System.currentTimeMillis() - nextBlinkTime) < EPSILON)  {
                        Log.i(TAG, "Blink at " + System.currentTimeMillis());
                        robotFace.showAction(OldRobotFace.Action.BLINK);
                        nextBlinkTime = System.currentTimeMillis() + BLINK_TIME + random.nextInt(10) * 1000;
                    }
                    if (Math.abs(System.currentTimeMillis() - nextBoringTime) < EPSILON) {
                        Log.i(TAG, "Look around at " + System.currentTimeMillis());
                        kubiLookAround();
                        nextBoringTime = System.currentTimeMillis() + BORING_TIME + random.nextInt(60) * 1000;
                    }
                }

            } catch (Exception e) {}
        }
    }

    private void kubiLookAround() {
        try {
            kubiManager.getKubi().performGesture(Kubi.GESTURE_RANDOM);
        } catch (Throwable e) {}
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
        if (!running) {
            robotFace.showAction(OldRobotFace.Action.SLEEP);
            robotFace.setEmotion(OldRobotFace.Emotion.SLEEP);
            kubiFaceDown();
        }
    }

    private void kubiFaceDown() {
        try {
            kubiManager.getKubi().performGesture(Kubi.GESTURE_FACE_DOWN);
        } catch (Throwable e) {
            Log.e(TAG, "Cannot show gesture : GESTURE_FACE_DOWN");
        }
    }
}
