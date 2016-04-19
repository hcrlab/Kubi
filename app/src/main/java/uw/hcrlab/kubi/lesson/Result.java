package uw.hcrlab.kubi.lesson;

/**
 * Created by Alexander on 4/18/2016.
 */
public abstract class Result {
    public boolean mIsCorrect = false;

    public Result(boolean correct) {
        mIsCorrect = correct;
    }

    public boolean isCorrect() {
        return mIsCorrect;
    }
}
