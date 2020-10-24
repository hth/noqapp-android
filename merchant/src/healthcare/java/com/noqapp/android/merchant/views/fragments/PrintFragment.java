package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPathologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomSpinnerAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.adapters.WorkDoneAdapter;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordPresenter;
import com.noqapp.android.merchant.views.pojos.CaseHistory;
import com.noqapp.android.merchant.views.pojos.PreferredStoreInfo;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;
import com.noqapp.android.merchant.views.utils.PdfGenerator;
import com.noqapp.android.merchant.views.utils.PreferredStoreList;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class PrintFragment extends BaseFragment implements MedicalRecordPresenter {
    private TextView tv_patient_name, tv_address, tv_symptoms, tv_diagnosis, tv_instruction, tv_pathology, tv_clinical_findings, tv_examination, tv_provisional_diagnosis;
    private TextView tv_radio_xray, tv_radio_sono, tv_radio_scan, tv_radio_mri, tv_radio_special, tv_details, tv_followup;
    private TextView tv_weight, tv_height, tv_respiratory, tv_temperature, tv_bp, tv_pulse;
    private TextView tv_note_for_patient;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private SegmentedControl sc_follow_up;
    private Button btn_print_pdf;
    private ListView lv_medicine;
    private MedicalRecordAdapter adapter;
    private ArrayList<String> follow_up_data = new ArrayList<>();
    private String followup;
    private LinearLayout ll_sono, ll_scan, ll_mri, ll_xray, ll_spec, ll_path;
    private AppCompatSpinner acsp_mri, acsp_scan, acsp_sono, acsp_xray, acsp_special, acsp_pathology, acsp_pharmacy;
    private PreferredStoreList preferredStoreList;
    private ListView list_view,list_view_wd;
    private TextView tv_tr, tv_nfp;
    private LinearLayout ll_wd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_print, container, false);
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        tv_tr = v.findViewById(R.id.tv_tr);
        tv_nfp = v.findViewById(R.id.tv_nfp);
        tv_patient_name = v.findViewById(R.id.tv_patient_name);
        tv_address = v.findViewById(R.id.tv_address);
        tv_symptoms = v.findViewById(R.id.tv_symptoms);
        tv_details = v.findViewById(R.id.tv_details);
        tv_diagnosis = v.findViewById(R.id.tv_diagnosis);
        tv_instruction = v.findViewById(R.id.tv_instruction);
        tv_radio_mri = v.findViewById(R.id.tv_radio_mri);
        tv_radio_special = v.findViewById(R.id.tv_radio_special);
        tv_radio_scan = v.findViewById(R.id.tv_radio_scan);
        tv_radio_sono = v.findViewById(R.id.tv_radio_sono);
        tv_radio_xray = v.findViewById(R.id.tv_radio_xray);
        tv_pathology = v.findViewById(R.id.tv_pathology);
        tv_clinical_findings = v.findViewById(R.id.tv_clinical_findings);
        tv_examination = v.findViewById(R.id.tv_examination);
        list_view = v.findViewById(R.id.list_view);
        list_view_wd = v.findViewById(R.id.list_view_wd);

        tv_weight = v.findViewById(R.id.tv_weight);
        tv_height = v.findViewById(R.id.tv_height);
        tv_respiratory = v.findViewById(R.id.tv_respiratory);
        tv_temperature = v.findViewById(R.id.tv_temperature);
        tv_bp = v.findViewById(R.id.tv_bp);
        tv_pulse = v.findViewById(R.id.tv_pulse);
        tv_followup = v.findViewById(R.id.tv_followup);
        tv_note_for_patient = v.findViewById(R.id.tv_note_for_patient);
        tv_provisional_diagnosis = v.findViewById(R.id.tv_provisional_diagnosis);
        lv_medicine = v.findViewById(R.id.lv_medicine);
        acsp_mri = v.findViewById(R.id.acsp_mri);
        acsp_scan = v.findViewById(R.id.acsp_scan);
        acsp_sono = v.findViewById(R.id.acsp_sono);
        acsp_xray = v.findViewById(R.id.acsp_xray);
        acsp_special = v.findViewById(R.id.acsp_special);
        acsp_pathology = v.findViewById(R.id.acsp_pathology);
        acsp_pharmacy = v.findViewById(R.id.acsp_pharmacy);

        ll_sono = v.findViewById(R.id.ll_sono);
        ll_scan = v.findViewById(R.id.ll_scan);
        ll_mri = v.findViewById(R.id.ll_mri);
        ll_xray = v.findViewById(R.id.ll_xray);
        ll_spec = v.findViewById(R.id.ll_spec);
        ll_path = v.findViewById(R.id.ll_path);

        ll_wd = v.findViewById(R.id.ll_wd);

        sc_follow_up = v.findViewById(R.id.sc_follow_up);
        follow_up_data.clear();
        follow_up_data.add(String.valueOf(DurationDaysEnum.D1D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D2D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D3D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D4D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D5D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D6D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D7D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D10D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D15D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D1M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D45D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D2M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D3M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D6M.getValue()));
        sc_follow_up.addSegments(follow_up_data);

        setProgressMessage("Uploading data...");

        sc_follow_up.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    followup = follow_up_data.get(segmentViewHolder.getAbsolutePosition());
                    tv_followup.setText("in " + followup + " days");
                    MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFollowup(tv_followup.getText().toString());
                }
                if (isReselected) {
                    followup = "";
                    tv_followup.setText("");
                    MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFollowup("");
                    sc_follow_up.clearSelection();
                }
            }
        });

        if (MedicalCaseActivity.getMedicalCaseActivity().isDental()) {
            CardView cv_symptoms = v.findViewById(R.id.cv_symptoms);
            CardView cv_examination = v.findViewById(R.id.cv_examination);
            CardView cv_pro_diagnosis = v.findViewById(R.id.cv_pro_diagnosis);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, 0, 0.0f);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
            cv_examination.setLayoutParams(lp0);
            cv_symptoms.setLayoutParams(lp1);
            cv_pro_diagnosis.setLayoutParams(lp1);
        }

        Button btn_submit = v.findViewById(R.id.btn_submit);
        btn_print_pdf = v.findViewById(R.id.btn_print_pdf);
        btn_submit.setOnClickListener(v1 -> {
            showProgress();
            CaseHistory caseHistory = MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory();
            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
            jsonMedicalRecord.setCodeQR(MedicalCaseActivity.getMedicalCaseActivity().codeQR);
            jsonMedicalRecord.setQueueUserId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getQueueUserId());
            jsonMedicalRecord.setChiefComplain(caseHistory.getSymptomsObject());
            jsonMedicalRecord.getJsonUserMedicalProfile().setPastHistory(caseHistory.getPastHistory());
            jsonMedicalRecord.getJsonUserMedicalProfile().setFamilyHistory(caseHistory.getFamilyHistory());
            jsonMedicalRecord.getJsonUserMedicalProfile().setKnownAllergies(caseHistory.getKnownAllergies());
            jsonMedicalRecord.getJsonUserMedicalProfile().setMedicineAllergies(caseHistory.getMedicineAllergies());
            jsonMedicalRecord.getJsonUserMedicalProfile().setHistoryDirty(caseHistory.isHistoryFilled());
            jsonMedicalRecord.getJsonUserMedicalProfile().setDentalAnatomy(caseHistory.getDentalAnatomy());
            jsonMedicalRecord.getJsonUserMedicalProfile().setDentalAnatomyDirty(caseHistory.isDentalAnatomyFilled());
            jsonMedicalRecord.setClinicalFinding(caseHistory.getClinicalFindings());
            jsonMedicalRecord.setProvisionalDifferentialDiagnosis(caseHistory.getProvisionalDiagnosis());
            jsonMedicalRecord.setExamination(caseHistory.getExaminationResults());
            jsonMedicalRecord.setDiagnosis(caseHistory.getDiagnosis());
            jsonMedicalRecord.setNoteForPatient(caseHistory.getNoteForPatient());
            jsonMedicalRecord.setNoteToDiagnoser(caseHistory.getNoteToDiagnoser());

            JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                    .setBloodPressure(caseHistory.getBloodPressure())
                    .setPulse(caseHistory.getPulse())
                    .setWeight(caseHistory.getWeight())
                    .setOxygen(caseHistory.getOxygenLevel())
                    .setTemperature(caseHistory.getTemperature())
                    .setHeight(caseHistory.getHeight())
                    .setRespiratory(caseHistory.getRespiratory())
                    .setPhysicalFilled(caseHistory.isPhysicalFilled());

            if (caseHistory.getPathologyList().size() > 0) {
                ArrayList<JsonMedicalPathology> pathologies = new ArrayList<>();
                for (int i = 0; i < caseHistory.getPathologyList().size(); i++) {
                    pathologies.add(new JsonMedicalPathology().setName(caseHistory.getPathologyList().get(i)));
                }
                JsonMedicalPathologyList jsonMedicalPathologyList = new JsonMedicalPathologyList();
                jsonMedicalPathologyList.setJsonMedicalPathologies(pathologies);
                ArrayList<JsonMedicalPathologyList> listData = new ArrayList<>();
                listData.add(jsonMedicalPathologyList);
                jsonMedicalRecord.setMedicalPathologiesLists(listData);
            }

            ArrayList<JsonMedicalRadiology> mriList = new ArrayList<>();
            if (caseHistory.getMriList().size() > 0) {
                for (int i = 0; i < caseHistory.getMriList().size(); i++) {
                    mriList.add(new JsonMedicalRadiology().setName(caseHistory.getMriList().get(i)));
                }
            }
            ArrayList<JsonMedicalRadiology> sonoList = new ArrayList<>();
            if (caseHistory.getSonoList().size() > 0) {
                for (int i = 0; i < caseHistory.getSonoList().size(); i++) {
                    sonoList.add(new JsonMedicalRadiology().setName(caseHistory.getSonoList().get(i)));
                }
            }
            ArrayList<JsonMedicalRadiology> scanList = new ArrayList<>();
            if (caseHistory.getScanList().size() > 0) {
                for (int i = 0; i < caseHistory.getScanList().size(); i++) {
                    scanList.add(new JsonMedicalRadiology().setName(caseHistory.getScanList().get(i)));
                }
            }
            ArrayList<JsonMedicalRadiology> xrayList = new ArrayList<>();
            if (caseHistory.getXrayList().size() > 0) {
                for (int i = 0; i < caseHistory.getXrayList().size(); i++) {
                    xrayList.add(new JsonMedicalRadiology().setName(caseHistory.getXrayList().get(i)));
                }
            }

            ArrayList<JsonMedicalRadiology> specList = new ArrayList<>();
            if (caseHistory.getSpecList().size() > 0) {
                for (int i = 0; i < caseHistory.getSpecList().size(); i++) {
                    specList.add(new JsonMedicalRadiology().setName(caseHistory.getSpecList().get(i)));
                }
            }


            List<JsonMedicalRadiologyList> medicalRadiologyLists = new ArrayList<>();
            if (mriList.size() > 0) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                if (null != preferredStoreList && null != preferredStoreList.getListMri() && preferredStoreList.getListMri().size() > 0) {
                    if (acsp_mri.getSelectedItemPosition() != 0) {
                        jsonMedicalRadiologyList.setBizStoreId(((JsonPreferredBusiness) acsp_mri.getSelectedItem()).getBizStoreId());
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }

                } else {
                    jsonMedicalRadiologyList.setBizStoreId("");
                }
                jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.MRI);
                jsonMedicalRadiologyList.setJsonMedicalRadiologies(mriList);
                medicalRadiologyLists.add(jsonMedicalRadiologyList);
            }

            if (sonoList.size() > 0) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                if (null != preferredStoreList && null != preferredStoreList.getListSono() && preferredStoreList.getListSono().size() > 0) {
                    if (acsp_sono.getSelectedItemPosition() != 0) {
                        jsonMedicalRadiologyList.setBizStoreId(((JsonPreferredBusiness) acsp_sono.getSelectedItem()).getBizStoreId());
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                } else {
                    jsonMedicalRadiologyList.setBizStoreId("");
                }
                jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SONO);
                jsonMedicalRadiologyList.setJsonMedicalRadiologies(sonoList);
                medicalRadiologyLists.add(jsonMedicalRadiologyList);
            }

            if (scanList.size() > 0) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                if (null != preferredStoreList && null != preferredStoreList.getListScan() && preferredStoreList.getListScan().size() > 0) {
                    if (acsp_scan.getSelectedItemPosition() != 0) {
                        jsonMedicalRadiologyList.setBizStoreId(((JsonPreferredBusiness) acsp_scan.getSelectedItem()).getBizStoreId());
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                } else {
                    jsonMedicalRadiologyList.setBizStoreId("");
                }
                jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SCAN);
                jsonMedicalRadiologyList.setJsonMedicalRadiologies(scanList);
                medicalRadiologyLists.add(jsonMedicalRadiologyList);
            }

            if (xrayList.size() > 0) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                if (null != preferredStoreList && null != preferredStoreList.getListXray() && preferredStoreList.getListXray().size() > 0) {
                    if (acsp_xray.getSelectedItemPosition() != 0) {
                        jsonMedicalRadiologyList.setBizStoreId(((JsonPreferredBusiness) acsp_xray.getSelectedItem()).getBizStoreId());
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                } else {
                    jsonMedicalRadiologyList.setBizStoreId("");
                }
                jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.XRAY);
                jsonMedicalRadiologyList.setJsonMedicalRadiologies(xrayList);
                medicalRadiologyLists.add(jsonMedicalRadiologyList);
            }

            if (specList.size() > 0) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                if (null != preferredStoreList && null != preferredStoreList.getListSpec() && preferredStoreList.getListSpec().size() > 0) {
                    if (acsp_special.getSelectedItemPosition() != 0) {
                        jsonMedicalRadiologyList.setBizStoreId(((JsonPreferredBusiness) acsp_special.getSelectedItem()).getBizStoreId());
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                } else {
                    jsonMedicalRadiologyList.setBizStoreId("");
                }
                jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SPEC);
                jsonMedicalRadiologyList.setJsonMedicalRadiologies(specList);
                medicalRadiologyLists.add(jsonMedicalRadiologyList);
            }
            jsonMedicalRecord.setMedicalRadiologyLists(medicalRadiologyLists);

            if (null != preferredStoreList && null != preferredStoreList.getListMedicine() && preferredStoreList.getListMedicine().size() > 0) {
                if (acsp_pharmacy.getSelectedItemPosition() != 0) {
                    jsonMedicalRecord.setStoreIdPharmacy(((JsonPreferredBusiness) acsp_pharmacy.getSelectedItem()).getBizStoreId());
                } else {
                    jsonMedicalRecord.setStoreIdPharmacy("");
                }
            } else {
                jsonMedicalRecord.setStoreIdPharmacy("");
            }

            if (null != preferredStoreList && null != preferredStoreList.getListPath() && preferredStoreList.getListPath().size() > 0) {
                if (acsp_pathology.getSelectedItemPosition() != 0) {
                    jsonMedicalRecord.setStoreIdPathology(((JsonPreferredBusiness) acsp_pathology.getSelectedItem()).getBizStoreId());
                } else {
                    jsonMedicalRecord.setStoreIdPathology("");
                }
            } else {
                jsonMedicalRecord.setStoreIdPathology("");
            }

            jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
            jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
            jsonMedicalRecord.setPlanToPatient(caseHistory.getInstructions());
            if (!TextUtils.isEmpty(followup)) {
                jsonMedicalRecord.setFollowUpInDays(followup);
            } else {
                jsonMedicalRecord.setFollowUpInDays(null);
            }
            medicalHistoryApiCalls.update(
                    AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(),
                    jsonMedicalRecord);
        });
        return v;
    }

    public void updateUI() {
        final CaseHistory caseHistory = MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory();
        caseHistory.setFollowup(tv_followup.getText().toString());
        String notAvailable = "N/A";
        tv_patient_name.setText(caseHistory.getName());
        tv_address.setText(caseHistory.getAddress());
        tv_details.setText(caseHistory.getGender() + ", " + caseHistory.getAge());
        tv_symptoms.setText(caseHistory.getSymptoms());
        tv_diagnosis.setText(caseHistory.getDiagnosis());
        tv_instruction.setText(caseHistory.getInstructions());
        tv_provisional_diagnosis.setText(caseHistory.getProvisionalDiagnosis());
        tv_clinical_findings.setText(Html.fromHtml("<b>" + "Clinical Findings: " + "</b> " + caseHistory.getClinicalFindings()));
        tv_examination.setText(Html.fromHtml("<b>" + "Result: " + "</b> " + caseHistory.getExaminationResults()));
        tv_radio_mri.setText(covertStringList2String(caseHistory.getMriList()));
        tv_radio_scan.setText(covertStringList2String(caseHistory.getScanList()));
        tv_radio_sono.setText(covertStringList2String(caseHistory.getSonoList()));
        tv_radio_xray.setText(covertStringList2String(caseHistory.getXrayList()));
        tv_radio_special.setText(covertStringList2String(caseHistory.getSpecList()));
        tv_pathology.setText(covertStringList2String(caseHistory.getPathologyList()));
        if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT) {
            list_view.setVisibility(View.VISIBLE);
            tv_tr.setVisibility(View.VISIBLE);
            parseDentalDiagnosis(caseHistory.getNoteForPatient());
            parseDentalWDDiagnosis(caseHistory.getNoteToDiagnoser());
            tv_note_for_patient.setVisibility(View.GONE);
            tv_nfp.setVisibility(View.GONE);
            ll_wd.setVisibility(View.VISIBLE);
        } else {
            tv_note_for_patient.setText(caseHistory.getNoteForPatient());
            tv_note_for_patient.setVisibility(View.VISIBLE);
            tv_nfp.setVisibility(View.VISIBLE);
            list_view.setVisibility(View.GONE);
            tv_tr.setVisibility(View.GONE);
            ll_wd.setVisibility(View.GONE);
        }


        hideInvestigationViews(caseHistory);
        if (null != caseHistory.getRespiratory()) {
            tv_respiratory.setText("Respiration Rate: " + caseHistory.getRespiratory());
        } else {
            tv_respiratory.setText("Respiration Rate: " + notAvailable);
        }
        if (null != caseHistory.getHeight()) {
            tv_height.setText("Height: " + caseHistory.getHeight());
        } else {
            tv_height.setText("Height: " + notAvailable);
        }
        if (null != caseHistory.getPulse()) {
            tv_pulse.setText("Pulse: " + caseHistory.getPulse());
        } else {
            tv_pulse.setText("Pulse: " + notAvailable);
        }
        if (null != caseHistory.getBloodPressure() && caseHistory.getBloodPressure().length == 2) {
            tv_bp.setText("Blood Pressure: " + caseHistory.getBloodPressure()[0] + "/" + caseHistory.getBloodPressure()[1]);
        } else {
            tv_bp.setText("Blood Pressure: " + notAvailable);
        }
        if (null != caseHistory.getWeight()) {
            tv_weight.setText("Weight: " + caseHistory.getWeight());
        } else {
            tv_weight.setText("Weight: " + notAvailable);
        }
        if (null != caseHistory.getTemperature()) {
            tv_temperature.setText("Temp: " + caseHistory.getTemperature());
        } else {
            tv_temperature.setText("Temp: " + notAvailable);
        }
        adapter = new MedicalRecordAdapter(getActivity(), caseHistory.getJsonMedicineList());
        lv_medicine.setAdapter(adapter);
        btn_print_pdf.setVisibility(View.VISIBLE);
        PermissionHelper permissionHelper = new PermissionHelper(getActivity());
        btn_print_pdf.setOnClickListener(v -> {
            if (permissionHelper.isStoragePermissionAllowed()) {
                PdfGenerator pdfGenerator = new PdfGenerator(getActivity());
                pdfGenerator.createPdf(caseHistory, TextUtils.isEmpty(followup) ? 0 : Integer.parseInt(followup),
                        MedicalCaseActivity.getMedicalCaseActivity().isDental());
            } else {
                permissionHelper.requestStoragePermission();
            }
        });

        if (!TextUtils.isEmpty(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getFollowUpInDays())) {
            int index = follow_up_data.indexOf(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getFollowUpInDays());
            if (-1 != index)
                sc_follow_up.setSelectedSegment(index);
        }

        List<JsonPreferredBusiness> jsonPreferredBusinessList = MedicalCaseActivity.getMedicalCaseActivity().jsonPreferredBusiness;
        if (null != jsonPreferredBusinessList && jsonPreferredBusinessList.size() > 0) {
            preferredStoreList = new PreferredStoreList(jsonPreferredBusinessList);
            acsp_mri.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListMri()));
            acsp_scan.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListScan()));
            acsp_sono.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListSono()));
            acsp_xray.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListXray()));
            acsp_special.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListSpec()));
            acsp_pathology.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListPath()));
            acsp_pharmacy.setAdapter(new CustomSpinnerAdapter(getActivity(), preferredStoreList.getListMedicine()));

            if (!TextUtils.isEmpty(MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getStoreIdPathology())) {
                acsp_pathology.setSelection(getSelectionPos(preferredStoreList.getListPath(), HealthCareServiceEnum.PATH, MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getStoreIdPathology()));
            } else {
                acsp_pathology.setSelection(getSelectionPos(preferredStoreList.getListPath(), HealthCareServiceEnum.PATH));
            }
            if (!TextUtils.isEmpty(MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getStoreIdPharmacy())) {
                acsp_pharmacy.setSelection(getSelectionPos(preferredStoreList.getListMedicine(), null, MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getStoreIdPharmacy()));
            } else {
                acsp_pharmacy.setSelection(getSelectionPos(preferredStoreList.getListMedicine(), null));
            }
            acsp_mri.setSelection(getSelectionPos(preferredStoreList.getListMri(), HealthCareServiceEnum.MRI));
            acsp_scan.setSelection(getSelectionPos(preferredStoreList.getListScan(), HealthCareServiceEnum.SCAN));
            acsp_sono.setSelection(getSelectionPos(preferredStoreList.getListSono(), HealthCareServiceEnum.SONO));
            acsp_xray.setSelection(getSelectionPos(preferredStoreList.getListXray(), HealthCareServiceEnum.XRAY));
            acsp_special.setSelection(getSelectionPos(preferredStoreList.getListSpec(), HealthCareServiceEnum.SPEC));
            JsonMedicalRecord jmr = MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord;
            for (int i = 0; i < jmr.getMedicalRadiologyLists().size(); i++) {
                LabCategoryEnum lce = jmr.getMedicalRadiologyLists().get(i).getLabCategory();

                switch (lce) {
                    case MRI:
                        acsp_mri.setSelection(getSelectionPos(preferredStoreList.getListMri(), HealthCareServiceEnum.MRI, jmr.getMedicalRadiologyLists().get(i).getBizStoreId()));
                        break;
                    case SCAN:
                        acsp_scan.setSelection(getSelectionPos(preferredStoreList.getListScan(), HealthCareServiceEnum.SCAN, jmr.getMedicalRadiologyLists().get(i).getBizStoreId()));
                        break;
                    case SONO:
                        acsp_sono.setSelection(getSelectionPos(preferredStoreList.getListSono(), HealthCareServiceEnum.SONO, jmr.getMedicalRadiologyLists().get(i).getBizStoreId()));
                        break;
                    case XRAY:
                        acsp_xray.setSelection(getSelectionPos(preferredStoreList.getListXray(), HealthCareServiceEnum.XRAY, jmr.getMedicalRadiologyLists().get(i).getBizStoreId()));
                        break;
                    case PATH: // Do nothing
                        break;
                    case SPEC:
                        acsp_special.setSelection(getSelectionPos(preferredStoreList.getListSpec(), HealthCareServiceEnum.SPEC, jmr.getMedicalRadiologyLists().get(i).getBizStoreId()));
                        break;
                }
            }
        }
    }

    private int getSelectionPos(List<JsonPreferredBusiness> temp, HealthCareServiceEnum hcse, String storeId) {
        if (null == storeId) {
            return 0;
        } else {
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getBizStoreId().equals(storeId)) {
                    return i;
                }
            }
            return 0;
        }
    }


    private int getSelectionPos(List<JsonPreferredBusiness> temp, HealthCareServiceEnum hcse) {
        PreferredStoreInfo preferredStoreInfo = MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getPreferredStoreInfoHashMap().
                get(MedicalCaseActivity.getMedicalCaseActivity().codeQR);
        if (null == preferredStoreInfo) {
            return 0;
        }
        if (null == hcse) {
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPharmacy())) {
                    return i;
                }
            }
            return 0;
        }
        switch (hcse) {
            case SONO: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdSono())) {
                        return i;
                    }
                }
                return 0;
            }
            case SCAN: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdScan())) {
                        return i;
                    }
                }
                return 0;
            }
            case MRI: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdMri())) {
                        return i;
                    }
                }
                return 0;
            }
            case PATH: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPath())) {
                        return i;
                    }
                }
                return 0;
            }
            case XRAY: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdXray())) {
                        return i;
                    }
                }
                return 0;
            }
            case SPEC: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdSpecial())) {
                        return i;
                    }
                }
                return 0;
            }
            case PHYS: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPhysio())) {
                        return i;
                    }
                }
                return 0;
            }
            default: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPharmacy())) {
                        return i;
                    }
                }
                return 0;
            }

        }
    }

    private void hideInvestigationViews(CaseHistory caseHistory) {
        ll_mri.setVisibility(caseHistory.getMriList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_scan.setVisibility(caseHistory.getScanList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_sono.setVisibility(caseHistory.getSonoList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_xray.setVisibility(caseHistory.getXrayList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_spec.setVisibility(caseHistory.getSpecList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_path.setVisibility(caseHistory.getPathologyList().size() == 0 ? View.GONE : View.VISIBLE);
    }


    private String covertStringList2String(List<String> data) {
        String temp = "";
        for (String a : data) {
            temp += "\u2022" + " " + a + "\n";
        }
        return temp;
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            LaunchActivity.getLaunchActivity().uploadMedicalFiles();
            new CustomToast().showToast(getActivity(), "Medical History updated successfully");
            getActivity().finish();
        } else {
            new CustomToast().showToast(getActivity(), "Failed to update");
        }
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        new CustomToast().showToast(getActivity(), "Failed to update");
    }


    private void parseDentalDiagnosis(String str) {
        try {
            ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();
            String[] temp = str.split("\\|",-1);
            if (temp.length > 0) {
                for (String act : temp) {
                    if (act.contains(":")) {
                        String[] strArray = act.split(":");
                        String str1 = strArray[0].trim();
                        String str2 = strArray[1];
                        toothWorkDoneList.add(new ToothWorkDone(str1, str2, ""));
                    }
                }
            }
            WorkDoneAdapter workDoneAdapter = new WorkDoneAdapter(getActivity(), toothWorkDoneList,true);
            list_view.setAdapter(workDoneAdapter);
            workDoneAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDentalWDDiagnosis(String str) {
        try {
            list_view_wd.setAdapter(new WorkDoneAdapter(getActivity(), new ArrayList<>()));
            ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();
            String[] temp = str.split("\\|", -1);
            if (temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains(":")) {
                        String[] strArray = act.split(":", -1);
                        String toothNum = strArray[0].trim();
                        String procedure = strArray[1];
                        String summary = strArray.length >= 3 ? strArray[2] : "";
                        if (strArray.length > 3) {
                            String status = strArray[3].trim();
                            String unit = strArray[4];
                            String period = strArray[5];
                            toothWorkDoneList.add(new ToothWorkDone(toothNum, procedure, summary, status, unit, period));
                        } else {
                            toothWorkDoneList.add(new ToothWorkDone(toothNum, procedure, summary));
                        }
                    }
                }
            }
            list_view_wd.setAdapter(new WorkDoneAdapter(getActivity(), toothWorkDoneList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
