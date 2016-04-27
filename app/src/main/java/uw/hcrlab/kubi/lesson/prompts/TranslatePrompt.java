package uw.hcrlab.kubi.lesson.prompts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.HintData;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.robot.Robot;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TranslatePrompt extends Prompt implements TextWatcher, View.OnClickListener {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    private ArrayList<WordButtonFragment> wordButtons;
    private HashMap<Integer, PromptData.Word> wordsById;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating TRANSLATE prompt fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_translate_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // make buttons for the src text words
        this.wordButtons = new ArrayList<>();
        this.wordsById = new HashMap<>();
        int idx = 0;
        for (PromptData.Word word: this.data.words) {
            WordButtonFragment wordButtonFragment = new WordButtonFragment();
            wordButtonFragment.setId(idx);
            wordButtonFragment.setWord(word);
            wordButtonFragment.setOnClickListener(this);
            String buttonTag = generateButtonTag(word.text, idx);

            this.wordButtons.add(wordButtonFragment);
            this.wordsById.put(idx, word);

            Log.i(TAG, "Adding button " + buttonTag);
            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.l2_source_text, wordButtonFragment, buttonTag).commit();
            idx += 1;
        }

        // Setup the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        resultText.setShowSoftInputOnFocus(false); // Never show soft keyboard. Forces use of bluetooth keyboard
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    private String generateButtonTag(String text, int idx) {
        return text + "-" + idx;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "clicked " + wordsById.get(v.getId()));
        TextView button = (TextView) v;
        PromptData.Word word = wordsById.get(v.getId());
        // can safely assume the word has hints; it if didn't it would not be clickable
        robot.showHint(word.hints);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        robot.setPromptResponse(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void handleResults(Result res) {

    }
}