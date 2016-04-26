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
public class FaceThread extends Thread {
    /* injected from MainActivity */
    private final RobotFace robotFace;
    private final KubiManager kubiManager;

    private ConcurrentLinkedQueue<FaceAction> faceActions = new ConcurrentLinkedQueue<FaceAction>();

    /* Class variables */
    private static final String TAG = FaceThread.class.getSimpleName();
    private boolean isRunning = true;
    private boolean isAsleep = false;

    /* Idle behavior periods */
    private final long EPSILON = 100;
    private final long BLINK_TIME = 2 * 1000;

    private Random random = new Random();

    private long nextBlinkTime;

    public FaceThread(RobotFace robotFace, KubiManager kubiManager) {
        super();
        Log.i(TAG, "Initializing FaceThread ...");

        this.robotFace = robotFace;
        this.kubiManager = kubiManager;

        nextBlinkTime = getNextBlinkTime();
    }

    public void act(FaceAction faceAction) {
        faceActions.add(faceAction);
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting the main loop");

        RobotFaceUtils.showAction(robotFace, FaceAction.WAKE);

        while (isRunning) {
            try {
                synchronized (robotFace) {
                    FaceAction faceAction = faceActions.poll();

                    if(!isAsleep || faceAction == FaceAction.WAKE) {
                        if (faceAction != null) {
                            // Carry out FaceAction
                            isAsleep = (faceAction == FaceAction.SLEEP);

                            RobotFaceUtils.showAction(robotFace, faceAction);
                            nextBlinkTime = getNextBlinkTime();
                        } else if (nextBlinkTime - System.currentTimeMillis() < EPSILON) {
                            // Blink
                            RobotFaceUtils.showAction(robotFace, FaceAction.BLINK);
                            nextBlinkTime = getNextBlinkTime();
                        }
                    }

                    Thread.sleep(250, 0);
                }

            } catch (Exception e) {
                // TODO: Implment this catch block...
            }
        }
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    private long getNextBlinkTime() {
        return System.currentTimeMillis() + BLINK_TIME + random.nextInt(10) * 200;
    }
}
