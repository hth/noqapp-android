package com.noqapp.android.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridSymptomAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.ShowAddDialog;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class SymptomsFragment extends BaseFragment implements
        StaggeredGridSymptomAdapter.StaggeredClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView rcv_gynac, rcv_obstretics, rcv_symptom_select;
    private TextView tv_add_new, tv_symptoms_name, tv_remove, tv_output;
    private StaggeredGridSymptomAdapter symptomsAdapter, obstreticsAdapter, symptomSelectedAdapter;
    private EditText edt_output;
    private ScrollView ll_symptom_note;
    private DataObj dataObj;
    private SegmentedControl sc_duration;
    private List<String> duration_data;
    private String no_of_days;
    private TextView btn_done;
    private ImageView tv_close;
    private View view_med;
    private ArrayList<DataObj> selectedSymptomsList = new ArrayList<>();
    private TextView tv_obes, tv_gyanc;
    private AutoCompleteTextView actv_search;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        rcv_gynac = v.findViewById(R.id.rcv_gynac);
        rcv_obstretics = v.findViewById(R.id.rcv_obstretics);
        rcv_symptom_select = v.findViewById(R.id.rcv_symptom_select);
        edt_output = v.findViewById(R.id.edt_output);
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
        ll_symptom_note = v.findViewById(R.id.ll_symptom_note);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(v12 -> AddItemDialog(getActivity()));

        actv_search = v.findViewById(R.id.actv_search);
        actv_search.setThreshold(1);
        ImageView iv_clear_actv = v.findViewById(R.id.iv_clear_actv);
        iv_clear_actv.setOnClickListener(v13 -> {
            actv_search.setText("");
            AppUtils.hideKeyBoard(getActivity());
        });
        return v;
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

    private void AddItemDialog(final Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList();
                temp.add(new DataObj(str, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSymptomsList(temp);

                rcv_gynac.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), SymptomsFragment.this, false);
                rcv_gynac.setAdapter(symptomsAdapter);
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getSymptomsList().add(new DataObj(str, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
            }
        });
        showDialog.displayDialog("Add Symptoms");
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptoms(symptomSelectedAdapter.getSelectedData());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptomsObject(symptomSelectedAdapter.getSelectedSymptomData());

    }


    @Override
    public void staggeredClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        AppUtils.hideKeyBoard(getActivity());
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
            AppUtils.hideKeyBoard(getActivity());
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
            AppUtils.hideKeyBoard(getActivity());
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
        AppUtils.hideKeyBoard(getActivity());
        actv_search.setText("");
        staggeredClick(true, false, dataObj, 0);
    }
}
