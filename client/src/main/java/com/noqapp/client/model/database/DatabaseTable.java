package com.noqapp.client.model.database;

/**
 * User: hitender
 * Date: 5/9/17 2:19 PM
 */
public class DatabaseTable {

    static final String DB_NAME = "noqueue.db";

    public static class KeyValue {
        public static final String TABLE_NAME = "KEY_VALUE";
        public static final String KEY = "key";
        public static final String VALUE = "value";

        private KeyValue() {
        }
    }

    public static class TokenQueue {
        public static final String TABLE_NAME = "TOKEN_QUEUE";
        public static final String CODE_QR = "codeQR";
        public static final String BUSINESS_NAME = "businessName";
        public static final String DISPLAY_NAME = "displayName";
        public static final String STORE_ADDRESS = "storeAddress";
        public static final String COUNTRY_SHORT_NAME = "countryShortName";
        public static final String STORE_PHONE = "storePhone";
        public static final String TOKEN_AVAILABLE_FROM = "tokenAvailableFrom";
        public static final String START_HOUR = "startHour";
        public static final String END_HOUR = "endHour";
        public static final String TOPIC = "topic";
        public static final String SERVING_NUMBER = "servingNumber";
        public static final String LAST_NUMBER = "lastNumber";
        public static final String TOKEN = "token";
        public static final String QUEUE_STATUS = "queueStatus";
        public static final String SERVICED_TIME = "servicedTime";
        public static final String CREATE_DATE = "createDate";

        private TokenQueue () {
        }
    }

    public static class TokenQueueHistory {
        public static final String TABLE_NAME = "TOKEN_QUEUE_H";

        private TokenQueueHistory () {
        }
    }
}
