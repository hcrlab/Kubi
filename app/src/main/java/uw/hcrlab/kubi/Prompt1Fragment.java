package uw.hcrlab.kubi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;

public class Prompt1Fragment extends Prompt {
    private static String TAG = Prompt1Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "Creating prompt 1 fragment ...");
        View view = inflater.inflate(R.layout.fragment_prompt_1, container, false);

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

}
