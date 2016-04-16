package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

public class PromptData {
    // Corresponds with question type numbers in the google doc slides
    public PromptTypes type;

    // For flash cards and other multiple choice elements
    public ArrayList<Option> options;

    // The main text to be translated
    public String srcText;

    public PromptData() {
        this.options = new ArrayList<Option>();
    }

    @Override
    public String toString() {
        String optionsString = "";
        for (Option option: this.options) {
            optionsString += option.toString() + ", ";
        }
        return String.format(Locale.US, "PromptData {type=%s, options={%s}, srcText=%s}",
                this.type, optionsString, this.srcText);
    }

    public static class Option {
        public Option(int idx, String text) {
            this.idx = idx;
            this.text = text;
        }
        // will have audio field
        // will have drawable field
        public int idx;
        public String text;

        @Override
        public String toString() {
            return String.format(Locale.US, "Option {idx=%d, text=%s}", this.idx, this.text);
        }
    }
    

}