package uw.hcrlab.kubi.wizard;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.robot.Robot;

/**
 * Created by Alexander on 4/16/2015.
 */
public abstract class WizardHandler implements ChildEventListener{
    private Firebase ref;
    protected Robot robot;

    public WizardHandler(String path) {
        this.ref = App.getFirebase().child(path);
        this.robot = Robot.getInstance();
    }

    public void Listen() {
        this.ref.addChildEventListener(this);
    }

    public void Stop() {
        this.ref.removeEventListener(this);
    }
}
