package com.noqapp.android.common.model.types.order;

/**
 * Created by hitender on 3/27/18.
 */

public enum PaymentModeEnum {
    CA("CA", "Cash"),
    DC("DC", "Debit Card"),
    CC("CC", "Credit Card"),
    CCE("CCE", "Credit Card EMI"),
    NTB("NTB", "Internet Banking"),
    UPI("UPI", "UPI"),
    PAL("PAL", "Paypal"),
    PPE("PPE", "PhonePe"),
    PTM("PTM", "Paytm"),
    AMZ("AMZ", "AmazonPay"),
    AIR("AIR", "Airtel Money Wallet"),
    FCH("FCH", "Freecharge Wallet\n"),
    MKK("MKK", "MobiKwik Wallet"),
    OLA("OLA", "Ola Wallet"),
    JIO("JIO", "JioMoney Wallet"),
    ZST("ZST", "ZestMoney"),
    INS("INS", "AmazonPay"),
    LPY("LPY", "LazyPay");

    private final String description;
    private final String name;

    PaymentModeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
