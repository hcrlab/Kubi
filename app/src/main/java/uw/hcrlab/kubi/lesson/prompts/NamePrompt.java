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

        ArrayList<ImageView> imgs = new ArrayList<>();
        imgs.add((ImageView) view.findViewById(R.id.name_picture_1));
        imgs.add((ImageView) view.findViewById(R.id.name_picture_2));
        imgs.add((ImageView) view.findViewById(R.id.name_picture_3));

        for(int i = 0; i < this.data.images.size(); ++i) {
            PromptData.Image img = this.data.images.get(i);
            ImageView imgView = imgs.get(i);

            if(img.hasURL()) {
                ImageLoader.getInstance().displayImage(img.imageUrl, imgView);
            } else {
                // debug case, until we start passing image URLs through from  duolingo
                Drawable drawable = view.getResources().getDrawable(
                        DrawableHelper.getIdFromString(img.drawable), getActivity().getTheme());
                imgView.setImageDrawable(drawable);
            }
        }

        // focus on the text input
        EditText resultText = (EditText) view.findViewById(R.id.l1_result_text);
        resultText.setShowSoftInputOnFocus(false); // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final String[] parts = this.data.PromptText.split("[“”]");

        if(parts.length > 1) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    robot.act(FaceAction.LOOK_LEFT);
                    robot.showHint("\"" + parts[1] + "\"");
                }
            }, 1000);
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    robot.hideHint();
                }
            }, 7000);
        }
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
