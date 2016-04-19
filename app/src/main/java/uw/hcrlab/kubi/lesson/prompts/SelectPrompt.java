package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FlashCardFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;

public class SelectPrompt extends Prompt implements FlashCardFragment.OnFlashCardSelectedListener {
    private static String TAG = SelectPrompt.class.getSimpleName();

    private ArrayList<String> mFlashCards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating select prompt from " + this.data);

        View view = inflater.inflate(R.layout.fragment_prompt_1, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        mFlashCards = new ArrayList<>();

        // add the card fragments
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

        for (PromptData.Option option: this.data.options) {
            FlashCardFragment cardFragment = new FlashCardFragment();
            cardFragment.configure(option);
            cardFragment.setOnFlashCardSelectedListener(this);

            String tag = createOptionTag(option.idx);
            mFlashCards.add(tag);
            trans.add(R.id.prompt_options, cardFragment, tag);
        }

        trans.commit();

        return view;
    }

    private String createOptionTag(int index) {
        return "option-" + Integer.toString(index);
    }

    public void onFlashCardSelected(String tag) {
        for(String card : mFlashCards) {
            if(!card.equals(tag)) {
                FlashCardFragment flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(card);
                flashCard.unselect();
            }
        }
    }

    public void handleResults(Result res) {
        FlashCardFragment flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(createOptionTag(res.getCorrectIndex()));
        flashCard.setCorrect();

        if(!res.isCorrect()) {
            flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(createOptionTag(res.getUsersResponse()));
            flashCard.setIncorrect();
        }
    }
}
