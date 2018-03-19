package com.noqapp.android.merchant.model.database;

/**
 * User: hitender
 * Date: 5/9/17 2:19 PM
 */
public class DatabaseTable {

    public static class Notification {
        public static final String TABLE_NAME = "NOTIFICATION";
        public static final String KEY = "key";
        public static final String CODE_QR = "codeQR";
        public static final String BODY = "body";
        public static final String TITLE = "title";
        public static final String STATUS = "status";
        public static final String SEQUENCE = "sequence";

        private Notification() {
        }
    }
}
