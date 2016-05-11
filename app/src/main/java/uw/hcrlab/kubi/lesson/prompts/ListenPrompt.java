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
import android.widget.Toast;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.Eyes;

public class ListenPrompt extends Prompt implements TextWatcher {
    private static String TAG = ListenPrompt.class.getSimpleName();

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
        resultText.setShowSoftInputOnFocus(false);
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        robot.speech.pronounceUrlAfterSpeech(data.slowAudio);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (robot != null) {
            robot.setPromptResponse(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}

    public void handleResults(Result res) {
        NameResult result = (NameResult) res;

        if(result.hasBlame()) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), result.getBlame(), Toast.LENGTH_SHORT);
            toast.show();
        }

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);
        } else {
            robot.look(Eyes.Look.LOOK_DOWN);
        }
    }
}
