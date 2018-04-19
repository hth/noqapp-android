package com.noqapp.android.client.model.types;

public enum PathologyEnum {
    AAAP("AAAP", "Amino Acid Analysis, Plasma"),
    AAAU("AAAU", "Amino Acid Analysis, Urine Random"),
    AAUR("AAUR", "Aminolevulinic Acid, Urine Random"),
    DENI("DENI", "Dengue IgM"),
    BHCG("BHCG", "BETA HCG");

    private final String name;
    private final String description;

    private PathologyEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }
}
