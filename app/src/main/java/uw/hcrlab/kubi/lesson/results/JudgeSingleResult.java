package uw.hcrlab.kubi.lesson.results;

import java.util.ArrayList;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 5/9/2016.
 */
public class JudgeSingleResult extends Result {
    private Integer solution;

    public JudgeSingleResult(boolean correct) {
        super(correct);
    }

    public void setSolution(Integer sol) {
        solution = sol;
    }

    public Integer getSolution() {
        return solution;
    }
}
