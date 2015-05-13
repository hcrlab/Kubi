package uw.hcrlab.kubi.wizard.model;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Task {
    private Speech speech;
    private String emotion;
    private String action;
    private String image;
    private String[] buttons;

    @SuppressWarnings("unused")
    private Task() {}

    Task(Speech speech, String emotion, String action, String image, String[] buttons) {
        this.speech = speech;
        this.emotion = emotion;
        this.action = action;
        this.image = image;
        this.buttons = buttons;
    }

    public Speech getSpeech() {
        return speech;
    }

    public String getEmotion() {
        return emotion;
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
