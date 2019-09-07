package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class PastHistoryFragment extends BaseFragment {
    private EditText edt_past_history, edt_known_allergy, edt_family_history, edt_medicine_allergies;
    private SwitchCompat sc_enable_history;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_past_history, container, false);
        edt_past_history = v.findViewById(R.id.edt_past_history);
        edt_known_allergy = v.findViewById(R.id.edt_known_allergy);
        edt_family_history = v.findViewById(R.id.edt_family_history);
        edt_medicine_allergies = v.findViewById(R.id.edt_medicine_allergies);
        sc_enable_history = v.findViewById(R.id.sc_enable_history);
        sc_enable_history.setChecked(false);
        sc_enable_history.setOnCheckedChangeListener((compoundButton, bChecked) -> {
            if (bChecked) {
                disableEditText(true, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            } else {
                disableEditText(false, edt_family_history, edt_known_allergy, edt_medicine_allergies, edt_past_history);
            }
        });
        return v;
    }

    private void disableEditText(boolean isChecked, EditText... editTexts) {
        for (EditText edt : editTexts) {
            edt.setEnabled(isChecked);
            edt.setFocusable(isChecked);
            edt.setFocusableInTouchMode(isChecked);
            edt.setBackground(isChecked
                    ? ContextCompat.getDrawable(getActivity(), R.drawable.square_white_bg_drawable)
                    : ContextCompat.getDrawable(getActivity(), R.drawable.edt_roun_rect));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        if (null != jsonMedicalRecord && null != jsonMedicalRecord.getJsonUserMedicalProfile()) {
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
    }
}
