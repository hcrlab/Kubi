package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

public class PromptData {
    // Corresponds with question type numbers in the google doc slides
    public PromptTypes type;

    // For flash cards and other multiple choice elements
    public ArrayList<Option> options;

    // For multi-image display (e.g. NamePrompt)
    public ArrayList<Option> images;

    // The prompt displayed/spoken to the learner
    public String PromptText;

    // TODO: Replace srcText with an array of Word objects
    // The main text to be translated
    public String srcText;
    public ArrayList<Word> words;

    public PromptData() {
        this.options = new ArrayList<>();
        this.images = new ArrayList<>();
        this.words = new ArrayList<>();
    }

    @Override
    public String toString() {
        String optionsString = "";
        for (Option option: this.options) {
            optionsString += option.toString() + ", ";
        }
        String imagesString = "";
        for (Option image: this.images) {
            imagesString += image.toString() + ", ";
        }
        String wordString = "";
        for (Word word: this.words) {
            wordString += word.text + " ";
        }

        return String.format(Locale.US,
                "PromptData {type=%s, srcText=%s, words=%s, options={%s}, images={%s}}",
                this.type, this.srcText, wordString, optionsString, imagesString);
    }

    public static class Word {
        public int index;
        public String text;
        public ArrayList<String> hints;

        public Word(int index, String text) {
            this.index = index;
            this.text = text;
            this.hints = new ArrayList<>();
        }

        public void addHint(String hint) {
            this.hints.add(hint);
        }
    }

    // for selectable options (e.g. SelectPrompt) and non-selectable options (e.g. NamePrompt)
    public static class Option {
        // will have audio field

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
            return String.format(Locale.US, "Option {idx=%d, title=%s, image=%s, drawable=%s}",
                    this.idx, this.title, this.imageUrl, this.drawable);
        }
    }
    

}