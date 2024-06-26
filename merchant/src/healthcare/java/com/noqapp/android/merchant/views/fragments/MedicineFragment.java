package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.CustomExpandListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class MedicineFragment extends BaseFragment implements CustomExpandListAdapter.RemoveChild {
    private CustomExpandListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<DataObj>> listDataChild;
    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private int selectionPos = -1;
    private EditText edt_item;
    private List<String> category_data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_medicine, container, false);
        ExpandableListView expListView = v.findViewById(R.id.lvExp);
        edt_item = v.findViewById(R.id.edt_item);
        final SegmentedControl sc_category = v.findViewById(R.id.sc_category);
        Button btn_add_medicine = v.findViewById(R.id.btn_add_medicine);
        btn_add_medicine.setOnClickListener(v1 -> {
            edt_item.setError(null);
            if (selectionPos == -1) {
                new CustomToast().showToast(getActivity(), "Please select medicine type");
            } else if (TextUtils.isEmpty(edt_item.getText().toString())) {
                edt_item.setError("Medicine name cann't be empty");
            } else {
                String category = category_data.get(selectionPos);
                String medicineName = category.substring(0, 3) + " " + edt_item.getText().toString();
                DataObj dataObj = new DataObj(medicineName, category, false);
                if (selectedList.contains(dataObj)) {
                    new CustomToast().showToast(getActivity(), "Medicine already added");
                } else {
                    listDataChild.get(category).add(dataObj);
                    selectedList.add(dataObj);
                    listAdapter.notifyDataSetChanged();
                    sc_category.clearSelection();
                    selectionPos = -1;
                    edt_item.setText("");
                }
                AppUtils.hideKeyBoard(getActivity());
            }

        });
        category_data.clear();
        category_data.addAll(PharmacyCategoryEnum.asListOfDescription());

        sc_category.removeAllSegments();
        sc_category.addSegments(category_data);
        sc_category.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    selectionPos = segmentViewHolder.getAbsolutePosition();
                }
            }
        });
        prepareListData();
        listAdapter = new CustomExpandListAdapter(getActivity(), listDataHeader, listDataChild, this);
        expListView.setAdapter(listAdapter);
        return v;
    }

    private void prepareListData() {
        if (null != listDataHeader)
            listDataHeader.clear();
        if (null != listDataChild)
            listDataChild.clear();
        listDataHeader = PharmacyCategoryEnum.asListOfDescription();
        listDataChild = new HashMap<String, List<DataObj>>();
        for (int i = 0; i < listDataHeader.size(); i++) {
            listDataChild.put(listDataHeader.get(i), new ArrayList<DataObj>()); // Header, Child data
        }
        selectedList = PreferenceActivity.getPreferenceActivity().preferenceObjects.getMedicineList();
        if (null == selectedList)
            selectedList = new ArrayList<>();

        for (int i = 0; i < selectedList.size(); i++) {
            listDataChild.get(selectedList.get(i).getCategory()).add(selectedList.get(i));
        }
    }

    @Override
    public void removeChildAtPos(int pos, DataObj dataObj) {
        listDataChild.get(dataObj.getCategory()).remove(pos);
        selectedList.remove(dataObj);
        listAdapter.notifyDataSetChanged();
        new CustomToast().showToast(getActivity(), "Record deleted from List");
    }

    private void saveData() {
        PreferenceActivity.getPreferenceActivity().preferenceObjects.setMedicineList(clearListSelection());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveData();
    }

    private ArrayList<DataObj> clearListSelection() {
        ArrayList<DataObj> temp = new ArrayList<>();
        for (DataObj d :
                selectedList) {
            temp.add(d.setSelect(false));
        }
        return temp;
    }
}
