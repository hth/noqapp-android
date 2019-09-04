package com.noqapp.android.merchant.views.pojos;

public class ToothWorkDone {
    private String toothNumber;
    private String procedure;
    private String summary;
    private String createdDate;
    private String teethStatus;
    private String teethUnit;
    private String teethPeriod;

    public ToothWorkDone(String toothNumber, String procedure, String summary) {
        this.toothNumber = toothNumber;
        this.procedure = procedure;
        this.summary = summary;
    }

    public ToothWorkDone(String toothNumber, String procedure, String summary,String teethStatus, String teethUnit, String teethPeriod) {
        this.toothNumber = toothNumber;
        this.procedure = procedure;
        this.summary = summary;
        this.teethStatus = teethStatus;
        this.teethUnit = teethUnit;
        this.teethPeriod = teethPeriod;
    }

    public ToothWorkDone(String toothNumber, String procedure, String summary, String createdDate) {
        this.toothNumber = toothNumber;
        this.procedure = procedure;
        this.summary = summary;
        this.createdDate = createdDate;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public ToothWorkDone setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getTeethStatus() {
        return teethStatus;
    }

    public void setTeethStatus(String teethStatus) {
        this.teethStatus = teethStatus;
    }

    public String getTeethUnit() {
        return teethUnit;
    }

    public void setTeethUnit(String teethUnit) {
        this.teethUnit = teethUnit;
    }

    public String getTeethPeriod() {
        return teethPeriod;
    }

    public void setTeethPeriod(String teethPeriod) {
        this.teethPeriod = teethPeriod;
    }

    @Override
    public String toString() {
        return "ToothWorkDone{" +
                "toothNumber='" + toothNumber + '\'' +
                ", procedure='" + procedure + '\'' +
                ", summary='" + summary + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", teethStatus='" + teethStatus + '\'' +
                ", teethUnit='" + teethUnit + '\'' +
                ", teethPeriod='" + teethPeriod + '\'' +
                '}';
    }
}
