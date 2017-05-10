package com.noqapp.client.model.database;

/**
 * User: hitender
 * Date: 5/9/17 2:19 PM
 */
public class DatabaseTable {

    public static final String DB_NAME = "noqueue.db";

    public static class KeyValue {
        public static final String TABLE_NAME = "KEY_VALUE";
        public static final String KEY = "key";
        public static final String VALUE = "value";

        private KeyValue() {
        }
    }

    public static class TokenQueue {
        public static final String TABLE_NAME = "TOKEN_QUEUE";
        public static final String CODE_QR = "codeqr";
        public static final String BUSINESS_NAME = "bussinessname";
        public static final String DISPLAY_NAME = "displayname";
        public static final String STORE_ADDRESS = "storeaddress";
        public static final String COUNTRY_SHORT_NAME = "countryShortName";
        public static final String STORE_PHONE = "storephone";
        public static final String TOKEN_AVAILABLE_FROM = "tokenavailablefrom";
        public static final String START_HOUR = "starthour";
        public static final String END_HOUR = "endhour";
        public static final String TOPIC = "topic";
        public static final String SERVING_NUMBER = "servingnumber";
        public static final String LAST_NUMBER = "lastnumber";
        public static final String TOKEN = "token";
        public static final String QUEUE_STATUS = "queuestatus";
        public static final String SERVICED_TIME = "servicedTime";
        public static final String CREATE_DATE = "createdate";

        private TokenQueue () {
        }
    }

    public static class TokenQueueHistory {
        public static final String TABLE_NAME = "TOKEN_QUEUE_H";

        private TokenQueueHistory () {
        }
    }
}
