package com.noqapp.android.merchant.views.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.DentalWorkDoneEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.TeethNumberAdapter;
import com.noqapp.android.merchant.views.adapters.WorkDoneAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;
import com.noqapp.android.merchant.views.utils.ShowAddDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class DentalWorkDoneFragment extends BaseFragment implements WorkDoneAdapter.OnItemClickListener {
    private ListView list_view;
    private List<String> dental_procedure;
    private List<String> dental_status;
    private List<String> dental_units;
    private List<String> dental_period;
    private String teethNumber;
    private String teethProcedure;
    private String teethStatus;
    private String teethUnit;
    private String teethPeriod;
    private ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();
    private WorkDoneAdapter workDoneAdapter;
    private List<String> dental_number;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_dental_work_done, container, false);
        list_view = v.findViewById(R.id.list_view);
        TextView tv_add_work = v.findViewById(R.id.tv_add_work);
        tv_add_work.setOnClickListener(v12 -> {
            clearOptionSelection();
            showAddWorkDoneDialog();
        });
        initDentalProcedure();
        dental_number = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getDentalDiagnosisList());
        dental_number.add(MedicalDataStatic.Dental.ADDITIONAL_OPTION);
        dental_status = DentalWorkDoneEnum.asListOfDescription();
        dental_units = Arrays.asList(getResources().getStringArray(R.array.units));
        dental_period = Arrays.asList(getResources().getStringArray(R.array.units));
        TextView tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(v12 -> AddItemDialog(getActivity()));

        return v;
    }

    private void initDentalProcedure() {
        dental_procedure = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getSymptoms());
        dental_procedure.addAll(MedicalDataStatic.convertDataObjListAsStringList(MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getDentalProcedureList()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        workDoneAdapter = new WorkDoneAdapter(getActivity(), toothWorkDoneList, this);
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
        teethNumber = "";
        teethProcedure = "";
        teethUnit = "";
        teethPeriod = "";
        teethStatus = "";
    }

    private void showAddWorkDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_add_dental_work_done, null, false);
        TextView tv_done = dialogView.findViewById(R.id.tv_done);
        EditText edt_summary = dialogView.findViewById(R.id.edt_summary);
        ImageView iv_close = dialogView.findViewById(R.id.iv_close);
        SegmentedControl sc_procedure = dialogView.findViewById(R.id.sc_procedure);
        SegmentedControl sc_status = dialogView.findViewById(R.id.sc_status);
        SegmentedControl sc_unit = dialogView.findViewById(R.id.sc_unit);
        SegmentedControl sc_period = dialogView.findViewById(R.id.sc_period);
        RecyclerView rcv_tooth_number = dialogView.findViewById(R.id.rcv_tooth_number);
        TeethNumberAdapter teethNumberAdapter = new TeethNumberAdapter(getActivity(), dental_number);
        rcv_tooth_number.setLayoutManager(new GridLayoutManager(getActivity(), LaunchActivity.isTablet ? 16 : 8));
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

        builder.setView(dialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mAlertDialog.show();
        iv_close.setOnClickListener(v1 -> {
            clearOptionSelection();
            mAlertDialog.dismiss();
        });
        tv_done.setOnClickListener(v1 -> {
            teethNumber = teethNumberAdapter.getSelectedItem();
            if (TextUtils.isEmpty(teethProcedure)) {
                new CustomToast().showToast(getActivity(), "Procedure is mandatory");
            } else if (TextUtils.isEmpty(teethNumber)) {
                new CustomToast().showToast(getActivity(), "Teeth number is mandatory");
            } else {
                // Save data
                if (isItemExist(teethNumber)) {
                    new CustomToast().showToast(getActivity(), "Tooth already added to list");
                } else {
                    toothWorkDoneList.add(new ToothWorkDone(teethNumber, teethProcedure, edt_summary.getText().toString(), DentalWorkDoneEnum.getDescriptionName(teethStatus), teethUnit, teethPeriod));
                    workDoneAdapter.setWorkDoneList(toothWorkDoneList);
                    clearOptionSelection();
                    mAlertDialog.dismiss();
                }
            }
        });

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
                    + ":" + toothWorkDoneList.get(i).getTeethStatus() + ":" + toothWorkDoneList.get(i).getTeethUnit() + ":" + toothWorkDoneList.get(i).getTeethPeriod()
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
            workDoneAdapter = new WorkDoneAdapter(getActivity(), toothWorkDoneList, this);
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

    private void AddItemDialog(final Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDentalProcedureList();
                temp.add(new DataObj(str, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setDentalProcedureList(temp);
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getDentalProcedureList().add(new DataObj(str, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                initDentalProcedure();
            }
        });
        showDialog.displayDialog("Add New Procedure");
    }
}
