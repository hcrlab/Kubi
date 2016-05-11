package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.Eyes;

public class JudgeMultiplePrompt extends Prompt implements AdapterView.OnItemSelectedListener {
    private static String TAG = JudgeSinglePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating JUDGE_MULTIPLE prompt fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_judge_multiple_prompt, container, false);
        if (savedInstanceState != null) {
            return view;
        }

        // L1 text to translate
        String[] parts = this.data.PromptText.split("[“”]");
        String l1String = "TEST L1 STRING";
        if (parts.length > 1) {
            l1String = parts[1];
        }
        TextView l1Text = (TextView) view.findViewById(R.id.l1_to_translate);
        l1Text.setText(l1String);

        // checkboxes for the options
        ViewGroup optionsContainer = (ViewGroup) view.findViewById(R.id.options_container);
        ArrayList<String> optionStrings = new ArrayList<>();
        for (PromptData.Option option: data.options) {
            Log.i(TAG, "option " + option);
            Log.i(TAG, "title " + option.title);
            optionStrings.add(option.title);

            CheckBox cb = (CheckBox) inflater.inflate(R.layout.check_box_item, optionsContainer, false);
            cb.setText("Yay!");
            optionsContainer.addView(cb);

            // TODO: figure out why the text is not showing up alongside the checkboxes
            // do we need to use a list adapter?
//            CheckBox checkBox = new CheckBox(this.getContext());
//            //checkBox.setText(option.title);
//            checkBox.setText("TEST OPTION TEXT");
//            checkBox.setTextSize(R.dimen.card_text_size);
//            optionsContainer.addView(checkBox);
        }

        return view;
    }

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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
//        Toast.makeText(parent.getContext(),
//        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//        Toast.LENGTH_SHORT).show();
        //String selectedText = (String)parent.getItemAtPosition(pos);
        //selection.setText(selectedText);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
