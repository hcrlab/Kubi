package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uw.hcrlab.kubi.lesson.PromptData;

public class FlashCardFragment extends Fragment {
    private static String TAG = FlashCardFragment.class.getSimpleName();
    private PromptData.Option option;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating flash card fragment ...");
        return inflater.inflate(R.layout.fragment_flash_card, container, false);
    }

    public void configure(PromptData.Option option) {
        this.option = option;
    }
}
