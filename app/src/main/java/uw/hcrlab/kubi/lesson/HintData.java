package uw.hcrlab.kubi.lesson;

import java.util.ArrayList;
import java.util.Locale;

/**
 * This is an object (rather than just using a collection of HintDetail objects
 * because it makes sense to think of a HintData as one object, for example, when
 * calling robot.showHint(hintData), we don't want to show each line of the hint
 * individually, we want to show it all as one.
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
        // will extend so this can be an "explain" or "conjugate" button when applicable
        public String text;

        public HintDetail setText(String text) {
            this.text = text;
            return this;
        }

        public String toString() {
            return String.format(Locale.US, "HintDetail {%s}", this.text);
        }
    }

    public boolean isEmpty() {
        return this.details.size() < 1;
    }
}
