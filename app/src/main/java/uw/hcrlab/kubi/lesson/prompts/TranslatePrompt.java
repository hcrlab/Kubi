package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;

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
        TextView srcText = (TextView) getView().findViewById(R.id.l2_source_text); // KubiLingoUtils.getViewByIdString("l2_source_text", view, this);
        srcText.setText(this.data.srcText);

        TextView resultText = (TextView) getView().findViewById(R.id.l1_result_text); // KubiLingoUtils.getViewByIdString("l1_result_text", view, this);
        resultText.requestFocus();

        return view;
    }
}