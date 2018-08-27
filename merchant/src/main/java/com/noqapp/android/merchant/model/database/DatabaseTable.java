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
        public static final String CREATE_DATE = "createDate";

        private Notification() {
        }
    }

    public static class PreferredStore {
        public static final String TABLE_NAME = "PREFERREDSTORE";
        public static final String PRODUCT_ID = "productID";
        public static final String BIZ_STORE_ID = "bizStoreID";
        public static final String DISPLAY_PRICE = "displayPrice";
        public static final String PRODUCT_NAME = "productName";
        public static final String PRODUCT_INFO = "productInfo";
        public static final String STORE_CAT_ID = "storeCatID";
        public static final String PRODUCT_TYPE = "productType";
        public static final String PRODUCT_UNIT_VALUE = "productUnitValue";
        public static final String PRODUCT_UNIT_MESAURE = "productUnitMeasure";


        private PreferredStore() {
        }
    }
}
