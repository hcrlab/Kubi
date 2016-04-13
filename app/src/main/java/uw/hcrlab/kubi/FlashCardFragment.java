package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FlashCardFragment extends Fragment {
    private static String TAG = FlashCardFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating flash card fragment ...");
        return inflater.inflate(R.layout.fragment_flash_card, container, false);
    }
}
