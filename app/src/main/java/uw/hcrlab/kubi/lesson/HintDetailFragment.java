package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uw.hcrlab.kubi.R;

/**
 * Created by lrperlmu on 4/25/16.
 */
public class HintDetailFragment extends Fragment {
    private static String TAG = HintFragment.class.getSimpleName();
    private HintData.HintDetail detail;

    /* To be called before onCreateView */
    public HintDetailFragment setDetail(HintData.HintDetail detail) {
        this.detail = detail;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "Creating hint detail fragment from " + this.detail);
        View view = inflater.inflate(R.layout.fragment_hint_detail, container, false);
        TextView detailView = (TextView) view.findViewById(R.id.text);
        detailView.setText(this.detail.text);
        return view;
    }
}
