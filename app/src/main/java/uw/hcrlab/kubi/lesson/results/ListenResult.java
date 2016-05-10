package uw.hcrlab.kubi.lesson.results;

import uw.hcrlab.kubi.lesson.Result;

/**
 * Created by Alexander on 5/10/2016.
 */
public class ListenResult extends Result {
    private String source;
    private String translation;

    public ListenResult(boolean correct) {
        super(correct);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String src) {
        source = src;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String trans) {
        translation = trans;
    }
}
