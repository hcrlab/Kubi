package uw.hcrlab.kubi.robot;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
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

    private FaceThread thread;
    private RobotFace robotFace;
    private KubiManager kubiManager;

    private boolean isAsleep = false;
    private boolean isBored = false;

    private View leftCard;
    private View rightCard;
    private View mPromptContainer;

    private Boolean leftIsShowing = false;
    private Boolean rightIsShowing = false;

    // The ID of the bot to use for the chatbot, can be changed
    // you can also make a new bot by creating an account in pandorabots.com and making a new chatbot robot
    private String PANDORA_BOT_ID = "b9581e5f6e343f72";
    private Bot bot;
    private TTS tts;

    private FragmentActivity mActivity;

    private CommandHandler questions;

    /**
     *  This class implements the Singleton pattern. Note that only the tts engine and RobotFace
     *  are updated when getInstance() is called.
     */
    private Robot(RobotFace face, final FragmentActivity context){
        //Only one copy of this ever
        kubiManager = new KubiManager(this, true);
        kubiManager.findAllKubis();

        createRecognizer(App.getContext());

        setup(face, context);

        if(App.InWizardMode()) {
            questions = new CommandHandler("questions");
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
    public static Robot getInstance(RobotFace face, FragmentActivity context) {
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
    private void setup(RobotFace face, FragmentActivity context) {
        mActivity = context;

        robotFace = face;
        robotFace.setOnTouchListener(faceListener);

        tts = TTS.getInstance(context);
        bot = new Bot(context, PANDORA_BOT_ID, tts);
    }

    /**
     * Starts the robot by starting the FaceThread if it has not already been started.
     */
    public void startup() {
        if (thread != null) {
            Log.i(TAG, "Robot already started ...");
            return;
        }

        thread = new FaceThread(robotFace, kubiManager);
        thread.start();

        if(App.InWizardMode()) {
            questions.Listen();
        }

        resetTimers();
    }

    /**
     * Stops the FaceThread
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down Main Thread ...");

        while (true) {
            try {
                if(App.InWizardMode()) {
                    questions.Stop();
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
            //tts.setRate(speed / 100.0f);
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
            Toast.makeText(mActivity, "ASR could not be started: invalid params", Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());
        }
    }

    public void act(FaceAction faceAction) {
        resetTimers();

        thread.act(faceAction);
    }


    // look around every 3 minutes
    private final long BORING_TIME = 1 * 60 * 1000;
    private final long SLEEP_TIME = 5 * 60 * 1000;

    private Random random = new Random();

    private Timer bored;
    private Timer sleep;

    private void kubiDo(int gesture) {
        if(kubiManager.getKubi() != null) {
            kubiManager.getKubi().performGesture(gesture);
        }
    }

    private void scheduleBored(long delay) {
        if(bored != null) {
            bored.cancel();
        }

        bored = new Timer();
        bored.schedule(new TimerTask() {
            @Override
            public void run() {
                isBored = true;
                thread.act(FaceAction.LOOK_LEFT);
                kubiDo(Kubi.GESTURE_RANDOM);
                scheduleBored(random.nextInt(20) * 1000);
            }
        }, delay);
    }

    private void resetTimers() {
        if (isBored) {
            if(kubiManager.getKubi() != null) kubiManager.getKubi().moveTo(0, 0);
            isBored = false;
        }

        scheduleBored(BORING_TIME);

        if (isAsleep) {
            thread.act(FaceAction.WAKE);
            if(kubiManager.getKubi() != null) kubiManager.getKubi().moveTo(0, 0);
            isAsleep = false;
        }

        if (sleep != null) {
            sleep.cancel();
        }

        sleep = new Timer();
        sleep.schedule(new TimerTask() {
            @Override
            public void run() {
                isAsleep = true;
                thread.act(FaceAction.SLEEP);
                kubiDo(Kubi.GESTURE_FACE_DOWN);

                if (bored != null) {
                    bored.cancel();
                }
            }
        }, SLEEP_TIME);
    }

    public void perform(Action action) {
        resetTimers();

        if(kubiManager.getKubi() != null) {
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
                case PAY_ATTENTION:
                    kubiManager.getKubi().moveTo(0, 0);
                    break;
                case YAY:
                    yay();
                    break;
                case OOPS:
                    oops();
                    break;
                case EXCELLENT:
                    excellent();
                    break;
                case LOWER_HANDS:
                    hideCard(Robot.Hand.Left);
                    hideCard(Robot.Hand.Right);
                    break;
                case RAISE_HANDS:
                    showCard(Robot.Hand.Left);
                    showCard(Robot.Hand.Right);
                    break;
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
     * Touch listener for the RobotFace
     */
    private View.OnTouchListener faceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG, "RobotFace touch occurred!");
            resetTimers();
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
        Toast.makeText(mActivity, "I'm listening.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void processAsrError(int errorCode) {
        //super.stopListening();
        super.cancel();

        String errorMessage = SpeechUtils.getErrorMessage(errorCode);

        // If there is an error, shows feedback to the user and writes it in the log
        Log.e(TAG, "Error: " + errorMessage);
        Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
    }

    public void setPromptContainer(View promptContainer) {
        this.mPromptContainer = promptContainer;
    }

    // Render the given PromptData to the user
    public void setPrompt(PromptData promptData) {
        Prompt prompt;

        // switch on the type of prompt
        switch (promptData.type) {
            case SELECT:
                // load prompt fragment
                prompt = new SelectPrompt();
                break;
            case TRANSLATE:
                prompt = new TranslatePrompt();
                break;
            default:
                throw new IllegalArgumentException(String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));
        }

        prompt.setData(promptData);

        this.mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.prompt_container, prompt)
                .commit();

        // Animate the prompt onto the screen
        final View promptView = this.mActivity.findViewById(R.id.prompt);
        boolean isHidden = ((FrameLayout.LayoutParams)promptView.getLayoutParams()).bottomMargin < 0;

        if(isHidden) {
            ValueAnimator anim = ValueAnimator.ofInt(-promptView.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) promptView.getLayoutParams();
                    params.bottomMargin = val;
                    promptView.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();
        }
    }

    public void hidePrompt() {
        final View promptView = this.mActivity.findViewById(R.id.prompt);

        ValueAnimator anim = ValueAnimator.ofInt(20, -promptView.getHeight() - 10);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) promptView.getLayoutParams();
                params.bottomMargin = val;
                promptView.setLayoutParams(params);
            }
        });
        anim.setDuration(500);
        anim.start();

        ProgressBar pb = (ProgressBar) this.mActivity.findViewById(R.id.progressBar);
        int progress = pb.getProgress();

        if(progress == 100) {
            pb.setProgress(0);
            progress = 0;
        }

        ObjectAnimator animation = ObjectAnimator.ofInt (pb, "progress", progress, progress + 20);
        animation.setDuration (500);
        animation.setInterpolator (new AccelerateDecelerateInterpolator());
        animation.start ();
    }

    public void setCards(View left, View right) {
        this.leftCard = left;
        this.rightCard = right;

        this.leftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "left image clicked!");
                if (rightIsShowing) {
                    hideCard(Robot.Hand.Right);
                    rightIsShowing = false;
                }
            }
        });

        this.rightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "right image clicked!");
                if (leftIsShowing) {
                    hideCard(Robot.Hand.Left);
                    leftIsShowing = false;
                }
            }
        });
    }

    public enum Hand {
        Left,
        Right
    }

    public void showCard(Hand leftOrRight) {
        if(leftOrRight == Hand.Left && leftIsShowing) return;
        if(leftOrRight == Hand.Right && rightIsShowing) return;

        final View card = leftOrRight == Hand.Left ? leftCard : rightCard;

        if(card == null) return;

        if(leftOrRight == Hand.Left) leftIsShowing = true;
        if(leftOrRight == Hand.Right) rightIsShowing = true;

        if(((FrameLayout.LayoutParams)card.getLayoutParams()).bottomMargin < 0) {
            ValueAnimator anim = ValueAnimator.ofInt(-card.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
                    params.bottomMargin = val;
                    card.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();
        }
    }

    public void showCard(Hand leftOrRight, int resID, String text) {
        final View card = leftOrRight == Hand.Left ? leftCard : rightCard;

        if(card == null) return;

        TextView t = null;
        ImageView i = null;

        if(leftOrRight == Hand.Left) {
            leftIsShowing = true;

            t = (TextView)card.findViewById(R.id.leftCardText);
            i = (ImageView)card.findViewById(R.id.leftCardImage);
        } else {
            rightIsShowing = true;

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

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
                    params.bottomMargin = val;
                    card.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();
        }
    }

    public ValueAnimator hideCard(Hand leftOrRight) {
        final View card = leftOrRight == Hand.Left ? leftCard : rightCard;

        if(card == null) return null;

        if(leftOrRight == Hand.Left && !leftIsShowing) return null;
        if(leftOrRight == Hand.Right && !rightIsShowing) return null;

        if(leftOrRight == Hand.Left) leftIsShowing = false;
        if(leftOrRight == Hand.Right) rightIsShowing = false;

        ValueAnimator anim = ValueAnimator.ofInt(20, -card.getHeight() - 10);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
                params.bottomMargin = val;
                card.setLayoutParams(params);
            }
        });
        anim.setDuration(500);
        anim.start();

        return anim;
    }
}
