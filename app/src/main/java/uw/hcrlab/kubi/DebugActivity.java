package uw.hcrlab.kubi;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import java.util.ArrayList;

public class DebugActivity extends FragmentActivity {
    private static String TAG = DebugActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Debug Activity ...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);

        // load eyes gif
        WebView eye_area = (WebView) findViewById(R.id.webview_eyes);
        eye_area.loadUrl("file:///android_asset/eyes.html");

        // load flash cards
        Prompt prompt = new Prompt1Fragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.prompt_container, prompt).commit();

    }

    private void loadPrompt(PromptData promptData) {
        Prompt prompt;

        // switch on the type of prompt
        switch (promptData.type) {
            case (1):
                // load prompt fragment
                prompt = new Prompt1Fragment();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("prompt type not found %d", promptData.type));
        }

        prompt.update(promptData);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.prompt_container, prompt).commit();
    }
/*
    private void interpretDebugPrompt(View view) {
        EditText editText = (EditText)findViewById(R.id.debug_prompt);
        int input = 1;
        try {
            input = Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException nfe) {
            Log.i(TAG, "invalid debug prompt input");
        }
        editText.setText("");
        switch (input) {
            case (1):
                PromptData pd = new PromptData();
                pd.type = 1;
                pd.srcText = "apple";
                pd.options = new ArrayList<PromptData.Option>();
                pd.options.add(new PromptData.Option(1, "apple"));
                pd.options.add(new PromptData.Option(2, "banana"));
                pd.options.add(new PromptData.Option(3, "girl"));
                loadPrompt(pd);
                break;
            case (3):
            case (4):
            default:
                throw new IllegalArgumentException(
                        String.format("prompt type not implemented %d", input));

        }
    }
    */
}

