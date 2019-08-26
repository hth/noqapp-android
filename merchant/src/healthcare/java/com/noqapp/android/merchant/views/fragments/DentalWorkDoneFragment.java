package com.noqapp.android.merchant.views.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class DentalWorkDoneFragment extends BaseFragment {

    private TableLayout tl_work_done;
    private TextView tv_add_work, tv_close;
    private SegmentedControl sc_teeth_number, sc_procedure;
    private List<String> dental_procedure;
    private List<String> dental_number;
    private TextView tv_done;
    private ScrollView ll_work_done;
    private String teethNumber;
    private String teethProcedure;
    private EditText edt_summary;
    private ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_dental_work_done, container, false);
        tl_work_done = v.findViewById(R.id.tl_work_done);
        tv_add_work = v.findViewById(R.id.tv_add_work);
        sc_teeth_number = v.findViewById(R.id.sc_teeth_number);
        sc_procedure = v.findViewById(R.id.sc_procedure);
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
        sc_teeth_number.addSegments(dental_number);
        sc_teeth_number.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethNumber = dental_number.get(segmentViewHolder.getAbsolutePosition());

                }
            }
        });
        sc_procedure.addSegments(dental_procedure);
        sc_procedure.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    teethProcedure = dental_procedure.get(segmentViewHolder.getAbsolutePosition());

                }
            }
        });
        tv_done.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(teethProcedure)) {
                new CustomToast().showToast(getActivity(), "Procedure is mandatory");
            } else {
                // Save data
                if (isItemExist(teethNumber)) {
                    new CustomToast().showToast(getActivity(), "Tooth already added to list");
                } else {
                    toothWorkDoneList.add(new ToothWorkDone(teethNumber, teethProcedure, edt_summary.getText().toString()));
                    drawTable(false, teethNumber, teethProcedure, edt_summary.getText().toString());
                    clearOptionSelection();
                }
            }
        });

        return v;
    }

    private void drawTable(boolean isHeader, String teethNumber, String teethProcedure, String summary) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.table_row_dental, null);
        TextView tv_header = view.findViewById(R.id.tv_header);
        TextView tv_right = view.findViewById(R.id.tv_right);
        TextView tv_left = view.findViewById(R.id.tv_left);
        if (isHeader) {
            tv_header.setText("Tooth No.");
            tv_header.setTypeface(null, Typeface.BOLD);
            tv_right.setTypeface(null, Typeface.BOLD);
            tv_left.setTypeface(null, Typeface.BOLD);
            tv_header.setGravity(Gravity.CENTER);
            tv_right.setText("Procedure");
            tv_left.setText("Summary");
        } else {
            tv_header.setText(teethNumber);
            tv_right.setText(teethProcedure);
            tv_left.setText(summary);
            tv_header.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv_header.setTypeface(null, Typeface.NORMAL);
            tv_right.setTypeface(null, Typeface.NORMAL);
            tv_left.setTypeface(null, Typeface.NORMAL);
        }
        tl_work_done.addView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tl_work_done.removeAllViews();
        drawTable(true, "", "", "");
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() &&
                    null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteToDiagnoser()) {
                parseAndRedrawTable(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getNoteToDiagnoser());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearOptionSelection() {
        ll_work_done.setVisibility(View.GONE);
        teethNumber = "";
        teethProcedure = "";
        edt_summary.setText("");
        sc_procedure.clearSelection();
        sc_teeth_number.clearSelection();

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
            data += toothWorkDoneList.get(i).getToothNumber() + ":" + toothWorkDoneList.get(i).getProcedure() + ":" + toothWorkDoneList.get(i).getSummary() + "|";
        }
        if (data.endsWith("| "))
            data = data.substring(0, data.length() - 2);
        return data;
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setNoteToDiagnoser(getSelectedData());
    }


    public void parseAndRedrawTable(String str) {
        try {
            String[] temp = str.split("\\|");
            if (null != temp && temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains(":")) {
                        String[] strArray = act.split(":");
                        String toothNum = strArray[0].trim();
                        String procedure = strArray[1];
                        String summary = strArray.length == 3 ? strArray[2] : "";
                        drawTable(false, toothNum, procedure, summary);
                        toothWorkDoneList.add(new ToothWorkDone(toothNum, procedure, summary));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
