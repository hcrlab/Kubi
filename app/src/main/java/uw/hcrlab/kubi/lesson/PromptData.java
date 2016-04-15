package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;

public class PromptData {
    public int type;
    public ArrayList<Option> options;
    public String srcText;

    public PromptData() {
        this.options = new ArrayList<Option>();
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
    }
    

}