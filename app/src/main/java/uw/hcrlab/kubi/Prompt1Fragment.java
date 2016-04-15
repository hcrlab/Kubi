package uw.hcrlab.kubi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;

public class Prompt1Fragment extends Prompt {
    private static String TAG = Prompt1Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating prompt 1 fragment ...");
        View view = inflater.inflate(R.layout.fragment_prompt_1, container, false);

        if (savedInstanceState != null) {
            return view;
        }
        // add the card fragments
        for (PromptData.Option option: this.data.options) {
            FlashCardFragment cardFragment = new FlashCardFragment();
            cardFragment.configure(option);

            View cardContainer = getViewByIdString(String.format("card%d_container", option.idx), view);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(cardContainer.getId(), (Fragment) cardFragment).commit();

        }


//        // Check that the activity is using the layout version with
//        // the fragment_container FrameLayout
//        if (findViewById(R.id.fragment_container) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            HeadlinesFragment firstFragment = new HeadlinesFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, firstFragment).commit();
//        }

        // stuff actual prompt data into the view
//        for (int i = 0; i < 3; i++) {
//            PromptData.Option option = this.data.options.get(i);
//
//            String cardId = String.format("card%d", i);
//            int cardResourceId = getResources().getIdentifier(
//                    cardId, "id", getActivity().getPackageName());
//            View cardView = view.findViewById(cardResourceId);
//
//            int pictureResourceId = getResources().getIdentifier(
//                    "picture", "id", getActivity().getPackageName());
//            ImageView pictureView = (ImageView)cardView.findViewById(pictureResourceId);
//            pictureView.setImageResource(R.drawable.);
//        }

        return view;
    }

    public View getViewByIdString(String name, View view) {
        int cardResourceId = getResources().getIdentifier(
                name, "id", getActivity().getPackageName());
        View target = view.findViewById(cardResourceId);
        if (target == null) {
            Log.i(TAG, String.format("getViewByStringID can't find '%s'", name));
        }

        return target;
    }

}
