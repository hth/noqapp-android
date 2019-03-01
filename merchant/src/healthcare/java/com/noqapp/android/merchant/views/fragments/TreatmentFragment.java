package com.noqapp.android.merchant.views.fragments;


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

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class TreatmentFragment extends Fragment implements StaggeredGridMedicineAdapter.StaggeredMedicineClick, AutoCompleteAdapterNew.SearchClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView recyclerView, recyclerView_one, rcv_medicine;
    private TextView tv_add_medicine, tv_add_diagnosis, tv_close, tv_remove, tv_medicine_name;
    private StaggeredGridAdapter diagnosisAdapter;
    private StaggeredGridMedicineAdapter medicineAdapter, medicineSelectedAdapter;
    private ScrollView ll_medicine;
    private SegmentedControl sc_duration, sc_medicine_timing, sc_frequency;
    private List<String> duration_data, timing_data, frequency_data;
    private Button btn_done;
    private String medicineTiming, medicineDuration, medicineFrequency;
    private View view_med;
    private ArrayList<DataObj> selectedMedicineList = new ArrayList<>();
    private DataObj dataObj;
    private int selectionPos = -1;
    private AutoCompleteTextView actv_search_medicine,actv_search_dia;

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
        duration_data = DurationDaysEnum.asListOfDescription();
        sc_duration.addSegments(duration_data);

        sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    medicineDuration = duration_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineDuration, Toast.LENGTH_LONG).show();
                    if (null != dataObj)
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
                    if (null != dataObj)
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
                    if (null != dataObj)
                        dataObj.setMedicineFrequency(medicineFrequency);
                }
            }
        });

        actv_search_medicine = v.findViewById(R.id.actv_search_medicine);
        actv_search_medicine.setThreshold(1);
        actv_search_dia = v.findViewById(R.id.actv_search_dia);
        actv_search_dia.setThreshold(1);




        ImageView iv_clear_actv_medicine = v.findViewById(R.id.iv_clear_actv_medicine);
        iv_clear_actv_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv_search_medicine.setText("");
                new AppUtils().hideKeyBoard(getActivity());
            }
        });
        ImageView iv_clear_actv_dia = v.findViewById(R.id.iv_clear_actv_dia);
        iv_clear_actv_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv_search_dia.setText("");
                new AppUtils().hideKeyBoard(getActivity());
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
        recyclerView.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this, false);
        recyclerView.setAdapter(medicineAdapter);

        recyclerView_one.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        diagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
        recyclerView_one.setAdapter(diagnosisAdapter);

        rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);

        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getDiagnosis()) {
                String[] temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getDiagnosis().split(",");
                diagnosisAdapter.updateSelection(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedMedicineList = medicineSelectedAdapter.updateMedicineSelectList(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalMedicines(),
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList());
        medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, this, true);
        rcv_medicine.setAdapter(medicineSelectedAdapter);
        clearOptionSelection();
        view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);

        AutoCompleteAdapterNew adapter = new AutoCompleteAdapterNew(getActivity(), android.R.layout.simple_dropdown_item_1line, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), this,null);
        actv_search_medicine.setAdapter(adapter);
        AutoCompleteAdapterNew adapterSearchDia = new AutoCompleteAdapterNew(getActivity(), R.layout.layout_autocomplete, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList(), null,this);
        actv_search_dia.setAdapter(adapterSearchDia);
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
                    recyclerView_one.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
                    recyclerView_one.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getDiagnosisList().add(new DataObj(edt_item.getText().toString(), false));
                    MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
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
                } else if (selectionPos == -1) {
                    Toast.makeText(getActivity(), "please select a category", Toast.LENGTH_LONG).show();
                } else {
                    String category = category_data.get(selectionPos);
                    String medicineName = category.substring(0, 3) + " " + edt_item.getText().toString();
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList();
                    temp.add(new DataObj(medicineName, category, false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMedicineList(temp);
                    recyclerView.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridMedicineAdapter customAdapter = new StaggeredGridMedicineAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList(), TreatmentFragment.this, false);
                    recyclerView.setAdapter(customAdapter);

                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getMedicineList().add(new DataObj(medicineName, category, false));
                    MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setJsonMedicineList(medicineSelectedAdapter.getSelectedDataListObject());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDiagnosis(diagnosisAdapter.getSelectedData());
    }

    @Override
    public void staggeredMedicineClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        if (!isEdit && isItemExist(temp.getShortName())) {
            ll_medicine.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Medicine Already added in list", Toast.LENGTH_LONG).show();
        } else {
            ll_medicine.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
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
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(medicineDuration) || TextUtils.isEmpty(medicineTiming) || TextUtils.isEmpty(medicineFrequency)) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
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
                    medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, TreatmentFragment.this, true);
                    rcv_medicine.setAdapter(medicineSelectedAdapter);
                }
            }
        });
        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    selectedMedicineList.remove(pos);
                }
                clearOptionSelection();
                view_med.setVisibility(selectedMedicineList.size() > 0 ? View.VISIBLE : View.GONE);
                rcv_medicine.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                medicineSelectedAdapter = new StaggeredGridMedicineAdapter(getActivity(), selectedMedicineList, TreatmentFragment.this, true);
                rcv_medicine.setAdapter(medicineSelectedAdapter);
            }
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
        actv_search_dia.setText("");
        diagnosisAdapter.selectItem(dataObj);
        diagnosisAdapter.notifyDataSetChanged();

    }
}
