package uw.hcrlab.kubi.wizard;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Speech {
    private String text;
    private String language;

    @SuppressWarnings("unused")
    private Speech() {}

    Speech(String text, String language) {
        this.text = text;
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }
}
