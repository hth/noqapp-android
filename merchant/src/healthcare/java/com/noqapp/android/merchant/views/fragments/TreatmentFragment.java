package com.noqapp.android.merchant.views.fragments;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.MedicationIntakeEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;

import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.adapters.StaggeredGridMedicineAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.List;

public class TreatmentFragment extends Fragment implements StaggeredGridMedicineAdapter.StaggeredClick {

    private RecyclerView recyclerView, recyclerView_one, rcv_medicine;
    private TextView tv_add_medicine, tv_add_diagnosis, tv_close,tv_remove, tv_medicine_name;
    private StaggeredGridAdapter diagnosisAdapter;
    private StaggeredGridMedicineAdapter medicineAdapter, medicineSelectedAdapter;
    private LinearLayout ll_medicine;
    private SegmentedControl sc_duration, sc_medicine_timing, sc_frequency;
    private List<String> duration_data, timing_data, frequency_data;
    private Button btn_done;
    private String medicineTiming, medicineDuration, medicineFrequency;
    private View view_med;
    private ArrayList<DataObj> selectedMedicineList = new ArrayList<>();
    private DataObj dataObj;
    private int selectionPos = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_treatment, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView_one = v.findViewById(R.id.recyclerViewOne);
        rcv_medicine = v.findViewById(R.id.rcv_medicine);
        tv_add_diagnosis = v.findViewById(R.id.tv_add_diagnosis);
        view_med = v.findViewById(R.id.view_med);
        tv_add_medicine = v.findViewById(R.id.tv_add_medicine);
        tv_close = v.findViewById(R.id.tv_close);
        tv_remove = v.findViewById(R.id.tv_remove);
        tv_medicine_name = v.findViewById(R.id.tv_medicine_name);
        ll_medicine = v.findViewById(R.id.ll_medicine);
        sc_duration = v.findViewById(R.id.sc_duration);
        sc_medicine_timing = v.findViewById(R.id.sc_medicine_timing);
        sc_frequency = v.findViewById(R.id.sc_frequency);
        btn_done = v.findViewById(R.id.btn_done);
        tv_add_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMedicineDialog(getActivity(), "Add Medicine");
            }
        });
        tv_add_diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Diagnosis");
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOptionSelection();
            }
        });
        duration_data = new ArrayList<>();
        duration_data.add("1");
        duration_data.add("2");
        duration_data.add("3");
        duration_data.add("4");
        duration_data.add("5");
        duration_data.add("6");
        duration_data.add("7");
        duration_data.add("10");
        duration_data.add("15");
        duration_data.add("30");
        duration_data.add("45");
        duration_data.add("60");
        duration_data.add("90");
        duration_data.add("180");
        sc_duration.addSegments(duration_data);

        sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    medicineDuration = duration_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineDuration, Toast.LENGTH_LONG).show();
                    if(null != dataObj)
                        dataObj.setMedicineDuration(medicineDuration);
                }
            }
        });

        timing_data = MedicationIntakeEnum.asListOfDescription();

        sc_medicine_timing.addSegments(timing_data);

        sc_medicine_timing.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    medicineTiming = timing_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineTiming, Toast.LENGTH_LONG).show();
                    if(null != dataObj)
                        dataObj.setMedicineTiming(medicineTiming);
                }
            }
        });

        frequency_data = DailyFrequencyEnum.asListOfDescription();
        sc_frequency.addSegments(frequency_data);

        sc_frequency.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    medicineFrequency = frequency_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineFrequency, Toast.LENGTH_LONG).show();
                    if(null != dataObj)
                        dataObj.setMedicineFrequency(medicineFrequency);
                }
            }
        });

        return v;
    }

    private void clearOptionSelection() {
        ll_medicine.setVisibility(View.GONE);
        medicineTiming = "";
        medicineDuration = "";
        medicineFrequency = "";
        tv_medicine_name.setText("");
        sc_medicine_timing.clearSelection();
        sc_duration.clearSelection();
        sc_frequency.clearSelection();
        dataObj = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        medicineAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this,false);
        recyclerView.setAdapter(medicineAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView_one.setLayoutManager(staggeredGridLayoutManager1);
        diagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
        recyclerView_one.setAdapter(diagnosisAdapter);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
        rcv_medicine.setLayoutManager(sglm);
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this,true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);
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
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList();
                        temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setDiagnosisList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(temp.size()), LinearLayoutManager.HORIZONTAL);
                        recyclerView_one.setLayoutManager(staggeredGridLayoutManager);
                        StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
                        recyclerView_one.setAdapter(customAdapter);
                        MedicalCaseActivity.getMedicalCaseActivity().getTestCaseObjects().getDiagnosisList().add(new DataObj(edt_item.getText().toString(), false));
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
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
                    //Toast.makeText(getActivity(), medicineDuration, Toast.LENGTH_LONG).show();

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
                }  else if (selectionPos == -1){
                    Toast.makeText(getActivity(),"please select a category",Toast.LENGTH_LONG).show();
                }else {
                    String category = category_data.get(selectionPos);
                    String medicineName = category.substring(0,3)+" "+edt_item.getText().toString();
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList();
                    temp.add(new DataObj(medicineName, category,false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMedicineList(temp);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(temp.size()), LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);

                    StaggeredGridMedicineAdapter customAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), TreatmentFragment.this,false);
                    recyclerView.setAdapter(customAdapter);

                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    MedicalCaseActivity.getMedicalCaseActivity().getTestCaseObjects().getMedicineList().add(new DataObj(medicineName, category,false));
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setJsonMedicineList(medicineSelectedAdapter.getSelectedDataListObject());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setDiagnosis(diagnosisAdapter.getSelectedData());
    }

    @Override
    public void staggeredClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if(!isEdit && isItemExist(temp.getShortName())){
            ll_medicine.setVisibility(View.GONE);
            Toast.makeText(getActivity(),"Medicine Already added in list",Toast.LENGTH_LONG).show();
        }else {
            ll_medicine.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
        tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        dataObj = temp;
        tv_medicine_name.setText(dataObj.getShortName());
        if(isEdit){
            // Pre fill the data
            sc_duration.setSelectedSegment(duration_data.indexOf(dataObj.getMedicineDuration()));
            sc_medicine_timing.setSelectedSegment(timing_data.indexOf(dataObj.getMedicineTiming()));
            sc_frequency.setSelectedSegment(frequency_data.indexOf(dataObj.getMedicineFrequency()));
        }else{
            medicineTiming = "";
            medicineDuration = "";
            medicineFrequency = "";
            sc_medicine_timing.clearSelection();
            sc_duration.clearSelection();
            sc_frequency.clearSelection();
        }
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(medicineDuration) || TextUtils.isEmpty(medicineTiming) || TextUtils.isEmpty(medicineFrequency)) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                } else {
                    // medicineAdapter.updateMedicine(medicine_name, medicineTiming, medicineDuration, medicineFrequency);
                    if(isEdit) {
                        selectedMedicineList.set(pos,dataObj);
                    }else{
                        selectedMedicineList.add(dataObj);
                    }
                    clearOptionSelection();
                    view_med.setVisibility(selectedMedicineList.size()>0 ? View.VISIBLE : View.GONE);

                    StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
                    rcv_medicine.setLayoutManager(sglm);
                    medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, TreatmentFragment.this,true);
                    rcv_medicine.setAdapter(medicineSelectedAdapter);
                }
            }
        });
        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isEdit) {
                        selectedMedicineList.remove(pos);
                    }
                    clearOptionSelection();
                    view_med.setVisibility(selectedMedicineList.size()>0 ? View.VISIBLE : View.GONE);
                    StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedMedicineList.size()), LinearLayoutManager.HORIZONTAL);
                    rcv_medicine.setLayoutManager(sglm);
                    medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, TreatmentFragment.this,true);
                    rcv_medicine.setAdapter(medicineSelectedAdapter);
            }
        });
    }

    private boolean isItemExist(String name){
        for (int i = 0; i < selectedMedicineList.size(); i++) {
            if(selectedMedicineList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }
}
