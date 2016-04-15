package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;

public class PromptData {
    public int type;
    public ArrayList<Option> options;
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
        return String.format("PromptData {type=%d, options={%s}, srcText=%s}",
                this.type, optionsString, this.srcText);
    }

    public static class Option {
        public Option(int idx, String text) {
            //this.image = image;
            this.idx = idx;
            this.text = text;
        }
        // audio
        //public String image;
        public int idx;
        public String text;

        @Override
        public String toString() {
            return String.format("Option {idx=%d, text=%s}", this.idx, this.text);
        }
    }
    

}