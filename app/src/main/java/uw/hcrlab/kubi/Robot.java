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

    /* constructor */

    private Robot(Context context, AttributeSet attrs){
        this.robotFace = new RobotFace(context, attrs);
        this.kubiManager = new KubiManager(this, true);
        tts = TTS.getInstance(context);
    }

    public static Robot getInstance(Context context, AttributeSet attrs) {
        if (robotInstance == null) {
            robotInstance = new Robot(context, attrs);
        } else {
            robotInstance.setContext(context, attrs);
        }
        return robotInstance;
    }

    public void setContext(Context context, AttributeSet attrs) {
        tts = TTS.getInstance(context);
        robotInstance = new Robot(context, attrs);
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
