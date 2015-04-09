package uw.hcrlab.kubi;

import android.util.Log;

import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;

import java.util.Random;

import trash.OldRobotFace;
import uw.hcrlab.kubi.screen.RobotFace;
import uw.hcrlab.kubi.screen.RobotFaceUtils;
import uw.hcrlab.kubi.screen.Action;

/**
 * Created by kimyen on 4/5/15.
 */
public class MainThread extends Thread {
    /* injected from MainActivity */
    private final RobotFace robotFace;
    private final KubiManager kubiManager;

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

    private long nextSleepTime;
    private long nextBlinkTime;
    private long nextBoringTime;

    public MainThread(RobotFace robotFace, KubiManager kubiManager) {
        super();
        Log.i(TAG, "Initializing MainThread ...");
        this.robotFace = robotFace;
        this.kubiManager = kubiManager;
        this.isRunning = true;
        nextSleepTime = getNextSleepTime();
        nextBlinkTime = getNextBlinkTime();
        nextBoringTime = getNextBoringTime();
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting the main loop");

        RobotFaceUtils.showAction(robotFace, Action.WAKE);

        while (isRunning) {
            try {
                synchronized (robotFace) {
                    if (Math.abs(System.currentTimeMillis() - nextSleepTime) < EPSILON) {
                        Log.i(TAG, "Sleep at " + System.currentTimeMillis());
                        RobotFaceUtils.showAction(robotFace, Action.SLEEP);
                        kubiFaceDown();
                        nextSleepTime = 0;
                        isRunning = false;
                    }

                    if (Math.abs(System.currentTimeMillis() - nextBlinkTime) < EPSILON)  {
                        Log.i(TAG, "Blink at " + System.currentTimeMillis());
                        RobotFaceUtils.showAction(robotFace, Action.BLINK);
                        nextBlinkTime = getNextBlinkTime();
                    }
                    if (Math.abs(System.currentTimeMillis() - nextBoringTime) < EPSILON) {
                        Log.i(TAG, "Look around at " + System.currentTimeMillis());
                        kubiLookAround();
                        nextBoringTime = getNextBoringTime();
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
            RobotFaceUtils.showAction(robotFace, Action.SLEEP);
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

    private long getNextSleepTime() {
        return System.currentTimeMillis() + SLEEP_TIME + random.nextInt(10) * 60 * 1000;
    }

    private long getNextBlinkTime() {
        return System.currentTimeMillis() + BLINK_TIME + random.nextInt(10) * 1000;
    }

    private long getNextBoringTime() {
        return System.currentTimeMillis() + BORING_TIME + random.nextInt(60) * 1000;
    }
}
