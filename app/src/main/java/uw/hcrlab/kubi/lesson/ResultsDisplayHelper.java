package uw.hcrlab.kubi.lesson;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 5/11/2016.
 */
public class ResultsDisplayHelper {
    private static String TAG = ResultsDisplayHelper.class.getSimpleName();

    public static int duration;

    public static void init(App application) {
        duration = application.getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    public static void showCorrectResult(TextView view, String text, final View old) {
        if(old == null || view == null) {
            Log.e(TAG, "Results cannot be displayed without the appropriate views!");
            return;
        }

        view.setText(text);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        old.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        old.setVisibility(View.GONE);
                    }
                });
    }

    public static void showIncorrectResult(TextView correct, String correctText, TextView incorrect, String incorrectText, final View old) {
        if(old == null || correct == null || incorrect == null) {
            Log.e(TAG, "Results cannot be displayed without the appropriate views!");
            return;
        }

        correct.setText(correctText);
        correct.setAlpha(0f);
        correct.setVisibility(View.VISIBLE);

        correct.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        incorrect.setText(incorrectText);
        incorrect.setAlpha(0f);
        incorrect.setVisibility(View.VISIBLE);

        incorrect.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        old.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        old.setVisibility(View.GONE);
                    }
                });
    }
}
