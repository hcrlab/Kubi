package uw.hcrlab.kubi.robot;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.screen.RobotFace;
import uw.hcrlab.kubi.speech.SpeechUtils;
import uw.hcrlab.kubi.wizard.CommandHandler;

/**
 * Created by kimyen on 4/7/15.
 */
public class Robot extends ASR implements IKubiManagerDelegate {
    public static String TAG = Robot.class.getSimpleName();

    private static Robot robotInstance = null;

    private String mDefaultLanguage = "EN";

    private RobotThread thread;
    private RobotFace robotFace;
    private KubiManager kubiManager;

    private View leftCard;
    private View rightCard;

    // The ID of the bot to use for the chatbot, can be changed
    // you can also make a new bot by creating an account in pandorabots.com and making a new chatbot robot
    private String PANDORA_BOT_ID = "b9581e5f6e343f72";
    private Bot bot;
    private TTS tts;

    private Context currentCxt;

    private CommandHandler responses;
    private CommandHandler lessons;
    private CommandHandler questions;
    private CommandHandler quizzes;

    /**
     *  This class implements the Singleton pattern. Note that only the tts engine and RobotFace
     *  are updated when getInstance() is called.
     */
    private Robot(RobotFace face, final Context context){
        //Only one copy of this ever
        kubiManager = new KubiManager(this, true);
        kubiManager.findAllKubis();

        createRecognizer(App.getContext());

        setup(face, context);

        if(App.InWizardMode()) {
            responses = new CommandHandler("response");
            lessons = new CommandHandler("lesson");
            questions = new CommandHandler("question");
            quizzes = new CommandHandler("quiz");
        }
    }

    public void cleanup() {
        kubiManager.disconnect();
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
     * This version will not create a new robot or setup the current robot, it will simply provide
     * the current instance
     *
     * @return The current robot instance
     */
    public static Robot getInstance() {
        return robotInstance;
    }

    /**
     * Handles the setup actions which must occur every time a new face is passed in.
     *
     * @param face The RobotFace view for the current Activity
     * @param context The current activity
     */
    private void setup(RobotFace face, Context context) {
        currentCxt = context;

        robotFace = face;
        robotFace.setOnTouchListener(faceListener);

        tts = TTS.getInstance(context);
        bot = new Bot((Activity)context, PANDORA_BOT_ID, tts);
    }

    /**
     * Starts the robot by starting the RobotThread if it has not already been started.
     */
    public void startup() {
        if (thread != null) {
            Log.i(TAG, "Robot already started ...");
            return;
        }

        thread = new RobotThread(robotFace, kubiManager);
        thread.start();

        if(App.InWizardMode()) {
            responses.Listen();
            lessons.Listen();
            questions.Listen();
            quizzes.Listen();
        }
    }

    /**
     * Stops the RobotThread
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down Main Thread ...");

        while (true) {
            try {
                if(App.InWizardMode()) {
                    responses.Stop();
                    lessons.Stop();
                    questions.Stop();
                    quizzes.Stop();
                }

                if(thread != null) {
                    thread.setRunning(false);
                    thread.join();
                    thread = null;
                }

                return;
            } catch (InterruptedException e) {
                Log.e(TAG, "Robot thread didn't join. Trying again.");
            }
        }
    }

    /**
     * Generates text-to-Speech.java for the provided message.
     *
     * @param msg Message to speak
     */
    public void say(String msg, String language) {
        try {
            tts.speak(msg, language);
        } catch (Exception e) {
            Log.e(TAG, language + " not available for TTS, default language used instead");
        }
    }

    public void say(String msg, String language, int speed) {
        try {
            Log.i(TAG, "Say: " + msg);
            tts.setRate(speed / 100.0f);
            tts.speak(msg, language);
        } catch (Exception e) {
            Log.e(TAG, language + " not available for TTS, default language used instead");
        }
    }

    public void listen() {
        Log.i(TAG, "listening");
        try {
            super.listen(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH, 1);
        } catch (Exception ex) {
            Toast.makeText(currentCxt, "ASR could not be started: invalid params", Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());
        }
    }

    public void act(FaceAction faceAction) {
        thread.act(faceAction);
    }

    public void perform(Action action) {
        thread.perform(action);
    }

    /**
     * Touch listener for the RobotFace
     */
    private View.OnTouchListener faceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG, "RobotFace touch occurred!");
            act(FaceAction.WAKE);
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
        Log.i(TAG, "Kubi Manager Failed: " + reason);
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

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        String speechInput = nBestList.get(0);
        Log.i(TAG, "Speech input: " + speechInput);

        String response = SpeechUtils.getResponse(speechInput);

        try {
            if(response != null){
                //We have a preprogrammed Response, so use it
                Log.i(TAG, "Saying : " + response);
                say(response, mDefaultLanguage);
            }  else {
                //We don't have a preprogrammed Response, so use the bot to create a Response
                Log.i(TAG, "Default response");
                bot.initiateQuery(speechInput);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error responding to speech input: " + speechInput);
            e.printStackTrace();
        }
    }

    @Override
    public void processAsrReadyForSpeech() {
        Log.i(TAG, "Listening to user's speech.");
        Toast.makeText(currentCxt, "I'm listening.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void processAsrError(int errorCode) {
        //super.stopListening();
        super.cancel();

        String errorMessage = SpeechUtils.getErrorMessage(errorCode);

//        if (errorMessage != null) {
//            say(errorMessage, mDefaultLanguage);
//        }

        // If there is an error, shows feedback to the user and writes it in the log
        Log.e(TAG, "Error: " + errorMessage);
        Toast.makeText(currentCxt, errorMessage, Toast.LENGTH_LONG).show();
    }

    public void setCards(View left, View right) {
        this.leftCard = left;
        this.rightCard = right;
    }

    public enum Hand {
        Left,
        Right
    }

    public void showCard(Hand leftOrRight, int resID, String text) {
        final View card = leftOrRight == Hand.Left ? leftCard : rightCard;

        if(card == null) return;


        TextView t = null;
        ImageView i = null;

        if(leftOrRight == Hand.Left) {
            t = (TextView)card.findViewById(R.id.leftCardText);
            i = (ImageView)card.findViewById(R.id.leftCardImage);
        } else {
            t = (TextView)card.findViewById(R.id.rightCardText);
            i = (ImageView)card.findViewById(R.id.rightCardImage);
        }

        t.setText(text);
        i.setImageResource(resID);

        if(((FrameLayout.LayoutParams)card.getLayoutParams()).bottomMargin < 0) {
            ValueAnimator anim = ValueAnimator.ofInt(-card.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)card.getLayoutParams();
                params.bottomMargin = val;
                card.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();
        }
    }

    public void hideCard(Hand leftOrRight) {
        final View card = leftOrRight == Hand.Left ? leftCard : rightCard;

        if(card == null) return;

        ValueAnimator anim = ValueAnimator.ofInt(20, -card.getHeight() - 10);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int val = (Integer) valueAnimator.getAnimatedValue();

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)card.getLayoutParams();
            params.bottomMargin = val;
            card.setLayoutParams(params);
            }
        });
        anim.setDuration(500);
        anim.start();
    }
}
