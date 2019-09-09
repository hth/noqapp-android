package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.DentalWorkDoneEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.TeethNumberAdapter;
import com.noqapp.android.merchant.views.adapters.WorkDoneAdapter;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class DentalWorkDoneFragment extends BaseFragment implements WorkDoneAdapter.OnItemClickListener{
    private ListView list_view;
    private TextView tv_add_work;
    private SegmentedControl  sc_procedure, sc_status, sc_unit, sc_period;
    private List<String> dental_procedure;
    private List<String> dental_status;
    private List<String> dental_units;
    private List<String> dental_period;
    private List<String> dental_number= new ArrayList<>();
    private ImageView tv_close;
    private TextView tv_done;
    private NestedScrollView ll_work_done;
    private String teethNumber;
    private String teethProcedure;
    private String teethStatus;
    private String teethUnit;
    private String teethPeriod;
    private EditText edt_summary;
    private ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();
    private WorkDoneAdapter workDoneAdapter;
    private TeethNumberAdapter teethNumberAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_dental_work_done, container, false);
        int spanCount = 4;
        spanCount = LaunchActivity.isTablet ? 16 : 4;
        list_view = v.findViewById(R.id.list_view);
        tv_add_work = v.findViewById(R.id.tv_add_work);
        RecyclerView rcv_tooth_number = v.findViewById(R.id.rcv_tooth_number);
        sc_procedure = v.findViewById(R.id.sc_procedure);

        sc_status = v.findViewById(R.id.sc_status);
        sc_unit = v.findViewById(R.id.sc_unit);
        sc_period = v.findViewById(R.id.sc_period);
        tv_done = v.findViewById(R.id.tv_done);
        edt_summary = v.findViewById(R.id.edt_summary);
        tv_close = v.findViewById(R.id.tv_close);
        tv_close.setOnClickListener(v13 -> clearOptionSelection());
        ll_work_done = v.findViewById(R.id.ll_work_done);
        tv_add_work.setOnClickListener(v12 -> {
            clearOptionSelection();
            ll_work_done.setVisibility(View.VISIBLE);
        });
        dental_procedure = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getSymptoms());
        dental_number = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getDentalDiagnosisList());
        dental_status = DentalWorkDoneEnum.asListOfDescription();
        dental_units = Arrays.asList(getResources().getStringArray(R.array.units));
        dental_period = Arrays.asList(getResources().getStringArray(R.array.units));

        teethNumberAdapter = new TeethNumberAdapter(getActivity(), dental_number);
        rcv_tooth_number.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        rcv_tooth_number.setAdapter(teethNumberAdapter);


        sc_procedure.addSegments(dental_procedure);
        sc_procedure.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethProcedure = dental_procedure.get(segmentViewHolder.getAbsolutePosition());
                }
            }
        });

        sc_unit.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethUnit = dental_units.get(segmentViewHolder.getAbsolutePosition());
                }
            }
        });

        sc_period.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethPeriod = dental_period.get(segmentViewHolder.getAbsolutePosition());
                }
            }
        });
        sc_status.addSegments(dental_status);
        sc_status.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethStatus = dental_status.get(segmentViewHolder.getAbsolutePosition());
                }
            }
        });
        tv_done.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(teethProcedure)) {
                new CustomToast().showToast(getActivity(), "Procedure is mandatory");
            } else {
                // Save data
                teethNumber = teethNumberAdapter.getSelectedItem();
                if (isItemExist(teethNumber)) {
                    new CustomToast().showToast(getActivity(), "Tooth already added to list");
                } else {
                    toothWorkDoneList.add(new ToothWorkDone(teethNumber, teethProcedure, edt_summary.getText().toString(), teethStatus, teethUnit, teethPeriod));
                    workDoneAdapter.setWorkDoneList(toothWorkDoneList);
                    clearOptionSelection();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        workDoneAdapter = new WorkDoneAdapter(getActivity(), toothWorkDoneList,this);
        list_view.setAdapter(workDoneAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() &&
                    null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteToDiagnoser()) {
                parseWorkDoneDate(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteToDiagnoser());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearOptionSelection() {
        ll_work_done.setVisibility(View.GONE);
        teethNumber = "";
        teethProcedure = "";
        teethUnit = "";
        teethPeriod = "";
        teethStatus = "";
        edt_summary.setText("");
        sc_procedure.clearSelection();
        teethNumberAdapter.clearSelection();
        sc_period.clearSelection();
        sc_unit.clearSelection();
        sc_status.clearSelection();

    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < toothWorkDoneList.size(); i++) {
            if (toothWorkDoneList.get(i).getToothNumber().equals(name))
                return true;
        }
        return false;
    }

    public String getSelectedData() {
        String data = "";
        for (int i = 0; i < toothWorkDoneList.size(); i++) {
            data += toothWorkDoneList.get(i).getToothNumber() + ":" + toothWorkDoneList.get(i).getProcedure() + ":" + toothWorkDoneList.get(i).getSummary()
                    + ":" + toothWorkDoneList.get(i).getTeethStatus()+ ":" + toothWorkDoneList.get(i).getTeethUnit()+ ":" + toothWorkDoneList.get(i).getTeethPeriod()
                    + "|";
        }
        if (data.endsWith("| "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setNoteToDiagnoser(getSelectedData());
    }


    public void parseWorkDoneDate(String str) {
        try {
            String[] temp = str.split("\\|", -1);
            if (temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains(":")) {
                        String[] strArray = act.split(":", -1);
                        String toothNum = strArray[0].trim();
                        String procedure = strArray[1];
                        String summary = strArray.length == 3 ? strArray[2] : "";
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
            workDoneAdapter = new WorkDoneAdapter(getActivity(), toothWorkDoneList,this);
            list_view.setAdapter(workDoneAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeWorkDone(ToothWorkDone item, int pos) {
        toothWorkDoneList.remove(pos);
        workDoneAdapter.setWorkDoneList(toothWorkDoneList);
    }
}
