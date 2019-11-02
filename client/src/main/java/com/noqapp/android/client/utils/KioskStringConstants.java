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
        if (language.equals("hi")) {
            EMPTY_ERROR = "कृपया समग्र मूल्यांकन करें";
            PROGRESS_TITLE = "फ़ीडबैक भेज रहे हैं...";
            SURVEY_TITLE = "सर्वेक्षण";
            SUCCESS_RESPONSE = "आपकी प्रतिक्रिया के लिए आपका धन्यवाद";
            FAILURE_RESPONSE = "प्रतिक्रिया सबमिट करते समय त्रुटि";
            RESET = "रीसेट करें";
            SUBMIT = "पूर्ण करें";
            STR_WORST = "संतुष्ट\nनहीं";
            STR_BEST = "अत्यंत\nसंतुष्ट";
            YES = "हाँ";
            NO = "नहीं";
        } else if (language.equals("es")) {
            EMPTY_ERROR = "Por favor califique la calificación general";
            PROGRESS_TITLE = "Enviar comentarios...";
            SURVEY_TITLE = "Encuesta";
            SUCCESS_RESPONSE = "Gracias por tus comentarios";
            FAILURE_RESPONSE = "Error al enviar comentarios";
            RESET = "Reiniciar";
            SUBMIT = "Enviar";
            STR_WORST = "De ningún modo\nsatisfecho";
            STR_BEST = "Extremadamente\nSatisfecho";
            YES = "Si";
            NO = "No";
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
            YES = "Yes";
            NO = "No";
        }
    }


    public static String getLanguageLabel(String language) {
        Log.e("language", language);
        if (language.equals("es")) {
            return "Español";
        } else if (language.equals("hi")) {
            return "हिंदी";
        } else {
            return "English";
        }
    }
}
