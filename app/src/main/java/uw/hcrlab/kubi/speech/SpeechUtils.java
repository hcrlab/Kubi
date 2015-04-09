package uw.hcrlab.kubi.speech;

import android.content.res.Resources;
import android.speech.SpeechRecognizer;

import java.util.HashMap;
import java.util.Map;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;

/**
 * Created by kimyen on 4/5/15.
 */
public class SpeechUtils {

    // Map containing key = simple questions and value = how the robot responds
    private static Map<String, String> simpleResponses = new HashMap<String, String>();

    static {
        /*
         Parse the simple questions and responses that the robot is able to perform.
         These responses are defined under res/values/arrays.xml
          */
        Resources res = App.getContext().getResources();
        String[] stringArray = res.getStringArray(R.array.promptsAndResponses);

        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            simpleResponses.put(splitResult[0], splitResult[1]);
        }
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
