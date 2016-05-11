package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.JudgeSingleResult;
import uw.hcrlab.kubi.robot.Eyes;

public class JudgeSinglePrompt extends Prompt implements AdapterView.OnItemSelectedListener {
    private static String TAG = JudgeSinglePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating JUDGE_SINGLE prompt fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_judge_single_prompt, container, false);
        if (savedInstanceState != null) {
            return view;
        }

        // text before and after the dropdown
        TextView textBefore = (TextView) view.findViewById(R.id.text_before_dropdown);
        textBefore.setText(data.textBefore);
        TextView textAfter = (TextView) view.findViewById(R.id.text_after_dropdown);
        textAfter.setText(data.textAfter);

        // make an ArrayList of the options as strings
        ArrayList<String> optionStrings = new ArrayList<>();
        for (PromptData.Option option: data.options) {
            optionStrings.add(option.title);
        }

        // dropdown menu with options from prompt
        Spinner spinner = (Spinner) view.findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(App.getContext(),
                R.layout.spinner_item, optionStrings);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return view;
    }

    public void handleResults(Result res) {
        JudgeSingleResult result = (JudgeSingleResult) res;

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);
        } else {
            robot.look(Eyes.Look.LOOK_DOWN);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        robot.setPromptResponse(pos);

        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, confirmationDelay);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
