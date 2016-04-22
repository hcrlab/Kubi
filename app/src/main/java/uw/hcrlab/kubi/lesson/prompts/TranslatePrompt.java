package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.Result;

public class TranslatePrompt extends Prompt {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating prompt 3 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_prompt_3, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // set src text according to prompt data
        TextView srcText = (TextView) view.findViewById(R.id.l2_source_text);
        srcText.setText(this.data.srcText);

        // focus on the text input
        TextView resultText = (TextView) view.findViewById(R.id.l1_result_text);
        resultText.requestFocus();

        return view;
    }

    public void handleResults(Result res) {

    }
}