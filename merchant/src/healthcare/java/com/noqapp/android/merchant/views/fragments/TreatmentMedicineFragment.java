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
import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.common.model.types.medical.MedicationIntakeEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.adapters.StaggeredGridMedicineAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class TreatmentMedicineFragment extends BaseFragment implements StaggeredGridMedicineAdapter.StaggeredMedicineClick,
        AutoCompleteAdapterNew.SearchClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView recyclerView, rcv_medicine;
    private StaggeredGridMedicineAdapter medicineAdapter, medicineSelectedAdapter;
    private List<String> duration_data, timing_data, frequency_data;
    private String medicineTiming, medicineDuration, medicineFrequency;
    private View view_med;
    private ArrayList<DataObj> selectedMedicineList = new ArrayList<>();
    private DataObj dataObj;
    private int selectionPos = -1;
    private AutoCompleteTextView actv_search_medicine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_medicine, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        rcv_medicine = v.findViewById(R.id.rcv_medicine);
        view_med = v.findViewById(R.id.view_med);
        TextView tv_add_medicine = v.findViewById(R.id.tv_add_medicine);
        tv_add_medicine.setOnClickListener(v1 -> AddMedicineDialog(getActivity(), "Add Medicine"));
        duration_data = DurationDaysEnum.asListOfDescription();
        timing_data = MedicationIntakeEnum.asListOfDescription();
        frequency_data = DailyFrequencyEnum.asListOfDescription();
        actv_search_medicine = v.findViewById(R.id.actv_search_medicine);
        actv_search_medicine.setThreshold(1);
        ImageView iv_clear_actv_medicine = v.findViewById(R.id.iv_clear_actv_medicine);
        iv_clear_actv_medicine.setOnClickListener(v14 -> {
            actv_search_medicine.setText("");
            AppUtils.hideKeyBoard(getActivity());
        });
        return v;
    }

    private void clearOptionSelection() {
        medicineTiming = "";
        medicineDuration = "";
        medicineFrequency = "";
        dataObj = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this, false);
        recyclerView.setAdapter(medicineAdapter);
        
        rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);

        selectedMedicineList = medicineSelectedAdapter.updateMedicineSelectList(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalMedicines(),
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList());
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);
        clearOptionSelection();
        view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);

        AutoCompleteAdapterNew adapter = new AutoCompleteAdapterNew(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this, null);
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
        ImageView iv_close = customDialogView.findViewById(R.id.iv_close);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        iv_close.setOnClickListener(v -> mAlertDialog.dismiss());
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
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setJsonMedicineList(medicineSelectedAdapter.getSelectedDataListObject());
    }

    @Override
    public void staggeredMedicineClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if (!isEdit && isItemExist(temp.getShortName())) {
            new CustomToast().showToast(getActivity(), "Medicine Already added in list");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            builder.setTitle(null);
            View dialogView = inflater.inflate(R.layout.dialog_add_medicine, null, false);
            TextView tv_remove = dialogView.findViewById(R.id.tv_remove);
            TextView tv_done = dialogView.findViewById(R.id.tv_done);
            TextView tv_medicine_name = dialogView.findViewById(R.id.tv_medicine_name);
            ImageView iv_close = dialogView.findViewById(R.id.iv_close);

            SegmentedControl sc_duration = dialogView.findViewById(R.id.sc_duration);
            SegmentedControl sc_medicine_timing = dialogView.findViewById(R.id.sc_medicine_timing);
            SegmentedControl sc_frequency = dialogView.findViewById(R.id.sc_frequency);
            sc_duration.addSegments(duration_data);
            sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
                @Override
                public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                    if (isSelected) {
                        medicineDuration = duration_data.get(segmentViewHolder.getAbsolutePosition());
                        if (null != dataObj)
                            dataObj.setMedicineDuration(medicineDuration);
                    }
                }
            });
            sc_medicine_timing.addSegments(timing_data);
            sc_medicine_timing.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
                @Override
                public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                    if (isSelected) {
                        medicineTiming = timing_data.get(segmentViewHolder.getAbsolutePosition());
                        if (null != dataObj)
                            dataObj.setMedicineTiming(medicineTiming);
                    }
                }
            });
            sc_frequency.addSegments(frequency_data);
            sc_frequency.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
                @Override
                public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                    if (isSelected) {
                        medicineFrequency = frequency_data.get(segmentViewHolder.getAbsolutePosition());
                        if (null != dataObj)
                            dataObj.setMedicineFrequency(medicineFrequency);
                    }
                }
            });
            builder.setView(dialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mAlertDialog.show();
            iv_close.setOnClickListener(v1 -> {
                clearOptionSelection();
                mAlertDialog.dismiss();
            });

            tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
            dataObj = temp;
            tv_medicine_name.setText(dataObj.getShortName());
            if (isEdit) {
                // Pre fill the data
                sc_duration.setSelectedSegment(duration_data.indexOf(dataObj.getMedicineDuration()));
                sc_medicine_timing.setSelectedSegment(timing_data.indexOf(dataObj.getMedicineTiming()));
                sc_frequency.setSelectedSegment(frequency_data.indexOf(dataObj.getMedicineFrequency()));
            } else {
                medicineTiming = "";
                medicineDuration = "";
                medicineFrequency = "";
                sc_medicine_timing.clearSelection();
                sc_duration.clearSelection();
                sc_frequency.clearSelection();
            }
            tv_done.setOnClickListener(v -> {
                if (TextUtils.isEmpty(medicineDuration) || TextUtils.isEmpty(medicineTiming) || TextUtils.isEmpty(medicineFrequency)) {
                    new CustomToast().showToast(getActivity(), "All fields are mandatory");
                } else {
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
                    mAlertDialog.dismiss();
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
                mAlertDialog.dismiss();
            });
        }
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
        AppUtils.hideKeyBoard(getActivity());
        actv_search_medicine.setText("");
        staggeredMedicineClick(isOpen, isEdit, dataObj, pos);
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        AppUtils.hideKeyBoard(getActivity());
    }
}
