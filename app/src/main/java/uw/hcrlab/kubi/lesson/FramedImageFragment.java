package uw.hcrlab.kubi.lesson;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import uw.hcrlab.kubi.R;

/**
 * Created by lrperlmu on 4/21/16.
 */
public class FramedImageFragment extends Fragment {
    private static String TAG = FramedImageFragment.class.getSimpleName();

    private PromptData.Image image;

    /* Should be called before onCreateView(). Not using setArguments because
     * it only allows strings. */
    public void configure(PromptData.Image img) {
        this.image = img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "Creating framed image fragment from " + this.image);

        View view = inflater.inflate(R.layout.fragment_framed_image, container, false);

        // Set picture according to image
        ImageView picture = (ImageView) view.findViewById(R.id.picture);

        if(this.image.hasURL()) {
            ImageLoader.getInstance().displayImage(this.image.imageUrl, picture);
        } else {
            // debug case, until we start passing image URLs through from  duolingo
            Drawable drawable = view.getResources().getDrawable(
                    DrawableHelper.getIdFromString(this.image.drawable), getActivity().getTheme());
            picture.setImageDrawable(drawable);
        }

        return view;
    }
}