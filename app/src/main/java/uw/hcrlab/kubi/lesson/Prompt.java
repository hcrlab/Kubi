package uw.hcrlab.kubi.lesson;

import android.support.v4.app.Fragment;
import android.view.View;

import com.firebase.client.DataSnapshot;

public abstract class Prompt extends Fragment {
    protected PromptData data;

    private View view;

    public void setData(PromptData data) {
        this.data = data;
    }

    public abstract void handleResults(Result res);
}
