package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 5/12/2016.
 */
public class LessonCompletePrompt extends Prompt {

    private int lesson = 0;

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_complete, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        TextView tv = (TextView) view.findViewById(R.id.complete_remaining_msg);

        if(lesson == 1) {
            tv.setText("2 Lessons Remaining");
        } else if(lesson == 2) {
            tv.setText("1 Lesson Remaining");
        } else {
            tv.setText("That's It!");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(lesson == 1) {
            robot.speech.say("Yay! You finished the first lesson. Two more to go", "en");
        } else if(lesson == 2) {
            robot.speech.say("Yay! You finished the second lesson. One more to go", "en");
        } else {
            robot.speech.say("Great work! That's the end of the lessons!", "en");
            robot.showHint("Thanks!");
        }
    }

    @Override
    public void handleResults(Result res) {

    }
}
