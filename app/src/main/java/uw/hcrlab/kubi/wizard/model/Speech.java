package uw.hcrlab.kubi.wizard.model;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Speech {
    private String text;
    private String language;
    private int speed;

    @SuppressWarnings("unused")
    private Speech() {}

    Speech(String text, String language, int speed) {
        this.text = text;
        this.language = language;
        this.speed = speed;
    }

    public String getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }

    public int getSpeed() {
        return speed;
    }
}
