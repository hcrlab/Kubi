package uw.hcrlab.kubi.lesson;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;

public class Prompt4Fragment extends Prompt {
    private static String TAG = Prompt4Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "Creating prompt 4 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_prompt_4, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // set picture dynamically based on srctext
        //ImageView image1 = (ImageView) getActivity().findViewById(R.id.picture1);  // null pointer exception -- why?
        ImageView image1 = (ImageView) KubiLingoUtils.getViewByIdString("picture1", view, this);
        Drawable drawable = KubiLingoUtils.getDrawableByString(this.data.srcText, this);
        image1.setImageDrawable(drawable);


        // set src text according to prompt data
//        TextView srcText = (TextView) KubiLingoUtils.getViewByIdString("l2_source_text", view, this);
//        srcText.setText(this.data.srcText);
//
//        TextView resultText = (TextView) KubiLingoUtils.getViewByIdString("l1_result_text", view, this);
//        resultText.requestFocus();

        return view;
    }
}
