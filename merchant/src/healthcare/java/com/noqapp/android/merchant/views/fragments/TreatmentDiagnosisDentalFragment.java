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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.MedicationIntakeEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridMedicineAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class TreatmentDiagnosisDentalFragment extends BaseFragment implements StaggeredGridMedicineAdapter.StaggeredMedicineClick,
        AutoCompleteAdapterNew.SearchClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView recyclerView, rcv_medicine;
    private TextView tv_add_medicine, tv_close, tv_remove, tv_medicine_name;
    private StaggeredGridMedicineAdapter medicineAdapter, medicineSelectedAdapter;
    private ScrollView ll_medicine;
    private SegmentedControl sc_dental_option;
    private List<String> dental_option_data;
    private Button btn_done;
    private String dentalOption;
    private View view_med;
    private ArrayList<DataObj> selectedMedicineList = new ArrayList<>();
    private DataObj dataObj;
    private int selectionPos = -1;
    private AutoCompleteTextView actv_search_medicine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_diagnosis_dental, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        rcv_medicine = v.findViewById(R.id.rcv_medicine);
        view_med = v.findViewById(R.id.view_med);
        tv_add_medicine = v.findViewById(R.id.tv_add_medicine);
        tv_close = v.findViewById(R.id.tv_close);
        tv_remove = v.findViewById(R.id.tv_remove);
        tv_medicine_name = v.findViewById(R.id.tv_medicine_name);
        ll_medicine = v.findViewById(R.id.ll_medicine);
        sc_dental_option = v.findViewById(R.id.sc_dental_option);
        btn_done = v.findViewById(R.id.btn_done);
        tv_add_medicine.setOnClickListener(v1 -> AddMedicineDialog(getActivity(), "Add Medicine"));
        tv_close.setOnClickListener(v13 -> clearOptionSelection());
        dental_option_data = MedicalDataStatic.Dental.getSymptomsAsStringList();
        sc_dental_option.addSegments(dental_option_data);
        sc_dental_option.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    dentalOption = dental_option_data.get(segmentViewHolder.getAbsolutePosition());
                    if (null != dataObj)
                        dataObj.setMedicineTiming(dentalOption);
                }
            }
        });


        actv_search_medicine = v.findViewById(R.id.actv_search_medicine);
        actv_search_medicine.setThreshold(1);
        ImageView iv_clear_actv_medicine = v.findViewById(R.id.iv_clear_actv_medicine);
        iv_clear_actv_medicine.setOnClickListener(v14 -> {
            actv_search_medicine.setText("");
            new AppUtils().hideKeyBoard(getActivity());
        });
        return v;
    }

    private void clearOptionSelection() {
        ll_medicine.setVisibility(View.GONE);
        dentalOption = "";
        tv_medicine_name.setText("");
        sc_dental_option.clearSelection();
        dataObj = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalDataStatic.Dental.getDentalDiagnosisList(), this, false);
        recyclerView.setAdapter(medicineAdapter);

        rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);

//        selectedMedicineList = medicineSelectedAdapter.updateMedicineSelectList(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalMedicines(),
//                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList());
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);
        clearOptionSelection();
        view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);

        AutoCompleteAdapterNew adapter = new AutoCompleteAdapterNew(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicalDataStatic.Dental.getDentalDiagnosisList(), this, null);
        actv_search_medicine.setAdapter(adapter);
    }


    private void AddMedicineDialog(final Context mContext, String title) {
        selectionPos = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.add_item_with_category, null, false);
        final EditText edt_item = customDialogView.findViewById(R.id.edt_item);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        SegmentedControl sc_category = customDialogView.findViewById(R.id.sc_category);
        final List<String> category_data = new ArrayList<>();
        category_data.addAll(PharmacyCategoryEnum.asListOfDescription());
        sc_category.addSegments(category_data);
        sc_category.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    selectionPos = segmentViewHolder.getAbsolutePosition();
                }
            }
        });
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
            } else if (selectionPos == -1) {
                new CustomToast().showToast(getActivity(), "please select a category");
            } else {
                String category = category_data.get(selectionPos);
                String medicineName = category.substring(0, 3) + " " + edt_item.getText().toString();
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList();
                temp.add(new DataObj(medicineName, category, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMedicineList(temp);
                recyclerView.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                StaggeredGridMedicineAdapter customAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this, false);
                recyclerView.setAdapter(customAdapter);

                new CustomToast().showToast(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list");
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getMedicineList().add(new DataObj(medicineName, category, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
       // MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setJsonMedicineList(medicineSelectedAdapter.getSelectedDataListObject());
    }

    @Override
    public void staggeredMedicineClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if (!isEdit && isItemExist(temp.getShortName())) {
            ll_medicine.setVisibility(View.GONE);
            new CustomToast().showToast(getActivity(), "Medicine Already added in list");
        } else {
            ll_medicine.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
        tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        dataObj = temp;
        tv_medicine_name.setText(dataObj.getShortName());
        if (isEdit) {
            // Pre fill the data
            // sc_dental_option.setSelectedSegment(duration_data.indexOf(dataObj.getMedicineDuration()));
        } else {
            dentalOption = "";
            sc_dental_option.clearSelection();
        }
        btn_done.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dentalOption)) {
                new CustomToast().showToast(getActivity(), "All fields are mandatory");
            } else {
                // medicineAdapter.updateMedicine(medicine_name, medicineTiming, medicineDuration, medicineFrequency);
                if (isEdit) {
                    selectedMedicineList.set(pos, dataObj);
                } else {
                    selectedMedicineList.add(dataObj);
                }
                clearOptionSelection();
                view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);

                rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
                rcv_medicine.setAdapter(medicineSelectedAdapter);
            }
        });
        tv_remove.setOnClickListener(v -> {
            if (isEdit) {
                selectedMedicineList.remove(pos);
            }
            clearOptionSelection();
            view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);
            rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
            medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
            rcv_medicine.setAdapter(medicineSelectedAdapter);
        });
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < selectedMedicineList.size(); i++) {
            if (selectedMedicineList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void searchClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos) {
        new AppUtils().hideKeyBoard(getActivity());
        actv_search_medicine.setText("");
        staggeredMedicineClick(isOpen, isEdit, dataObj, pos);
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        new AppUtils().hideKeyBoard(getActivity());
    }
}
