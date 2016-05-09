package uw.hcrlab.kubi;

import uw.hcrlab.kubi.lesson.PromptData.HintCollection;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.prompts.JudgePrompt;
import uw.hcrlab.kubi.lesson.prompts.ListenPrompt;
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
import android.view.Window;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_debug);

        // make sure we have the permissions needed to connect bluetooth
        PermissionsManager.requestPermissionsDialogIfNecessary(this);

        // load robot face into layout
        robot = Robot.Factory.create(this, R.id.main_eyes, R.id.prompt, R.id.thought_bubble);

        // set up the debug prompt
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText debugPrompt = (EditText) findViewById(R.id.debug_prompt);
        debugPrompt.setShowSoftInputOnFocus(false);
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
            case JUDGE:
                prompt = new JudgePrompt();
                break;
            case LISTEN:
                prompt = new ListenPrompt();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));
        }

        prompt.setUid("testID-notUnique");
        prompt.setData(promptData);

        robot.setPrompt(prompt);
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

        showSamplePrompt(text);
        // executeEnumAction(text);
        // robot.say(text, "en");
        // moveHead(text);

        editText.getText().clear();
    }

    private void moveHead(String text) {
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
                pd.PromptText = "Select translation of \"an apple\"";
                pd.options.add(new PromptData.Option(1, "una manzana").setDrawable("apple"));
                pd.options.add(new PromptData.Option(2, "un plátano").setDrawable("banana"));
                pd.options.add(new PromptData.Option(3, "una niña").setDrawable(("girl")));
                loadPrompt(pd);

                hd.details.add(new PromptData.Hint().setText("apple"));
                loadHint(hd);

                break;
            case (3):
                pd.type = PromptTypes.TRANSLATE;
                pd.PromptText = "Translate this text";//una manzana y un plátano";
                pd.words.add(new PromptData.Word(0,"una").addHint("a").addHint("an"));
                pd.words.add(new PromptData.Word(1, "manzana").addHint("apple"));
                pd.words.add(new PromptData.Word(2, "y").addHint("and"));
                pd.words.add(new PromptData.Word(3, "un").addHint("a").addHint("an"));
                pd.words.add(new PromptData.Word(4, "plátano").addHint("banana"));
                loadPrompt(pd);
                break;
            case (4):
                pd.type = PromptTypes.NAME;
                pd.PromptText = "Translate \"an apple\"";
                pd.images.add(new PromptData.Image("apple", false));
                pd.images.add(new PromptData.Image("banana", false));
                pd.images.add(new PromptData.Image("apple", false));
                loadPrompt(pd);
                break;
            case (5):
                pd.type = PromptTypes.JUDGE;
                pd.PromptText = "Select the missing word.";
                pd.textBefore = "El niño";
                pd.options.add(new PromptData.Option(1, "como"));
                pd.options.add(new PromptData.Option(2, "comes"));
                pd.options.add(new PromptData.Option(3, "come"));
                pd.textAfter = "un plátano.";
                loadPrompt(pd);
                break;
            case (6):
                pd.type = PromptTypes.LISTEN;
                pd.PromptText = "una manzana";
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

