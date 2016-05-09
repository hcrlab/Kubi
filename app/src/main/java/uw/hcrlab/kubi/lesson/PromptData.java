package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

public class PromptData {
    // Corresponds with question type numbers in the google doc slides
    public PromptTypes type;

    // For flash cards and other multiple choice elements
    public ArrayList<Option> options;

    // For multi-image display (e.g. NamePrompt)
    public ArrayList<Image> images;

    // For prompts that display words with hints
    public ArrayList<Word> words;

    // For JudgePrompt, the text before and after the dropdown
    public String textBefore;
    public String textAfter;

    // The prompt displayed/spoken to the learner
    public String PromptText;

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
        for (Image image: this.images) {
            imagesString += image.toString() + ", ";
        }

        String wordString = "";
        for (Word word: this.words) {
            wordString += word.text + " ";
        }

        return String.format(Locale.US,
                "PromptData {type=%s, prompt=%s, words=%s, options={%s}, images={%s}}",
                this.type, this.PromptText, wordString, optionsString, imagesString);
    }

    public static class Hint {
        // will extend so this can be an "explain" or "conjugate" button when applicable
        public String text;

        public Hint setText(String text) {
            this.text = text;
            return this;
        }

        public String toString() {
            return String.format(Locale.US, "Hint {%s}", this.text);
        }
    }

    public static class HintCollection {
        public ArrayList<Hint> details;

        public HintCollection() {
            this.details = new ArrayList<>();
        }

        public String toString() {
            String detailString = "";
            for (Hint detail: this.details) {
                detailString += detail.toString() + ", ";
            }
            return String.format(Locale.US, "HintCollection {details=%s}", detailString);
        }

        public boolean isEmpty() {
            return this.details.size() < 1;
        }
    }

    public static class Word {
        public int index;
        public String text;
        public HintCollection hints;

        public Word(String text) {
            this.text = text;
            this.hints = new HintCollection();
        }

        public Word(int index, String text) {
            this.index = index;
            this.text = text;
            this.hints = new HintCollection();
        }

        public boolean hasHint() {
            return !this.hints.isEmpty();
        }

        public Word addHint(String hint) {
            this.hints.details.add(new Hint().setText(hint));
            return this;
        }
    }

    public static String combineWords(ArrayList<Word> words) {
        String out = "";

        for(Word word : words) {
            out += word.text;
        }

        return out;
    }

    public static class Image {
        public String imageUrl;
        public String drawable;

        public Image(String res, boolean isURL) {
            if(isURL) {
                imageUrl = res;
            } else {
                drawable = res;
            }
        }

        public boolean hasURL() {
            return this.imageUrl != null && !this.imageUrl.isEmpty();
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "Image {url=%s, drawable=%s}",
                    this.imageUrl, this.drawable);
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