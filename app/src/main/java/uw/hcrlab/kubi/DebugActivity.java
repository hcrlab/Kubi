package uw.hcrlab.kubi;

import uw.hcrlab.kubi.lesson.PromptData.HintCollection;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.prompts.NamePrompt;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.robot.Action;
import uw.hcrlab.kubi.robot.PermissionsManager;
import uw.hcrlab.kubi.robot.Robot;

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
    private Robot robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Debug Activity ...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);

        // load eyes gif
        WebView eye_area = (WebView) findViewById(R.id.webview_eyes);
        eye_area.loadUrl("file:///android_asset/eyes.html");

        // make sure we have the permissions needed to connect bluetooth
        PermissionsManager.requestPermissionsDialogIfNecessary(this);

        // load robot face into layout
        robot = Robot.Factory.create(this, R.id.robot_face_view, R.id.prompt_container, R.id.thought_bubble);

        // do not show the virtual keyboard on the debug prompt
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // focus on the debug prompt
        final EditText debugPrompt = (EditText) findViewById(R.id.debug_prompt);
        debugPrompt.setShowSoftInputOnFocus(false);

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
            case NAME:
                prompt = new NamePrompt();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));
        }

        prompt.setUid("testID-notUnique");
        prompt.setData(promptData);

        // add the prompt fragment to the container (replacing last one, if applicable)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.prompt_container, prompt).commit();
    }

    // Render the given HintCollection to the user
    private void loadHint(HintCollection hintData) {
        robot.showHint(hintData);
    }

    private void interpretDebugPrompt(View view) {
        Log.i(TAG, "interpret debug prompt");
        EditText editText = (EditText)findViewById(R.id.debug_prompt);
        String text = editText.getText().toString();
        Log.i(TAG, String.format("edit text '%s'", text));

        // showSamplePrompt(text);
        // executeEnumAction(text);
        // robot.say(text, "en");

        String[] strings = text.split(" ");
        boolean invalid = false;
        if (strings.length == 2) {
            try {
                int x = Integer.parseInt(strings[0]);
                int y = Integer.parseInt(strings[1]);
                robot.moveTo(x, y);
            } catch (NumberFormatException nfe) {
                invalid = true;
            }
        } else {
            invalid = true;
        }

        if (invalid) {
            Toast.makeText(getApplicationContext(), "invalid", Toast.LENGTH_SHORT).show();
        }

        editText.getText().clear();
    }

    private void executeEnumAction(String text) {
        try {
            Action action = Action.valueOf(text.toUpperCase());
            robot.perform(action);
        } catch (IllegalArgumentException iae) {
            String toastText = "invalid action " + text;
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
        }
    }


    private void showSamplePrompt(String text) {
        Log.i(TAG, "show sample prompt");
        int input = 0;
        try {
            input = Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            Log.i(TAG, "could not be formatted as a number, using 1");
            String toastText = String.format("invalid: %s", text);
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
        }

        PromptData pd = new PromptData();
        HintCollection hd = new HintCollection();
        switch (input) {
            case (1):
                pd.type = PromptTypes.SELECT;
                pd.PromptText = "apple";
                pd.options.add(new PromptData.Option(1, "manzana").setDrawable("apple"));
                pd.options.add(new PromptData.Option(2, "plátano").setDrawable("banana"));
                pd.options.add(new PromptData.Option(3, "niña").setDrawable(("girl")));
                loadPrompt(pd);

                hd.details.add(new PromptData.Hint().setText("apple"));
                loadHint(hd);

                break;
            case (3):
                pd.type = PromptTypes.TRANSLATE;
                pd.words.add(new PromptData.Word(0,"una").addHint("a").addHint("an"));
                pd.words.add(new PromptData.Word(1, "manzana").addHint("apple"));
                pd.words.add(new PromptData.Word(2, "y").addHint("and"));
                pd.words.add(new PromptData.Word(3, "un").addHint("a").addHint("an"));
                pd.words.add(new PromptData.Word(4, "plátano").addHint("banana"));
                loadPrompt(pd);
                break;
            case (4):
                pd.type = PromptTypes.NAME;
                pd.PromptText = "apple";
                pd.images.add(new PromptData.Image("apple", false));
                pd.images.add(new PromptData.Image("banana", false));
                pd.images.add(new PromptData.Image("apple", false));
                loadPrompt(pd);
                break;
            default:
                String msg = String.format(Locale.US, "Invalid debug input -- %d", input);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                //editText.getText().clear();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        PermissionsManager.onRequestPermissionsResult(
                this, requestCode, permissions, grantResults);
    }

}

