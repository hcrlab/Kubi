package uw.hcrlab.kubi.wizard.model;

/**
 * Created by Alexander on 4/14/2015.
 */
public class Task {
    private Speech speech;
    private String emotion;
    private String action;
    private String leftImage;
    private String leftText;
    private String rightImage;
    private String rightText;
    private String[] buttons;

    @SuppressWarnings("unused")
    private Task() {}

    Task(Speech speech, String emotion, String action, String leftImage, String leftText, String rightImage, String rightText, String[] buttons) {
        this.speech = speech;
        this.emotion = emotion;
        this.action = action;
        this.leftImage = leftImage;
        this.leftText = leftText;
        this.rightImage = rightImage;
        this.rightText = rightText;
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

    public String getLeftImage() {
        return leftImage;
    }

    public String getLeftText() {
        return leftText;
    }

    public String getRightImage() {
        return rightImage;
    }

    public String getRightText() {
        return rightText;
    }

    public String[] getButtons() {
        return buttons;
    }
}
