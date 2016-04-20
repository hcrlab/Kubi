package uw.hcrlab.kubi.lesson;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uw.hcrlab.kubi.KubiLingoUtils;
import uw.hcrlab.kubi.R;

public class Prompt1Fragment extends Prompt {
    private static String TAG = Prompt1Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating prompt 1 fragment from " + this.data);
        View view = inflater.inflate(R.layout.fragment_prompt_1, container, false);

        if (savedInstanceState != null) {
            return view;
        }

        // add the card fragments
        for (PromptData.Option option: this.data.options) {
            FlashCardFragment cardFragment = new FlashCardFragment();
            cardFragment.configure(option);

            View cardContainer = KubiLingoUtils.getViewByIdString(
                    String.format("card%d_container", option.idx), view, this);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(cardContainer.getId(), (Fragment) cardFragment).commit();

        }
        return view;
    }

}