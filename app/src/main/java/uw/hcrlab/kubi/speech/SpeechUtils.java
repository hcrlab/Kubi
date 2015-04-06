package uw.hcrlab.kubi.speech;

import android.speech.SpeechRecognizer;

/**
 * Created by kimyen on 4/5/15.
 */
public class SpeechUtils {
    public static String getResponse(String speechInput) {
        return null;
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
