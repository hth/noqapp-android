package com.noqapp.android.common.model.types.category;

/**
 * hitender
 * 6/25/18 6:38 PM
 */
public enum MedicalDepartmentEnum {
    AYU("AYU", "Ayurveda"),
    CRD("CRD", "Cardiologist"),
    CHT("CHT", "Chest Physician"),
    CPY("CPY", "Clinical Psychologist"),
    DNT("DNT", "Dental"),
    DER("DER", "Dermatologist & Cosmetologist"),
    DIA("DIA", "Diabetologist"),
    DIE("DIE", "Dietitian"),
    ENT("ENT", "E.N.T"),
    GAS("GAS", "Gastroenterology"),
    GPY("GPY", "General Physician"),
    GSR("GSR", "General Surgeon"),
    HOM("HOM", "Homoeopathy"),
    LCS("LCS", "Laproscopic & Colorectal Surgeon"),
    NEP("NEP", "Nephrologist"),
    NEU("NEU", "Neuro Physician"),
    OFP("OFP", "Orofacial Pain & TMJ Disorder"),
    OGY("OGY", "Obstetrician and Gynaecologist"),
    ONC("ONC", "Oncologist"),
    OPT("OPT", "Opthalmologist"),
    ORT("ORT", "Orthopaedic"),
    PAE("PAE", "Paediatrician"),
    PAN("PAN", "Pain Specialist"),
    PNE("PNE", "Pediatric Neurologist"),
    PES("PES", "Pediatric Surgeon"),
    PHY("PHY", "Physiotherapist"),
    PLS("PLS", "Plastic Surgeon"),
    PSY("PSY", "Psychiatrist"),
    RAD("RAD", "Radiologist & Sonologist"),
    RHE("RHE", "Rheumatologist"),
    SPS("SPS", "Spine Surgeon"),
    SPM("SPM", "Sports Medicine"),
    URO("URO", "Urologist");

    private final String description;
    private final String name;

    MedicalDepartmentEnum(String name, String description) {
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
