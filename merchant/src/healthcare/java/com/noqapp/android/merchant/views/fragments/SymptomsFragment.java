package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.StaggeredGridSymptomAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;


import java.util.ArrayList;
import java.util.List;

public class SymptomsFragment extends Fragment implements StaggeredGridSymptomAdapter.StaggeredClick {

    private RecyclerView rcv_gynac, rcv_obstretics, rcv_symptom_select;
    private TextView tv_add_new, tv_symptoms_name, tv_close, tv_remove, tv_output;
    private StaggeredGridSymptomAdapter symptomsAdapter, obstreticsAdapter, symptomSelectedAdapter;
    private AutoCompleteTextView actv_past_history, actv_known_allergy, actv_family_history;
    private LinearLayout ll_symptom_note;
    private DataObj dataObj;
    private SegmentedControl sc_duration;
    private List<String> duration_data;
    private String no_of_days;
    private Button btn_done;
    private View view_med;
    private ArrayList<DataObj> selectedMedicineList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        rcv_gynac = v.findViewById(R.id.rcv_gynac);
        rcv_obstretics = v.findViewById(R.id.rcv_obstretics);
        rcv_symptom_select = v.findViewById(R.id.rcv_symptom_select);
        actv_past_history = v.findViewById(R.id.actv_past_history);
        actv_family_history = v.findViewById(R.id.actv_family_history);
        actv_known_allergy = v.findViewById(R.id.actv_known_allergy);
        btn_done = v.findViewById(R.id.btn_done);
        view_med = v.findViewById(R.id.view_med);
        tv_symptoms_name = v.findViewById(R.id.tv_symptoms_name);
        tv_close = v.findViewById(R.id.tv_close);
        tv_remove = v.findViewById(R.id.tv_remove);
        tv_output = v.findViewById(R.id.tv_output);
        sc_duration = v.findViewById(R.id.sc_duration);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOptionSelection();
            }
        });
        duration_data =  DurationDaysEnum.asListOfDescription();
        sc_duration.addSegments(duration_data);

        sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    no_of_days = duration_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineDuration, Toast.LENGTH_LONG).show();
                    tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
                    if(null != dataObj)
                        dataObj.setAdditionalNotes(no_of_days);
                }
            }
        });
        ll_symptom_note = v.findViewById(R.id.ll_symptom_note);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Symptoms");
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_gynac.setLayoutManager(staggeredGridLayoutManager);
        symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), this, false);
        rcv_gynac.setAdapter(symptomsAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_obstretics.setLayoutManager(staggeredGridLayoutManager1);
        obstreticsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList(), this, false);
        rcv_obstretics.setAdapter(obstreticsAdapter);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
        rcv_symptom_select.setLayoutManager(sglm);
        symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedMedicineList, this,true);
        rcv_symptom_select.setAdapter(symptomSelectedAdapter);
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
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_item.setError(null);
                if (edt_item.getText().toString().equals("")) {
                    edt_item.setError("Empty field not allowed");
                } else {

                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSymptomsList(temp);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                    rcv_gynac.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
                    symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), SymptomsFragment.this, false);
                    rcv_gynac.setAdapter(symptomsAdapter);
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                    MedicalCaseActivity.getMedicalCaseActivity().getTestCaseObjects().getSymptomsList().add(new DataObj(edt_item.getText().toString(), false));
                    MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setKnownAllergies(actv_known_allergy.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPastHistory(actv_past_history.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFamilyHistory(actv_family_history.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptoms(symptomSelectedAdapter.getSelectedData());

    }


    @Override
    public void staggeredClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if (!isEdit && isItemExist(temp.getShortName())) {
            ll_symptom_note.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Medicine Already added in list", Toast.LENGTH_LONG).show();
        } else {
            ll_symptom_note.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
        tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        dataObj = temp;
        tv_symptoms_name.setText(dataObj.getShortName());
        if (isEdit) {
            // Pre fill the data
            sc_duration.setSelectedSegment(duration_data.indexOf(dataObj.getAdditionalNotes()));
            no_of_days = dataObj.getAdditionalNotes();
            tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
        }else{
            sc_duration.clearSelection();
            tv_output.setText("");
            no_of_days = "";
        }
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(getActivity());
                dataObj.setAdditionalNotes(no_of_days);
                if (TextUtils.isEmpty(no_of_days)) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                } else {
                    if (isEdit) {
                        selectedMedicineList.set(pos, dataObj);
                    } else {
                        selectedMedicineList.add(dataObj);
                    }

                    view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);

                    StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
                    rcv_symptom_select.setLayoutManager(sglm);
                    symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedMedicineList, SymptomsFragment.this, true);
                    rcv_symptom_select.setAdapter(symptomSelectedAdapter);
                    clearOptionSelection();
                }
            }
        });
        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(getActivity());
                if (isEdit) {
                    selectedMedicineList.remove(pos);
                }
                clearOptionSelection();
                view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);
                StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
                rcv_symptom_select.setLayoutManager(sglm);
                symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedMedicineList, SymptomsFragment.this, true);
                rcv_symptom_select.setAdapter(symptomSelectedAdapter);
            }
        });
    }

    private void clearOptionSelection() {
        ll_symptom_note.setVisibility(View.GONE);
        tv_symptoms_name.setText("");
        sc_duration.clearSelection();
        no_of_days = "";
        tv_output.setText("");
        dataObj = null;
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < selectedMedicineList.size(); i++) {
            if (selectedMedicineList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }

}
