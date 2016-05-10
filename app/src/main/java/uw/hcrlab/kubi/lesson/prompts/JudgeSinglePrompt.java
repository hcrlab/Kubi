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
import android.widget.Toast;

import java.util.ArrayList;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.FaceAction;

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

        // TODO: figure out why text is not showing up in the spinner once it has been selected.

        return view;
    }

    public void handleResults(Result res) {
        NameResult result = (NameResult) res;

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
