package com.noqapp.android.common.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TextToSpeechHelper {
    private TextToSpeech textToSpeech;

    public TextToSpeechHelper(Context context) {
        textToSpeech = new TextToSpeech(context, null, "com.google.android.tts");
    }

    public void makeAnnouncement(List<JsonTextToSpeech> jsonTextToSpeeches) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            for (JsonTextToSpeech jsonTextToSpeech : jsonTextToSpeeches) {
                if (jsonTextToSpeech.getJsonVoiceInput().getLanguageCode().startsWith("en")) {
                    Voice v = new Voice(jsonTextToSpeech.getJsonVoiceInput().getName(),
                            new Locale(jsonTextToSpeech.getJsonVoiceInput().getLanguageCode()),
                            400, 200, true, null);
                    textToSpeech.setVoice(v);
                    textToSpeech.speak(
                            jsonTextToSpeech.getJsonTextInput().getText(),
                            TextToSpeech.QUEUE_FLUSH,
                            null);
                }
            }
        } else {
            for (JsonTextToSpeech jsonTextToSpeech : jsonTextToSpeeches) {
                Voice v = new Voice(jsonTextToSpeech.getJsonVoiceInput().getName(),
                        new Locale(jsonTextToSpeech.getJsonVoiceInput().getLanguageCode()),
                        400, 200, true, null);
                textToSpeech.setVoice(v);
                textToSpeech.speak(
                        jsonTextToSpeech.getJsonTextInput().getText(),
                        TextToSpeech.QUEUE_ADD,
                        null,
                        UUID.randomUUID().toString());
            }
        }
    }
}
