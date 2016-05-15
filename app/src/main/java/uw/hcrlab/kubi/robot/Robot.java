package uw.hcrlab.kubi.robot;

import android.animation.ValueAnimator;
import android.os.Handler;
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

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.HintArrayAdapter;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.wizard.CommandHandler;

/**
 * Created by kimyen on 4/7/15.
 */
public class Robot {
    public static String TAG = Robot.class.getSimpleName();

    /**
     * An enumeration describing the two types of single hand flash cards that can be displayed
     */
    public enum Hand {
        Left,
        Right
    }

    /**
     * A toast object used for coordinating toasts that the Robot displays
     */
    private static Toast currentToast;

    /**
     * The activity that owns this Robot
     */
    private FragmentActivity owner;

    // Primary Robot components
    private static Robot instance = null;
    public Body body;
    public Speech speech;
    private ProgressIndicator progress;
    private CommandHandler questions;

    // State variables
    private boolean isStarted = false;
    private boolean isBored = false;
    private boolean isPromptOpen = false;
    private boolean isHintOpen = false;
    private Boolean isLeftShowing = false;
    private Boolean isRightShowing = false;

    // Fields for managing the "bored" state of the robot
    private final long BORING_TIME = 1000 * 60 * 1000;
    private Random random = new Random();
    private Timer bored;

    // References to UI Components
    private int eyesResId;
    private int leftCardResId;
    private int rightCardResId;
    private int promptResId;
    private int thoughtResId;

    /**
     * The current prompt object
     */
    private Prompt prompt;

    private String lastCorrectResponse = "";
    private String[] correctResponses = {
            "Yay! You got it!",
            "You are correct.",
            "Good job!",
            "Well done.",
            "You got it!",
            "Nice work!",
            "That's it! Keep up the good work!",
            "Woohoo!",
            "You are doing great!",
            "Way to go! You got it!"
    };
    private String lastIncorrectResponse = "";
    private String[] incorrectResponses = {
            "Oops! There is a mistake.",
            "Oops! That\'s not the correct answer.",
            "Ut oh. That's not it",
            "Sorry, that's incorrect.",
            "Almost. Let's try another question.",
            "So close!",
            "Almost. Let's keep working at it!",
            "Dang, you almost got it."
    };

    private Body.Action lastHappyMove = null;
    private Body.Action[] happyMoves = {
            Body.Action.EXCELLENT_GESTURE,
            Body.Action.YAY_GESTURE,
            Body.Action.NOD
    };

    private Body.Action lastSadMove = null;
    private Body.Action[] sadMoves = {
            Body.Action.OOPS_GESTURE,
            Body.Action.SHAKE
    };

    /**
     * This class implements the Singleton pattern and the Factory pattern. All initialization of
     * Robot objects is performed by the Robot.Factory subclass.
     */
    private Robot() {}

    /**
     * Cleans up resources which exist regardless of whether a robot has been setup or not.
     */
    public void cleanup() {
        body.cleanup();
        speech.cleanup();
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
                instance.speech.shutdown();
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
            instance.owner = context;

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
            instance.owner = context;

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
            instance.body = Body.getInstance((App) instance.owner.getApplication());
            instance.speech = Speech.getInstance(instance.owner);

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
     * Starts the robot.
     */
    public void startup() {
        if (isStarted) {
            Log.i(TAG, "Robot already started ...");
            return;
        }

        View left = owner.findViewById(this.leftCardResId);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "left image clicked!");
                if (isRightShowing) {
                    hideCard(Robot.Hand.Right);
                    isRightShowing = false;
                }
            }
        });

        View right = owner.findViewById(this.rightCardResId);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "right image clicked!");
                if (isLeftShowing) {
                    hideCard(Robot.Hand.Left);
                    isLeftShowing = false;
                }
            }
        });

        speech.startup();
        body.startSubtleMovement();

        progress = ProgressIndicator.getInstance(this.owner, R.id.progressBar, R.id.progressText);

        if (App.InWizardMode()) {
            questions.Listen();
        }

        resetTimers();

        isStarted = true;
    }

    /**
     * Stops the robot
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down the robot ...");

        if (!isStarted) {
            Log.i(TAG, "Robot already shutdown ...");
            return;
        }

        if (bored != null) {
            bored.cancel();
        }

        speech.shutdown();

        progress.cleanup();

        if (App.InWizardMode()) {
            questions.Stop();
        }

        isStarted = false;
    }

    /**
     * Creates or replaces the currently displayed toast object. This allows the robot to show one
     * toast at a time.
     *
     * @param text The text to display in the toast
     */
    public static void replaceCurrentToast(String text) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    /**
     * Performs an eye gesture such as "Look Left" or "Look Happy". These can be directional or
     * emotional eye gestures.
     *
     * @param look The eye gesture to preform
     */
    public void look(Eyes.Look look) {
        Eyes eyes = (Eyes) owner.findViewById(eyesResId);

        if(eyes == null) {
            throw new NullPointerException("look(...) cannot be called if robot doesn't have a reference to an Eyes view!");
        }

        resetTimers();

        eyes.look(look);
    }

    /**
     * Sets a timer that makes the robot look off into space if the user isn't interacting with it
     * (i.e. this makes the robot pretend to be bored).
     *
     * @param delay Milliseconds for the robot to wait before becoming bored
     */
    private void scheduleBored(long delay) {
        if(bored != null) {
            bored.cancel();
        }

        bored = new Timer();
        bored.schedule(new TimerTask() {
            @Override
            public void run() {
                isBored = true;

                Eyes eyes = (Eyes) owner.findViewById(eyesResId);
                eyes.look(Eyes.Look.LOOK_LEFT);

                body.move(Body.Action.LOOK_AROUND);
                scheduleBored(random.nextInt(20) * 1000);
            }
        }, delay);
    }

    /**
     * Resets the bored timer by canceling the current timer and creating a new one.
     */
    private void resetTimers() {
        if (isBored) {
            body.moveTo(0,0);
            isBored = false;
        }

        scheduleBored(BORING_TIME);
    }



    /**
     * Listener for responding to touch gestures on on the robot which are not handled by any
     * subviews (such as the prompt fragments).
     */
    private View.OnTouchListener faceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG, "RobotFace touch occurred!");
            resetTimers();
            return false;
        }
    };

    /**
     * Show a HintCollection, which can contain multiple hints, in the robot's thought bubble
     */
    public void showHint(final ArrayList<PromptData.Hint> hints) {
        if(isHintOpen) {
            hideHint();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHint(hints);
                }
            }, 800);

            return;
        }

        final View bubble = owner.findViewById(this.thoughtResId);

        HintArrayAdapter adapter = new HintArrayAdapter(owner, hints);

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

            isHintOpen = true;
        }
    }

    /**
     * Shows a string hint in the robot's thought bubble
     *
     * @param hint The text to display
     */
    public void showHint(final String hint) {
        if(isHintOpen) {
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


        final View bubble = owner.findViewById(this.thoughtResId);

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

            isHintOpen = true;
        }
    }

    /**
     * Indicates if the robot is currently displaying a hint
     *
     * @return True if the thought bubble is visible, false otherwise
     */
    public boolean isHintOpen() {
        return isHintOpen;
    }

    /**
     * Hides the robot's thought bubble if it is visible.
     */
    public void hideHint() {
        if(!isHintOpen) {
            return;
        }

        final View bubble = owner.findViewById(thoughtResId);
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

        isHintOpen = false;
    }

    /**
     * Indicates if the robot is currently displaying a prompt to the user.
     *
     * @return True if a prompt is visible, false otherwise.
     */
    public boolean isPromptOpen() {
        return isPromptOpen;
    }

    /**
     * Gets the robot's current prompt
     *
     * @return Returns the robot's current prompt, or null if the robot does not currently have a prompt.
     */
    public Prompt getPrompt() {
        return prompt;
    }

    /**
     * Animates a prompt onto the robot's screen
     *
     * @param prompt The prompt to display
     */
    public void setPrompt(final Prompt prompt) {
        if(isHintOpen) {
            hideHint();
        }

        if(isPromptOpen) {
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

        this.owner.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(promptResId, prompt, prompt.getUid())
                .commit();

        isPromptOpen = true;
    }

    /**
     * Stores the user's response to the current prompt to Firebase
     *
     * @param response The user's response to store to Firebase
     */
    public void setPromptResponse(Object response) {
        if (prompt != null && prompt.getUid() != null) {
            long time = prompt.getTotalTime();
            Firebase fbElapsed = App.getFirebase().child("questions").child(prompt.getUid()).child("elapsed");
            fbElapsed.setValue(time);

            Firebase fb = App.getFirebase().child("questions").child(prompt.getUid()).child("response");
            fb.setValue(response);
        } else {
            Log.e(TAG, "null promptId, not setting firebase value");
        }
    }

    /**
     * Display the correct response to the current response and carry out the robot's reaction to
     * whether the user got the answer correct or incorrect.
     *
     * @param res The result of the user's answer to the current prompt (indicating if the user's answer was correct or not)
     */
    public void showResult(Result res) {
        if(res.isCorrect()) {
            lastCorrectResponse = speech.sayRandomResponse(correctResponses, lastCorrectResponse);
            lastHappyMove = body.doRandomMove(happyMoves, lastHappyMove);
        } else {
            lastIncorrectResponse = speech.sayRandomResponse(incorrectResponses, lastIncorrectResponse);
            lastSadMove = body.doRandomMove(sadMoves, lastSadMove);
        }

        prompt.handleResults(res);
    }

    /**
     * Animates the current prompt off of the screen and sets the current prompt to null.
     */
    public void hidePrompt() {
        this.owner.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .remove(prompt)
                .commit();

        isPromptOpen = false;
        prompt = null;
    }

    /**
     * Shows a single hand flash card
     *
     * @param leftOrRight The single hand flash card to display (left or right)
     */
    public void showCard(Hand leftOrRight) {
        if(leftOrRight == Hand.Left && isLeftShowing) return;
        if(leftOrRight == Hand.Right && isRightShowing) return;

        if(leftOrRight == Hand.Left) isLeftShowing = true;
        if(leftOrRight == Hand.Right) isRightShowing = true;

        final View card = owner.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);
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

    /**
     * Shows a single hand flash card with the given image and text
     *
     * @param leftOrRight The hand to display this flash card in
     * @param resID The resource ID of the image to display
     * @param text The text to display on the flash card
     */
    public void showCard(Hand leftOrRight, int resID, String text) {
        final View card = owner.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);

        if(card == null) return;

        TextView t = null;
        ImageView i = null;

        if(leftOrRight == Hand.Left) {
            isLeftShowing = true;

            t = (TextView)card.findViewById(R.id.leftCardText);
            i = (ImageView)card.findViewById(R.id.leftCardImage);
        } else {
            isRightShowing = true;

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

    /**
     * Animates the indicated flash card off of th screen
     *
     * @param leftOrRight The flash card to hide
     * @return The animator which is animating the card off of the screen
     */
    public ValueAnimator hideCard(Hand leftOrRight) {
        final View card = owner.findViewById(leftOrRight == Hand.Left ? leftCardResId : rightCardResId);

        if(card == null) return null;

        if(leftOrRight == Hand.Left && !isLeftShowing) return null;
        if(leftOrRight == Hand.Right && !isRightShowing) return null;

        if(leftOrRight == Hand.Left) isLeftShowing = false;
        if(leftOrRight == Hand.Right) isRightShowing = false;

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
