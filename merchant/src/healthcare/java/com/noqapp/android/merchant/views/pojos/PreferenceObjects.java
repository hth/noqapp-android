package com.noqapp.android.merchant.views.pojos;

import com.google.gson.annotations.SerializedName;
import com.noqapp.android.merchant.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferenceObjects {

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

    @SerializedName("SPEC")
    private ArrayList<DataObj> specList = new ArrayList<>();

    @SerializedName(Constants.MEDICINE)
    private ArrayList<DataObj> medicineList = new ArrayList<>();

    @SerializedName(Constants.SYMPTOMS)
    private ArrayList<DataObj> symptomsList = new ArrayList<>();

    @SerializedName(Constants.PROVISIONAL_DIAGNOSIS)
    private ArrayList<DataObj> proDiagnosisList = new ArrayList<>();

    @SerializedName(Constants.DIAGNOSIS)
    private ArrayList<DataObj> diagnosisList = new ArrayList<>();

    @SerializedName(Constants.DENTAL_PROCEDURE)
    private ArrayList<DataObj> dentalProcedureList = new ArrayList<>();

    @SerializedName(Constants.INSTRUCTION)
    private ArrayList<String> instructionList = new ArrayList<>();

    @SerializedName(Constants.PREFERRED_STORE)
    private HashMap<String,PreferredStoreInfo> preferredStoreInfoHashMap = new HashMap<>();

    public ArrayList<DataObj> getPathologyList() {
        return pathologyList;
    }

    public PreferenceObjects setPathologyList(ArrayList<DataObj> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public ArrayList<DataObj> getMriList() {
        return mriList;
    }

    public PreferenceObjects setMriList(ArrayList<DataObj> mriList) {
        this.mriList = mriList;
        return this;
    }

    public ArrayList<DataObj> getScanList() {
        return scanList;
    }

    public PreferenceObjects setScanList(ArrayList<DataObj> scanList) {
        this.scanList = scanList;
        return this;
    }

    public ArrayList<DataObj> getSonoList() {
        return sonoList;
    }

    public PreferenceObjects setSonoList(ArrayList<DataObj> sonoList) {
        this.sonoList = sonoList;
        return this;
    }

    public ArrayList<DataObj> getXrayList() {
        return xrayList;
    }

    public PreferenceObjects setXrayList(ArrayList<DataObj> xrayList) {
        this.xrayList = xrayList;
        return this;
    }

    public ArrayList<DataObj> getSpecList() {
        return specList;
    }

    public PreferenceObjects setSpecList(ArrayList<DataObj> specList) {
        this.specList = specList;
        return this;
    }

    public ArrayList<DataObj> getMedicineList() {
        return medicineList;
    }

    public PreferenceObjects setMedicineList(ArrayList<DataObj> medicineList) {
        this.medicineList = medicineList;
        return this;
    }

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public PreferenceObjects setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
        return this;
    }

    public ArrayList<DataObj> getProDiagnosisList() {
        return proDiagnosisList;
    }

    public PreferenceObjects setProDiagnosisList(ArrayList<DataObj> proDiagnosisList) {
        this.proDiagnosisList = proDiagnosisList;
        return this;
    }

    public ArrayList<DataObj> getDiagnosisList() {
        return diagnosisList;
    }

    public PreferenceObjects setDiagnosisList(ArrayList<DataObj> diagnosisList) {
        this.diagnosisList = diagnosisList;
        return this;
    }

    public ArrayList<DataObj> getDentalProcedureList() {
        return dentalProcedureList;
    }

    public void setDentalProcedureList(ArrayList<DataObj> dentalProcedureList) {
        this.dentalProcedureList = dentalProcedureList;
    }

    public ArrayList<String> getInstructionList() {
        return instructionList;
    }

    public PreferenceObjects setInstructionList(ArrayList<String> instructionList) {
        this.instructionList = instructionList;
        return this;
    }

    public ArrayList<DataObj> clearListSelection(ArrayList<DataObj> selectList) {
        ArrayList<DataObj> temp = new ArrayList<>();
        for (DataObj d :
                selectList) {
            temp.add(d.setSelect(false));
        }
        return temp;
    }

    public HashMap<String, PreferredStoreInfo> getPreferredStoreInfoHashMap() {
        return preferredStoreInfoHashMap;
    }

    public PreferenceObjects setPreferredStoreInfoHashMap(HashMap<String, PreferredStoreInfo> preferredStoreInfoHashMap) {
        this.preferredStoreInfoHashMap = preferredStoreInfoHashMap;
        return this;
    }


    public boolean isEmpty() {
        return pathologyList.isEmpty() && mriList.isEmpty() &&
                scanList.isEmpty() && sonoList.isEmpty() &&
                specList.isEmpty() && symptomsList.isEmpty() &&
                xrayList.isEmpty() && medicineList.isEmpty() &&
                diagnosisList.isEmpty() && proDiagnosisList.isEmpty() &&
                instructionList.isEmpty() && preferredStoreInfoHashMap.isEmpty() ;
    }
}
