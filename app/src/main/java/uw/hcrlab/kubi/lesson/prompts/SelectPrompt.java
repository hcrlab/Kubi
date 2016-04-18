package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FlashCardFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;

public class SelectPrompt extends Prompt {
    private static String TAG = SelectPrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating select prompt from " + this.data);

        View view = inflater.inflate(R.layout.fragment_prompt_1, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // add the card fragments
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

        for (PromptData.Option option: this.data.options) {
            FlashCardFragment cardFragment = new FlashCardFragment();
            cardFragment.configure(option);

            trans.add(R.id.prompt_options, cardFragment, "option-" + Integer.toString(option.idx));
        }

        trans.commit();

        return view;
    }
}
