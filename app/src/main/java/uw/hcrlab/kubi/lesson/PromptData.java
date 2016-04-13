package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;

public class PromptData {

    public ArrayList<Option> options;
    public String srcText;

    private class Option {
        // image
        // audio
        public int idx;
        public String text;
    }
    

}