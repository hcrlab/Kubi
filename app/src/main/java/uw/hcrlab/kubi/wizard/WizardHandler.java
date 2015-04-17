package uw.hcrlab.kubi.wizard;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import uw.hcrlab.kubi.App;

/**
 * Created by Alexander on 4/16/2015.
 */
public abstract class WizardHandler implements ChildEventListener{
    private Firebase ref;

    public WizardHandler(String path) {
        this.ref = App.getFirebase().child(path);
    }

    public void Listen() {
        this.ref.addChildEventListener(this);
    }

    public void Stop() {
        this.ref.removeEventListener(this);
    }
}
