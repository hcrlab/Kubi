package uw.hcrlab.kubi.robot;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import sandra.libs.asr.asrlib.ASR;
import sandra.libs.tts.TTS;
import sandra.libs.vpa.vpalib.Bot;
import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 5/8/2016.
 */
public class Speech extends ASR {
    public static String TAG = Speech.class.getSimpleName();

    private static Speech instance;

    private Bot bot;
    private TTS tts;

    private Activity activity;

    private String language = "EN";

    protected HttpProxyCacheServer proxy;
    private HashMap<String, MediaPlayer> mPronunciations;
    private String delayedPronunciation;
    private String delayedPronunciationUrl;

    private Random random = new Random();

    private boolean isSpeaking = false;

    // Map containing key = simple questions and value = how the robot responds
    private static Map<String, String> simpleResponses = new HashMap<String, String>();

    ArrayBlockingQueue<String> speechQueue = new ArrayBlockingQueue<String>(10);

    static {
        /*
         Parse the simple questions and responses that the robot is able to move.
         These responses are defined under res/values/arrays.xml
          */
        Resources res = App.getContext().getResources();
        String[] stringArray = res.getStringArray(R.array.promptsAndResponses);

        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            simpleResponses.put(splitResult[0], splitResult[1]);
        }
    }

    private Speech(Activity activity) {
        this.activity = activity;

        createRecognizer(App.getContext());

        proxy = App.getProxy(activity);
        mPronunciations = new HashMap<>();

        tts = TTS.getInstance(activity);
        bot = new Bot(activity, "b9581e5f6e343f72", tts);
    }

    public static Speech getInstance(Activity activity) {
        if(instance == null) {
            instance = new Speech(activity);
        }

        return instance;
    }

    public void cleanup() {
        //TODO: Make sure we don't leak a TTS service...
        tts.shutdown();
    }

    public void startup() {
        tts.setUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                isSpeaking = true;
            }

            @Override
            public void onDone(String s) {
                isSpeaking = false;

                if(speechQueue.size() > 0) {
                    String next = speechQueue.remove();
                    say(next, "en");
                    return;
                }

                if(delayedPronunciation != null) {
                    pronounce(delayedPronunciation);
                    delayedPronunciation = null;
                } else if (delayedPronunciationUrl != null) {
                    pronounceUrl(delayedPronunciationUrl);
                    delayedPronunciationUrl = null;
                }
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    public void shutdown() {
        //called when the activity is shutting down...

        unloadAllPronunciations();
    }

    private String normalizeText(String text) {
        // Normalize the text
        text = text.toLowerCase();
        text = text.replace(',',' ');
        text = text.replace('.',' ');
        text = text.trim();
        return text;
    }

    public void loadPronunciation(String text) {
        text = normalizeText(text);

        String url = App.getAudioURL(text);

        if (url != null) {
            String audioUrl = proxy.getProxyUrl(url);
            mPronunciations.put(text, MediaPlayer.create(activity, Uri.parse(audioUrl)));
        }
    }

    public void unloadPronunciation(String text) {
        text = normalizeText(text);

        MediaPlayer mp = mPronunciations.remove(text);

        if (mp != null) {
            mp.release();
        }
    }

    public void unloadAllPronunciations() {
        for (Map.Entry kvp : mPronunciations.entrySet()) {
            MediaPlayer mp = (MediaPlayer) kvp.getValue();
            mp.release();
        }

        mPronunciations.clear();
    }

    public void pronounceAfterSpeech(String text) {
        delayedPronunciation = text;
    }

    public void pronounceUrlAfterSpeech(String url) {
        delayedPronunciationUrl = url;
    }

    public boolean pronounce(String text) {
        text = normalizeText(text);

        for(MediaPlayer mp : mPronunciations.values()) {
            if(mp.isPlaying()) {
                mp.pause();
                mp.seekTo(0);
            }
        }

        MediaPlayer mp = mPronunciations.get(text);

        if(mp != null) {
            mp.start();
            return true;
        } else {
            say(text, "EN");
            return false;
        }
    }

    public void pronounceUrl(String audioUri) {
        String audioUrl = proxy.getProxyUrl(audioUri);
        MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(audioUrl));

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        mp.start();
    }

    public String getDefaultLanguage() {
        return language;
    }

    public void setDefaultLanguage(String lan) {
        language = lan;
    }

    /**
     * Generates text-to-Speech.java for the provided message.
     *
     * @param msg Message to speak
     */
    public void say(String msg, String language) {
        if(isSpeaking) {
            speechQueue.add(msg);
            return;
        }

        try {
            isSpeaking = true;
            tts.speak(msg, language);
        } catch (Exception e) {
            Log.e(TAG, language + " not available for TTS, default language used instead");
        }
    }

    /**
     * Say a random response from the given collection of choices.
     * @param choices - collection to choose a random string from
     * @param dontSay - if a string matching this is selected, pick again
     * @return - the string that was said
     */
    public String sayRandomResponse(String[] choices, String dontSay) {
        String selection;
        do {
            selection = choices[random.nextInt(choices.length)];
        } while (selection.equals(dontSay));
        say(selection, "en");
        return selection;
    }

    public void shutup() {
        tts.stop();
    }

    public void listen() {
        Log.i(TAG, "listening");
        try {
            super.listen(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH, 1);
        } catch (Exception ex) {
            Robot.replaceCurrentToast("ASR could not be started: invalid params");
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        String speechInput = nBestList.get(0);
        Log.i(TAG, "Speech input: " + speechInput);

        String response = getResponse(speechInput);

        try {
            if(response != null){
                //We have a preprogrammed Response, so use it
                Log.i(TAG, "Saying : " + response);
                say(response, language);
            }  else {
                //We don't have a preprogrammed Response, so use the bot to create a Response
                Log.i(TAG, "Default response");
                bot.initiateQuery(speechInput);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error responding to speech input: " + speechInput);
            e.printStackTrace();
        }
    }

    @Override
    public void processAsrReadyForSpeech() {
        Log.i(TAG, "Listening to user's speech.");
        Robot.replaceCurrentToast("I'm listening");
    }

    @Override
    public void processAsrError(int errorCode) {
        //super.stopListening();
        super.cancel();

        String errorMessage = getErrorMessage(errorCode);

        // If there is an error, shows feedback to the user and writes it in the log
        Log.e(TAG, "Error: " + errorMessage);
        Robot.replaceCurrentToast(errorMessage);
    }

    public static String getResponse(String speechInput) {
        return simpleResponses.get(speechInput);
    }

    public static String getErrorMessage(int errorCode) {
        String errorMessage = null;
        switch (errorCode)
        {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions" ;
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Network related error" ;
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Network operation timeout";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "RecognitionServiceBusy" ;
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Server sends error status";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No matching message" ;
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "Input not audible";
                break;
            default:
                errorMessage = "ASR error";
                break;
        }
        return errorMessage;
    }
}
