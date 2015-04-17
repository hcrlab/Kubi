package uw.hcrlab.kubi.wizard.model;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Task {
    private Speech speech;

    private String expr;
    private String action;

    private String image;
    private String[] buttons;

    @SuppressWarnings("unused")
    private Task() {}

    Task(Speech speech, String expr, String action) {
        this.speech = speech;
        this.expr = expr;
        this.action = action;
    }

    public Speech getSpeech() {
        return speech;
    }

    public String getExpr() {
        return expr;
    }

    public String getAction() {
        return action;
    }

    public String getImage() {
        return image;
    }

    public String[] getButtons() {
        return buttons;
    }
}
