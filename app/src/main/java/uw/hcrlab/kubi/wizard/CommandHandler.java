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

        return drawables.get("APPLE");
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
                if(sp != null) {
                    robot.say(sp.getText(), sp.getLanguage(), sp.getSpeed());
                }

                String emotion = res.getEmotion();
                if(emotion != null) {
                    Log.d(TAG, "Got emotion request: " + emotion);
                    robot.act(FaceAction.valueOf(emotion));
                }

                String action = res.getAction();
                if(action != null) {
                    Log.d(TAG, "Got action request: " + action);
                    robot.perform(Action.valueOf(action));
                }

                int imageCount = 0;

                String left = res.getLeftImage();
                if(left != null) {
                    Log.d(TAG, "Displaying left hand image");

                    String leftTxt = res.getLeftText();
                    if(leftTxt == null) {
                        leftTxt = "";
                    }
                    imageCount = 1;
                    robot.showCard(Robot.Hand.Left, getDrawable(left), leftTxt);
                }

                String right = res.getRightImage();
                if(right != null) {
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
