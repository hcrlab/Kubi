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

import java.util.HashMap;
import java.util.Map;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.robot.FaceAction;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.screen.RobotFace;

public class FlashCardFragment extends Fragment implements View.OnTouchListener {
    private static String TAG = FlashCardFragment.class.getSimpleName();

    private Robot robot;
    private PromptData.Option option;
    private boolean mSelected = false;

    public interface OnFlashCardSelectedListener {
        void onFlashCardSelected(String tag);
    }

    private OnFlashCardSelectedListener mParent;

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

        robot = Robot.getInstance();

        return view;
    }

    public void setOnFlashCardSelectedListener(OnFlashCardSelectedListener listener) {
        mParent = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "Item selected! " + MotionEvent.actionToString(event.getAction()));

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View frame = v.findViewById(R.id.flash_card_border);

            if(mSelected) {
                frame.setBackgroundResource(R.drawable.card_border);
                mSelected = false;
            } else {
                frame.setBackgroundResource(R.drawable.card_border_selected);
                mSelected = true;

                if(mParent != null) {
                    mParent.onFlashCardSelected(this.getTag());
                }
            }

            return true;
        }

        return false;
    }

    public void unselect() {
        View frame = getView().findViewById(R.id.flash_card_border);

        if(mSelected) {
            frame.setBackgroundResource(R.drawable.card_border);
            mSelected = false;
        }
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setCorrect() {
        View frame = getView().findViewById(R.id.flash_card_border);

        frame.setBackgroundResource(R.drawable.card_border_correct);
        mSelected = false;

        // TODO: Remove this. This just shows how the robot can be accessed and calls can be made to it.
        robot.act(FaceAction.GIGGLE);
    }

    public void setIncorrect() {
        View frame = getView().findViewById(R.id.flash_card_border);

        frame.setBackgroundResource(R.drawable.card_border_incorrect);
        mSelected = false;
    }

}
