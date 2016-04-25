package uw.hcrlab.kubi.wizard;

import android.net.ParseException;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.prompts.NamePrompt;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.lesson.results.SelectResult;
import uw.hcrlab.kubi.lesson.results.TranslateResult;
import uw.hcrlab.kubi.robot.Action;
import uw.hcrlab.kubi.robot.FaceAction;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.wizard.model.Speech;
import uw.hcrlab.kubi.wizard.model.Task;

/**
 * Created by Alexander on 4/16/2015.
 *
 * Handles general commands such as responses, lessons, quizzes, and questions
 */
public class CommandHandler extends WizardHandler {
    public static String TAG = CommandHandler.class.getSimpleName();

    public CommandHandler(String ref) {
        super(ref);
    }

    private int getDrawable(String command) {
        Map<String, Integer> drawables = new HashMap<String, Integer>();

        drawables.put("APPLE", R.drawable.apple);
        drawables.put("BANANA", R.drawable.banana);
        drawables.put("GIRL", R.drawable.girl);
        drawables.put("BOY", R.drawable.boy);
        drawables.put("FRANCE", R.drawable.france);

        return drawables.get(command);
    }

    private PromptTypes getType(DataSnapshot snap) {
        if(!snap.hasChild("type")) {
            throw new IllegalArgumentException("Data snapshot does not contain a property named `type`");
        }

        return PromptTypes.valueOf(snap.child("type").getValue(String.class).toUpperCase());
    }

    private boolean validateSelect(DataSnapshot snap) {
        if(!snap.child("prompt").exists()) {
            return false;
        }

        if(!snap.child("opts").exists()) {
            return false;
        }

        for(DataSnapshot option : snap.child("opts").getChildren()) {
            if(!option.child("image").exists()) {
                return false;
            }

            if(!option.child("num").exists()) {
                return false;
            }

            if(!option.child("title").exists()) {
                return false;
            }
        }

        return true;
    }

    private boolean validateSelectResult(DataSnapshot res) {
        return true;
    }

    private boolean validateTranslate(DataSnapshot snap) {
        if(!snap.child("prompt").exists()) {
            return false;
        }

        if(!snap.child("words").exists()) {
            return false;
        }

        for(DataSnapshot option : snap.child("words").getChildren()) {
            if(!option.child("text").exists()) {
                return false;
            }
        }

        return true;
    }

    private boolean validateTranslateResult(DataSnapshot res) {
        return true;
    }

    private boolean validateName(DataSnapshot snap) {
        if(!snap.child("prompt").exists()) {
            return false;
        }

        if(!snap.child("images").exists() || !snap.child("images").hasChildren()) {
            return false;
        }

        return true;
    }

    private boolean validateNameResult(DataSnapshot res) {
        return true;
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.hasChild("handled") || !snap.child("handled").getValue(Boolean.class)) {
            Log.i(TAG, "received a new command");

            if(robot == null) {
                robot = Robot.getInstance();
            }

            PromptTypes type;

            try {
                type = getType(snap);
            } catch (IllegalArgumentException|NullPointerException ex) {
                // TODO: Handle the exception...
                Log.e(TAG, "Unknown command type!");
                return;
            }

            Prompt prompt;
            PromptData pd = new PromptData();
            pd.type = type;

            switch(type) {
                case SELECT:
                    if(!validateSelect(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.PromptText = (String) snap.child("prompt").getValue();

                    DataSnapshot options = snap.child("opts");
                    for(DataSnapshot option : options.getChildren()) {
                        int num = option.child("num").getValue(int.class);
                        String title = option.child("title").getValue(String.class);
                        String url = option.child("image").getValue(String.class);

                        pd.options.add(new PromptData.Option(num, title).setURL(url));
                    }

                    prompt = new SelectPrompt();
                    break;

                case TRANSLATE:
                    if(!validateTranslate(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.PromptText = (String) snap.child("prompt").getValue();

                    DataSnapshot words = snap.child("words");
                    for(DataSnapshot word : words.getChildren()) {
                        // TODO: Decide if this is the right place to start ignoring space tokens
                        if(word.child("idx").exists()) {
                            int idx = word.child("idx").getValue(int.class);
                            String text = (String) word.child("text").getValue();
                            PromptData.Word w = new PromptData.Word(idx, text);

                            if(word.child("hints").exists()) {
                                for(DataSnapshot hint : word.child("hints").getChildren()) {
                                    w.addHint((String) hint.getValue());
                                }
                            }

                            pd.words.add(w);
                        }
                    }

                    prompt = new TranslatePrompt();
                    break;

                case NAME:
                    if(!validateName(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.PromptText = (String) snap.child("prompt").getValue();

                    // TODO: when articles are added to the FB data structure, parse them here...
                    DataSnapshot images = snap.child("images");
                    for(DataSnapshot image : images.getChildren()) {
                        pd.images.add(new PromptData.Image((String) image.getValue(), true));
                    }

                    prompt = new NamePrompt();
                    break;

                default:
                    // throw new IllegalArgumentException(String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));

                    // For the time being, ignore all other types of questions
                    return;
            }

            prompt.setData(pd);
            robot.setPrompt(prompt, snap.getKey());

            snap.child("handled").getRef().setValue(true);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snap, String s) {
        DataSnapshot res = snap.child("result");

        if(res.exists() && (!res.hasChild("handled") || !res.child("handled").getValue(Boolean.class))) {
            if(robot == null) {
                robot = Robot.getInstance();
            }

            if(!robot.getCurrentPromptId().equals(snap.getKey())) {
                Log.e(TAG, "Received a results update for a prompt that isn't currently showing!");
                return;
            }

            Log.i(TAG, "Received a new result!");

            PromptTypes type;

            try {
                type = getType(snap);
            } catch (IllegalArgumentException|NullPointerException ex) {
                // TODO: Handle the exception...
                Log.e(TAG, "Unknown command type!");
                return;
            }

            Result result;

            switch(type) {
                case SELECT:
                    if(!validateSelectResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    Boolean isCorrect = res.child("correct").getValue(Boolean.class);
                    Integer correctIdx = res.child("solutions").child("0").getValue(Integer.class);
                    Integer usersIdx = res.child("response").getValue(Integer.class);

                    result = new SelectResult(isCorrect, usersIdx, correctIdx);

                    break;

                case TRANSLATE:
                    if(!validateTranslateResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    // TODO: Update with actual result...
                    result = new TranslateResult(false);

                    break;

                case NAME:
                    if(!validateNameResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    // TODO: Update with actual result...
                    result = new NameResult(false);

                    break;

                default:
                    // throw new IllegalArgumentException(String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));

                    // For the time being, ignore all other types of questions
                    return;
            }

            robot.showResult(result);

            res.child("handled").getRef().setValue(true);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        //Ignore onChildRemoved
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //Ignore onChildMoved
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, "Firebase Error: " + firebaseError.toString());
    }
}
