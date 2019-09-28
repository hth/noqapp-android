package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.SelectItemListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LocalPreferenceHCServiceFragment extends BaseFragment implements
        SelectItemListAdapter.RemoveListItem {
    private EditText edt_add;
    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private SelectItemListAdapter selectItemListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_local_pre_hc_service, container, false);
        edt_add = v.findViewById(R.id.edt_add);
        ListView lv_tests = v.findViewById(R.id.lv_tests);

        selectedList = getPreviousList(getArguments().getInt("type"));
        if (null == selectedList)
            selectedList = new ArrayList<>();


        selectItemListAdapter = new SelectItemListAdapter(getActivity(), selectedList, this);
        lv_tests.setAdapter(selectItemListAdapter);

        Button btn_add = v.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v1 -> {
            AppUtils.hideKeyBoard(getActivity());
            edt_add.setError(null);
            if (TextUtils.isEmpty(edt_add.getText().toString())) {
                edt_add.setError(getString(R.string.error_field_required));
            } else {
                DataObj dataObj = new DataObj();
                dataObj.setShortName(edt_add.getText().toString());
                dataObj.setSelect(false);
                selectedList.add(dataObj);
                selectItemListAdapter.notifyDataSetChanged();
                edt_add.setText("");
                new CustomToast().showToast(getActivity(), "Record added Successfully");
            }
        });
        return v;
    }

    private ArrayList<DataObj> getPreviousList(int pos) {
        if (null == PreferenceActivity.getPreferenceActivity().preferenceObjects)
            return null;
        else {
            ArrayList<DataObj> temp;
            switch (pos) {
                case 0: {
                    temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getSymptomsList();
                    break;
                }
                case 1: {
                    temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getProDiagnosisList();
                    break;
                }
                case 2: {
                    temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getDiagnosisList();
                    break;
                }
                case 3: {
                    temp = MedicalDataStatic.convertStringListAsDataObjList(PreferenceActivity.getPreferenceActivity().preferenceObjects.getInstructionList());
                    break;
                }
                case 4: {
                    temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getDentalProcedureList();
                    break;
                }
                default: {
                    temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getPathologyList();
                }
            }
            sortListData(temp);
            return temp;
        }
    }

    private void sortListData(ArrayList<DataObj> dataObjs) {
        Collections.sort(dataObjs, new Comparator<DataObj>() {
            @Override
            public int compare(DataObj item1, DataObj item2) {
                return item1.getShortName().compareToIgnoreCase(item2.getShortName());
            }
        });
    }

    @Override
    public void removeItem(int pos) {
        selectedList.remove(pos);
        selectItemListAdapter.notifyDataSetChanged();
        new CustomToast().showToast(getActivity(), "Record deleted from List");
    }

    private void saveData() {
        switch (getArguments().getInt("type")) {
            case 0: {
                PreferenceActivity.getPreferenceActivity().preferenceObjects.setSymptomsList(selectedList);
                break;
            }
            case 1: {
                PreferenceActivity.getPreferenceActivity().preferenceObjects.setProDiagnosisList(selectedList);
                break;
            }
            case 2: {
                PreferenceActivity.getPreferenceActivity().preferenceObjects.setDiagnosisList(selectedList);
                break;
            }
            case 3: {
                PreferenceActivity.getPreferenceActivity().preferenceObjects.setInstructionList(MedicalDataStatic.convertDataObjListAsStringList(selectedList));
                break;
            }
            case 4: {
                PreferenceActivity.getPreferenceActivity().preferenceObjects.setDentalProcedureList(selectedList);
                break;
            }
            default: {

            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        saveData();
    }

}
