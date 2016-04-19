package uw.hcrlab.kubi.wizard;

import android.net.ParseException;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
import uw.hcrlab.kubi.robot.Action;
import uw.hcrlab.kubi.robot.FaceAction;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.wizard.model.Speech;
import uw.hcrlab.kubi.wizard.model.Task;

/**
 * Created by Alexander on 4/16/2015.
 *
 * Handles general commands such as responses, lessons, quizzes, and questions
 */
public class CommandHandler extends WizardHandler {
    public static String TAG = CommandHandler.class.getSimpleName();

    public CommandHandler(String ref) {
        super(ref);
    }

    private int getDrawable(String command) {
        Map<String, Integer> drawables = new HashMap<String, Integer>();

        drawables.put("APPLE", R.drawable.apple);
        drawables.put("BANANA", R.drawable.banana);
        drawables.put("GIRL", R.drawable.girl);
        drawables.put("BOY", R.drawable.boy);
        drawables.put("FRANCE", R.drawable.france);

        return drawables.get(command);
    }

    private PromptTypes getType(DataSnapshot snap) {
        if(!snap.hasChild("type")) {
            throw new IllegalArgumentException("Data snapshot does not contain a property named `type`");
        }

        return PromptTypes.valueOf(snap.child("type").getValue(String.class).toUpperCase());
    }

    private boolean validateSelect(DataSnapshot snap) {
        if(!snap.child("prompt").exists()) {
            return false;
        }

        if(!snap.child("opts").exists()) {
            return false;
        }

        for(DataSnapshot option : snap.child("opts").getChildren()) {
            if(!option.child("image").exists()) {
                return false;
            }

            if(!option.child("num").exists()) {
                return false;
            }

            if(!option.child("title").exists()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.hasChild("handled") || !snap.child("handled").getValue(Boolean.class)) {
            Log.i(TAG, "received a new command");

            if(robot == null) {
                robot = Robot.getInstance();
            }

            PromptTypes type = null;

            try {
                type = getType(snap);
            } catch (IllegalArgumentException|NullPointerException ex) {
                // TODO: Handle the exception...
                Log.e(TAG, "Unknown command type!");
                return;
            }

            Prompt prompt;
            PromptData pd = new PromptData();

            switch(type) {
                case SELECT:
                    if(!validateSelect(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.type = type;
                    pd.srcText = snap.child("prompt").getValue(String.class);

                    DataSnapshot options = snap.child("opts");
                    for(DataSnapshot option : options.getChildren()) {
                        int num = option.child("num").getValue(int.class);
                        String title = option.child("title").getValue(String.class);
                        String url = option.child("image").getValue(String.class);

                        pd.options.add(new PromptData.Option(num, title).setURL(url));
                    }

                    prompt = new SelectPrompt();
                    break;

                case TRANSLATE:
                    prompt = new TranslatePrompt();
                    break;

                default:
                    // throw new IllegalArgumentException(String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));

                    // For the time being, ignore all other types of questions
                    return;
            }

            prompt.setData(pd);
            robot.setPrompt(prompt, s);

            snap.child("handled").getRef().setValue(true);

//            for (DataSnapshot taskData : snap.child("tasks").getChildren()) {
//                Task res = taskData.getValue(Task.class);
//
//                if(robot == null) {
//                    robot = Robot.getInstance();
//                }
//
//                Speech sp = res.getSpeech();
//                if(sp != null && sp.getText() != null && !sp.getText().equalsIgnoreCase("")) {
//                    robot.say(sp.getText(), sp.getLanguage(), sp.getSpeed());
//                }
//
//                String emotion = res.getEmotion();
//                if(emotion != null && !emotion.equalsIgnoreCase("")) {
//                    Log.d(TAG, "Got emotion request: " + emotion);
//                    robot.act(FaceAction.valueOf(emotion));
//                }
//
//                String action = res.getAction();
//                if(action != null && !action.equalsIgnoreCase("")) {
//                    Log.d(TAG, "Got action request: " + action);
//                    robot.perform(Action.valueOf(action));
//                }
//
//                int imageCount = 0;
//
//                String left = res.getLeftImage();
//                if(left != null && !left.equalsIgnoreCase("")) {
//                    Log.d(TAG, "Displaying left hand image");
//
//                    String leftTxt = res.getLeftText();
//                    if(leftTxt == null) {
//                        leftTxt = "";
//                    }
//                    imageCount = 1;
//                    robot.showCard(Robot.Hand.Left, getDrawable(left), leftTxt);
//                }
//
//                String right = res.getRightImage();
//                if(right != null && !right.equalsIgnoreCase("")) {
//                    Log.d(TAG, "Displaying right hand image");
//
//                    String rightTxt = res.getRightText();
//                    if(rightTxt == null) {
//                        rightTxt = "";
//                    }
//                    imageCount = (imageCount == 0) ? 2 : 3;
//                    robot.showCard(Robot.Hand.Right, getDrawable(right), rightTxt);
//                }
//
//                switch(imageCount) {
//                    case 1: robot.act(FaceAction.LOOK_DOWN_LEFT); break;
//                    case 2: robot.act(FaceAction.LOOK_DOWN_RIGHT); break;
//                    case 3: robot.act(FaceAction.LOOK_DOWN); break;
//                    default: break;
//                }
//
//                String[] buttons = res.getButtons();
//                if(buttons != null) {
//                    Log.d(TAG, "You need to display buttons!");
//                }
//            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snap, String s) {
        if(snap.hasChild("result")) {
            DataSnapshot res = snap.child("result");

            if(robot == null) {
                robot = Robot.getInstance();
            }

            if(!robot.getCurrentPromptId().equals(s)) {
                Log.e(TAG, "Received a results update for a prompt that isn't currently showing!");
                return;
            }

            Boolean isCorrect = res.child("correct").getValue(Boolean.class);
            Integer correctIdx = res.child("solutions").child("0").getValue(Integer.class);
            Integer usersIdx = res.child("response").getValue(Integer.class);

            robot.showResult(new Result(isCorrect, usersIdx, correctIdx));
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        //Ignore onChildRemoved
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //Ignore onChildMoved
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, "Firebase Error: " + firebaseError.toString());
    }
}
