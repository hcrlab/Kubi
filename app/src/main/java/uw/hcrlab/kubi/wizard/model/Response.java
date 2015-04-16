package uw.hcrlab.kubi.wizard.model;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Response {
    private Speech speech;
    private String expr;
    private String action;

    @SuppressWarnings("unused")
    private Response() {}

    Response(Speech speech, String expr, String action) {
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
}
