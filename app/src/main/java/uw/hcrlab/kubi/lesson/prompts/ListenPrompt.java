package uw.hcrlab.kubi.lesson.prompts;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.DrawableHelper;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.FaceAction;

public class ListenPrompt extends Prompt implements TextWatcher {
    private static String TAG = ListenPrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating LISTEN prompt fragment from " + this.data);

        View view = inflater.inflate(R.layout.fragment_listen_prompt, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // focus on the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        resultText.setShowSoftInputOnFocus(false);
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        robot.loadPronunciation(PromptData.combineWords(this.data.words));
    }

    @Override
    public void onResume() {
        super.onResume();

        robot.pronounceAfterSpeech(PromptData.combineWords(this.data.words));
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
            robot.act(FaceAction.GIGGLE);
        } else {
            robot.act(FaceAction.LOOK_DOWN);
        }
    }
}
