package uw.hcrlab.kubi.robot;

import android.util.Log;

import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import uw.hcrlab.kubi.screen.RobotFace;
import uw.hcrlab.kubi.screen.RobotFaceUtils;

/**
 * Created by kimyen on 4/5/15.
 */
public class RobotThread extends Thread {
    /* injected from MainActivity */
    private final RobotFace robotFace;
    private final KubiManager kubiManager;

    private ConcurrentLinkedQueue<FaceAction> faceActions = new ConcurrentLinkedQueue<FaceAction>();
    private ConcurrentLinkedQueue<Action> actions = new ConcurrentLinkedQueue<Action>();

    /* Class variables */
    private static final String TAG = RobotThread.class.getSimpleName();
    private boolean isRunning;
    private boolean isAsleep;

    /* Idle behavior periods */

    // the different between real time and calculated time to perform an action
    private final long EPSILON = 100;
    // sleep after 10 minutes
    private final long SLEEP_TIME = 10 * 60 * 1000;
    // blink after 5 seconds
    private final long BLINK_TIME = 5 * 1000;
    // look around every 3 minutes
    private long BORING_TIME = 3 * 60 * 1000;

    private Random random = new Random();

    private long nextSleepTime;
    private long nextBlinkTime;
    private long nextBoringTime;

    public RobotThread(RobotFace robotFace, KubiManager kubiManager) {
        super();
        Log.i(TAG, "Initializing RobotThread ...");

        this.robotFace = robotFace;
        this.kubiManager = kubiManager;
        this.isRunning = true;
        this.isAsleep = false;

        nextSleepTime = getNextSleepTime();
        nextBlinkTime = getNextBlinkTime();
        nextBoringTime = getNextBoringTime();
    }

    public void act(FaceAction faceAction) {
        faceActions.add(faceAction);
    }

    public void perform(Action action) { actions.add(action); }

    @Override
    public void run() {
        Log.d(TAG, "Starting the main loop");

        RobotFaceUtils.showAction(robotFace, FaceAction.WAKE);

        while (isRunning) {
            try {
                synchronized (robotFace) {
                    if (!isAsleep && Math.abs(System.currentTimeMillis() - nextSleepTime) < EPSILON) {
                        Log.i(TAG, "Sleep at " + System.currentTimeMillis());
                        RobotFaceUtils.showAction(robotFace, FaceAction.SLEEP);
                        kubiFaceDown();
                        isAsleep = true;
                    }

                    FaceAction faceAction = faceActions.poll();
                    Action action = actions.poll();

                    if(faceAction != null || action != null) {
                        if (faceAction != null)
                            Log.d(TAG, faceAction.toString() + " at " + System.currentTimeMillis());
                        if (action != null)
                            Log.d(TAG, action.toString() + " at " + System.currentTimeMillis());

                        //Ignore !Asleep and action == WAKE
                        if(isAsleep || faceAction != FaceAction.WAKE) {
                            if(isAsleep && faceAction != FaceAction.WAKE) {
                                RobotFaceUtils.showAction(robotFace, FaceAction.WAKE);
                            }

                            isAsleep = false;

                            RobotFaceUtils.showAction(robotFace, faceAction);
                        }

                        performAction(action);

                        nextBlinkTime = getNextBlinkTime();
                        nextSleepTime = getNextSleepTime();
                        nextBoringTime = getNextBoringTime();
                    } else if (!isAsleep){
                        if (Math.abs(System.currentTimeMillis() - nextBlinkTime) < EPSILON) {
                            Log.i(TAG, "Blink at " + System.currentTimeMillis());
                            RobotFaceUtils.showAction(robotFace, FaceAction.BLINK);
                            nextBlinkTime = getNextBlinkTime();
                        }
                        if (Math.abs(System.currentTimeMillis() - nextBoringTime) < EPSILON) {
                            Log.i(TAG, "Look around at " + System.currentTimeMillis());
                            kubiLookAround();
                            nextBoringTime = getNextBoringTime();
                        }
                    }

                    Thread.sleep(250, 0);
                }

            } catch (Exception e) {}
        }
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
        /*if (!running) {
            RobotFaceUtils.showAction(robotFace, Action.BLINK);
            kubiFaceDown();
        }*/
    }

    private long getNextSleepTime() {
        return System.currentTimeMillis() + SLEEP_TIME;
    }

    private long getNextBlinkTime() {
        return System.currentTimeMillis() + BLINK_TIME + random.nextInt(10) * 1000;
    }

    private long getNextBoringTime() {
        return System.currentTimeMillis() + BORING_TIME + random.nextInt(60) * 1000;
    }

    private void performAction(Action action) {
        switch (action) {
            case SLEEP: kubiFaceDown(); break;
            case WAKE:  kubiFaceUp(); break;
            case LOOK_AROUND: kubiLookAround(); break;
            case NOD: kubiManager.getKubi().performGesture(Kubi.GESTURE_NOD); break;
            case SHAKE: kubiManager.getKubi().performGesture(Kubi.GESTURE_SHAKE); break;
        }
    }

    private void kubiFaceDown() {
        try {
            kubiManager.getKubi().performGesture(Kubi.GESTURE_FACE_DOWN);
        } catch (Throwable e) {
            Log.e(TAG, "Cannot show gesture : GESTURE_FACE_DOWN");
        }
    }

    private void kubiFaceUp() {
        try {
            kubiManager.getKubi().performGesture(Kubi.GESTURE_FACE_UP);
        } catch (Throwable e) {
            Log.e(TAG, "Cannot show gesture : GESTURE_FACE_UP");
        }
    }

    private void kubiLookAround() {
        try {
            kubiManager.getKubi().performGesture(Kubi.GESTURE_RANDOM);
        } catch (Throwable e) {
            Log.e(TAG, "Cannot show gesture : GESTURE_RANDOM");
        }
    }
}
