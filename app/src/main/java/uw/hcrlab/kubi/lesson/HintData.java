package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by lrperlmu on 4/25/16.
 */
public class HintData {
    public ArrayList<HintDetail> details;
    public HintData() {
        this.details = new ArrayList<>();
    }
    public String toString() {
        String detailString = "";
        for (HintDetail detail: this.details) {
            detailString += detail.toString() + ", ";
        }
        return String.format(Locale.US, "HintData {details=%s}", detailString);
    }

    public static class HintDetail {
        String text;
        public HintDetail setText(String text) {
            this.text = text;
            return this;
        }
        public String toString() {
            return String.format(Locale.US, "HintDetail {%s}", this.text);
        }
    }
}
