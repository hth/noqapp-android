package com.noqapp.android.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridSymptomAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class SymptomsFragment extends BaseFragment implements StaggeredGridSymptomAdapter.StaggeredClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView rcv_gynac, rcv_obstretics, rcv_symptom_select;
    private TextView tv_add_new, tv_symptoms_name, tv_close, tv_remove, tv_output;
    private StaggeredGridSymptomAdapter symptomsAdapter, obstreticsAdapter, symptomSelectedAdapter;
    private EditText edt_past_history, edt_known_allergy, edt_family_history, edt_medicine_allergies, edt_output;
    private ScrollView ll_symptom_note;
    private DataObj dataObj;
    private SegmentedControl sc_duration;
    private List<String> duration_data;
    private String no_of_days;
    private Button btn_done;
    private View view_med;
    private ArrayList<DataObj> selectedSymptomsList = new ArrayList<>();
    private TextView tv_obes, tv_gyanc;
    private SwitchCompat sc_enable_history;
    private AutoCompleteTextView actv_search;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        rcv_gynac = v.findViewById(R.id.rcv_gynac);
        rcv_obstretics = v.findViewById(R.id.rcv_obstretics);
        rcv_symptom_select = v.findViewById(R.id.rcv_symptom_select);
        edt_past_history = v.findViewById(R.id.edt_past_history);
        edt_known_allergy = v.findViewById(R.id.edt_known_allergy);
        edt_family_history = v.findViewById(R.id.edt_family_history);
        edt_medicine_allergies = v.findViewById(R.id.edt_medicine_allergies);
        edt_output = v.findViewById(R.id.edt_output);
        sc_enable_history = v.findViewById(R.id.sc_enable_history);
        btn_done = v.findViewById(R.id.btn_done);
        view_med = v.findViewById(R.id.view_med);
        tv_obes = v.findViewById(R.id.tv_obes);
        tv_gyanc = v.findViewById(R.id.tv_gyanc);
        tv_symptoms_name = v.findViewById(R.id.tv_symptoms_name);
        tv_close = v.findViewById(R.id.tv_close);
        tv_remove = v.findViewById(R.id.tv_remove);
        tv_output = v.findViewById(R.id.tv_output);
        sc_duration = v.findViewById(R.id.sc_duration);
        tv_close.setOnClickListener(v1 -> clearOptionSelection());
        duration_data = DurationDaysEnum.asListOfDescription();
        sc_duration.addSegments(duration_data);

        sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    no_of_days = duration_data.get(segmentViewHolder.getAbsolutePosition());
                    tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
                    if (null != dataObj)
                        dataObj.setNoOfDays(no_of_days);
                }
            }
        });
        sc_enable_history.setChecked(false);
        sc_enable_history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    disableEditText(true, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
                } else {
                    disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
                }
            }
        });
        ll_symptom_note = v.findViewById(R.id.ll_symptom_note);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(v12 -> AddItemDialog(getActivity(), "Add Symptoms"));

        actv_search = v.findViewById(R.id.actv_search);
        actv_search.setThreshold(1);
        ImageView iv_clear_actv = v.findViewById(R.id.iv_clear_actv);
        iv_clear_actv.setOnClickListener(v13 -> {
            actv_search.setText("");
            new AppUtils().hideKeyBoard(getActivity());
        });
        return v;
    }

    private void disableEditText(boolean isChecked, EditText... editTexts) {
        for (EditText edt :
                editTexts) {
            edt.setEnabled(isChecked);
            edt.setFocusable(isChecked);
            edt.setFocusableInTouchMode(isChecked);
            edt.setBackground(isChecked ? ContextCompat.getDrawable(getActivity(), R.drawable.square_white_bg_drawable) : ContextCompat.getDrawable(getActivity(), R.drawable.edt_roun_rect));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rcv_gynac.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), this, false);
        rcv_gynac.setAdapter(symptomsAdapter);


        rcv_obstretics.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        obstreticsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList(), this, false);
        rcv_obstretics.setAdapter(obstreticsAdapter);

        rcv_symptom_select.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, this, true);
        rcv_symptom_select.setAdapter(symptomSelectedAdapter);
        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        if (null != jsonMedicalRecord && null != jsonMedicalRecord.getJsonUserMedicalProfile()) {
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies())
                edt_known_allergy.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory())
                edt_past_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory())
                edt_family_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies())
                edt_medicine_allergies.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies());

            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies()) {
                sc_enable_history.setChecked(true);
                disableEditText(true, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            } else {
                sc_enable_history.setChecked(false);
                disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            }
        } else {
            sc_enable_history.setChecked(false);
            disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
        }

        try {
            ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
            dataObjArrayList.addAll(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList());
            dataObjArrayList.addAll(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
            setupAutoComplete(dataObjArrayList);
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() && null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getChiefComplain()) {
                selectedSymptomsList = symptomSelectedAdapter.updateDataObj(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getChiefComplain(), dataObjArrayList);
                symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, this, true);
                rcv_symptom_select.setAdapter(symptomSelectedAdapter);
                view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MedicalCaseActivity.getMedicalCaseActivity().isGynae()) {
            tv_gyanc.setVisibility(View.VISIBLE);
            tv_obes.setVisibility(View.VISIBLE);
        } else {
            tv_gyanc.setVisibility(View.GONE);
            tv_obes.setVisibility(View.GONE);
        }
    }

    private void AddItemDialog(final Context mContext, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.add_item, null, false);
        final EditText edt_item = customDialogView.findViewById(R.id.edt_item);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        tvtitle.setText(title);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        btn_cancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_add.setOnClickListener(v -> {
            edt_item.setError(null);
            if (edt_item.getText().toString().equals("")) {
                edt_item.setError("Empty field not allowed");
            } else {

                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList();
                temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSymptomsList(temp);

                rcv_gynac.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), SymptomsFragment.this, false);
                rcv_gynac.setAdapter(symptomsAdapter);
                new CustomToast().showToast(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list");
                mAlertDialog.dismiss();
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getSymptomsList().add(new DataObj(edt_item.getText().toString(), false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        if (sc_enable_history.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setKnownAllergies(edt_known_allergy.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPastHistory(edt_past_history.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFamilyHistory(edt_family_history.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setMedicineAllergies(edt_medicine_allergies.getText().toString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setKnownAllergies(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPastHistory(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFamilyHistory(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setMedicineAllergies(null);
        }

        if (null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getKnownAllergies()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getPastHistory()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getFamilyHistory()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getMedicineAllergies()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHistoryFilled(true);
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHistoryFilled(false);
        }
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptoms(symptomSelectedAdapter.getSelectedData());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptomsObject(symptomSelectedAdapter.getSelectedSymptomData());

    }


    @Override
    public void staggeredClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        new AppUtils().hideKeyBoard(getActivity());
        if (!isEdit && isItemExist(temp.getShortName())) {
            ll_symptom_note.setVisibility(View.GONE);
            new CustomToast().showToast(getActivity(), "Symptom Already added in list");
        } else {
            ll_symptom_note.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
        tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        dataObj = temp;
        tv_symptoms_name.setText(dataObj.getShortName());
        if (isEdit) {
            // Pre fill the data
            sc_duration.setSelectedSegment(duration_data.indexOf(dataObj.getNoOfDays()));
            no_of_days = dataObj.getNoOfDays();
            tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
            edt_output.setText(TextUtils.isEmpty(dataObj.getAdditionalNotes()) ? "" : dataObj.getAdditionalNotes());
        } else {
            sc_duration.clearSelection();
            tv_output.setText("");
            edt_output.setText("");
            no_of_days = "";
        }
        btn_done.setOnClickListener(v -> {
            new AppUtils().hideKeyBoard(getActivity());
            dataObj.setNoOfDays(no_of_days);
            if (TextUtils.isEmpty(no_of_days)) {
                new CustomToast().showToast(getActivity(), "All fields are mandatory");
            } else {
                dataObj.setAdditionalNotes(edt_output.getText().toString());
                if (isEdit) {
                    selectedSymptomsList.set(pos, dataObj);
                } else {
                    selectedSymptomsList.add(dataObj);
                }

                view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);


                rcv_symptom_select.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, SymptomsFragment.this, true);
                rcv_symptom_select.setAdapter(symptomSelectedAdapter);
                clearOptionSelection();
            }
        });
        tv_remove.setOnClickListener(v -> {
            new AppUtils().hideKeyBoard(getActivity());
            if (isEdit) {
                selectedSymptomsList.remove(pos);
            }
            clearOptionSelection();
            view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);
            rcv_symptom_select.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
            symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, SymptomsFragment.this, true);
            rcv_symptom_select.setAdapter(symptomSelectedAdapter);
        });
    }

    private void clearOptionSelection() {
        ll_symptom_note.setVisibility(View.GONE);
        tv_symptoms_name.setText("");
        sc_duration.clearSelection();
        no_of_days = "";
        tv_output.setText("");
        edt_output.setText("");
        dataObj = null;
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < selectedSymptomsList.size(); i++) {
            if (selectedSymptomsList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }

    private void setupAutoComplete(final List<DataObj> objects) {
        AutoCompleteAdapterNew adapterSearch = new AutoCompleteAdapterNew(getActivity(), R.layout.layout_autocomplete, objects, null, this);
        actv_search.setAdapter(adapterSearch);
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        new AppUtils().hideKeyBoard(getActivity());
        actv_search.setText("");
        staggeredClick(true, false, dataObj, 0);
    }
}
