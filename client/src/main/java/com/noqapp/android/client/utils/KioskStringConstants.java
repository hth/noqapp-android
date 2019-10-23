package com.noqapp.android.client.utils;

import android.util.Log;

public class KioskStringConstants {

    public static String EMPTY_ERROR = "";
    public static String PROGRESS_TITLE = "";
    public static String SURVEY_TITLE = "";
    public static String SUCCESS_RESPONSE = "";
    public static String FAILURE_RESPONSE = "";
    public static String RESET = "";
    public static String SUBMIT = "";
    public static String STR_WORST = "";
    public static String STR_BEST = "";
    public static String YES = "";
    public static String NO = "";

    public static void init(String language) {
        Log.e("language", language);
        if (language.equals("en")) {
            EMPTY_ERROR = "Please rate overall rating";
            PROGRESS_TITLE = "Submitting feedback ...";
            SURVEY_TITLE = "Survey";
            SUCCESS_RESPONSE = "Thank you for your feedback";
            FAILURE_RESPONSE = "Error submitting feedback";
            RESET = "RESET";
            SUBMIT = "Submit";
            STR_WORST = "Not at all\nsatisfied";
            STR_BEST = "Extremely\nSatisfied";
            YES = "YES";
            NO = "NO";
        } else if (language.equals("es")) {
            EMPTY_ERROR = "Por favor califique la calificación general";
            PROGRESS_TITLE = "Enviar comentarios ...";
            SURVEY_TITLE = "Encuesta";
            SUCCESS_RESPONSE = "Gracias por tus comentarios";
            FAILURE_RESPONSE = "Error al enviar comentarios";
            RESET = "Reiniciar";
            SUBMIT = "Enviar";
            STR_WORST = "De ningún modo\nsatisfecho";
            STR_BEST = "Extremadamente\nSatisfecho";
            YES = "SI";
            NO = "NO";
        } else {
            EMPTY_ERROR = "Please rate overall rating";
            PROGRESS_TITLE = "Submitting feedback ...";
            SURVEY_TITLE = "Survey";
            SUCCESS_RESPONSE = "Thank you for your feedback";
            FAILURE_RESPONSE = "Error submitting feedback";
            RESET = "RESET";
            SUBMIT = "Submit";
            STR_WORST = "Not at all\nsatisfied";
            STR_BEST = "Extremely\nSatisfied";
            YES = "YES";
            NO = "NO";
        }
    }


    public static String getLanguageLabel(String language) {
        Log.e("language", language);
        if (language.equals("en")) {
            return "English";
        } else if (language.equals("es")) {
            return "Español";
        } else {
            return "English";
        }
    }
}
