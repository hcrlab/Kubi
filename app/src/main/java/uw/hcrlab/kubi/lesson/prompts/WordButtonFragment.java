package uw.hcrlab.kubi.lesson.prompts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.PromptData;

/**
 * Created by lrperlmu on 4/22/16.
 */
public class WordButtonFragment extends Fragment {
    private static String TAG = TranslatePrompt.class.getSimpleName();

    private PromptData.Word word;

    /* Should be called before onCreateView */
    public void setWord(PromptData.Word word) {
        this.word = word;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(word.hasHints()) {
            Log.i(TAG, "Creating word button fragment fragment from " + this.word.text);
            View view = inflater.inflate(R.layout.fragment_word_button, container, false);

            if (savedInstanceState != null) {
                return view;
            }

            Button wordButton = (Button) view.findViewById(R.id.button);
            wordButton.setText(word.text);

            wordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button button = (Button) view;
                    // TODO: show the button's hint (using robot's hint-showing capability)
                    // might have to implement onClickListener in parent fragment or in activity to do this?
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "clicked word button " + button.getText().toString(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            return view;
        } else {
            Log.i(TAG, "Creating word fragment from " + this.word.text);
            View view = inflater.inflate(R.layout.fragment_word_plain, container, false);

            if (savedInstanceState != null) {
                return view;
            }

            TextView wordView = (TextView) view.findViewById(R.id.word_view);
            wordView.setText(word.text);

            return view;
        }
    }
}
