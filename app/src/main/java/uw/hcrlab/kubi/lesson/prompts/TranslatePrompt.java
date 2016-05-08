package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.TranslateResult;
import uw.hcrlab.kubi.robot.FaceAction;

public class TranslatePrompt extends Prompt implements TextWatcher {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    private HashMap<Integer, PromptData.Word> wordsById;

    private View.OnClickListener hintClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "clicked " + wordsById.get(view.getId()));
            PromptData.Word word = wordsById.get(view.getId());
            robot.showHint(word.hints);
            robot.pronounce(word.text);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating TRANSLATE prompt fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_translate_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        this.wordsById = new HashMap<>();

       LinearLayout sourceText = (LinearLayout) view.findViewById(R.id.l2_source_text);
       for (PromptData.Word word: this.data.words) {
           // Filter whitespace words
           if(word.text.trim().length() > 0) {
               sourceText.addView(getWordView(inflater, sourceText, word));
           }
       }

        // Setup the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        // Never show soft keyboard. Forces use of bluetooth keyboard
        resultText.setShowSoftInputOnFocus(false);
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        robot.loadPronunciation(PromptData.combineWords(this.data.words));
    }

    @Override
    public void onResume() {
        super.onResume();
        robot.pronounceAfterSpeech(PromptData.combineWords(this.data.words));
    }

    @Override
    public void onStop() {
        super.onStop();

        for(PromptData.Word word : this.data.words) {
            if(word.hasHint()) {
                robot.unloadPronunciation(word.text);
            }
        }
    }

    private View getWordView(LayoutInflater inflater, ViewGroup container, PromptData.Word word) {
        View view;

        if(word.hasHint()) {
            Log.i(TAG, "Creating word button: " + word.text);

            int id = View.generateViewId();
            wordsById.put(id, word);

            robot.loadPronunciation(word.text);

            view = inflater.inflate(R.layout.word_button, container, false);

            Button wordButton = (Button) view.findViewById(R.id.button);
            wordButton.setText(word.text);
            wordButton.setId(id);  // TODO: is this safe? (http://tinyurl.com/lzeu2at suggests it is)
            wordButton.setOnClickListener(hintClickListener);
        } else {
            Log.i(TAG, "Creating word:" + word.text);

            view = inflater.inflate(R.layout.word_plain, container, false);

            TextView wordView = (TextView) view.findViewById(R.id.word_view);
            wordView.setText(word.text);
        }

        return view;
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
        TranslateResult result = (TranslateResult) res;

        if(result.hasBlame()) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), result.getBlame(), Toast.LENGTH_SHORT);
            toast.show();
        }

        if(result.isCorrect()) {
            robot.act(FaceAction.GIGGLE);
        } else {
            robot.act(FaceAction.LOOK_DOWN);
        }
    }
}