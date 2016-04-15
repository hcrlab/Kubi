package uw.hcrlab.kubi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uw.hcrlab.kubi.lesson.PromptData;

public class FlashCardFragment extends Fragment {
    private static String TAG = FlashCardFragment.class.getSimpleName();
    private PromptData.Option option;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating flash card fragment from " + this.option);
        View view = inflater.inflate(R.layout.fragment_flash_card, container, false);

        // set text according to option
        TextView caption = (TextView) KubiLingoUtils.getViewByIdString("caption", view, this);
        caption.setText(this.option.text);

        // Set picture according to option
        // Eventually option will contain a drawable. For now just name to find
        // an existing drawable.
        ImageView picture = (ImageView) KubiLingoUtils.getViewByIdString("picture", view, this);
        Drawable drawable = KubiLingoUtils.getDrawableByString(this.option.text, this);
        picture.setImageDrawable(drawable);

        return view;
    }

    /* Should be called before onCreateView(). Not using setArguments because
     * it only allows strings. */
    public void configure(PromptData.Option option) {
        this.option = option;
    }
}
