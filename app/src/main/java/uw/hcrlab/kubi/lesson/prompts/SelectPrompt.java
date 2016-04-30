package uw.hcrlab.kubi.lesson.prompts;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FlashCard;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.SelectResult;
import uw.hcrlab.kubi.robot.FaceAction;

public class SelectPrompt extends Prompt implements FlashCard.OnFlashCardSelectedListener {
    private static String TAG = SelectPrompt.class.getSimpleName();

    private ArrayList<Integer> mFlashCards;

    private HashMap<String, MediaPlayer> mPronunciations;

    private int currentSelection = -1;

    private Handler handler = new Handler();
    private Runnable confirm = new Runnable() {
        @Override
        public void run() {
            robot.say("Is that your final answer?", "en");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating SELECT prompt from " + this.data);

        View view = inflater.inflate(R.layout.fragment_select_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        LinearLayout options = (LinearLayout) view.findViewById(R.id.prompt_options);

        // add the card fragments
        mFlashCards = new ArrayList<>();
        for (PromptData.Option option: this.data.options) {
            FlashCard card = (FlashCard) inflater.inflate(R.layout.flash_card, options, false);

            card.setOption(option);
            card.setOnFlashCardSelectedListener(this);
            options.addView(card);

            mFlashCards.add(card.getId());
        }

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
                String audioUrl = proxy.getProxyUrl(url);
                mPronunciations.put(createOptionTag(option.idx), MediaPlayer.create(activity, Uri.parse(audioUrl)));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final String[] parts = this.data.PromptText.split("[“”]");

        if(parts.length > 1) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    robot.act(FaceAction.LOOK_LEFT);
                    robot.showHint("\"" + parts[1] + "\"");
                }
            }, 1000);
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    robot.hideHint();
                }
            }, 7000);
        }
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

    public void onFlashCardSelected(FlashCard card) {
        View view = getView();

        if(view == null) {
            return;
        }

        int cardId = card.getId();
        FlashCard flashCard;

        // Unselect all other cards
        for(Integer id : mFlashCards) {
            if(!id.equals(cardId)) {
                flashCard = (FlashCard) view.findViewById(id);
                flashCard.unselect();
            }
        }

        // Only let one pronunciation play at any given time
        for(MediaPlayer mp : mPronunciations.values()) {
            if(mp.isPlaying()) {
                mp.pause();
                mp.seekTo(0);
            }
        }

        // Play the pronunciation for this option
        String tag = createOptionTag(card.getOption().idx);
        if(mPronunciations.containsKey(tag)) {
            mPronunciations.get(tag).start();
        } else {
            // Fallback if we don't have audio for this text
            robot.say(card.getOption().title, "en");
        }

        // Notify the wizard that this card was selected
        currentSelection = card.getOption().idx;
        robot.setPromptResponse(currentSelection);

        robot.shutup();
        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, 3000);
    }

    public void handleResults(Result res) {
        View view = getView();

        if(view == null) {
            return;
        }

        SelectResult result = (SelectResult) res;
        Integer correct = result.getCorrectIndex();

        for(Integer id : mFlashCards) {
            FlashCard card = (FlashCard) getView().findViewById(id);

            if(card.getOption().idx == correct) {
                card.setCorrect();
            } else if(card.isSelected()){
                card.setIncorrect();
            }
        }

        if(result.isCorrect()) {
            robot.act(FaceAction.GIGGLE);
        } else {
            robot.act(FaceAction.LOOK_DOWN);
        }
    }
}
