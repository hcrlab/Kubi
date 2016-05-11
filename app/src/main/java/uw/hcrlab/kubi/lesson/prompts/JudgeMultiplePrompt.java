package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.JudgeMultipleResult;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.Eyes;

public class JudgeMultiplePrompt extends Prompt implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = JudgeSinglePrompt.class.getSimpleName();

    private HashMap<Integer, PromptData.Option> optionIDs = new HashMap<>();
    private ArrayList<Integer> checked = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating JUDGE_MULTIPLE prompt fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_judge_multiple_prompt, container, false);
        if (savedInstanceState != null) {
            return view;
        }

        TextView l1Text = (TextView) view.findViewById(R.id.l1_to_translate);
        l1Text.setText(data.textBefore);

        // checkboxes for the options
        ViewGroup optionsContainer = (ViewGroup) view.findViewById(R.id.options_container);
        for (PromptData.Option option : data.options) {
            Integer id = View.generateViewId();
            optionIDs.put(id, option);

            CheckBox cb = (CheckBox) inflater.inflate(R.layout.check_box_item, optionsContainer, false);
            cb.setId(id);
            cb.setText(option.title);
            cb.setOnCheckedChangeListener(this);
            optionsContainer.addView(cb);
        }

        return view;
    }

    public void handleResults(Result res) {
        JudgeMultipleResult result = (JudgeMultipleResult) res;

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);
        } else {
            robot.look(Eyes.Look.LOOK_DOWN);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
        Integer id = cb.getId();
        Integer idx = optionIDs.get(id).idx;

        if(isChecked && !checked.contains(idx)) {
            checked.add(idx);
        } else if(!isChecked && checked.contains(idx)){
            checked.remove(checked.indexOf(idx));
        }

        Integer[] response = new Integer[checked.size()];

        robot.setPromptResponse(checked.toArray(response));

        handler.removeCallbacks(confirm);
        handler.postDelayed(confirm, confirmationDelay);
    }
}
