package uw.hcrlab.kubi.robot;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.HintData;
import uw.hcrlab.kubi.lesson.HintFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.Result;
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
    private KubiManager kubiManager;

    private boolean isAsleep = false;
    private boolean isBored = false;

    public enum Hand {
        Left,
        Right
    }

    private int faceResId;
    private int leftCardResId;
    private int rightCardResId;
    private int promptResId;
    private int thoughtResId;

    private boolean mIsPromptOpen = false;
    private boolean mIsHintOpen = false;


    private String mCurrentPromptId;
    private Prompt mCurrentPrompt;

    private Boolean leftIsShowing = false;
    private Boolean rightIsShowing = false;


    // RobotFace timers and timeout values
    private final long BORING_TIME = 1 * 60 * 1000;
    private final long SLEEP_TIME = 5 * 60 * 1000;
    private Random random = new Random();
    private Timer bored;
    private Timer sleep;

    // The ID of the bot to use for the chatbot, can be changed
    // you can also make a new bot by creating an account in pandorabots.com and making a new chatbot robot
    private String PANDORA_BOT_ID = "b9581e5f6e343f72";
    private Bot bot;
    private TTS tts;

    private FragmentActivity mActivity;

    private ProgressIndicator progress;

    private CommandHandler questions;

    /**
     *  This class implements the Singleton pattern. Note that only the tts engine and RobotFace
     *  are updated when getInstance() is called.
     */
    private Robot(){
        //Only one copy of this ever
        kubiManager = new KubiManager(this, true);
        kubiManager.findAllKubis();

        createRecognizer(App.getContext());

        if(App.InWizardMode()) {
            questions = new CommandHandler("questions");
        }
    }

    public void cleanup() {
        kubiManager.disconnect();
    }

    public static class Factory {
        public static Robot create(FragmentActivity context, int faceRes, int promptRes, int thoughtRes, int leftRes, int rightRes) {
            if(robotInstance == null) {
                robotInstance = new Robot();
            } else {
                //Shutdown resources tied to the previous robot face to allow them to be recreated
                robotInstance.shutdown();
                robotInstance.tts.shutdown();
            }

            // Setup UI components
            robotInstance.setup(context, faceRes, promptRes, thoughtRes, leftRes, rightRes);

            return robotInstance;
        }
    }

    /**
     * Gets the singleton instance of the Robot object. Note that after calling this method, the
     * robot.startup() method must be called, or the RobotFace will never be drawn.
     *
     * @throws NullPointerException If the robot has not been created with the robot factory yet
     * @return The Robot singleton
     */
    public static Robot getInstance() {
        if (robotInstance == null) {
            throw new NullPointerException("Use Factory.create(...) to create a robot before calling getInstance()!");
        }

        return robotInstance;
    }

    /**
     * Handles the setup actions which must occur every time a new face is passed in.
     *
     * @param faceRes The RobotFace view for the current Activity
     * @param promptRes
     * @param bubbleRes
     * @param leftRes
     * @param rightRes
     * @param context The current activity
     */
    private void setup(FragmentActivity context, int faceRes, int promptRes, int bubbleRes, int leftRes, int rightRes) {
        mActivity = context;

        faceResId = faceRes;
        promptResId = promptRes;
        thoughtResId = bubbleRes;
        leftCardResId = leftRes;
        rightCardResId = rightRes;

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

        View left = mActivity.findViewById(this.leftCardResId);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "left image clicked!");
                if (rightIsShowing) {
                    hideCard(Robot.Hand.Right);
                    rightIsShowing = false;
                }
            }
        });

        View right = mActivity.findViewById(this.rightCardResId);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "right image clicked!");
                if (leftIsShowing) {
                    hideCard(Robot.Hand.Left);
                    leftIsShowing = false;
                }
            }
        });

        RobotFace face = (RobotFace) mActivity.findViewById(this.faceResId);
        face.setOnTouchListener(faceListener);

        progress = new ProgressIndicator(this.mActivity, R.id.progressBar, R.id.progressText);

        thread = new FaceThread(face, kubiManager);
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

        if(sleep != null) {
            sleep.cancel();
        }

        if(bored != null) {
            bored.cancel();
        }

        progress.cleanup();

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
        RobotFace face = (RobotFace) mActivity.findViewById(this.faceResId);
        if(face == null) {
            throw new NullPointerException("act(...) cannot be called if robot doesn't have a reference to a RobotFace view!");
        }

        resetTimers();

        thread.act(faceAction);
    }


    // look around every 3 minutes
    private void kubiDo(int gesture) {
        if(kubiManager.getKubi() == null) {
            Log.w(TAG, "Unable to get a reference to a Kubi robot!");
            return;
        }

        kubiManager.getKubi().performGesture(gesture);
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


    /** Show a HintData, which can contain multiple hints */
    public void showHint(HintData hintData) {
        Fragment hint = new HintFragment().setHintData(hintData);

        // add the hint fragment to the container (replacing last one, if applicable)
        this.mActivity.getSupportFragmentManager().beginTransaction()
                .replace(thoughtResId.getId(), hint).commit();
    }

    // Render the given PromptData to the user
    public void showHint(final String hint) {
        if(mIsHintOpen) {
            hideHint();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHint(hint);
                }
            }, 800);

            return;
        }

        ((TextView) thoughtResId.findViewById(R.id.thought_text)).setText(hint);

        // Animate the prompt onto the screen
        if(((FrameLayout.LayoutParams) thoughtResId.getLayoutParams()).topMargin < 0) {
            ValueAnimator anim = ValueAnimator.ofInt(-thoughtResId.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thoughtResId.getLayoutParams();
                    params.topMargin = val;
                    thoughtResId.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();

            mIsHintOpen = true;
        }
    }

    public boolean isHintOpen() {
        return mIsHintOpen;
    }

    public void hideHint() {
        if(!mIsHintOpen) {
            return;
        }

        ValueAnimator anim = ValueAnimator.ofInt(20, -thoughtResId.getHeight() - 10);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thoughtResId.getLayoutParams();
                params.topMargin = val;
                thoughtResId.setLayoutParams(params);
            }
        });
        anim.setDuration(500);
        anim.start();

        mIsHintOpen = false;
    }

    public boolean isPromptOpen() {
        return mIsPromptOpen;
    }

    public String getCurrentPromptId() {
        return mCurrentPromptId;
    }

    // Render the given PromptData to the user
    public void setPrompt(final Prompt prompt, final String promptId) {
        if(mIsPromptOpen) {
            hidePrompt();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPrompt(prompt, promptId);
                }
            }, 800);

            return;
        }

        mCurrentPromptId = promptId;
        mCurrentPrompt = prompt;

        this.mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(promptResId.getId(), prompt, mCurrentPromptId)
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

            mIsPromptOpen = true;
        }
    }

    public void setPromptResponse(Object response) {
        if (mCurrentPromptId != null) {
            Firebase fb = App.getFirebase().child("questions").child(mCurrentPromptId).child("response");
            fb.setValue(response);
        } else {
            Log.e(TAG, "null mCurrentPromptId, not setting firebase value");
        }
    }

    public void showResult(Result res) {
        if(res.isCorrect()) {
            this.say("Yay! You got it!", "en");
        } else {
            this.say("Oops! That\'s not the correct answer.", "en");
        }

        mCurrentPrompt.handleResults(res);
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

        mIsPromptOpen = false;
        mCurrentPrompt = null;
    }

    public void showCard(Hand leftOrRight) {
        if(leftOrRight == Hand.Left && leftIsShowing) return;
        if(leftOrRight == Hand.Right && rightIsShowing) return;

        final View card = leftOrRight == Hand.Left ? leftCardResId : rightCardResId;

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
        final View card = leftOrRight == Hand.Left ? leftCardResId : rightCardResId;

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
        final View card = leftOrRight == Hand.Left ? leftCardResId : rightCardResId;

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
