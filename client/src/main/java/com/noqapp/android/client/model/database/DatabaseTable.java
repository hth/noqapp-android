package com.noqapp.android.client.model.database;

/**
 * User: hitender
 * Date: 5/9/17 2:19 PM
 */
public class DatabaseTable {

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
        public static final String SERVICE_END_TIME = "serviceEndTime";
        public static final String RATING_COUNT = "ratingCount";
        public static final String AVERAGE_SERVICE_TIME = "averageServiceTime";
        public static final String HOURS_SAVED = "hoursSaved";
        public static final String CREATE_DATE = "createDate";
        public static final String BUSINESS_TYPE = "businessType";
        public static final String GEO_HASH = "geohash";
        public static final String TOWN = "town";
        public static final String AREA = "area";
        public static final String DISPLAY_IMAGE = "displayImage";
        public static final String QID = "queueUserID";
        public static final String PURCHASE_ORDER_STATE = "purchaseOrderState";
        public static final String TRANSACTION_ID = "transactionId";

        private TokenQueue() {
        }
    }

    public static class TokenQueueHistory {
        public static final String TABLE_NAME = "TOKEN_QUEUE_H";

        private TokenQueueHistory() {
        }
    }

    public static class Review {
        public static final String TABLE_NAME = "REVIEW";
        public static final String CODE_QR = "codeQR";
        public static final String TOKEN = "token";
        public static final String QID = "qUserId";
        public static final String KEY_REVIEW_SHOWN = "isReviewShown";  // default value is -1, toShow value is 1 to not show value is 0
        public static final String KEY_SKIP = "isSkipped";  // default value -1 , for skip value is 1
        public static final String KEY_GOTO = "gotoCounter"; // default value ""
        public static final String KEY_BUZZER_SHOWN ="isBuzzerShown"; //default value is -1, toShow value is 0 buzzer already shown value is 1

        private Review() {
        }
    }

    public static class Notification {
        public static final String TABLE_NAME = "NOTIFICATION";
        public static final String KEY = "type";
        public static final String CODE_QR = "codeQR";
        public static final String BODY = "body";
        public static final String TITLE = "title";
        public static final String STATUS = "status";
        public static final String SEQUENCE = "sequence";
        public static final String CREATE_DATE = "createDate";
        public static final String BUSINESS_TYPE = "businessType";
        public static final String IMAGE_URL = "imageUrl";


        private Notification() {
        }
    }
}
