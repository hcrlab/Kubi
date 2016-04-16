package uw.hcrlab.kubi;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

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

        // do not show the virtual keyboard on the debug prompt
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // focus on the debug prompt
        final EditText debugPrompt = (EditText) findViewById(R.id.debug_prompt);

        // set up the debug prompt
        debugPrompt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.i(TAG, "enter key pressed");
                    interpretDebugPrompt(debugPrompt);
                    handled = true;
                }
                return handled;
            }
        });
        debugPrompt.requestFocus();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO: send key event to proper fragment

        // Returns true to stop propagation of the key event (i.e. signal the event was handled)
        return true;
    }

    // Render the given PromptData to the user
    private void loadPrompt(PromptData promptData) {
        Prompt prompt;

        // switch on the type of prompt
        switch (promptData.type) {
            case SELECT:
                // load prompt fragment
                prompt = new SelectPrompt();
                break;
            case TRANSLATE:
                prompt = new TranslatePrompt();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));
        }

        prompt.setData(promptData);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.prompt_container, prompt).commit();
    }

    private void interpretDebugPrompt(View view) {
        Log.i(TAG, "interpret debug prompt");
        EditText editText = (EditText)findViewById(R.id.debug_prompt);
        String text = editText.getText().toString();
        Log.i(TAG, String.format("edit text '%s'", text));
        int input = 0;
        try {
            input = Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            Log.i(TAG, "could not be formatted as a number, using 1");
            String toastText = String.format("invalid: %s", text);
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
        }
        editText.getText().clear();
        PromptData pd = new PromptData();
        switch (input) {
            case (1):
                pd.type = PromptTypes.NAME;
                pd.srcText = "apple";
                pd.options.add(new PromptData.Option(1, "apple"));
                pd.options.add(new PromptData.Option(2, "banana"));
                pd.options.add(new PromptData.Option(3, "girl"));
                loadPrompt(pd);
                break;
            case (3):
                pd.type = PromptTypes.TRANSLATE;
                pd.srcText = "una manzana y un plátano";
                loadPrompt(pd);
                break;
            case (4):
            default:
                String msg = String.format(Locale.US, "Invalid debug input -- %d", input);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                editText.getText().clear();

        }
    }
}

