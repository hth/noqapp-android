package com.noqapp.android.merchant.views.pojos;

public class DataObj implements Comparable<DataObj> {
    private String shortName;
    private String fullName;
    private String category;
    private boolean isSelect = false;
    private String medicineTiming;
    private String medicineDuration;
    private String medicineFrequency;
    private String additionalNotes;
    private boolean isNewlyAdded = false;

    public DataObj() {
    }

    public DataObj(String shortName, boolean isSelect) {
        this.shortName = shortName;
        fullName = shortName;
        this.category = "";
        this.isSelect = isSelect;
    }

    public DataObj(String shortName, String category, boolean isSelect) {
        this.shortName = shortName;
        fullName = shortName;
        this.category = category;
        this.isSelect = isSelect;
    }

    public DataObj(String fullName, String shortName, String category, boolean isSelect) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.category = category;
        this.isSelect = isSelect;
    }

    public String getShortName() {
        return toCamelCase(shortName);
    }

    public DataObj setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public DataObj setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public DataObj setCategory(String category) {
        this.category = category;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public DataObj setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    public String getMedicineTiming() {
        return medicineTiming;
    }

    public DataObj setMedicineTiming(String medicineTiming) {
        this.medicineTiming = medicineTiming;
        return this;
    }

    public String getMedicineDuration() {
        return medicineDuration;
    }

    public DataObj setMedicineDuration(String medicineDuration) {
        this.medicineDuration = medicineDuration;
        return this;
    }

    public String getMedicineFrequency() {
        return medicineFrequency;
    }

    public DataObj setMedicineFrequency(String medicineFrequency) {
        this.medicineFrequency = medicineFrequency;
        return this;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public DataObj setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
        return this;
    }

    public boolean isNewlyAdded() {
        return isNewlyAdded;
    }

    public DataObj setNewlyAdded(boolean newlyAdded) {
        isNewlyAdded = newlyAdded;
        return this;
    }

    public int compareTo(DataObj other) {
        return shortName.compareTo(other.shortName);
    }

    @Override
    public boolean equals(Object obj) {
        return (this.shortName.equals(((DataObj) obj).shortName));
    }

    private String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    @Override
    public String toString() {
        return "DataObj{" +
                "shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", category='" + category + '\'' +
                ", isSelect=" + isSelect +
                ", medicineTiming='" + medicineTiming + '\'' +
                ", medicineDuration='" + medicineDuration + '\'' +
                ", medicineFrequency='" + medicineFrequency + '\'' +
                ", additionalNotes='" + additionalNotes + '\'' +
                ", isNewlyAdded=" + isNewlyAdded +
                '}';
    }
}
