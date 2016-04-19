package uw.hcrlab.kubi.lesson;

/**
 * Created by Alexander on 4/18/2016.
 */
public class Result {
    public boolean mIsCorrect = false;
    public int mCorrectIdx = 0;
    public int mUsersResponse = 0;

    public Result(boolean correct, int response, int idx) {
        mIsCorrect = correct;
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
