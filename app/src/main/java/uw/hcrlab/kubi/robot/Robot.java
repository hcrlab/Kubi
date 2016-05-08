package uw.hcrlab.kubi.robot;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.firebase.client.Firebase;
import com.revolverobotics.kubiapi.IKubiManagerDelegate;
import com.revolverobotics.kubiapi.Kubi;
import com.revolverobotics.kubiapi.KubiManager;
import com.revolverobotics.kubiapi.KubiSearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.HintArrayAdapter;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.speech.SpeechUtils;
import uw.hcrlab.kubi.wizard.CommandHandler;

/**
 * Created by kimyen on 4/7/15.
 */
public class Robot extends ASR  {
    public static String TAG = Robot.class.getSimpleName();

    public enum Hand {
        Left,
        Right
    }

    private static Robot instance = null;

    private FragmentActivity mActivity;
    private ProgressIndicator progress;
    private CommandHandler questions;

    private String language = "EN";

    private boolean isStarted = false;
    private boolean isBored = false;
    private final long BORING_TIME = 1000 * 60 * 1000;

    private Random random = new Random();
    private Timer bored;

    protected HttpProxyCacheServer proxy;
    private HashMap<String, MediaPlayer> mPronunciations;
    private String delayedPronunciation;

    private static Toast currentToast;

    private Bot bot;
    private TTS tts;
    private boolean isSpeaking = false;

    private int eyesResId;
    private int leftCardResId;
    private int rightCardResId;
    private int promptResId;
    private int thoughtResId;

    private boolean mIsPromptOpen = false;
    private boolean mIsHintOpen = false;
    private Boolean leftIsShowing = false;
    private Boolean rightIsShowing = false;

    private Prompt prompt;

    private String lastCorrectResponse = "";
    private String[] correctResponses = {
            "Yay! You got it!",
            "You are correct.",
            "Good job!",
            "Well done.",
    };
    private String lastIncorrectResponse = "";
    private String[] incorrectResponses = {
            "Oops! That\'s not the correct answer.",
            "Nope.",
            "Sorry, you are incorrect.",
            "Incorrect.",
    };

    public Body body;

    /**
     * This class implements the Singleton pattern. Note that only the tts engine and RobotFace
     * are updated when getInstance() is called.
     */
    private Robot() {
        body = new Body();
        createRecognizer(App.getContext());
        mPronunciations = new HashMap<>();
    }



    /**
     * Cleans up resources which exist regardless of whether a robot has been setup or not.
     */
    public void cleanup() {
        body.cleanup();
    }

    /**
     * A factory class for creating robot objects.
     */
    public static class Factory {
        /**
         * Creates a robot instance given resource IDs for important UI components. Creates a robot
         * without individual flash cards (left and right hands)
         *
         * @param context    A reference to the Activity the robot belongs to
         * @param eyesRes    Resource ID for the SurfaceHolder for the robot eyes
         * @param promptRes  Resource ID for the prompt container view
         * @param thoughtRes Resource ID for the though bubble container view
         * @return The created instance of the Robot class
         */
        public static Robot create(FragmentActivity context, int eyesRes, int promptRes, int thoughtRes) {
            preSetup();
            setup(context, eyesRes, promptRes, thoughtRes);
            postSetup();

            return instance;
        }

        /**
         * Creates a robot instance given resource IDs for important UI components
         *
         * @param context    A reference to the Activity the robot belongs to
         * @param eyesRes    Resource ID for the SurfaceHolder for the robot eyes
         * @param promptRes  Resource ID for the prompt container view
         * @param thoughtRes Resource ID for the though bubble container view
         * @param leftRes    Resource ID for the left card view (the robot's right hand)
         * @param rightRes   Resource ID for the right card view (the robot's left hand)
         * @return The created instance of the Robot class
         */
        public static Robot create(FragmentActivity context, int eyesRes, int promptRes, int thoughtRes, int leftRes, int rightRes) {
            preSetup();
            setup(context, eyesRes, promptRes, thoughtRes, leftRes, rightRes);
            postSetup();

            return instance;
        }

        /**
         * Creates a bare robot object which has not yet been initialized. If a robot instance
         * already exists, this will shutdown the robot and prepare the robot to be re-initialized.
         */
        private static void preSetup() {
            if (instance == null) {
                instance = new Robot();
            } else {
                //Shutdown resources tied to the previous robot face to allow them to be recreated
                instance.shutdown();
                instance.tts.shutdown();
            }
        }

        /**
         * Handles the setup actions which must occur every time a new face is passed in.
         *
         * @param eyesRes   Resource ID for the SurfaceHolder for the robot eyes
         * @param promptRes Resource ID for the prompt container view
         * @param bubbleRes Resource ID for the though bubble container view
         * @param context   The current activity
         */
        private static void setup(FragmentActivity context, int eyesRes, int promptRes, int bubbleRes) {
            instance.mActivity = context;

            instance.eyesResId = eyesRes;
            instance.promptResId = promptRes;
            instance.thoughtResId = bubbleRes;

            postSetup();
        }

        /**
         * Handles the setup actions which must occur every time a new face is passed in.
         *
         * @param eyesRes   Resource ID for the SurfaceHolder for the robot eyes
         * @param promptRes Resource ID for the prompt container view
         * @param bubbleRes Resource ID for the though bubble container view
         * @param leftRes   Resource ID for the left card view (the robot's right hand)
         * @param rightRes  Resource ID for the right card view (the robot's left hand)
         * @param context   The current activity
         */
        private static void setup(FragmentActivity context, int eyesRes, int promptRes, int bubbleRes, int leftRes, int rightRes) {
            instance.mActivity = context;

            instance.eyesResId = eyesRes;
            instance.promptResId = promptRes;
            instance.thoughtResId = bubbleRes;
            instance.leftCardResId = leftRes;
            instance.rightCardResId = rightRes;

            postSetup();
        }

        /**
         * Post-setup actions that are taken for all versions of the create function
         */
        private static void postSetup() {
            instance.tts = TTS.getInstance(instance.mActivity);
            instance.bot = new Bot(instance.mActivity, "b9581e5f6e343f72", instance.tts);
            instance.proxy = App.getProxy(instance.mActivity);

            if (App.InWizardMode()) {
                instance.questions = new CommandHandler("questions");
            }
        }
    }

    /**
     * Gets the singleton instance of the Robot object. Note that after calling this method, the
     * robot.startup() method must be called, or the RobotFace will never be drawn.
     *
     * @return The Robot singleton
     * @throws NullPointerException If the robot has not been created with the robot factory yet
     */
    public static Robot getInstance() {
        if (instance == null) {
            throw new NullPointerException("Use Factory.create(...) to create a robot before calling getInstance()!");
        }

        return instance;
    }

    /**
     * Starts the robot by starting the FaceThread if it has not already been started.
     */
    public void startup() {
        if (isStarted) {
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

        tts.setUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                isSpeaking = true;
            }

            @Override
            public void onDone(String s) {
                if(delayedPronunciation != null) {
                    pronounce(delayedPronunciation);
                    delayedPronunciation = null;
                }

                isSpeaking = false;
            }

            @Override
            public void onError(String s) {

            }
        });

        progress = ProgressIndicator.getInstance(this.mActivity, R.id.progressBar, R.id.progressText);

        if (App.InWizardMode()) {
            questions.Listen();
        }

        resetTimers();

        isStarted = true;
    }

    /**
     * Stops the FaceThread
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down Main Thread ...");

        if (bored != null) {
            bored.cancel();
        }

        unloadAllPronunciations();

        progress.cleanup();

        if (App.InWizardMode()) {
            questions.Stop();
        }
    }

    public static void replaceCurrentToast(String text) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    private String normalizeText(String text) {
        // Normalize the text
        text = text.toLowerCase();
        text = text.replace(',',' ');
        text = text.replace('.',' ');
        text = text.trim();
        return text;
    }

    public void loadPronunciation(String text) {
        text = normalizeText(text);

        String url = App.getAudioURL(text);

        if (url != null) {
            String audioUrl = proxy.getProxyUrl(url);
            mPronunciations.put(text, MediaPlayer.create(mActivity, Uri.parse(audioUrl)));
        }
    }

    public void unloadPronunciation(String text) {
        text = normalizeText(text);

        MediaPlayer mp = mPronunciations.remove(text);

        if (mp != null) {
            mp.release();
        }
    }

    public void unloadAllPronunciations() {
        for (Map.Entry kvp : mPronunciations.entrySet()) {
            MediaPlayer mp = (MediaPlayer) kvp.getValue();
            mp.release();
        }

        mPronunciations.clear();
    }

    public void pronounceAfterSpeech(String text) {
        delayedPronunciation = text;
    }

    public boolean pronounce(String text) {
        text = normalizeText(text);

        for(MediaPlayer mp : mPronunciations.values()) {
            if(mp.isPlaying()) {
                mp.pause();
                mp.seekTo(0);
            }
        }

        MediaPlayer mp = mPronunciations.get(text);

        if(mp != null) {
            mp.start();
            return true;
        } else {
            say(text, "EN");
            return false;
        }
    }

    public String getDefaultLanguage() {
        return language;
    }

    public void setDefaultLanguage(String lan) {
        language = lan;
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

    /**
     * Say a random response from the given collection of choices.
     * @param choices - collection to choose a random string from
     * @param dontSay - if a string matching this is selected, pick again
     * @return - the string that was said
     */
    public String sayRandomResponse(String[] choices, String dontSay) {
        String selection;
        do {
            selection = choices[random.nextInt(choices.length)];
        } while (selection.equals(dontSay));
        say(selection, "en");
        return selection;
    }


    public void shutup() {
        tts.stop();
    }

    public void listen() {
        Log.i(TAG, "listening");
        try {
            super.listen(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH, 1);
        } catch (Exception ex) {
            replaceCurrentToast("ASR could not be started: invalid params");
            Log.e(TAG, ex.getMessage());
        }
    }

    public void look(Eyes.Look look) {
        Eyes eyes = (Eyes) mActivity.findViewById(eyesResId);

        if(eyes == null) {
            throw new NullPointerException("look(...) cannot be called if robot doesn't have a reference to an Eyes view!");
        }

        resetTimers();

        eyes.look(look);
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

                Eyes eyes = (Eyes) mActivity.findViewById(eyesResId);
                eyes.look(Eyes.Look.LOOK_LEFT);

                body.move(Action.LOOK_AROUND);
                scheduleBored(random.nextInt(20) * 1000);
            }
        }, delay);
    }

    private void resetTimers() {
        if (isBored) {
            body.moveTo(0,0);
            isBored = false;
        }

        scheduleBored(BORING_TIME);
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

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        String speechInput = nBestList.get(0);
        Log.i(TAG, "Speech input: " + speechInput);

        String response = SpeechUtils.getResponse(speechInput);

        try {
            if(response != null){
                //We have a preprogrammed Response, so use it
                Log.i(TAG, "Saying : " + response);
                say(response, language);
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


    /** Show a HintCollection, which can contain multiple hints */
    public void showHint(final PromptData.HintCollection hint) {
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

        final View bubble = mActivity.findViewById(this.thoughtResId);

        HintArrayAdapter adapter = new HintArrayAdapter(mActivity, hint.details);

        TextView tv = (TextView) bubble.findViewById(R.id.thought_bubble_big);
        ListView lv = (ListView) bubble.findViewById(R.id.thought_bubble_list);
        lv.setAdapter(adapter);

        lv.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);

        // Animate the prompt onto the screen
        if(((FrameLayout.LayoutParams) bubble.getLayoutParams()).topMargin < 0) {
            ValueAnimator anim = ValueAnimator.ofInt(-bubble.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bubble.getLayoutParams();
                    params.topMargin = val;
                    bubble.setLayoutParams(params);
                }
            });
            anim.setDuration(500);
            anim.start();

            mIsHintOpen = true;
        }
    }

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


        final View bubble = mActivity.findViewById(this.thoughtResId);

        ListView lv = (ListView) bubble.findViewById(R.id.thought_bubble_list);
        TextView tv = (TextView) bubble.findViewById(R.id.thought_bubble_big);
        tv.setText(hint);

        lv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);

        // Animate the prompt onto the screen
        if(((FrameLayout.LayoutParams) bubble.getLayoutParams()).topMargin < 0) {
            ValueAnimator anim = ValueAnimator.ofInt(-bubble.getHeight() - 10, 20);
            anim.setInterpolator(new AnticipateOvershootInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bubble.getLayoutParams();
                    params.topMargin = val;
                    bubble.setLayoutParams(params);
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

        final View bubble = mActivity.findViewById(thoughtResId);
        ValueAnimator anim = ValueAnimator.ofInt(20, -bubble.getHeight() - 10);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bubble.getLayoutParams();
                params.topMargin = val;
                bubble.setLayoutParams(params);
            }
        });
        anim.setDuration(500);
        anim.start();

        mIsHintOpen = false;
    }

    public boolean isPromptOpen() {
        return mIsPromptOpen;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    // Render the given PromptData to the user
    public void setPrompt(final Prompt prompt) {
        if(mIsHintOpen) {
            hideHint();
        }

        if(mIsPromptOpen) {
            hidePrompt();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPrompt(prompt);
                }
            }, 500);

            return;
        }

        this.prompt = prompt;

        this.mActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(promptResId, prompt, prompt.getUid())
                .commit();

        mIsPromptOpen = true;
    }

    public void setPromptResponse(Object response) {
        if (prompt != null && prompt.getUid() != null) {
            Firebase fb = App.getFirebase().child("questions").child(prompt.getUid()).child("response");
            fb.setValue(response);
        } else {
            Log.e(TAG, "null promptId, not setting firebase value");
        }
    }

    public void showResult(Result res) {
        if(res.isCorrect()) {
            lastCorrectResponse = sayRandomResponse(correctResponses, lastCorrectResponse);
            body.move(Action.NOD);
        } else {
            lastIncorrectResponse = sayRandomResponse(incorrectResponses, lastIncorrectResponse);
            body.move(Action.SHAKE);
        }

        prompt.handleResults(res);
    }

    public void hidePrompt() {
        this.mActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .remove(prompt)
                .commit();

        mIsPromptOpen = false;
        prompt = null;
    }

    public void showCard(Hand leftOrRight) {
        if(leftOrRight == Hand.Left && leftIsShowing) return;
        if(leftOrRight == Hand.Right && rightIsShowing) return;

        if(leftOrRight == Hand.Left) leftIsShowing = true;
        if(leftOrRight == Hand.Right) rightIsShowing = true;

        final View card = mActivity.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);
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
        final View card = mActivity.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);

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
        final View card = mActivity.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);

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
