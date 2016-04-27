package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.FramedImageFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;

public class NamePrompt extends Prompt implements TextWatcher {
    private static String TAG = NamePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating NAME prompt fragment from " + this.data);

        View view = inflater.inflate(R.layout.fragment_name_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }


        for (PromptData.Image image: this.data.images) {
            FramedImageFragment imageFragment = new FramedImageFragment();
            imageFragment.configure(image);

            Log.i(TAG, "making image " + image.toString());

            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.images, imageFragment).commit();
        }

        // focus on the text input
        TextView resultText = (TextView) view.findViewById(R.id.l1_result_text);
        resultText.setShowSoftInputOnFocus(false); // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (robot != null) {
            robot.setPromptResponse(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void handleResults(Result res) {

    }
}
