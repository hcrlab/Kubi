package uw.hcrlab.kubi.lesson.prompts;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.DrawableHelper;
import uw.hcrlab.kubi.lesson.FramedImageFragment;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.Result;

public class NamePrompt extends Prompt {
    private static String TAG = NamePrompt.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating prompt 4 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_prompt_4, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        for (PromptData.Option image: this.data.images) {
            FramedImageFragment imageFragment = new FramedImageFragment();
            imageFragment.configure(image);

            String fragmentTag = createImageFragmentTag(image.idx);
            Log.i(TAG, "making image with tag " + fragmentTag);

            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.images, imageFragment, fragmentTag).commit();
        }

        // focus on the text input
        TextView resultText = (TextView) view.findViewById(R.id.l1_result_text);
        resultText.requestFocus();

        return view;
    }

    private String createImageFragmentTag(int idx) {
        return "image-" + idx;
    }

    public void handleResults(Result res) {

    }

}
