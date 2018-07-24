package com.noqapp.android.common.model.types.medical;

/**
 * hitender
 * 7/24/18 10:51 AM
 */
public enum FormVersionEnum {
    /* Format of four letters, A1 to A9 and then B1 to B9 and so on. */
    GFA1("GFA1", "General Form a1", "Since 2018", "Exp 2019"),
    GFA2("GFA2", "General Form a2", "Since 2018", "Exp 2019");

    private final String name;
    private final String description;
    private final String formSince;
    private final String formExpiration;

    FormVersionEnum(String name, String description, String formSince, String formExpiration) {
        this.name = name;
        this.description = description;
        this.formSince = formSince;
        this.formExpiration = formExpiration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFormSince() {
        return formSince;
    }

    public String getFormExpiration() {
        return formExpiration;
    }

    @Override
    public String toString() {
        return description;
    }
}
