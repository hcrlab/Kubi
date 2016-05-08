package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.danikula.videocache.HttpProxyCacheServer;
import com.fasterxml.jackson.databind.deser.impl.NullProvider;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.robot.Robot;

public abstract class Prompt extends Fragment {
    protected PromptData data;

    protected Robot robot;

    protected HttpProxyCacheServer proxy;

    protected String uid;

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
            robot.speech.say(this.data.PromptText, "en");
        }
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
