package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FlashCard;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.SelectResult;
import uw.hcrlab.kubi.robot.FaceAction;

public class SelectPrompt extends Prompt implements FlashCard.FlashCardListener {
    private static String TAG = SelectPrompt.class.getSimpleName();

    private ArrayList<Integer> flashCardIds;

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
        flashCardIds = new ArrayList<>();
        for (PromptData.Option option: this.data.options) {
            FlashCard card = (FlashCard) inflater.inflate(R.layout.flash_card, options, false);

            card.setOption(option);
            card.setOnFlashCardSelectedListener(this);
            options.addView(card);

            flashCardIds.add(card.getId());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Load the audio resources
        for(PromptData.Option option: this.data.options) {
            robot.loadPronunciation(option.title);
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

        for(PromptData.Option option : this.data.options) {
            robot.unloadPronunciation(option.title);
        }
    }

    @Override
    public void onFlashCardSelected(FlashCard card) {
        View view = getView();

        if(view == null) {
            return;
        }

        int cardId = card.getId();
        FlashCard flashCard;

        // Unselect all other cards
        for(Integer id : flashCardIds) {
            if(!id.equals(cardId)) {
                flashCard = (FlashCard) view.findViewById(id);
                flashCard.unselect();
            }
        }

        // Notify the wizard that this card was selected
        robot.setPromptResponse(card.getOption().idx);

        robot.shutup();
        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, 3000);

        robot.pronounce(card.getOption().title);
    }

    @Override
    public void onSelectedFlashCardClicked(FlashCard card) {
        View view = getView();

        if(view == null) {
            return;
        }

        robot.shutup();
        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, 3000);

        robot.pronounce(card.getOption().title);
    }

    public void handleResults(Result res) {
        View view = getView();

        if(view == null) {
            return;
        }

        SelectResult result = (SelectResult) res;
        Integer correct = result.getCorrectIndex();

        for(Integer id : flashCardIds) {
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
