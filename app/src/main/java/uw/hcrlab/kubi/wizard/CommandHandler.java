package uw.hcrlab.kubi.wizard;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

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

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.child("handled").getValue(Boolean.class)) {
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
                Log.e(TAG, action);
                if(action != null) {
                    Log.d(TAG, "Got action request: " + action);
                    robot.perform(Action.valueOf(action));
                }

                String image = res.getImage();
                if(image != null) {
                    Log.d(TAG, "You need to display an image!");
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
