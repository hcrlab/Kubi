package uw.hcrlab.kubi.lesson.prompts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TranslatePrompt extends Prompt {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating prompt 3 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_translate_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // make buttons for the src text words
        int idx = 0;
        for (PromptData.Word word: this.data.words) {
            WordButtonFragment wordButton = new WordButtonFragment();
            wordButton.setText(word.text);
            String buttonTag = word.text + "-" + idx;

            Log.i(TAG, "Adding button " + buttonTag);
            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.l2_source_text, wordButton, buttonTag).commit();
            idx += 1;
        }

        // focus on the text input
        TextView resultText = (TextView) view.findViewById(R.id.l1_result_text);
        resultText.requestFocus();

        return view;
    }

    public void handleResults(Result res) {

    }
}