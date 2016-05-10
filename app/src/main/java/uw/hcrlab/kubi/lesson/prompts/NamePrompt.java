package uw.hcrlab.kubi.lesson.prompts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.DrawableHelper;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.robot.Eyes;

public class NamePrompt extends Prompt implements TextWatcher {
    private static String TAG = NamePrompt.class.getSimpleName();

    private String response;
    private int duration;

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
        //resultText.setShowSoftInputOnFocus(false); // Make sure the on-screen keyboard never shows. Forces the use of the bluetooth keyboard
        resultText.requestFocus();
        resultText.addTextChangedListener(this);

        duration = getResources().getInteger(android.R.integer.config_longAnimTime);

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
                    robot.look(Eyes.Look.LOOK_LEFT);
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
            response = s.toString();
            robot.setPromptResponse(response);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void handleResults(Result res) {
        View view = getView();

        if(view == null) {
            Log.e(TAG, "Unable to get TRANSLATE prompt view!");
            return;
        }

        NameResult result = (NameResult) res;

        robot.hideHint();

        if(result.hasBlame()) {
            robot.showHint(result.getBlame());
        }

        final View usersText = view.findViewById(R.id.l1_result_text);

        if(result.isCorrect()) {
            robot.look(Eyes.Look.HAPPY);

            TextView correctText = (TextView) view.findViewById(R.id.translate_original_correct);

            if(usersText == null || correctText == null) {
                Log.e(TAG, "Unable to get views for displaying TRANSLATE results!");
                return;
            }

            correctText.setText(response);
            correctText.setAlpha(0f);
            correctText.setVisibility(View.VISIBLE);

            correctText.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null);

            usersText.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            usersText.setVisibility(View.GONE);
                        }
                    });
        } else {
            robot.look(Eyes.Look.SAD);

            if(result.hasBlame()) {
                robot.showHint(result.getBlame());
            }

            TextView correctText = (TextView) view.findViewById(R.id.translate_correct_text);
            TextView incorrectText = (TextView) view.findViewById(R.id.translate_incorrect_text);

            if(usersText == null || correctText == null || incorrectText == null) {
                Log.e(TAG, "Unable to get views for displaying TRANSLATE results!");
                return;
            }

            correctText.setText(result.getSolutions().get(0));
            correctText.setAlpha(0f);
            correctText.setVisibility(View.VISIBLE);

            correctText.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null);

            incorrectText.setText(response);
            incorrectText.setAlpha(0f);
            incorrectText.setVisibility(View.VISIBLE);

            incorrectText.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null);

            usersText.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            usersText.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
