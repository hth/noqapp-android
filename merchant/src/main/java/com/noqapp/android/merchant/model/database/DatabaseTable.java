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
        public static final String TABLE_NAME = "PREFERRED_STORE";
        public static final String PRODUCT_ID = "productID";
        public static final String BIZ_STORE_ID = "bizStoreID";
        public static final String DISPLAY_PRICE = "displayPrice";
        public static final String PRODUCT_NAME = "productName";
        public static final String PRODUCT_INFO = "productInfo";
        public static final String STORE_CAT_ID = "storeCatID";
        public static final String PRODUCT_TYPE = "productType";
        public static final String PRODUCT_UNIT_VALUE = "productUnitValue";
        public static final String PRODUCT_UNIT_MEASURE = "productUnitMeasure";

        private PreferredStore() {
        }
    }

    public static class MedicalFiles {
        public static final String TABLE_NAME = "MEDICAL_FILES";
        public static final String RECORD_REFERENCE_ID = "recordReferenceId";
        public static final String FILE_LOCATION = "fileLocation";
        public static final String FILE_CREATED_DATE = "fileCreatedDate";
        public static final String UPLOAD_STATUS = "uploadStatus";
        public static final String UPLOAD_ATTEMPT_COUNT = "uploadAttemptCount";
        public static final String FORM_SUBMISSION_STATUS = "formSubmissionStatus";

        private MedicalFiles() {
        }
    }
}
