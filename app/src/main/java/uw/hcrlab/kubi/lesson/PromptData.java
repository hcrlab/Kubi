package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

public class PromptData {
    // Corresponds with question type numbers in the google doc slides
    public PromptTypes type;

    // For flash cards and other multiple choice elements
    public ArrayList<Option> options;

    // The prompt displayed/spoken to the learner
    public String PromptText;

    // TODO: Replace srcText with an array of Word objects

    // The main text to be translated
    public String srcText;

    public PromptData() {
        this.options = new ArrayList<>();
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

    public static class Word {
        public String text;
        public ArrayList<String> hints;

        public Word(String text) {
            this.text = text;
            this.hints = new ArrayList<>();
        }

        public void addHint(String hint) {
            this.hints.add(hint);
        }
    }

    public static class Option {
        // will have audio field
        // will have drawable field

        public int idx;
        public String title;
        public String imageUrl;
        public String drawable;

        public Option(int idx, String title) {
            this.idx = idx;
            this.title = title;
        }

        public Option setURL(String url) {
            this.imageUrl = url;
            return this;
        }

        public Option setDrawable(String key) {
            this.drawable = key;
            return this;
        }

        public boolean hasURL() {
            return this.imageUrl != null && !this.imageUrl.isEmpty();
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "Option {idx=%d, title=%s, image=%s}", this.idx, this.title, this.imageUrl);
        }
    }
    

}