package com.noqapp.android.merchant.views.pojos;

public class ToothWorkDone {
    private String toothNumber;
    private String procedure;
    private String summary;

    public ToothWorkDone(String toothNumber, String procedure, String summary) {
        this.toothNumber = toothNumber;
        this.procedure = procedure;
        this.summary = summary;
    }

    public String getToothNumber() {
        return toothNumber;
    }

    public ToothWorkDone setToothNumber(String toothNumber) {
        this.toothNumber = toothNumber;
        return this;
    }

    public String getProcedure() {
        return procedure;
    }

    public ToothWorkDone setProcedure(String procedure) {
        this.procedure = procedure;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public ToothWorkDone setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ToothWorkDone{");
        sb.append("toothNumber='").append(toothNumber).append('\'');
        sb.append(", procedure='").append(procedure).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append('}');
        return sb.toString();
    }
}