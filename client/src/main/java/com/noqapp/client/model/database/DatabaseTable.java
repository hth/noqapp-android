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
        public static final String COLUMN_CODE_QR = "codeqr";
        public static final String COLUMN_BUSINESS_NAME = "bussinessname";
        public static final String COLUMN_DISPLAY_NAME = "displayname";
        public static final String COLUMN_STORE_ADDRESS = "storeaddress";
        public static final String COLUMN_COUNTRY_SHORT_NAME = "countryShortName";
        public static final String COLUMN_STORE_PHONE = "storephone";
        public static final String COLUMN_TOKEN_AVAILABLE_FROM = "tokenavailablefrom";
        public static final String COLUMN_START_HOUR = "starthour";
        public static final String COLUMN_END_HOUR = "endhour";
        public static final String COLUMN_TOPIC = "topic";
        public static final String COLUMN_SERVING_NUMBER = "servingnumber";
        public static final String COLUMN_LAST_NUMBER = "lastnumber";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_QUEUE_STATUS = "queuestatus";
        public static final String COLUMN_SERVICED_TIME = "servicedTime";
        public static final String COLUMN_CREATE_DATE = "createdate";

        private TokenQueue () {
        }
    }

    public static class TokenQueueHistory {
        public static final String TABLE_NAME = "TOKEN_QUEUE_H";

        private TokenQueueHistory () {
        }
    }
}
