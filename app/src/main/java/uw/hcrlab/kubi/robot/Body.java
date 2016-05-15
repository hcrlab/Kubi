package uw.hcrlab.kubi.robot;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.Random;

import uw.hcrlab.kubi.App;

/**
 * Created by Alexander on 5/8/2016.
 */
public class Body {
    public static String TAG = Body.class.getSimpleName();

    private static Body instance;

    public enum Action {
        SLEEP,
        WAKE,
        LOOK_AROUND,
        NOD,
        SHAKE,
        FACE_FORWARD,
        YAY_GESTURE,
        OOPS_GESTURE,
        EXCELLENT_GESTURE
    }

    private KubiManager kubiManager;

    private Handler handler = new Handler();

    private Random random = new Random();

    /**
     * Private constructor for the Body class. This class implements the Singleton pattern - preventing
     * the creation of multiple instances of Body objects.
     */
    private Body(App app) {
        kubiManager = app.getKubiManager();
    }

    public static Body getInstance(App app) {
        if(instance == null) {
            instance = new Body(app);
        }

        return instance;
    }

    public void cleanup() {
        // App should handle this
        //kubiManager.disconnect();
    }

    public void moveTo(float x, float y) {
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
                handler.removeCallbacks(subtleMovement);

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
                        kubiManager.getKubi().moveTo(5.0f, 0);
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

                startSubtleMovement();
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
    /**
     * Do a random move from the given collection of choices.
     *
     * @param choices - collection to choose a random action from
     * @param dontDo - if an action matching this is selected, pick again
     * @return - the action that was taken
     */
    public Action doRandomMove(Action[] choices, Action dontDo) {
        Action selection;

        do {
            selection = choices[random.nextInt(choices.length)];
        } while (selection.equals(dontDo));

        move(selection);

        return selection;
    }

    private Runnable subtleMovement = new Runnable() {
        @Override
        public void run() {
            float pan = random.nextFloat() * 10 - 5;
            float tilt = random.nextFloat() * 10 - 5;

            moveTo(pan, tilt);

            handler.postDelayed(subtleMovement, (int)(2500 + random.nextFloat() * 3000));
        }
    };

    public void startSubtleMovement() {
        handler.postDelayed(subtleMovement, 6000);
    }
}
