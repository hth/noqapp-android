package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.merchant.utils.Constants;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestCaseObjects {

    @SerializedName("PATH")
    private ArrayList<DataObj> pathologyList = new ArrayList<>();

    @SerializedName("MRI")
    private ArrayList<DataObj> mriList = new ArrayList<>();

    @SerializedName("SCAN")
    private ArrayList<DataObj> scanList = new ArrayList<>();

    @SerializedName("SONO")
    private ArrayList<DataObj> sonoList = new ArrayList<>();

    @SerializedName("XRAY")
    private ArrayList<DataObj> xrayList = new ArrayList<>();

    @SerializedName(Constants.MEDICINE)
    private ArrayList<DataObj> medicineList = new ArrayList<>();

    @SerializedName(Constants.SYMPTOMS)
    private ArrayList<DataObj> symptomsList = new ArrayList<>();

    @SerializedName(Constants.PROVISIONAL_DIAGNOSIS)
    private ArrayList<DataObj> proDiagnosisList = new ArrayList<>();

    @SerializedName(Constants.DIAGNOSIS)
    private ArrayList<DataObj> diagnosisList = new ArrayList<>();

    @SerializedName(Constants.INSTRUCTION)
    private ArrayList<DataObj> instructionList = new ArrayList<>();

    public ArrayList<DataObj> getPathologyList() {
        return pathologyList;
    }

    public TestCaseObjects setPathologyList(ArrayList<DataObj> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public ArrayList<DataObj> getMriList() {
        return mriList;
    }

    public TestCaseObjects setMriList(ArrayList<DataObj> mriList) {
        this.mriList = mriList;
        return this;
    }

    public ArrayList<DataObj> getScanList() {
        return scanList;
    }

    public TestCaseObjects setScanList(ArrayList<DataObj> scanList) {
        this.scanList = scanList;
        return this;
    }

    public ArrayList<DataObj> getSonoList() {
        return sonoList;
    }

    public TestCaseObjects setSonoList(ArrayList<DataObj> sonoList) {
        this.sonoList = sonoList;
        return this;
    }

    public ArrayList<DataObj> getXrayList() {
        return xrayList;
    }

    public TestCaseObjects setXrayList(ArrayList<DataObj> xrayList) {
        this.xrayList = xrayList;
        return this;
    }

    public ArrayList<DataObj> getMedicineList() {
        return medicineList;
    }

    public TestCaseObjects setMedicineList(ArrayList<DataObj> medicineList) {
        this.medicineList = medicineList;
        return this;
    }

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public TestCaseObjects setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
        return this;
    }

    public ArrayList<DataObj> getProDiagnosisList() {
        return proDiagnosisList;
    }

    public TestCaseObjects setProDiagnosisList(ArrayList<DataObj> proDiagnosisList) {
        this.proDiagnosisList = proDiagnosisList;
        return this;
    }

    public ArrayList<DataObj> getDiagnosisList() {
        return diagnosisList;
    }

    public TestCaseObjects setDiagnosisList(ArrayList<DataObj> diagnosisList) {
        this.diagnosisList = diagnosisList;
        return this;
    }

    public ArrayList<DataObj> getInstructionList() {
        return instructionList;
    }

    public TestCaseObjects setInstructionList(ArrayList<DataObj> instructionList) {
        this.instructionList = instructionList;
        return this;
    }
}
