package uw.hcrlab.kubi.lesson.results;

import java.util.ArrayList;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 5/9/2016.
 */
public class JudgeMultipleResult extends Result {
    private ArrayList<Integer> solutions;

    public JudgeMultipleResult(boolean correct) {
        super(correct);
    }

    public void addSolution(Integer sol) {
        solutions.add(sol);
    }

    public ArrayList<Integer> getSolutions() {
        return solutions;
    }
}
