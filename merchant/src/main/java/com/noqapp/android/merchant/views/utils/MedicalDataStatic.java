package com.noqapp.android.merchant.views.utils;

import com.google.gson.Gson;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import java.util.ArrayList;

public class MedicalDataStatic {

    public static class Gynae {
        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("DISCHARGE FROM VAGINA", "PV DISCHARGE", "", false));
            dataObjs.add(new DataObj("ITCHING AT VAGINA", "ITCHING AT VAGINA", "", false));
            dataObjs.add(new DataObj("DYSPAREUNIA (PAINFUL INTERCOURSE)", "DYSPAREUNIA (PAINFUL INTERCOURSE)", "", false));
            dataObjs.add(new DataObj("PAIN IN LOWER ABDOMEN", "PAIN IN LOWER ABDOMEN", "", false));
            dataObjs.add(new DataObj("PER VAGINAL BLEEDING", "PV BLEED", "", false));
            dataObjs.add(new DataObj("MENORRHAGIA", "MENORRHAGIA", "", false));
            dataObjs.add(new DataObj("MENOMETRORRHAGIA", "MENOMETRORRHAGIA", "", false));
            dataObjs.add(new DataObj("POLYMENORRHOEA", "POLYMENORRHOEA", "", false));
            dataObjs.add(new DataObj("FEVER", "FEVER", "", false));
            dataObjs.add(new DataObj("PAIN IN BREAST (MASTALGIA)", "PAIN IN BREAST (MASTALGIA)", "", false));
            dataObjs.add(new DataObj("AMENORRHOEA", "AMENORRHOEA", "", false));
            dataObjs.add(new DataObj("PALLOR", "PALLOR", "", false));
            dataObjs.add(new DataObj("LOOSE MOTIONS", "LOOSE MOTIONS", "", false));
            dataObjs.add(new DataObj("PAIN AT VAGINAL REGION", "PAIN AT VAGINAL REGION", "", false));
            dataObjs.add(new DataObj("WEIGHT GAIN", "WEIGHT GAIN", "", false));
            dataObjs.add(new DataObj("WHITE DISCHARGE PER VAGINAL (LEUCORRHOEA)", "WHITE DISCHARGE PER VAGINAL (LEUCORRHOEA)", "", false));
            dataObjs.add(new DataObj("BURNING MICTURATION", "BURNING MICTURATION", "", false));
            dataObjs.add(new DataObj("DELAYED & IRREGULAR CYCLES", "DELAYED & IRREGULAR CYCLES", "", false));
            dataObjs.add(new DataObj("PAIN AT ILIAC REGION", "PAIN AT ILIAC REGION", "", false));
            dataObjs.add(new DataObj("POST MENOPAUSAL SYMPTOMS", "POST MENOPAUSAL SYMPTOMS", "", false));
            dataObjs.add(new DataObj("HEAVINESS IN LOWER ABDOMEN", "HEAVINESS IN LOWER ABDOMEN", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("MENORRHAGIA", "MENORRHAGIA", "", false));
            dataObjs.add(new DataObj("VAGINITIS", "VAGINITIS", "", false));
            dataObjs.add(new DataObj("VULVOVAGINITIS", "VULVOVAGINITIS", "", false));
            dataObjs.add(new DataObj("CERVICITIS", "CERVICITIS", "", false));
            dataObjs.add(new DataObj("PELVIC INFLAMMATORY DISEASE", "PID", "", false));
            dataObjs.add(new DataObj("UTERINE FIBROID", "UTERINE FIBROID", "", false));
            dataObjs.add(new DataObj("ENDOMETROSIS", "ENDOMETROSIS", "", false));
            dataObjs.add(new DataObj("INFERTILITY : PRIMARY", "INFERTILITY : PRIMARY", "", false));
            dataObjs.add(new DataObj("INFERTILITY – SECONDARY", "INFERTILITY – SECONDARY", "", false));
            dataObjs.add(new DataObj("5 - 40 WEEKS PREGNANCY", "5 - 40 WEEKS PREGNANCY", "", false));
            dataObjs.add(new DataObj("EARLY PREGNANCY WITH PV BLEEDING", "EARLY PREGNANCY WITH PV BLEEDING", "", false));
            dataObjs.add(new DataObj("VESICULAR MOLE", "VESICULAR MOLE", "", false));
            dataObjs.add(new DataObj("BARTHOLIN CYST", "BARTHOLIN CYST", "", false));
            dataObjs.add(new DataObj("BARTHOLIN ABSCESS", "BARTHOLIN ABSCESS", "", false));
            dataObjs.add(new DataObj("PREMATURE DELIVERY", "PREMATURE DELIVERY", "", false));
            dataObjs.add(new DataObj("MISSED ABORTION", "MISSED ABORTION", "", false));
            dataObjs.add(new DataObj("THREATENED ABORTION", "THREATENED ABORTION", "", false));
            dataObjs.add(new DataObj("COMPLETE ABORTION", "COMPLETE ABORTION", "", false));
            dataObjs.add(new DataObj("PREMATURE CONTRACTION", "PREMATURE CONTRACTION", "", false));
            dataObjs.add(new DataObj("INTRAUTERINE GROWTH RETARDATION WITH PREGANANCY", "IUGR WITH PREG", "", false));
            dataObjs.add(new DataObj(" PLACENTA PREVIA WITH PREGNANCY", "PL. PREVIA WITH PREG", "", false));
            dataObjs.add(new DataObj("FULL TERM PREGNANCY WITH LABOUR PAIN", "FTP WITH LP", "", false));
            dataObjs.add(new DataObj("FULL TERM PREGNANCY WITH LEAKING", "FTP WITH LEAKING", "", false));
            dataObjs.add(new DataObj("POST PARTEM HAEMORRHAGE", "PPH", "", false));
            dataObjs.add(new DataObj("ANTE PARTEM HAEMORRHAGE", "APH", "", false));
            dataObjs.add(new DataObj("HYPEREMESIS GRAVIDANUM", "HYPEREMESIS GRAVIDANUM", "", false));
            dataObjs.add(new DataObj("ECTOPIC PREGNANCY", "ECTOPIC PREGNANCY", "", false));
            dataObjs.add(new DataObj("CHOCOLATE CYST IN OVERY", "CHOCOLATE CYST IN OVERY", "", false));
            dataObjs.add(new DataObj("POLYCYSTIC OVARIAN DISEASE", "PCOD", "", false));
            dataObjs.add(new DataObj("UTERINE POLYP", "UTERINE POLYP", "", false));
            dataObjs.add(new DataObj("VAGINAL POLYP", "VAGINAL POLYP", "", false));
            dataObjs.add(new DataObj("ABRUPTIO PLACENTA", "ABRUPTIO PLACENTA", "", false));
            dataObjs.add(new DataObj("UTERINE CARCINOMA", "CA UTERUS", "", false));
            dataObjs.add(new DataObj("FIBROADENOMA", "FIBROADENOMA", "", false));
            dataObjs.add(new DataObj("HYDROMNIOS", "HYDROMNIOS", "", false));
            dataObjs.add(new DataObj("BREAST CANCER", "CA BREAST", "", false));
            dataObjs.add(new DataObj("MASTITIS", "MASTITIS", "", false));
            dataObjs.add(new DataObj("ANAEMIA IN PREGNANCY", "ANAEMIA IN PREGNANCY", "", false));
            dataObjs.add(new DataObj("ECLAMPSIA", "ECLAMPSIA", "", false));
            dataObjs.add(new DataObj("PRURITUS VULVAE", "PRURITUS VULVAE", "", false));
            dataObjs.add(new DataObj("CERVICAL CARCINOMA", "CA CERVIX", "", false));
            dataObjs.add(new DataObj("PREGNANCY INDUCED HYPERTENSION", "PIH", "", false));
            dataObjs.add(new DataObj("OLIGOHYDRAMNIOS", "OLIGOHYDRAMNIOS", "", false));
            dataObjs.add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 1ST TRIMESTER", "MTP 1", "", false));
            dataObjs.add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 2ND TRIMESTER", "MTP 2", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getObstetrics() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("PAIN IN ABDOMEN", "PAIN IN ABDOMEN", "", false));
            dataObjs.add(new DataObj("PER VAGINAL BLEEDING", "PV BLEED", "", false));
            dataObjs.add(new DataObj("PER VAGINAL LEAKING", "PV LEAK", "", false));
            dataObjs.add(new DataObj("PAIN AT ILIAC REGION", "PAIN AT ILIAC REGION", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN BREATHING", "DIFFICULTY IN BREATHING", "", false));
            dataObjs.add(new DataObj("SWELLING OVER FEETS", "SWELLING OVER FEETS", "", false));
            dataObjs.add(new DataObj("CONVULSION IN PREGANANCY", "CONVULSION IN PREGANANCY", "", false));
            dataObjs.add(new DataObj("CONSTIPATION", "CONSTIPATION", "", false));
            dataObjs.add(new DataObj("HYPERTENSION IN PREGNANCY", "HYPERTENSION IN PREGNANCY", "", false));
            dataObjs.add(new DataObj("GESTATIONAL DIABETES", "GESTATIONAL DIABETES", "", false));
            return dataObjs;
        }
    }

    public static class Pediatrician {
        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("MALARIA FEVER", "", false));
            dataObjs.add(new DataObj("DENGUE FEVER", "", false));
            dataObjs.add(new DataObj("THYPHOID FEVER (ENTERIC FEVER)", "", false));
            dataObjs.add(new DataObj("VIRAL FEVER", "", false));
            dataObjs.add(new DataObj("URINARY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("FEBRILE CONVULSION", "", false));
            dataObjs.add(new DataObj("BIRTH ASPHYSIA", "", false));
            dataObjs.add(new DataObj("DEVELOPMENTAL DELAY", "", false));
            dataObjs.add(new DataObj("RESPIRATORY DISTRESS", "", false));
            dataObjs.add(new DataObj("UPPER RESPIRATORY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("LOWER RESPIRATORY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("ACUTE GASTROENTERITIS", "", false));
            dataObjs.add(new DataObj("TYPE 1 DM", "", false));
            dataObjs.add(new DataObj("NEPHROTIC SYNDROME", "", false));
            dataObjs.add(new DataObj("NEONATAL JAUNDICE", "", false));
            dataObjs.add(new DataObj("DUCTUS ARTERIOSUS", "", false));
            dataObjs.add(new DataObj("DUCTUS VENOSUS", "", false));
            dataObjs.add(new DataObj("CARDIOMEGALY", "", false));
            dataObjs.add(new DataObj("CARDIOMYOPATHY", "", false));
            dataObjs.add(new DataObj("PNEUMONIA", "", false));
            dataObjs.add(new DataObj("PNEUMONITIS", "", false));
            dataObjs.add(new DataObj("ASYMPTOMATIC HYPOGLYCEMIA", "", false));
            dataObjs.add(new DataObj("PULMONARY KOCHS", "", false));
            dataObjs.add(new DataObj("TINEA CORPORIS", "", false));
            dataObjs.add(new DataObj("PHEMOSIS", "", false));
            dataObjs.add(new DataObj("UNDESCENDED TESTIS", "", false));
            dataObjs.add(new DataObj("HYPONATREMIA", "", false));
            dataObjs.add(new DataObj("HYPERNATREMIA", "", false));
            dataObjs.add(new DataObj("HYPOKALEMIA", "", false));
            dataObjs.add(new DataObj("HYPERKALEMIA", "", false));
            dataObjs.add(new DataObj("PTOSIS", "", false));
            dataObjs.add(new DataObj("SCABIES", "", false));
            dataObjs.add(new DataObj("SINUSITIS", "", false));
            dataObjs.add(new DataObj("PHARYNGITIS", "", false));
            dataObjs.add(new DataObj("MOUTH ULCERS", "", false));
            dataObjs.add(new DataObj("THROMBOCYTOPENIA", "", false));
            dataObjs.add(new DataObj("CHICKEN POX", "", false));
            dataObjs.add(new DataObj("MEASELS", "", false));
            dataObjs.add(new DataObj("MUMPS", "", false));
            dataObjs.add(new DataObj("CONJUNCTIVITIS", "", false));
            dataObjs.add(new DataObj("ACUTE SUPPURATIVE OTITIS MEDIA", "", false));
            dataObjs.add(new DataObj("CHRONIC SUPPURATIVE OTITIS MEDIA", "", false));
            dataObjs.add(new DataObj("ACUTE TONSILLITIS", "", false));
            dataObjs.add(new DataObj("CHRONIC TONSILLITIS", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("FEVER", "", false));
            dataObjs.add(new DataObj("COUGH", "", false));
            dataObjs.add(new DataObj("HEADACHE", "", false));
            dataObjs.add(new DataObj("COLD", "", false));
            dataObjs.add(new DataObj("THROAT PAIN", "", false));
            dataObjs.add(new DataObj("DECREASED APPETITE", "", false));
            dataObjs.add(new DataObj("RASHES ALL OVER BODY", "", false));
            dataObjs.add(new DataObj("NOCTURIA", "", false));
            dataObjs.add(new DataObj("PAINFUL MICTURATION", "", false));
            dataObjs.add(new DataObj("BURNING MICTURATION", "", false));
            dataObjs.add(new DataObj("ABDOMINAL PAIN", "", false));
            dataObjs.add(new DataObj("LOOSE MOTION", "", false));
            dataObjs.add(new DataObj("VOMITING", "", false));
            dataObjs.add(new DataObj("BOILS ON BODY PART", "", false));
            dataObjs.add(new DataObj("ICTERUS", "", false));
            dataObjs.add(new DataObj("BLUISH DISCOLOURATION OF SCLERA", "", false));
            dataObjs.add(new DataObj("DROWSYNESS", "", false));
            dataObjs.add(new DataObj("CONVUSION", "", false));
            dataObjs.add(new DataObj("FROATHING FROM MOUTH", "", false));
            dataObjs.add(new DataObj("IRRITABILITY", "", false));
            dataObjs.add(new DataObj("DEHYDRATION", "", false));
            dataObjs.add(new DataObj("CONSTIPATION", "", false));
            dataObjs.add(new DataObj("EARACHE", "", false));
            dataObjs.add(new DataObj("REDNESS OF EYES", "", false));
            dataObjs.add(new DataObj("WATERING FROM BOTH EYES", "", false));
            dataObjs.add(new DataObj("BODYACHE", "", false));
            dataObjs.add(new DataObj("ALL JOINT PAIN", "", false));
            dataObjs.add(new DataObj("NAUSEA", "", false));
            dataObjs.add(new DataObj("SHORTNESS OF BREATH", "", false));
            dataObjs.add(new DataObj("ITCHING ALL OVER BODY", "", false));
            dataObjs.add(new DataObj("ABDOMINAL DISTENTION", "", false));
            dataObjs.add(new DataObj("CLUBBING", "", false));
            dataObjs.add(new DataObj("REDNESS IN MOUTH", "", false));
            dataObjs.add(new DataObj("MUSCLE CRAMPS", "", false));
            dataObjs.add(new DataObj("DISCHARGE FROM EAR", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN SWALLOWING", "", false));
            return dataObjs;
        }
    }

    public static class Surgeon {
        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("RENAL STONE", "", false));
            dataObjs.add(new DataObj("URETERIC STONE", "", false));
            dataObjs.add(new DataObj("ACUTE APPENDICITIS", "", false));
            dataObjs.add(new DataObj("CHRONIC APPENDICITIS", "", false));
            dataObjs.add(new DataObj("ACUTE PANCREATITIS", "", false));
            dataObjs.add(new DataObj("CHOLELITHIASIS", "", false));
            dataObjs.add(new DataObj("CHOLECYSTITIS", "", false));
            dataObjs.add(new DataObj("CHRONIC PANCREATITIS", "", false));
            dataObjs.add(new DataObj("CARBUNCLE", "", false));
            dataObjs.add(new DataObj("BOILS", "", false));
            dataObjs.add(new DataObj("INTESTINAL OBSTRUCTION", "", false));
            dataObjs.add(new DataObj("URINARY OBSTRUCTION", "", false));
            dataObjs.add(new DataObj("MALENA", "", false));
            dataObjs.add(new DataObj("HAEMATEMESIS", "", false));
            dataObjs.add(new DataObj("HAEMAPTOSIS", "", false));
            dataObjs.add(new DataObj("GASTRITIS", "", false));
            dataObjs.add(new DataObj("IRRITABLE BOWEL SYNDROME", "", false));
            dataObjs.add(new DataObj("SPLEENOMEGALY", "", false));
            dataObjs.add(new DataObj("DIABETIC FOOT", "", false));
            dataObjs.add(new DataObj("DIABETIC ULCERS", "", false));
            dataObjs.add(new DataObj("DUODINITIS", "", false));
            dataObjs.add(new DataObj("CELLULITIS", "", false));
            dataObjs.add(new DataObj("VASICAL STONE", "", false));
            dataObjs.add(new DataObj("CLW SUTURING", "", false));
            dataObjs.add(new DataObj("PENILE INJURY", "", false));
            dataObjs.add(new DataObj("CIRCUMCISION", "", false));
            dataObjs.add(new DataObj("HYDROCELE", "", false));
            dataObjs.add(new DataObj("INGUINAL HERNIA", "", false));
            dataObjs.add(new DataObj("UMBILICAL HERNIA", "", false));
            dataObjs.add(new DataObj("RETENTION OF URINE", "", false));
            dataObjs.add(new DataObj("TORTION OF TESTIS", "", false));
            dataObjs.add(new DataObj("AVUSION OF NAIL", "", false));
            dataObjs.add(new DataObj("CRUSH INJURY", "", false));
            dataObjs.add(new DataObj("TENDON CUT", "", false));
            dataObjs.add(new DataObj("FISSURE IN ANO", "", false));
            dataObjs.add(new DataObj("HAEMORRHOIDS", "", false));
            dataObjs.add(new DataObj("FISTULA IN ANO", "", false));
            dataObjs.add(new DataObj("URINARY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("PERITONITIS", "", false));
            dataObjs.add(new DataObj("PERFORATIVE PERITONITIS", "", false));
            dataObjs.add(new DataObj("DEODENAL ULCER", "", false));
            dataObjs.add(new DataObj("GASTRIC ULCER", "", false));
            dataObjs.add(new DataObj("ULCERATIVE COLLITIS", "", false));
            dataObjs.add(new DataObj("ENTERITIS", "", false));
            dataObjs.add(new DataObj("CORN", "", false));
            dataObjs.add(new DataObj("BURN INJURY", "", false));
            dataObjs.add(new DataObj("TRAUMATIC INJURY TO SPLEEN", "", false));
            dataObjs.add(new DataObj("TRAUMATIC INJURY TO LIVER", "", false));
            dataObjs.add(new DataObj("SPLEENIC CYST", "", false));
            dataObjs.add(new DataObj("DRY GANGRENE", "", false));
            dataObjs.add(new DataObj("WET GANGRENE", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("PAIN IN ABDOMEN", "", false));
            dataObjs.add(new DataObj("NAUSEA", "", false));
            dataObjs.add(new DataObj("VOMITING", "", false));
            dataObjs.add(new DataObj("FEVER", "", false));
            dataObjs.add(new DataObj("PAIN IN ABDOMEN LOIN TO GROIN REGION", "", false));
            dataObjs.add(new DataObj("BURNING MICTURATION", "", false));
            dataObjs.add(new DataObj("INCREASED FREQUENCY OF URINATION ", "", false));
            dataObjs.add(new DataObj("DRIBBLING OF MICTURATION", "", false));
            dataObjs.add(new DataObj("PAIN AT ANAL REGION WHILE PASSING MOTION", "", false));
            dataObjs.add(new DataObj("BLEEDING FROM ANAL REGION WHILE PASSING MOTION", "", false));
            dataObjs.add(new DataObj("REDNESS AT ANAL REGION", "", false));
            dataObjs.add(new DataObj("WOUND OVER BODY", "", false));
            dataObjs.add(new DataObj("DISCHARGE FROM WOUND", "", false));
            dataObjs.add(new DataObj("SWELLING OVER LOWER LIMB", "", false));
            dataObjs.add(new DataObj("SWELLING OVER UPPER LIMB", "", false));
            dataObjs.add(new DataObj("REDNESS +", "", false));
            dataObjs.add(new DataObj("CARBUNCLE +", "", false));
            dataObjs.add(new DataObj("ERUCTATIONS", "", false));
            dataObjs.add(new DataObj("CONSTIPATIONS", "", false));
            dataObjs.add(new DataObj("AVULSION OF NAIL PAIN", "", false));
            dataObjs.add(new DataObj("LOOSE STOOLS", "", false));
            dataObjs.add(new DataObj("BLEEDING IN VOMITING", "", false));
            dataObjs.add(new DataObj("BLEEDING IN SPUTUM", "", false));
            dataObjs.add(new DataObj("BURNING AT EPIGASTRIC REGION", "", false));
            dataObjs.add(new DataObj("INGESTION OF FOREIGN BODY", "", false));
            dataObjs.add(new DataObj("CLW", "", false));
            dataObjs.add(new DataObj("SWELLING AT INGUINAL REGION WHILE COUGHING", "", false));
            dataObjs.add(new DataObj("SWELLING AT UMBILICAL REGION", "", false));
            dataObjs.add(new DataObj("HEAVINESS IN ABDOMEN", "", false));
            dataObjs.add(new DataObj("GASES DISTENTION", "", false));
            dataObjs.add(new DataObj("PENILE INJURY", "", false));
            dataObjs.add(new DataObj("PAIN IN PENIS", "", false));
            dataObjs.add(new DataObj("PAIN AT EPIGASTRIC REGION RADIATED TOWARDS BACK", "", false));
            dataObjs.add(new DataObj("PAIN AT SPLEENIC REGION", "", false));
            dataObjs.add(new DataObj("PAIN AT TESTIS", "", false));
            dataObjs.add(new DataObj("SWELLING AT SCROTUM", "", false));
            dataObjs.add(new DataObj("ACCIDENTAL BURN INJURY ", "", false));
            dataObjs.add(new DataObj("PUS DISCHARGE", "", false));
            return dataObjs;
        }
    }

    public static class Ortho {
        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("FRACTURE RIGHT CLAVICLE", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT CLAVICLE", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT HEAD OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT HEAD OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT MIDSHALF OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT MIDSHAFT OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT LOWER END OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT LOWER END OF HUMERUS", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT UPPER END OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT UPPER END OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT MIDSHAFT OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT MIDSHAFT OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT LOWER END OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT LOWER END OF RADIUS & ULNA", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT METATARSAL BONE", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT METATARSAL BONE", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT IT FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT IT  FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT HEAD OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT HEAD OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT MIDSHAFT OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT MIDSHAFT OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT LOWER END OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT LOWER END OF FEMUR", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT UPPER END OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT UPPER END OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT MIDSHAFT OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT MIDSHAFT OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT LOWER END OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT LOWER END OF TIBIA & FIBULA", "", false));
            dataObjs.add(new DataObj("FRACTURE LEFT METACARPAL BONES", "", false));
            dataObjs.add(new DataObj("FRACTURE RIGHT METACARPAL BONES", "", false));
            dataObjs.add(new DataObj("RHEUMATOID ARTHRITIS", "", false));
            dataObjs.add(new DataObj("PROLAPSE INTERVERTIBRAL DISC", "", false));
            dataObjs.add(new DataObj("CERVICAL SPONDYLOSIS", "", false));
            dataObjs.add(new DataObj("LUMBAR SPONDYLOSIS", "", false));
            dataObjs.add(new DataObj("LORDOSIS", "", false));
            dataObjs.add(new DataObj("GOUT", "", false));
            dataObjs.add(new DataObj("DISLOCATION OF RIGHT SHOULDER JOINT", "", false));
            dataObjs.add(new DataObj("DISLOCATION OF LEFT SHOULDER JOINT", "", false));
            dataObjs.add(new DataObj("FRACTURE THORACIC RIB RIGHT SIDE", "", false));
            dataObjs.add(new DataObj("FRACTURE THORACIC RIB LEFT SIDE", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("PAIN AT RIGHT SHOULDER JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT SHOULDER JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT ELBOW JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT ELBOW JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT WRIST JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT WRIST JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT HAND", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT HAND", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT HIP JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT HIP JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT KNEE JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT KNEE JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT ANKLE JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT ANKLE JOINT", "", false));
            dataObjs.add(new DataObj("PAIN AT RIGHT FOOT", "", false));
            dataObjs.add(new DataObj("PAIN AT LEFT FOOT", "", false));
            dataObjs.add(new DataObj("SWELLING ", "", false));
            dataObjs.add(new DataObj("MOVEMENT RESTRICTION", "", false));
            dataObjs.add(new DataObj("LUMBAR PAIN", "", false));
            dataObjs.add(new DataObj("SCAPULAR PAIN", "", false));
            dataObjs.add(new DataObj("CERVICAL PAIN", "", false));
            dataObjs.add(new DataObj("PAIN ALL OVER JOINTS (MULTIPLE)", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN WALKING", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN SITTING", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN LYING DOWN", "", false));
            dataObjs.add(new DataObj("BLUNT INJURY", "", false));
            dataObjs.add(new DataObj("CLW ", "", false));
            dataObjs.add(new DataObj("PULSATIONS +", "", false));
            dataObjs.add(new DataObj("PAIN OVER RIGHT CLAVICLE REGION", "", false));
            dataObjs.add(new DataObj("PAIN OVER LEFT CLAVICLE REGION", "", false));
            return dataObjs;
        }
    }

    public static class Dental {
        public static String ADDITIONAL_OPTION = "All";

        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("CONSULTATION", "", false));
            dataObjs.add(new DataObj("OPG REVIEW", "", false));
            dataObjs.add(new DataObj("IOPA (XRAYS)", "", false));
            dataObjs.add(new DataObj("SCALING", "", false));
            dataObjs.add(new DataObj("SCALING + POLISHING", "", false));
            dataObjs.add(new DataObj("SCALING + FLUORIDE", "", false));
            dataObjs.add(new DataObj("SEALNTS", "", false));
            dataObjs.add(new DataObj("LDD", "", false));
            dataObjs.add(new DataObj("RESTORATION", "", false));
            dataObjs.add(new DataObj("CORE", "", false));
            dataObjs.add(new DataObj("GIC FILLING", "", false));
            dataObjs.add(new DataObj("RCX", "", false));
            dataObjs.add(new DataObj("RCX+ SSC", "", false));
            dataObjs.add(new DataObj("PULPECTOMY", "", false));
            dataObjs.add(new DataObj("CALCIFIED TOOTH RCX", "", false));
            dataObjs.add(new DataObj("EXTRACTION", "", false));
            dataObjs.add(new DataObj("IMPACTION SURGERY", "", false));
            dataObjs.add(new DataObj("ALVEOLECTOMY", "", false));
            dataObjs.add(new DataObj("OPERCULECTOMY", "", false));
            dataObjs.add(new DataObj("PARTIAL DENTURE", "", false));
            dataObjs.add(new DataObj("COMPLETE DENTURE", "", false));
            dataObjs.add(new DataObj("IMPLANT", "", false));
            dataObjs.add(new DataObj("CROWN", "", false));
            dataObjs.add(new DataObj("BRIDGE", "", false));
            dataObjs.add(new DataObj("ORTHO BRACES", "", false));
            dataObjs.add(new DataObj("SPACE MAINTAINER", "", false));
            dataObjs.add(new DataObj("RETAINER PLATE", "", false));
            dataObjs.add(new DataObj("SPLINT", "", false));
            dataObjs.add(new DataObj("NTI", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("TOOTHACHE", "", false));
            dataObjs.add(new DataObj("CAVITIES", "", false));
            dataObjs.add(new DataObj("BLEEDING, PAINFUL GUMS OR TEETH", "", false));
            dataObjs.add(new DataObj("NEED FOR SCALING AND CLEANING", "", false));
            dataObjs.add(new DataObj("NEED REPAIR OF APPLIANCE OR RESTORATION", "", false));
            dataObjs.add(new DataObj("EXTRACTION", "", false));
            dataObjs.add(new DataObj("DIAGNOSTICS SERVICE / ROUTINE CHECKUP", "", false));
            dataObjs.add(new DataObj("FULL DENTURE / PARTIAL DENTURE", "", false));
            dataObjs.add(new DataObj("CROWNS / VENEERS", "", false));
            dataObjs.add(new DataObj("ROOT CANAL THERAPY", "", false));
            dataObjs.add(new DataObj("COSMETIC TREATMENT", "", false));
            dataObjs.add(new DataObj("IMPLANTS", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getDentalDiagnosisList() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.clear();
            dataObjs.add(new DataObj("18", false));
            dataObjs.add(new DataObj("17", false));
            dataObjs.add(new DataObj("16", false));
            dataObjs.add(new DataObj("15", false));
            dataObjs.add(new DataObj("14", false));
            dataObjs.add(new DataObj("13", false));
            dataObjs.add(new DataObj("12", false));
            dataObjs.add(new DataObj("11", false));

            dataObjs.add(new DataObj("21", false));
            dataObjs.add(new DataObj("22", false));
            dataObjs.add(new DataObj("23", false));
            dataObjs.add(new DataObj("24", false));
            dataObjs.add(new DataObj("25", false));
            dataObjs.add(new DataObj("26", false));
            dataObjs.add(new DataObj("27", false));
            dataObjs.add(new DataObj("28", false));

            dataObjs.add(new DataObj("48", false));
            dataObjs.add(new DataObj("47", false));
            dataObjs.add(new DataObj("46", false));
            dataObjs.add(new DataObj("45", false));
            dataObjs.add(new DataObj("44", false));
            dataObjs.add(new DataObj("43", false));
            dataObjs.add(new DataObj("42", false));
            dataObjs.add(new DataObj("41", false));

            dataObjs.add(new DataObj("31", false));
            dataObjs.add(new DataObj("32", false));
            dataObjs.add(new DataObj("33", false));
            dataObjs.add(new DataObj("34", false));
            dataObjs.add(new DataObj("35", false));
            dataObjs.add(new DataObj("36", false));
            dataObjs.add(new DataObj("37", false));
            dataObjs.add(new DataObj("38", false));

            return dataObjs;
        }
    }

    public static class Physician {
        public static ArrayList<DataObj> getDiagnosis() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("ACUTE FEBRILE ILLNESS", "", false));
            dataObjs.add(new DataObj("ACUTE GASTROENTERITIS", "", false));
            dataObjs.add(new DataObj("ACUTE GASTRITIS", "", false));
            dataObjs.add(new DataObj("ACUTE ABDOMEN", "", false));
            dataObjs.add(new DataObj("MALARIA FEVER", "", false));
            dataObjs.add(new DataObj("DENGUE FEVER", "", false));
            dataObjs.add(new DataObj("ACUTE PANCREATITIS", "", false));
            dataObjs.add(new DataObj("ACUTE APPENDICITIS", "", false));
            dataObjs.add(new DataObj("ACCELERATED HYPERTENSION", "", false));
            dataObjs.add(new DataObj("ALLERGIC RHINITIS", "", false));
            dataObjs.add(new DataObj("DIABETES MELLITUS", "", false));
            dataObjs.add(new DataObj("SINUSITIS", "", false));
            dataObjs.add(new DataObj("ACUTE EXACERBATION OF BRONCHIAL ASTHMA", "", false));
            dataObjs.add(new DataObj("ACUTE EXACERBATION OF CHRONIC OBSTRUCTIVE PULMONARY DISEASE", "", false));
            dataObjs.add(new DataObj("ACUTE CORONARY SYNDROME", "", false));
            dataObjs.add(new DataObj("MIGRAINE", "", false));
            dataObjs.add(new DataObj("MYOCARDIAL INFARCTION", "", false));
            dataObjs.add(new DataObj("ISCHAEMIC HEART DISEASE", "", false));
            dataObjs.add(new DataObj("MITRAL STENOSIS", "", false));
            dataObjs.add(new DataObj("AORTIC STENOSIS", "", false));
            dataObjs.add(new DataObj("LEFT HEMIPLEGIA", "", false));
            dataObjs.add(new DataObj("RIGHT HEMIPLEGIA", "", false));
            dataObjs.add(new DataObj("RHEUMATOID ARTHRITIS", "", false));
            dataObjs.add(new DataObj("GOUT", "", false));
            dataObjs.add(new DataObj("INTERSTITIAL LUNG DISEASE", "", false));
            dataObjs.add(new DataObj("HYPOTHYROIDISM", "", false));
            dataObjs.add(new DataObj("HYPERTHYROIDISM", "", false));
            dataObjs.add(new DataObj("BRONCHITIS", "", false));
            dataObjs.add(new DataObj("PULMONARY KOCHS", "", false));
            dataObjs.add(new DataObj("PNEUMONITIS", "", false));
            dataObjs.add(new DataObj("PNEUMONIA", "", false));
            dataObjs.add(new DataObj("POTS SPINE", "", false));
            dataObjs.add(new DataObj("PHARYNGITIS", "", false));
            dataObjs.add(new DataObj("LARYNGITIS", "", false));
            dataObjs.add(new DataObj("JAUNDICE", "", false));
            dataObjs.add(new DataObj("HEPATITIS", "", false));
            dataObjs.add(new DataObj("THALASSEMIA ", "", false));
            dataObjs.add(new DataObj("IRON DEFFICIENCY ANAEMIA", "", false));
            dataObjs.add(new DataObj("MEGALOBLASTIC ANAEMIA", "", false));
            dataObjs.add(new DataObj("SICKLE CELL ANAEMIA", "", false));
            dataObjs.add(new DataObj("STROKE ", "", false));
            dataObjs.add(new DataObj("CEREBRO VASCULAR ACCIDENT", "", false));
            dataObjs.add(new DataObj("CHRONIC KIDNEY DISEASE", "", false));
            dataObjs.add(new DataObj("ACUTE KIDNEY DISEASE", "", false));
            dataObjs.add(new DataObj("ACUTE ON CHRONIC KIDNEY DISEASE", "", false));
            dataObjs.add(new DataObj("RIGHT URETERIC COLIC", "", false));
            dataObjs.add(new DataObj("LEFT URETERIC COLIC", "", false));
            dataObjs.add(new DataObj("THYPHOID FEVER (ENTERIC FEVER)", "", false));
            dataObjs.add(new DataObj("RIGHT RENAL COLIC", "", false));
            dataObjs.add(new DataObj("LEFT RENAL COLIC", "", false));
            dataObjs.add(new DataObj("VASICAL CALCULOUS", "", false));
            dataObjs.add(new DataObj("URINARY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("DOWNS SYNDROME", "", false));
            dataObjs.add(new DataObj("SYSTEMIC LUPUS ERYTHEMATOSUS", "", false));
            dataObjs.add(new DataObj("HEAD INJURY", "", false));
            dataObjs.add(new DataObj("SUBARACHNOID HAEMORRHAGE", "", false));
            dataObjs.add(new DataObj("SUBDURAL HAEMMORRHAGE", "", false));
            dataObjs.add(new DataObj("CHRONIC TONSILLITIS", "", false));
            dataObjs.add(new DataObj("INFECTIVE TONSILLITIS", "", false));
            dataObjs.add(new DataObj("PULMONARY EMBOLISM", "", false));
            dataObjs.add(new DataObj("ATRIAL FIBRILLATION", "", false));
            dataObjs.add(new DataObj("ATRIAL FLUTTER", "", false));
            dataObjs.add(new DataObj("AORTIC INSUFFICIENCY", "", false));
            dataObjs.add(new DataObj("ACUTE INTERSTITIAL GLOMERULONEPHRITIS", "", false));
            dataObjs.add(new DataObj("AORTIC REGURGITATION", "", false));
            dataObjs.add(new DataObj("ACUTE RHEUMATIC FEVER", "", false));
            dataObjs.add(new DataObj("ATRIAL SEPTAL DEFECT", "", false));
            dataObjs.add(new DataObj("OVERDOSE OF THE MEDICINES", "", false));
            dataObjs.add(new DataObj("PHENYL POISONING", "", false));
            dataObjs.add(new DataObj("MULTIPLE MYELOMA", "", false));
            dataObjs.add(new DataObj("LEUKEMIA", "", false));
            dataObjs.add(new DataObj("PROSTATOMEGALY", "", false));
            dataObjs.add(new DataObj("CONGESTIVE CARDIAC FAILURE", "", false));
            dataObjs.add(new DataObj("CHOLECYSTITIS", "", false));
            dataObjs.add(new DataObj("CHOLELITHIASIS", "", false));
            dataObjs.add(new DataObj("FACIAL PALSY", "", false));
            dataObjs.add(new DataObj("DIABETES INSIPIDUS", "", false));
            dataObjs.add(new DataObj("DISSEMINATED INTRAVASCULAR COAGULATION", "", false));
            dataObjs.add(new DataObj("DIABETIC KETOACIDOSIS", "", false));
            dataObjs.add(new DataObj("DEEP VEIN THROMBOSIS", "", false));
            dataObjs.add(new DataObj("DUODENITIS", "", false));
            dataObjs.add(new DataObj("GUILLAIN BARRE SYNDROME", "", false));
            dataObjs.add(new DataObj("GLOMERULONEPHRITIS", "", false));
            dataObjs.add(new DataObj("HYPONATREMIA", "", false));
            dataObjs.add(new DataObj("HYPOKALEMIA", "", false));
            dataObjs.add(new DataObj("HYPERNATREMIA", "", false));
            dataObjs.add(new DataObj("HYPERKALEMIA", "", false));
            dataObjs.add(new DataObj("INFLAMMATORY BOWEL DISEASE", "", false));
            dataObjs.add(new DataObj("IRRITABLE BOWEL SYNDROME", "", false));
            dataObjs.add(new DataObj("RIGHT BUNDLE BRANCH BLOCK", "", false));
            dataObjs.add(new DataObj("LEFT BUNDLE BRANCH BLOCK", "", false));
            dataObjs.add(new DataObj("MEASLES", "", false));
            dataObjs.add(new DataObj("CHICKEN POX", "", false));
            dataObjs.add(new DataObj("MUMPS", "", false));
            dataObjs.add(new DataObj("MITRAL VALVE PROLAPSE", "", false));
            dataObjs.add(new DataObj("OSTEOARTHRITIS", "", false));
            dataObjs.add(new DataObj("SMALL BOWEL OBSTRUCTION", "", false));
            dataObjs.add(new DataObj("CHRONIC OTITIS MEDIA", "", false));
            dataObjs.add(new DataObj("ACUTE OTITIS MEDIA", "", false));
            dataObjs.add(new DataObj("STOMATITIS", "", false));
            dataObjs.add(new DataObj("POLYCYSTIC KIDNEY DISEASE", "", false));
            dataObjs.add(new DataObj("PULMONARY HYPERTENSION", "", false));
            dataObjs.add(new DataObj("RHEUMATIC HEART DISEASE", "", false));
            dataObjs.add(new DataObj("ACUTE RESPIRATORY DISTRESS SYNDROME", "", false));
            dataObjs.add(new DataObj("SQUAMOUS CELL CARCINOMA", "", false));
            dataObjs.add(new DataObj("SEIZURE DISORDER", "", false));
            dataObjs.add(new DataObj("DIVERTICULITIS", "", false));
            dataObjs.add(new DataObj("TOXIC SHOCK SYNDROME", "", false));
            dataObjs.add(new DataObj("UNSTABLE ANGINA", "", false));
            dataObjs.add(new DataObj("ULCERATIVE COLLITIS", "", false));
            dataObjs.add(new DataObj("UPPER RESPIRATORY TRACT INFECTION", "", false));
            dataObjs.add(new DataObj("LOWER RESPIRATORY TRACT INFECTION", "", false));
            return dataObjs;
        }

        public static ArrayList<DataObj> getProvisionalDiagnosis() {
            return getDiagnosis();
        }

        public static ArrayList<DataObj> getSymptoms() {
            ArrayList<DataObj> dataObjs = new ArrayList<>();
            dataObjs.add(new DataObj("COLD", "", false));
            dataObjs.add(new DataObj("COUGH WITH EXPECTORATION", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN BREATHING (DYSPONEA)", "", false));
            dataObjs.add(new DataObj("CHEST PAIN", "", false));
            dataObjs.add(new DataObj("CHEST PAIN RADIATING TO SHOULDER JOINT", "", false));
            dataObjs.add(new DataObj("UNEASINESS", "", false));
            dataObjs.add(new DataObj("VOMITING", "", false));
            dataObjs.add(new DataObj("NAUSEA", "", false));
            dataObjs.add(new DataObj("LOOSE STOOL", "", false));
            dataObjs.add(new DataObj("PAIN IN ABDOMEN", "", false));
            dataObjs.add(new DataObj("BURNING MICTURATION", "", false));
            dataObjs.add(new DataObj("FREQUENCY OF MICTURATION INCREASED", "", false));
            dataObjs.add(new DataObj("HAEMAPTOSIS  (BLOOD IN SPUTUM)", "", false));
            dataObjs.add(new DataObj("HAEMATURIA (BLOOD IN URINE)", "", false));
            dataObjs.add(new DataObj("HAEMATEMESIS (BLOOD IN VOMIT)", "", false));
            dataObjs.add(new DataObj("SLURRED SPEECH", "", false));
            dataObjs.add(new DataObj("TINGLING SENSATION & WEAKNESS IN RIGHT UPPER & LOWER LIMB", "", false));
            dataObjs.add(new DataObj("TINGLING SENSATION & WEAKNESS IN LEFT UPPER & LOWER LIMB", "", false));
            dataObjs.add(new DataObj("DEVIATION OF ANGLE OF MOUTH", "", false));
            dataObjs.add(new DataObj("GENERALISED WEAKNESS", "", false));
            dataObjs.add(new DataObj("WEAKNESS IN ALL FOUR LIMB (QUADRIPLEGIA)", "", false));
            dataObjs.add(new DataObj("PAIN IN ABDOMEN LEFT LOIN TO GROIN REGION ", "", false));
            dataObjs.add(new DataObj("PAIN IN ABDOMEN RIGHT LOIN TO GROIN REGION ", "", false));
            dataObjs.add(new DataObj("REDNESS IN MOUTH", "", false));
            dataObjs.add(new DataObj("BLISTER IN MOUTH", "", false));
            dataObjs.add(new DataObj("PAIN AT ANAL REGION", "", false));
            dataObjs.add(new DataObj("BLEEDING FROM ANAL REGION WHILE PASSING MOTION", "", false));
            dataObjs.add(new DataObj("PAIN IN EPIGASTRIC REGION ", "", false));
            dataObjs.add(new DataObj("BURNING SENSATION IN EPIGASTRIC REGION ", "", false));
            dataObjs.add(new DataObj("SWELLING ALL OVER BODY (GENERALISED ANASARCA)", "", false));
            dataObjs.add(new DataObj("SWELLING OVER BOTH LOVER LIMB (PEDAL ODEMA)", "", false));
            dataObjs.add(new DataObj("FEVER WITH CHILLS ", "", false));
            dataObjs.add(new DataObj("ITCHING ALL OVER BODY ", "", false));
            dataObjs.add(new DataObj("REDNESS ALL OVER BODY", "", false));
            dataObjs.add(new DataObj("NOCTURIA (BED WETTING AT NIGHT)", "", false));
            dataObjs.add(new DataObj("HEADACHE", "", false));
            dataObjs.add(new DataObj("BODYACHE (MYALGIA)", "", false));
            dataObjs.add(new DataObj("DRY COUGH ", "", false));
            dataObjs.add(new DataObj("DROWSY", "", false));
            dataObjs.add(new DataObj("UNCONSCIOUS", "", false));
            dataObjs.add(new DataObj("ARROUSABLE", "", false));
            dataObjs.add(new DataObj("DISORIENTED", "", false));
            dataObjs.add(new DataObj("ALTERED SENSORIUM", "", false));
            dataObjs.add(new DataObj("CONVULSION", "", false));
            dataObjs.add(new DataObj("JOINT PAIN", "", false));
            dataObjs.add(new DataObj("SWELLING OF JOINTS", "", false));
            dataObjs.add(new DataObj("THROAT PAIN", "", false));
            dataObjs.add(new DataObj("DIFFICULTY IN SWALLOWING", "", false));
            dataObjs.add(new DataObj("PALPITATION", "", false));
            dataObjs.add(new DataObj("GIDDINESS", "", false));
            dataObjs.add(new DataObj("LOSS OF APPETITE", "", false));
            dataObjs.add(new DataObj("WEIGHT LOSS", "", false));
            dataObjs.add(new DataObj("WEIGHT GAIN", "", false));
            dataObjs.add(new DataObj("YELLOWISH DISCOLOURATION OF SKIN & SCLERA (HYPERBILIRUBINEMIA) ", "", false));
            dataObjs.add(new DataObj("PALLOR", "", false));
            dataObjs.add(new DataObj("SORE THROAT", "", false));
            return dataObjs;
        }
    }

    public static ArrayList<DataObj> getSymptomsOnCategoryType(String bizCategoryId) {
        ArrayList<DataObj> tempList = new ArrayList<>();
        PreferenceObjects preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), PreferenceObjects.class);
        if (null != preferenceObjects && preferenceObjects.getSymptomsList().size() > 0) {
            tempList.addAll(preferenceObjects.getSymptomsList());
        }
        switch (MedicalDepartmentEnum.valueOf(bizCategoryId)) {
            case OGY:
                tempList.addAll(MedicalDataStatic.Gynae.getSymptoms());
                break;
            case PAE:
                tempList.addAll(MedicalDataStatic.Pediatrician.getSymptoms());
                break;
            case ORT:
                tempList.addAll(MedicalDataStatic.Ortho.getSymptoms());
                break;
            case DNT:
                tempList.addAll(MedicalDataStatic.Dental.getSymptoms());
                break;
            case GSR:
                tempList.addAll(MedicalDataStatic.Surgeon.getSymptoms());
        }
        return tempList;
    }

    public static ArrayList<String> convertDataObjListAsStringList(ArrayList<DataObj> tempList) {
        ArrayList<String> dataObjs = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            dataObjs.add(tempList.get(i).getShortName());
        }
        return dataObjs;
    }

    public static ArrayList<DataObj> convertStringListAsDataObjList(ArrayList<String> tempList) {
        ArrayList<DataObj> dataObjs = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            dataObjs.add(new DataObj(tempList.get(i),false));
        }
        return dataObjs;
    }
}
