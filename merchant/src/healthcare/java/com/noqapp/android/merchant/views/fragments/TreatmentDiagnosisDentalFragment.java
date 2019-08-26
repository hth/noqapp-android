package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

    private static final String ADDITIONAL_OPTION = "Mouth" ;
    private RecyclerView recyclerView, rcv_medicine;
    private TextView tv_add_medicine, tv_close, tv_remove, tv_medicine_name;
    private StaggeredGridDentalAdapter dentalAdapter, dentalSelectAdapter;
    private ScrollView ll_medicine;
    private SegmentedControl sc_dental_option;
    private List<String> dental_option_data;
    private Button btn_done;
    private String dentalOption;
    private View view_med;
    private ArrayList<DataObj> selectedDentalList = new ArrayList<>();
    private DataObj dataObj;
    private int spanCount = 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_diagnosis_dental, container, false);
        spanCount = LaunchActivity.isTablet ? 16 : 4;
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
        tv_close.setOnClickListener(v13 -> clearOptionSelection());
        dental_option_data = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getSymptoms());
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
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setNoteForPatient(dentalSelectAdapter.getSelectedData());  }

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
        tv_medicine_name.setText(dataObj.getShortName().equalsIgnoreCase(ADDITIONAL_OPTION)?
                dataObj.getShortName():"Tooth Number: "+dataObj.getShortName());
        if (isEdit) {
            // Pre fill the data
            sc_dental_option.setSelectedSegment(dental_option_data.indexOf(dataObj.getDentalProcedure()));
        } else {
            dentalOption = "";
            sc_dental_option.clearSelection();
        }
        btn_done.setOnClickListener(v -> {
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
            }
        });
        tv_remove.setOnClickListener(v -> {
            if (isEdit) {
                selectedDentalList.remove(pos);
            }
            clearOptionSelection();
            view_med.setVisibility(selectedDentalList.size() > 0 ? View.VISIBLE : View.GONE);
            rcv_medicine.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
            dentalSelectAdapter = new StaggeredGridDentalAdapter(getActivity(), selectedDentalList, this, true);
            rcv_medicine.setAdapter(dentalSelectAdapter);
        });
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
        new AppUtils().hideKeyBoard(getActivity());
        staggeredMedicineClick(isOpen, isEdit, dataObj, pos);
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        new AppUtils().hideKeyBoard(getActivity());
    }


    private ArrayList<DataObj> getToothNumbersList(){
       ArrayList<DataObj> temp = MedicalDataStatic.Dental.getDentalDiagnosisList();
        temp.add(new DataObj(ADDITIONAL_OPTION, false));
        return temp;
    }
}