package uw.hcrlab.kubi.wizard;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uw.hcrlab.kubi.R;
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

        return drawables.get(command);
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.child("handled").getValue(Boolean.class)) {
            Log.i(TAG, "received a new command");
            for (DataSnapshot taskData : snap.child("tasks").getChildren()) {
                Task res = taskData.getValue(Task.class);

                if(robot == null) {
                    robot = Robot.getInstance();
                }

                Speech sp = res.getSpeech();
                if(sp != null && sp.getText() != null && !sp.getText().equalsIgnoreCase("")) {
                    robot.say(sp.getText(), sp.getLanguage(), sp.getSpeed());
                }

                String emotion = res.getEmotion();
                if(emotion != null && !emotion.equalsIgnoreCase("")) {
                    Log.d(TAG, "Got emotion request: " + emotion);
                    robot.act(FaceAction.valueOf(emotion));
                }

                String action = res.getAction();
                if(action != null && !action.equalsIgnoreCase("")) {
                    if(action.equalsIgnoreCase("LOWER_HANDS")) {
                        robot.hideCard(Robot.Hand.Left);
                        robot.hideCard(Robot.Hand.Right);
                    } else if (action.equalsIgnoreCase("RAISE_HANDS")) {
                        robot.showCard(Robot.Hand.Left);
                        robot.showCard(Robot.Hand.Right);
                    } else {
                        Log.d(TAG, "Got action request: " + action);
                        robot.perform(Action.valueOf(action));
                    }
                }

                int imageCount = 0;

                String left = res.getLeftImage();
                if(left != null && !left.equalsIgnoreCase("")) {
                    Log.d(TAG, "Displaying left hand image");

                    String leftTxt = res.getLeftText();
                    if(leftTxt == null) {
                        leftTxt = "";
                    }
                    imageCount = 1;
                    robot.showCard(Robot.Hand.Left, getDrawable(left), leftTxt);
                }

                String right = res.getRightImage();
                if(right != null && !right.equalsIgnoreCase("")) {
                    Log.d(TAG, "Displaying right hand image");

                    String rightTxt = res.getRightText();
                    if(rightTxt == null) {
                        rightTxt = "";
                    }
                    imageCount = (imageCount == 0) ? 2 : 3;
                    robot.showCard(Robot.Hand.Right, getDrawable(right), rightTxt);
                }

                switch(imageCount) {
                    case 1: robot.act(FaceAction.LOOK_DOWN_LEFT); break;
                    case 2: robot.act(FaceAction.LOOK_DOWN_RIGHT); break;
                    case 3: robot.act(FaceAction.LOOK_DOWN); break;
                    default: break;
                }

                String[] buttons = res.getButtons();
                if(buttons != null) {
                    Log.d(TAG, "You need to display buttons!");
                }
            }

            snap.child("handled").getRef().setValue(true);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //Ignore onChildChanged
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
