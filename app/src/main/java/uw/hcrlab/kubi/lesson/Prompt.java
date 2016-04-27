package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danikula.videocache.HttpProxyCacheServer;
import com.firebase.client.DataSnapshot;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.robot.Robot;

public abstract class Prompt extends Fragment {
    protected PromptData data;

    protected Robot robot;

    protected HttpProxyCacheServer mProxy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProxy = App.getProxy(getActivity());

        robot = Robot.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (robot != null && this.data != null && this.data.PromptText != null) {
            robot.say(this.data.PromptText, "en");
        }
    }

    public void setData(PromptData data) {
        this.data = data;
    }

    public abstract void handleResults(Result res);
}
