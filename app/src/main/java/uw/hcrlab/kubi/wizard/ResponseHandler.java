package uw.hcrlab.kubi.wizard;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import uw.hcrlab.kubi.robot.Action;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.wizard.model.Speech;
import uw.hcrlab.kubi.wizard.model.Task;

/**
 * Created by Alexander on 4/16/2015.
 */
public class ResponseHandler extends WizardHandler {
    public static String TAG = ResponseHandler.class.getSimpleName();

    private Robot kubi;

    public ResponseHandler(Robot kubi) {
        super("response");

        this.kubi = kubi;
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.child("handled").getValue(Boolean.class)) {
            for (DataSnapshot taskData : snap.child("tasks").getChildren()) {
                Task res = taskData.getValue(Task.class);

                Speech sp = res.getSpeech();

                if(sp != null) {
                    kubi.say(sp.getText(), sp.getLanguage());
                }

                String action = res.getAction();

                if(action != null) {
                    kubi.act(Action.valueOf(action));
                }

                String expr = res.getExpr();

                //What do we do with expressions?
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
