package uw.hcrlab.kubi.robot;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;

import uw.hcrlab.kubi.App;

/**
 * Created by Alexander on 5/8/2016.
 */
public class Body implements IKubiManagerDelegate {
    public static String TAG = Body.class.getSimpleName();

    private KubiManager kubiManager;
    Handler connectionHandler = new Handler();
    private int numAttempts = 0;

    public Body() {
        kubiManager = new KubiManager(this, true);
        kubiManager.findAllKubis();
    }

    public void cleanup() {
        kubiManager.disconnect();
    }

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

        connectionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(numAttempts < 9) {
                    numAttempts += 1;
                    Robot.replaceCurrentToast("Attempt " + (numAttempts + 1) + " to connect to kubi base...");
                    kubiManager.findAllKubis();
                } else {
                    Robot.replaceCurrentToast("Max attempts exceeded. Could not connect to a Kubi robot!");
                    Log.d(TAG, "Could not connect to a Kubi!");
                }
            }
        }, 2000);
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
        Log.i(TAG, "Kubi Manager Failed: " + reason);
        attemptKubiConnect();  // engage retry logic
    }

    @Override
    public void kubiManagerStatusChanged(KubiManager manager, int oldStatus, int newStatus) {
        // When the Kubi has successfully connected, nod as a sign of success
        if (newStatus == KubiManager.STATUS_CONNECTED && oldStatus == KubiManager.STATUS_CONNECTING) {
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
            manager.stopFinding();
            // Attempt to connect to the kubi
            manager.connectToKubi(result.get(0));
        } else {
            attemptKubiConnect();  // engage retry logic
        }
    }

    public void moveTo(int x, int y) {
        Kubi kubi = kubiManager.getKubi();

        if(kubi != null) {
            kubi.moveTo(x, y);
        }
    }

    public void move(Action action) {
        Kubi kubi = kubiManager.getKubi();

        if(kubi != null) {
            Log.i(TAG, "perform " + action.toString());

            if (kubiManager.getKubi() != null) {
                switch (action) {
                    case SLEEP:
                        kubiManager.getKubi().performGesture(Kubi.GESTURE_FACE_DOWN);
                        break;
                    case WAKE:
                        kubiManager.getKubi().performGesture(Kubi.GESTURE_FACE_UP);
                        break;
                    case LOOK_AROUND:
                        kubiManager.getKubi().performGesture(Kubi.GESTURE_RANDOM);
                        break;
                    case NOD:
                        kubiManager.getKubi().performGesture(Kubi.GESTURE_NOD);
                        break;
                    case SHAKE:
                        kubiManager.getKubi().performGesture(Kubi.GESTURE_SHAKE);
                        break;
                    case FACE_FORWARD:
                        kubiManager.getKubi().moveTo(0, 0);
                        break;
                    case YAY_GESTURE:
                        yay();
                        break;
                    case OOPS_GESTURE:
                        oops();
                        break;
                    case EXCELLENT_GESTURE:
                        excellent();
                        break;
                }
            }
        }
    }

    private void yay() {
        final Kubi kubi = kubiManager.getKubi();

        if(kubi == null) return;

        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(0, 20, 1.0f, false);
            }
        }, 200);
        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(0, 0, 1.0f, false);
            }
        }, 800);
    }

    private void oops() {
        final Kubi kubi = kubiManager.getKubi();

        if(kubi == null) return;

        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(0, -15, 1.0f, false);
            }
        }, 200);
        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(0, 0, 1.0f, false);
            }
        }, 600);
    }

    private void excellent() {
        final Kubi kubi = kubiManager.getKubi();

        if(kubi == null) return;

        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(10, 20, 1.0f, false);
            }
        }, 200);
        kubi.act(new Runnable() {
            @Override
            public void run() {
                if(kubi != null) kubi.moveTo(0, 0, 1.0f, false);
            }
        }, 600);
        kubi.act(new Runnable() {
            @Override
            public void run() {
                kubi.moveTo(-10, 20, 1.0f, false);
            }
        }, 1000);
        kubi.act(new Runnable() {
            @Override
            public void run() {
                kubi.moveTo(0, 0, 1.0f, false);
            }
        }, 1400);
    }
}
