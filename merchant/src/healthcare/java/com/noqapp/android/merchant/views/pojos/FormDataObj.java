package com.noqapp.android.merchant.views.pojos;

import java.util.ArrayList;

public class FormDataObj {

    private ArrayList<DataObj> radiologyList = new ArrayList<>();
    private ArrayList<DataObj> pathologyList = new ArrayList<>();
    private ArrayList<DataObj> symptomsList = new ArrayList<>();
    private ArrayList<DataObj> diagnosisList = new ArrayList<>();
    private ArrayList<DataObj> medicineList = new ArrayList<>();
    private ArrayList<String> instructionList = new ArrayList<>();
    private ArrayList<DataObj> provisionalDiagnosisList = new ArrayList<>();


    public ArrayList<DataObj> getProvisionalDiagnosisList() {
        return provisionalDiagnosisList;
    }

    public FormDataObj setProvisionalDiagnosisList(ArrayList<DataObj> provisionalDiagnosisList) {
        this.provisionalDiagnosisList = provisionalDiagnosisList;
        return this;
    }

    public ArrayList<DataObj> getRadiologyList() {
        return radiologyList;
    }

    public FormDataObj setRadiologyList(ArrayList<DataObj> radiologyList) {
        this.radiologyList = radiologyList;
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
}
