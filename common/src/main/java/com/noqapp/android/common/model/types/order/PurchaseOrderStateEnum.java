package com.noqapp.android.common.model.types.order;

/**
 * User: hitender
 * Date: 5/3/18 9:05 AM
 */
public enum PurchaseOrderStateEnum {
    IN("IN", "Initial"),
    PC("PC", "Price Changed"),
    VB("VB", "Valid Before Purchase"),
    IB("IB", "Invalid Before Purchase"),
    PO("PO", "Placed Order"),
    FO("FO", "Failed Order"),
    NM("NM", "Notified Merchant"),
    OP("OP", "Order being Processed"),
    PR("PR", "Processed"),
    OW("OW", "On the Way"),
    LO("LO", "Lost"),
    RD("RD", "Ready for Delivery"),
    FD("FD", "Failed Delivery"),
    OD("OD", "Order Delivered"),
    DA("DA", "Delivery Re-attempt"),
    CO("CO", "Cancelled Order");

    private final String name;
    private final String description;

    PurchaseOrderStateEnum(String name, String description) {
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
