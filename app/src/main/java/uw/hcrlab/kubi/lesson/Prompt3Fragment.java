package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;

public class Prompt3Fragment extends Prompt {
    private static String TAG = Prompt3Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

        Log.i(TAG, "Creating prompt 3 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_prompt_3, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // set src text according to prompt data
        TextView srcText = (TextView) KubiLingoUtils.getViewByIdString("l2_source_text", view, this);
        srcText.setText(this.data.srcText);

        TextView resultText = (TextView) KubiLingoUtils.getViewByIdString("l1_result_text", view, this);
        resultText.requestFocus();

        return view;
    }
}