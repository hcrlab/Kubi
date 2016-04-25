package uw.hcrlab.kubi.lesson.results;

import java.util.ArrayList;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 4/25/2016.
 */
public class NameResult extends Result {
    private String blame;

    private ArrayList<String> solutions;

    public NameResult(boolean correct) {
        super(correct);

        solutions = new ArrayList<>();
    }

    public void setBlame(String blame) {
        this.blame = blame;
    }

    public String getBlame() {
        return blame;
    }

    public boolean hasBlame() {
        return blame != null;
    }

    public void addSolution(String sol) {
        solutions.add(sol);
    }

    public ArrayList<String> getSolutions() {
        return solutions;
    }
}
