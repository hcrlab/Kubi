package uw.hcrlab.kubi.lesson;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import uw.hcrlab.kubi.R;

public class FlashCardFragment extends Fragment implements View.OnTouchListener {
    private static String TAG = FlashCardFragment.class.getSimpleName();

    public interface OnFlashCardSelectedListener {
        void onFlashCardSelected(String tag);
    }

    private PromptData.Option option;

    private boolean selected = false;

    private OnFlashCardSelectedListener parent;

    /* Should be called before onCreateView(). Not using setArguments because
     * it only allows strings. */
    public void configure(PromptData.Option option) {
        this.option = option;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "Creating flash card fragment from " + this.option);

        View view = inflater.inflate(R.layout.fragment_flash_card, container, false);
        view.setOnTouchListener(this);

        // set title according to option
        TextView caption = (TextView) view.findViewById(R.id.caption);
        caption.setText(this.option.title);

        // Set picture according to option
        ImageView picture = (ImageView) view.findViewById(R.id.picture);

        if(this.option.hasURL()) {
            ImageLoader.getInstance().displayImage(this.option.imageUrl, picture);
        } else {
            // debug case, until we start passing image URLs through from  duolingo
            Drawable drawable = view.getResources().getDrawable(
                    DrawableHelper.getIdFromString(this.option.drawable), getActivity().getTheme());
            picture.setImageDrawable(drawable);
        }

        return view;
    }

    public void setOnFlashCardSelectedListener(OnFlashCardSelectedListener listener) {
        parent = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "Item selected! " + MotionEvent.actionToString(event.getAction()));

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View frame = v.findViewById(R.id.flash_card_border);

            if(selected) {
                frame.setBackgroundResource(R.drawable.card_border);
                selected = false;
            } else {
                frame.setBackgroundResource(R.drawable.card_border_selected);
                selected = true;

                if(parent != null) {
                    parent.onFlashCardSelected(this.getTag());
                }
            }

            return true;
        }

        return false;
    }

    public PromptData.Option getOption() {
        return this.option;
    }

    public void unselect() {
        View view = getView();

        if(view != null) {
            View frame = view.findViewById(R.id.flash_card_border);

            if (frame != null && selected) {
                frame.setBackgroundResource(R.drawable.card_border);
                selected = false;
            }
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setCorrect() {
        View view = getView();

        if(view != null) {
            View frame = view.findViewById(R.id.flash_card_border);

            if (frame != null) {
                frame.setBackgroundResource(R.drawable.card_border_correct);
                selected = false;
            }
        }
    }

    public void setIncorrect() {
        View view = getView();

        if(view != null) {
            View frame = view.findViewById(R.id.flash_card_border);

            if (frame != null) {
                frame.setBackgroundResource(R.drawable.card_border_incorrect);
                selected = false;
            }
        }
    }

}
