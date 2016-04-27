package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.R;

/**
 * Created by lrperlmu on 4/25/16.
 */
public class HintFragment extends Fragment {
    private static String TAG = HintFragment.class.getSimpleName();
    private HintData hintData;

    /* To be called before onCreateView */
    public HintFragment setHintData(HintData hintData) {
        this.hintData = hintData;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "Creating hint fragment from " + this.hintData);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_hint, container, false);

        for (HintData.HintDetail hintDetail: this.hintData.details) {
            String tag = "";

            HintDetailFragment detailFragment = new HintDetailFragment().setDetail(hintDetail);
            FragmentTransaction transaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.hint_fragment, detailFragment, tag).commit();
        }

        return view;
    }
}
