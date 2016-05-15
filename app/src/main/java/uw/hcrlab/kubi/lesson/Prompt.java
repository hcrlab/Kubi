package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.danikula.videocache.HttpProxyCacheServer;
import com.fasterxml.jackson.databind.deser.impl.NullProvider;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.robot.Body;
import uw.hcrlab.kubi.robot.Robot;

public abstract class Prompt extends Fragment {
    protected PromptData data;

    protected Robot robot;

    protected HttpProxyCacheServer proxy;

    protected String uid;

    protected long startTime;
    protected long finishTime;
    protected long milliseconds;

    protected int confirmationDelayLong = 2500;
    protected int confirmationDelay = 2000;

    protected Handler handler = new Handler();

    protected Runnable confirm = new Runnable() {
        @Override
        public void run() {
            robot.speech.shutup();
            robot.speech.say("Is that your final answer?", "en");
        }
    };

    protected void cancelConfirm() {
        handler.removeCallbacks(confirm);
        //robot.speech.shutup();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(data == null || uid == null) {
            throw new NullPointerException("You must set the data and UID for this prompt before adding it to a view!");
        }

        proxy = App.getProxy(getActivity());

        robot = Robot.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (robot != null && this.data != null && this.data.PromptText != null) {
            robot.body.move(Body.Action.FACE_FORWARD);
            robot.speech.say(this.data.PromptText, "en");
        }

        startTime = System.nanoTime();
    }

    public long getTotalTime() {
        finishTime = System.nanoTime();
        return (finishTime - startTime) / 1000000;
    }

    public void setData(PromptData data) {
        this.data = data;
    }

    public PromptData getData() {
        return this.data;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public abstract void handleResults(Result res);
}
