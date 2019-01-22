package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class SymptomsFragment extends Fragment implements StaggeredGridSymptomAdapter.StaggeredClick {

    private RecyclerView rcv_gynac, rcv_obstretics, rcv_symptom_select;
    private TextView tv_add_new, tv_symptoms_name, tv_close, tv_remove, tv_output;
    private StaggeredGridSymptomAdapter symptomsAdapter, obstreticsAdapter, symptomSelectedAdapter;
    private EditText edt_past_history, edt_known_allergy, edt_family_history, edt_medicine_allergies,edt_output;
    private ScrollView ll_symptom_note;
    private DataObj dataObj;
    private SegmentedControl sc_duration;
    private List<String> duration_data;
    private String no_of_days;
    private Button btn_done;
    private View view_med;
    private ArrayList<DataObj> selectedSymptomsList = new ArrayList<>();
    private TextView tv_obes, tv_gyanc;
    private SwitchCompat sc_enable_history;
    private AutoCompleteTextView actv_search;
    private final int columnCount =4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        rcv_gynac = v.findViewById(R.id.rcv_gynac);
        rcv_obstretics = v.findViewById(R.id.rcv_obstretics);
        rcv_symptom_select = v.findViewById(R.id.rcv_symptom_select);
        edt_past_history = v.findViewById(R.id.edt_past_history);
        edt_known_allergy = v.findViewById(R.id.edt_known_allergy);
        edt_family_history = v.findViewById(R.id.edt_family_history);
        edt_medicine_allergies = v.findViewById(R.id.edt_medicine_allergies);
        edt_output = v.findViewById(R.id.edt_output);
        sc_enable_history = v.findViewById(R.id.sc_enable_history);
        btn_done = v.findViewById(R.id.btn_done);
        view_med = v.findViewById(R.id.view_med);
        tv_obes = v.findViewById(R.id.tv_obes);
        tv_gyanc = v.findViewById(R.id.tv_gyanc);
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
        duration_data = DurationDaysEnum.asListOfDescription();
        sc_duration.addSegments(duration_data);

        sc_duration.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    no_of_days = duration_data.get(segmentViewHolder.getAbsolutePosition());
                    //Toast.makeText(getActivity(), medicineDuration, Toast.LENGTH_LONG).show();
                    tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
                    if (null != dataObj)
                        dataObj.setNoOfDays(no_of_days);
                }
            }
        });
        sc_enable_history.setChecked(false);
        sc_enable_history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    disableEditText(true, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
                } else {
                    disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
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

        actv_search = v.findViewById(R.id.actv_search);
        actv_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(actv_search, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });
        actv_search.setThreshold(1);
//        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String value = (String) parent.getItemAtPosition(position);
//
//                new AppUtils().hideKeyBoard(getActivity());
//            }
//        });
        actv_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (actv_search.getRight() - actv_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        actv_search.setText("");
                        new AppUtils().hideKeyBoard(getActivity());
                        return true;
                    }
                    if (event.getRawX() <= (20 + actv_search.getLeft() + actv_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        //performSearch();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    private void disableEditText(boolean isChecked, EditText... editTexts) {
        for (EditText edt :
                editTexts) {
            edt.setEnabled(isChecked);
            edt.setFocusable(isChecked);
            edt.setFocusableInTouchMode(isChecked);
            edt.setBackground(isChecked ? ContextCompat.getDrawable(getActivity(), R.drawable.square_white_bg_drawable) : ContextCompat.getDrawable(getActivity(), R.drawable.edt_roun_rect));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList().size() / columnCount) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_gynac.setLayoutManager(staggeredGridLayoutManager);
        symptomsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList(), this, false);
        rcv_gynac.setAdapter(symptomsAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList().size() / columnCount) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_obstretics.setLayoutManager(staggeredGridLayoutManager1);
        obstreticsAdapter = new StaggeredGridSymptomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList(), this, false);
        rcv_obstretics.setAdapter(obstreticsAdapter);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedSymptomsList.size(),columnCount), LinearLayoutManager.HORIZONTAL);
        rcv_symptom_select.setLayoutManager(sglm);
        symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, this, true);
        rcv_symptom_select.setAdapter(symptomSelectedAdapter);
        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        if (null != jsonMedicalRecord.getJsonUserMedicalProfile()) {
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies())
                edt_known_allergy.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory())
                edt_past_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory())
                edt_family_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory());
            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies())
                edt_medicine_allergies.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies());

            if (null != jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory()
                    || null != jsonMedicalRecord.getJsonUserMedicalProfile().getMedicineAllergies()) {
                sc_enable_history.setChecked(true);
                disableEditText(true, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            } else {
                sc_enable_history.setChecked(false);
                disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            }
        } else {
            sc_enable_history.setChecked(false);
            disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
        }

        try {
            ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
            dataObjArrayList.addAll(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList());
            dataObjArrayList.addAll(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
            setupAutoComplete(dataObjArrayList);
            selectedSymptomsList = symptomSelectedAdapter.updateDataObj(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getChiefComplain(), dataObjArrayList);
            symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, this, true);
            rcv_symptom_select.setAdapter(symptomSelectedAdapter);
            view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MedicalCaseActivity.getMedicalCaseActivity().isGynae()) {
            tv_gyanc.setVisibility(View.VISIBLE);
            tv_obes.setVisibility(View.VISIBLE);
        } else {
            tv_gyanc.setVisibility(View.GONE);
            tv_obes.setVisibility(View.GONE);
        }
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
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / columnCount) + 1, LinearLayoutManager.HORIZONTAL);
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
        if (sc_enable_history.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setKnownAllergies(edt_known_allergy.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPastHistory(edt_past_history.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFamilyHistory(edt_family_history.getText().toString());
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setMedicineAllergies(edt_medicine_allergies.getText().toString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setKnownAllergies(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPastHistory(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFamilyHistory(null);
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setMedicineAllergies(null);
        }

        if (null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getKnownAllergies()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getPastHistory()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getFamilyHistory()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getMedicineAllergies()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHistoryFilled(true);
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHistoryFilled(false);
        }
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptoms(symptomSelectedAdapter.getSelectedData());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSymptomsObject(symptomSelectedAdapter.getSelectedSymptomData());

    }


    @Override
    public void staggeredClick(boolean isOpen, final boolean isEdit, DataObj temp, final int pos) {
        new AppUtils().hideKeyBoard(getActivity());
        if (!isEdit && isItemExist(temp.getShortName())) {
            ll_symptom_note.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Symptom Already added in list", Toast.LENGTH_LONG).show();
        } else {
            ll_symptom_note.setVisibility(isOpen ? View.VISIBLE : View.GONE);
        }
        tv_remove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        dataObj = temp;
        tv_symptoms_name.setText(dataObj.getShortName());
        if (isEdit) {
            // Pre fill the data
            sc_duration.setSelectedSegment(duration_data.indexOf(dataObj.getNoOfDays()));
            no_of_days = dataObj.getNoOfDays();
            tv_output.setText("Having " + dataObj.getShortName() + " since last " + no_of_days);
            edt_output.setText(TextUtils.isEmpty(dataObj.getAdditionalNotes())?"":dataObj.getAdditionalNotes());
        } else {
            sc_duration.clearSelection();
            tv_output.setText("");
            edt_output.setText("");
            no_of_days = "";
        }
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(getActivity());
                dataObj.setNoOfDays(no_of_days);
                if (TextUtils.isEmpty(no_of_days)) {
                    Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                } else {
                    dataObj.setAdditionalNotes(edt_output.getText().toString());
                    if (isEdit) {
                        selectedSymptomsList.set(pos, dataObj);
                    } else {
                        selectedSymptomsList.add(dataObj);
                    }

                    view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);

                    StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedSymptomsList.size(),columnCount), LinearLayoutManager.HORIZONTAL);
                    rcv_symptom_select.setLayoutManager(sglm);
                    symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, SymptomsFragment.this, true);
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
                    selectedSymptomsList.remove(pos);
                }
                clearOptionSelection();
                view_med.setVisibility(selectedSymptomsList.size() > 0 ? View.VISIBLE : View.GONE);
                StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(new AppUtils().calculateColumnCount(selectedSymptomsList.size(),columnCount), LinearLayoutManager.HORIZONTAL);
                rcv_symptom_select.setLayoutManager(sglm);
                symptomSelectedAdapter = new StaggeredGridSymptomAdapter(getActivity(), selectedSymptomsList, SymptomsFragment.this, true);
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
        edt_output.setText("");
        dataObj = null;
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < selectedSymptomsList.size(); i++) {
            if (selectedSymptomsList.get(i).getShortName().equals(name))
                return true;
        }
        return false;
    }

    private void setupAutoComplete( final List<DataObj> objects) {
        List<String> names = new AbstractList<String>() {
            @Override
            public int size() { return objects.size(); }

            @Override
            public String get(int location) {
                return objects.get(location).getShortName();
            }
        };

        actv_search.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names));
    }
}
