package uw.hcrlab.kubi.lesson.results;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 4/19/2016.
 */
public class SelectResult extends Result {
    public int mCorrectIdx = 0;
    public int mUsersResponse = 0;

    public SelectResult(boolean correct, int response, int idx) {
        super(correct);

        mCorrectIdx = idx;
        mUsersResponse = response;
    }

    public boolean isCorrect() {
        return mIsCorrect;
    }

    public int getCorrectIndex() {
        return mCorrectIdx;
    }

    public int getUsersResponse() {
        return mUsersResponse;
    }
}
