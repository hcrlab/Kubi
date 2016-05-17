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
import uw.hcrlab.kubi.lesson.ResultsDisplayHelper;
import uw.hcrlab.kubi.lesson.results.JudgeSingleResult;
import uw.hcrlab.kubi.lesson.results.TranslateResult;
import uw.hcrlab.kubi.robot.Eyes;

public class JudgeSinglePrompt extends Prompt implements AdapterView.OnItemSelectedListener {
    private static String TAG = JudgeSinglePrompt.class.getSimpleName();

    private String response;
    private ArrayList<String> optionStrings;
    private String promptString = "Select a Word";
    private ArrayAdapter<String> adapter;

    protected static boolean firstRun = true;

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
        optionStrings = new ArrayList<>();
        optionStrings.add(promptString);
        for (PromptData.Option option: data.options) {
            optionStrings.add(option.title);
        }

        // dropdown menu with options from prompt
        final Spinner spinner = (Spinner) view.findViewById(R.id.dropdown);
        adapter = new ArrayAdapter<>(App.getContext(), R.layout.spinner_item, optionStrings);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        if(firstRun) {
            robot.speech.say("For this type of question, select the correct word by touching the dropdown in the middle of the sentence.", "en");
            firstRun = false;
        }

        super.onResume();
    }

    public void handleResults(Result res) {
        cancelConfirm();

        View view = getView();

        if(view == null) {
            Log.e(TAG, "Unable to get JUDGE_SINGLE prompt view!");
            return;
        }

        JudgeSingleResult result = (JudgeSingleResult) res;

        final View usersText = view.findViewById(R.id.dropdown);

        robot.hideHint();

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);

            TextView correctText = (TextView) view.findViewById(R.id.judge_original_correct);

            ResultsDisplayHelper.showCorrectResult(correctText, response, usersText);
        } else {
            robot.look(Eyes.Look.LOOK_DOWN);

            TextView correctText = (TextView) view.findViewById(R.id.judge_correct_text);
            TextView incorrectText = (TextView) view.findViewById(R.id.judge_incorrect_text);

            ResultsDisplayHelper.showIncorrectResult(correctText, optionStrings.get(result.getSolution() + 1), incorrectText, response, usersText);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        response = (String) parent.getSelectedItem();

        if(response.equals(promptString)) {
            return;
        }

        robot.setPromptResponse(pos - 1);

        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, confirmationDelay);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
