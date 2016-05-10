package uw.hcrlab.kubi.wizard;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.lesson.Result;
import uw.hcrlab.kubi.lesson.prompts.JudgeMultiplePrompt;
import uw.hcrlab.kubi.lesson.prompts.JudgeSinglePrompt;
import uw.hcrlab.kubi.lesson.prompts.NamePrompt;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.lesson.prompts.TranslatePrompt;
import uw.hcrlab.kubi.lesson.results.JudgeMultipleResult;
import uw.hcrlab.kubi.lesson.results.JudgeSingleResult;
import uw.hcrlab.kubi.lesson.results.NameResult;
import uw.hcrlab.kubi.lesson.results.SelectResult;
import uw.hcrlab.kubi.lesson.results.TranslateResult;
import uw.hcrlab.kubi.robot.Robot;

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

    private PromptTypes getType(DataSnapshot snap) {
        if(!snap.hasChild("type")) {
            throw new IllegalArgumentException("Data snapshot does not contain a property named `type`");
        }

        return PromptTypes.valueOf(snap.child("type").getValue(String.class).replace('-', '_').toUpperCase());
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
        if(!res.child("correct").exists()) {
            return false;
        }

        if(!res.child("solutions").exists()) {
            return false;
        }

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
        if(!res.child("correct").exists()) {
            return false;
        }

        if(!res.child("solutions").exists()) {
            return false;
        }

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
        if(!res.child("correct").exists()) {
            return false;
        }

        if(!res.child("solutions").exists()) {
            return false;
        }

        return true;
    }

    private boolean validateJudgeSingle(DataSnapshot snap) {
        if(!snap.child("prompt").exists() || !snap.child("after").exists() || !snap.child("before").exists()) {
            return false;
        }

        if(!snap.child("opts").exists() || !snap.child("opts").hasChildren()) {
            return false;
        }

        return true;
    }

    private boolean validateJudgeSingleResult(DataSnapshot res) {
        if(!res.child("correct").exists()) {
            return false;
        }

        if(!res.child("solutions").exists()) {
            return false;
        }

        return true;
    }

    private boolean validateJudgeMultiple(DataSnapshot snap) {
        if(!snap.child("prompt").exists() || !snap.child("before").exists()) {
            return false;
        }

        if(!snap.child("opts").exists() || !snap.child("opts").hasChildren()) {
            return false;
        }

        return true;
    }

    private boolean validateJudgeMultipleResult(DataSnapshot res) {
        if(!res.child("correct").exists()) {
            return false;
        }

        if(!res.child("solutions").exists()) {
            return false;
        }

        return true;
    }

    @Override
    public void onChildAdded(DataSnapshot snap, String s) {
        if(!snap.hasChild("handled") || !snap.child("handled").getValue(Boolean.class)) {
            Log.i(TAG, "received a new command: " + snap.getKey());

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
                        } else {
                            String text = (String) word.child("text").getValue();
                            PromptData.Word w = new PromptData.Word(text);
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

                case JUDGE_SINGLE:
                    if(!validateJudgeSingle(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.PromptText = (String) snap.child("prompt").getValue();
                    pd.textBefore = (String) snap.child("before").getValue();
                    pd.textAfter = (String) snap.child("after").getValue();

                    DataSnapshot options2 = snap.child("opts");
                    for(DataSnapshot option : options2.getChildren()) {
                        int num = Integer.parseInt(option.getKey());
                        String title = option.getValue(String.class);

                        pd.options.add(new PromptData.Option(num, title));
                    }

                    prompt = new JudgeSinglePrompt();
                    break;

                case JUDGE_MULTIPLE:
                    if(!validateJudgeMultiple(snap)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    pd.PromptText = (String) snap.child("prompt").getValue();
                    pd.textBefore = (String) snap.child("before").getValue();

                    DataSnapshot options3 = snap.child("opts");
                    for(DataSnapshot option : options3.getChildren()) {
                        int num = Integer.parseInt(option.getKey());
                        String title = option.getValue(String.class);

                        pd.options.add(new PromptData.Option(num, title));
                    }

                    prompt = new JudgeMultiplePrompt();
                    break;

                default:
                    // throw new IllegalArgumentException(String.format(Locale.US, "Prompt type not implemented: %s", promptData.type));

                    // For the time being, ignore all other types of questions
                    return;
            }

            prompt.setUid(snap.getKey());
            prompt.setData(pd);

            robot.setPrompt(prompt);

            snap.child("handled").getRef().setValue(true);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot snap, String s) {
        DataSnapshot res = snap.child("result");

        Prompt prompt = robot.getPrompt();

        if(prompt != null && res.exists() && (!res.hasChild("handled") || !res.child("handled").getValue(Boolean.class))) {
            if(!prompt.getUid().equals(snap.getKey())) {
                Log.e(TAG, "Received a results update for a prompt that isn't currently showing!");
                return;
            }

            Log.i(TAG, "Received a result: " + prompt.getUid());

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

                    SelectResult sr = new SelectResult(res.child("correct").getValue(Boolean.class));

                    sr.setCorrectIdx(res.child("solutions").child("0").getValue(Integer.class));

                    result = sr;
                    break;

                case TRANSLATE:
                    if(!validateTranslateResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    TranslateResult tr = new TranslateResult(res.child("correct").getValue(Boolean.class));

                    if(res.hasChild("blame")) {
                        tr.setBlame(res.child("blame").getValue(String.class));
                    }

                    for(DataSnapshot sol : res.child("solutions").getChildren()) {
                        tr.addSolution(sol.getValue(String.class));
                    }

                    result = tr;
                    break;

                case NAME:
                    if(!validateNameResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    NameResult nr = new NameResult(res.child("correct").getValue(Boolean.class));

                    if(res.hasChild("blame")) {
                        nr.setBlame(res.child("blame").getValue(String.class));
                    }

                    for(DataSnapshot sol : res.child("solutions").getChildren()) {
                        nr.addSolution(sol.getValue(String.class));
                    }

                    result = nr;
                    break;

                case JUDGE_SINGLE:
                    if(!validateJudgeSingleResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    JudgeSingleResult jsr = new JudgeSingleResult(res.child("correct").getValue(Boolean.class));

                    // We really only expect one item to be in this list... It probably shouldn't be a list at all
                    for(DataSnapshot sol : res.child("solutions").getChildren()) {
                        jsr.setSolution(sol.getValue(Integer.class));
                    }

                    result = jsr;
                    break;

                case JUDGE_MULTIPLE:
                    if(!validateJudgeMultipleResult(res)) {
                        Log.e(TAG, "Data snap does not contain all required properties!");
                        return;
                    }

                    JudgeMultipleResult jmr = new JudgeMultipleResult(res.child("correct").getValue(Boolean.class));

                    for(DataSnapshot sol : res.child("solutions").getChildren()) {
                        jmr.addSolution(sol.getValue(Integer.class));
                    }

                    result = jmr;
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
