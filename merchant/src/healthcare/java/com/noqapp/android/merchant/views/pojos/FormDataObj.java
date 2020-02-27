package com.noqapp.android.merchant.views.pojos;

import java.util.ArrayList;

public class FormDataObj {

    private ArrayList<DataObj> mriList = new ArrayList<>();
    private ArrayList<DataObj> sonoList = new ArrayList<>();
    private ArrayList<DataObj> scanList = new ArrayList<>();
    private ArrayList<DataObj> xrayList = new ArrayList<>();
    private ArrayList<DataObj> specList = new ArrayList<>();
    private ArrayList<DataObj> pathologyList = new ArrayList<>();
    private ArrayList<DataObj> symptomsList = new ArrayList<>();
    private ArrayList<DataObj> diagnosisList = new ArrayList<>();
    private ArrayList<DataObj> medicineList = new ArrayList<>();
    private ArrayList<String> instructionList = new ArrayList<>();
    private ArrayList<DataObj> provisionalDiagnosisList = new ArrayList<>();
    private ArrayList<DataObj> obstetricsList = new ArrayList<>();
    private ArrayList<DataObj> dentalProcedureList = new ArrayList<>();

    public ArrayList<DataObj> getProvisionalDiagnosisList() {
        return provisionalDiagnosisList;
    }

    public FormDataObj setProvisionalDiagnosisList(ArrayList<DataObj> provisionalDiagnosisList) {
        this.provisionalDiagnosisList = provisionalDiagnosisList;
        return this;
    }

    public ArrayList<DataObj> getMriList() {
        return mriList;
    }

    public FormDataObj setMriList(ArrayList<DataObj> mriList) {
        this.mriList = mriList;
        return this;
    }

    public ArrayList<DataObj> getSonoList() {
        return sonoList;
    }

    public FormDataObj setSonoList(ArrayList<DataObj> sonoList) {
        this.sonoList = sonoList;
        return this;
    }

    public ArrayList<DataObj> getScanList() {
        return scanList;
    }

    public FormDataObj setScanList(ArrayList<DataObj> scanList) {
        this.scanList = scanList;
        return this;
    }

    public ArrayList<DataObj> getXrayList() {
        return xrayList;
    }

    public FormDataObj setXrayList(ArrayList<DataObj> xrayList) {
        this.xrayList = xrayList;
        return this;
    }

    public ArrayList<DataObj> getPathologyList() {
        return pathologyList;
    }

    public FormDataObj setPathologyList(ArrayList<DataObj> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public FormDataObj setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
        return this;
    }

    public ArrayList<DataObj> getDiagnosisList() {
        return diagnosisList;
    }

    public FormDataObj setDiagnosisList(ArrayList<DataObj> diagnosisList) {
        this.diagnosisList = diagnosisList;
        return this;
    }

    public ArrayList<DataObj> getMedicineList() {
        return medicineList;
    }

    public FormDataObj setMedicineList(ArrayList<DataObj> medicineList) {
        this.medicineList = medicineList;
        return this;
    }

    public ArrayList<String> getInstructionList() {
        return instructionList;
    }

    public FormDataObj setInstructionList(ArrayList<String> instructionList) {
        this.instructionList = instructionList;
        return this;
    }

    public ArrayList<DataObj> getObstetricsList() {
        return obstetricsList;
    }

    public ArrayList<DataObj> getSpecList() {
        return specList;
    }

    public FormDataObj setSpecList(ArrayList<DataObj> specList) {
        this.specList = specList;
        return this;
    }

    public FormDataObj setObstetricsList(ArrayList<DataObj> obstetricsList) {
        this.obstetricsList = obstetricsList;
        return this;
    }

    public ArrayList<DataObj> getDentalProcedureList() {
        return dentalProcedureList;
    }

    public void setDentalProcedureList(ArrayList<DataObj> dentalProcedureList) {
        this.dentalProcedureList = dentalProcedureList;
    }
}
