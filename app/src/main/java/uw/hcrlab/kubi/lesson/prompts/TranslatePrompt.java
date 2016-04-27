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

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.robot.Robot;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TranslatePrompt extends Prompt implements TextWatcher, View.OnClickListener {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    private ArrayList<WordButtonFragment> wordButtons;

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
        int idx = 0;
        for (PromptData.Word word: this.data.words) {
            WordButtonFragment wordButton = new WordButtonFragment();
            wordButton.setWord(word);
            wordButton.setOnClickListener(this);
            String buttonTag = generateButtonTag(word.text, idx);
            this.wordButtons.add(wordButton);

            Log.i(TAG, "Adding button " + buttonTag);
            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.l2_source_text, wordButton, buttonTag).commit();
            idx += 1;
        }

        // Setup the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        resultText.setShowSoftInputOnFocus(false); // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

//            wordButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Button button = (Button) view;
//                    // TODO: show the button's hint (using robot's hint-showing capability)
//                    // might have to implement onClickListener in parent fragment or in activity to do this?
//                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
//                            "clicked word button " + button.getText().toString(),
//                            Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            });

    private String generateButtonTag(String text, int idx) {
        return text + "-" + idx;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        TextView button = (TextView) v;
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "clicked word button " + button.getText().toString(),
                Toast.LENGTH_SHORT);
        toast.show();
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