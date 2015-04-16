package uw.hcrlab.kubi.wizard;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import uw.hcrlab.kubi.App;

/**
 * Created by Alexander on 4/16/2015.
 */
public class ResponseHandler implements ChildEventListener {
    public static String TAG = ResponseHandler.class.getSimpleName();

    private Firebase ref;

    public ResponseHandler() {
        this.ref = App.getFirebase().child("response");
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        Log.d(TAG, "Child Added!");

        if(!snap.child("handled").getValue(Boolean.class)) {
            Log.d(TAG, "Child Added - unhandled!");
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
