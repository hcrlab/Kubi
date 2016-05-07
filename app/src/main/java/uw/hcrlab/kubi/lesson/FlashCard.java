package uw.hcrlab.kubi.lesson;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import uw.hcrlab.kubi.R;

public class FlashCard extends LinearLayout implements View.OnTouchListener {
    private static String TAG = FlashCard.class.getSimpleName();

    private PromptData.Option option;

    private boolean selected = false;

    private FlashCardListener parent;

    public interface FlashCardListener {
        void onFlashCardSelected(FlashCard view);
        void onSelectedFlashCardClicked(FlashCard view);
    }

    public FlashCard(Context context) {
        super(context);
        initializeViews(context);
    }

    public FlashCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public FlashCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        this.setId(View.generateViewId());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.flash_card_layout, this);

        this.setOnTouchListener(this);
    }

    /* Should be called before onCreateView(). Not using setArguments because
     * it only allows strings. */
    public void setOption(PromptData.Option option) {
        this.option = option;

        Log.i(TAG, "Creating flash card from " + this.option);

        // set title according to option
        TextView caption = (TextView) this.findViewById(R.id.caption);
        caption.setText(this.option.title);

        // Set picture according to option
        ImageView picture = (ImageView) this.findViewById(R.id.picture);

        if(this.option.hasURL()) {
            ImageLoader.getInstance().displayImage(this.option.imageUrl, picture);
        } else {
            // debug case, until we start passing image URLs through from  duolingo
            Drawable drawable = this.getResources().getDrawable(DrawableHelper.getIdFromString(this.option.drawable), getContext().getTheme());
            picture.setImageDrawable(drawable);
        }
    }

    public PromptData.Option getOption() {
        return this.option;
    }

    public void setOnFlashCardSelectedListener(FlashCardListener listener) {
        parent = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "Item selected! " + MotionEvent.actionToString(event.getAction()));

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(selected) {
                if(parent != null) {
                    parent.onSelectedFlashCardClicked(this);
                }
            } else {
                this.setBackgroundResource(R.drawable.card_border_selected);
                selected = true;

                if(parent != null) {
                    parent.onFlashCardSelected(this);
                }
            }

            return true;
        }

        return false;
    }


    public void unselect() {
        if (selected) {
            this.setBackgroundResource(R.drawable.card_border);
            selected = false;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setCorrect() {
        this.setBackgroundResource(R.drawable.card_border_correct);
        selected = false;
    }

    public void setIncorrect() {
        this.setBackgroundResource(R.drawable.card_border_incorrect);
        selected = false;
    }

}
