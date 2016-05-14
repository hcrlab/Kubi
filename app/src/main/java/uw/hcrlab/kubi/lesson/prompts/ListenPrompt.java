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
import android.widget.TextView;
import android.widget.Toast;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.ResultsDisplayHelper;
import uw.hcrlab.kubi.lesson.results.JudgeSingleResult;
import uw.hcrlab.kubi.lesson.results.ListenResult;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.Eyes;

public class ListenPrompt extends Prompt implements TextWatcher {
    private static String TAG = ListenPrompt.class.getSimpleName();

    private String response;

    protected static boolean firstRun = true;

    private View.OnClickListener repeatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "clicked " + view.getId());
            robot.speech.pronounce(data.PromptText);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating LISTEN prompt fragment from " + this.data);

        View view = inflater.inflate(R.layout.fragment_listen_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // Repeat buttons
        // TODO: implement the ability to pronounce the same text at different speeds
        Button repeat_button = (Button) view.findViewById(R.id.repeat_button);
        repeat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.speech.pronounceUrl(data.normalAudio);
            }
        });

        Button repeat_slow_button = (Button) view.findViewById(R.id.repeat_slow_button);
        repeat_slow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.speech.pronounceUrl(data.slowAudio);
            }
        });

        // focus on the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        //resultText.setShowSoftInputOnFocus(false);
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        if(firstRun) {
            robot.speech.say("For this type of question, type what you hear on my keyboard below.", "en");
            firstRun = false;
        }

        super.onResume();

        robot.speech.pronounceUrlAfterSpeech(data.slowAudio);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        response = s.toString();
        robot.setPromptResponse(response);

        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, confirmationDelayLong);
    }

    @Override
    public void afterTextChanged(Editable s) {}

    public void handleResults(Result res) {
        cancelConfirm();

        View view = getView();

        if(view == null) {
            Log.e(TAG, "Unable to get JUDGE_SINGLE prompt view!");
            return;
        }

        ListenResult result = (ListenResult) res;

        final View usersText = view.findViewById(R.id.l1_result_text);

        robot.hideHint();

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);

            TextView correctText = (TextView) view.findViewById(R.id.listen_original_correct);

            ResultsDisplayHelper.showCorrectResult(correctText, response, usersText);
        } else {
            robot.look(Eyes.Look.LOOK_DOWN);

            TextView correctText = (TextView) view.findViewById(R.id.listen_correct_text);
            TextView incorrectText = (TextView) view.findViewById(R.id.listen_incorrect_text);

            ResultsDisplayHelper.showIncorrectResult(correctText, result.getSource(), incorrectText, response, usersText);

            robot.showHint("Translation: " + result.getTranslation());
        }
    }
}
