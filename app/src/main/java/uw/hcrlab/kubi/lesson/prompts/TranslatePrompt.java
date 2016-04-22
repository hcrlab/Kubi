package uw.hcrlab.kubi.lesson.prompts;

import android.content.Context;
import android.os.Bundle;
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
        ViewGroup textFrame = (ViewGroup) view.findViewById(R.id.l2_source_text);
        for (PromptData.Word word: this.data.words) {
            Context context = getActivity().getApplicationContext();
            Button wordButton = new Button(context);
            wordButton.setText(word.text);
            //wordButton.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            String buttonTag = word.text + "-" + idx;
            wordButton.setTag(buttonTag);
            idx += 1;
            wordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button button = (Button) view;
                    // TODO: show the button's hint
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "clicked word button " + button.getText().toString(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            Log.i(TAG, "Adding button " + buttonTag);
            textFrame.addView(wordButton);
        }

        // focus on the text input
        TextView resultText = (TextView) view.findViewById(R.id.l1_result_text);
        resultText.requestFocus();

        return view;
    }

    public void handleResults(Result res) {

    }
}