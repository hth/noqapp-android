package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridDentalAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;


public class TreatmentDiagnosisDentalFragment extends BaseFragment implements StaggeredGridDentalAdapter.StaggeredMedicineClick,
        AutoCompleteAdapterNew.SearchClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView recyclerView, rcv_medicine;
    private StaggeredGridDentalAdapter dentalAdapter, dentalSelectAdapter;
    private List<String> dental_option_data;
    private String dentalOption;
    private View view_med;
    private ArrayList<DataObj> selectedDentalList = new ArrayList<>();
    private DataObj dataObj;
    private int spanCount = 8;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_diagnosis_dental, container, false);
        spanCount = LaunchActivity.isTablet ? 16 : 8;
        recyclerView = v.findViewById(R.id.recyclerView);
        rcv_medicine = v.findViewById(R.id.rcv_medicine);
        view_med = v.findViewById(R.id.view_med);
        dental_option_data = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getSymptoms());
        return v;
    }

    private void clearOptionSelection() {
        dentalOption = "";
        dataObj = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        dentalAdapter = new StaggeredGridDentalAdapter(getActivity(), getToothNumbersList(), this, false);
        recyclerView.setAdapter(dentalAdapter);

        rcv_medicine.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        dentalSelectAdapter = new StaggeredGridDentalAdapter(getActivity(), selectedDentalList, this, true);
        rcv_medicine.setAdapter(dentalSelectAdapter);
        clearOptionSelection();
        view_med.setVisibility(selectedDentalList.size() > 0 ? View.VISIBLE : View.GONE);

        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() &&
                    null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteForPatient()) {
                selectedDentalList = dentalSelectAdapter.updateDataObj(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteForPatient(), getToothNumbersList());
                dentalSelectAdapter = new StaggeredGridDentalAdapter(getActivity(), selectedDentalList, this, true);
                rcv_medicine.setAdapter(dentalSelectAdapter);
                dentalSelectAdapter.notifyDataSetChanged();
                view_med.setVisibility(selectedDentalList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setNoteForPatient(dentalSelectAdapter.getSelectedData());
    }

    @Override
    public void staggeredMedicineClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if (!isEdit && isItemExist(temp.getShortName())) {
            new CustomToast().showToast(getActivity(), "Tooth Number Already added in list");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            builder.setTitle(null);
            View dialogView = inflater.inflate(R.layout.dialog_add_dental_treat, null, false);
            TextView tv_remove = dialogView.findViewById(R.id.tv_remove);
            TextView tv_done = dialogView.findViewById(R.id.tv_done);
            TextView tv_medicine_name = dialogView.findViewById(R.id.tv_medicine_name);
            ImageView iv_close = dialogView.findViewById(R.id.iv_close);
            SegmentedControl sc_dental_option = dialogView.findViewById(R.id.sc_dental_option);
            sc_dental_option.addSegments(dental_option_data);
            sc_dental_option.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
                @Override
                public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                    if (isSelected) {
                        dentalOption = dental_option_data.get(segmentViewHolder.getAbsolutePosition());
                        if (null != dataObj)
                            dataObj.setDentalProcedure(dentalOption);
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

            tv_remove.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
            dataObj = temp;
            tv_medicine_name.setText(dataObj.getShortName().equalsIgnoreCase(MedicalDataStatic.Dental.ADDITIONAL_OPTION)
                    ? dataObj.getShortName()
                    : "Tooth Number: " + dataObj.getShortName());
            if (isEdit) {
                // Pre fill the data
                sc_dental_option.setSelectedSegment(dental_option_data.indexOf(dataObj.getDentalProcedure()));
            } else {
                dentalOption = "";
                sc_dental_option.clearSelection();
            }
            tv_done.setOnClickListener(v -> {
                if (TextUtils.isEmpty(dentalOption)) {
                    new CustomToast().showToast(getActivity(), "All fields are mandatory");
                } else {
                    if (isEdit) {
                        selectedDentalList.set(pos, dataObj);
                    } else {
                        selectedDentalList.add(dataObj);
                    }
                    clearOptionSelection();
                    view_med.setVisibility(selectedDentalList.size() > 0 ? View.VISIBLE : View.GONE);

                    rcv_medicine.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
                    dentalSelectAdapter = new StaggeredGridDentalAdapter(getActivity(), selectedDentalList, this, true);
                    rcv_medicine.setAdapter(dentalSelectAdapter);
                    mAlertDialog.dismiss();
                }
            });

            tv_remove.setOnClickListener(v -> {
                if (isEdit) {
                    selectedDentalList.remove(pos);
                    view_med.setVisibility(selectedDentalList.size() > 0 ? View.VISIBLE : View.GONE);
                    rcv_medicine.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
                    dentalSelectAdapter = new StaggeredGridDentalAdapter(getActivity(), selectedDentalList, this, true);
                    rcv_medicine.setAdapter(dentalSelectAdapter);
                    clearOptionSelection();
                }
                mAlertDialog.dismiss();
            });
        }
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < selectedDentalList.size(); i++) {
            if (selectedDentalList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void searchClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos) {
        AppUtils.hideKeyBoard(getActivity());
        staggeredMedicineClick(isOpen, isEdit, dataObj, pos);
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        AppUtils.hideKeyBoard(getActivity());
    }


    private ArrayList<DataObj> getToothNumbersList() {
        ArrayList<DataObj> temp = MedicalDataStatic.Dental.getDentalDiagnosisList();
        temp.add(new DataObj(MedicalDataStatic.Dental.ADDITIONAL_OPTION, false));
        return temp;
    }
}