package uw.hcrlab.kubi.lesson;

import android.support.v4.app.Fragment;
import android.view.View;

public class Prompt extends Fragment {
    protected PromptData data;
    private View view;
    public void update(PromptData data) {
        this.data = data;
    }
}
