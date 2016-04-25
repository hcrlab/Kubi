package uw.hcrlab.kubi.lesson.prompts;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FlashCardFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.SelectResult;
import uw.hcrlab.kubi.robot.Robot;

public class SelectPrompt extends Prompt implements FlashCardFragment.OnFlashCardSelectedListener {
    private static String TAG = SelectPrompt.class.getSimpleName();

    private ArrayList<String> mFlashCards;

    private Robot robot;

    private HttpProxyCacheServer mProxy;
    private HashMap<String, MediaPlayer> mPronunciations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating SELECT prompt from " + this.data);

        View view = inflater.inflate(R.layout.fragment_select_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        mProxy = App.getProxy(getActivity());

        // add the card fragments
        mFlashCards = new ArrayList<>();
        for (PromptData.Option option: this.data.options) {

            FlashCardFragment cardFragment = new FlashCardFragment();
            cardFragment.configure(option);
            cardFragment.setOnFlashCardSelectedListener(this);

            String tag = createOptionTag(option.idx);
            mFlashCards.add(tag);

            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            trans.add(R.id.prompt_options, cardFragment, tag).commit();
        }

        robot = Robot.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Load the audio resources
        Activity activity = getActivity();

        mPronunciations = new HashMap<>();

        for(PromptData.Option option: this.data.options) {
            String url = App.getAudioURL(option.title);

            if(url != null) {
                String audioUrl = mProxy.getProxyUrl(url);
                mPronunciations.put(createOptionTag(option.idx), MediaPlayer.create(activity, Uri.parse(audioUrl)));
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        robot.say(this.data.PromptText, "en");
    }

    @Override
    public void onStop() {
        super.onStop();

        // Release the audio resources
        for (Map.Entry kvp : mPronunciations.entrySet()) {
            MediaPlayer mp = (MediaPlayer) kvp.getValue();
            mp.release();
        }

        mPronunciations.clear();
    }

    private String createOptionTag(int index) {
        return "option-" + Integer.toString(index);
    }

    public void onFlashCardSelected(String tag) {
        FlashCardFragment flashCard;

        // Unselect all other cards
        for(String card : mFlashCards) {
            if(!card.equals(tag)) {
                flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(card);
                flashCard.unselect();
            }
        }

        flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(tag);

        // Play the pronunciation for this option
        if(mPronunciations.containsKey(tag)) {
            mPronunciations.get(tag).start();
        } else {
            robot.say(flashCard.getOption().title, "en");
        }

        // Notify the wizard that this card was selected
        robot.setPromptResponse(flashCard.getOption().idx);
    }


    public void handleResults(Result res) {
        SelectResult sr = (SelectResult) res;

        FlashCardFragment flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(createOptionTag(sr.getCorrectIndex()));
        flashCard.setCorrect();

        if(!res.isCorrect()) {
            flashCard = (FlashCardFragment) this.getFragmentManager().findFragmentByTag(createOptionTag(sr.getUsersResponse()));
            flashCard.setIncorrect();
        }
    }
}
