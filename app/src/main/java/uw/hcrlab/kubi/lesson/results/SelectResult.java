package uw.hcrlab.kubi.lesson.results;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 4/19/2016.
 */
public class SelectResult extends Result {
    private int correctIdx = 0;

    public SelectResult(boolean correct) {
        super(correct);
    }

    public boolean isCorrect() {
        return mIsCorrect;
    }

    public void setCorrectIdx (int correct) {
        correctIdx = correct;
    }

    public int getCorrectIndex() {
        return correctIdx;
    }
}
