package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.CustomExpandListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicineFragment extends BaseFragment implements CustomExpandListAdapter.RemoveChild {
    private CustomExpandListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<DataObj>> listDataChild;
    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private int selectionPos = -1;
    private EditText edt_item;
    final List<String> category_data = new ArrayList<>();

    public ArrayList<DataObj> getSelectedList() {
        return selectedList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_medicine, container, false);
        expListView = v.findViewById(R.id.lvExp);
        edt_item = v.findViewById(R.id.edt_item);
        final SegmentedControl sc_category = v.findViewById(R.id.sc_category);
        Button btn_add_medicine = v.findViewById(R.id.btn_add_medicine);
        btn_add_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    new AppUtils().hideKeyBoard(getActivity());
                }

            }
        });

        category_data.addAll(PharmacyCategoryEnum.asListOfDescription());


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
        if (null != PreferenceActivity.getPreferenceActivity().preferenceObjects)
            selectedList = PreferenceActivity.getPreferenceActivity().preferenceObjects.getMedicineList();
        if (null == selectedList)
            selectedList = new ArrayList<>();
        listAdapter = new CustomExpandListAdapter(getActivity(), listDataHeader, listDataChild, this);
        expListView.setAdapter(listAdapter);
        return v;
    }

    private void prepareListData() {
        listDataHeader = PharmacyCategoryEnum.asListOfDescription();
        listDataChild = new HashMap<String, List<DataObj>>();

        for (int i = 0; i < listDataHeader.size(); i++) {
            listDataChild.put(listDataHeader.get(i), new ArrayList<DataObj>()); // Header, Child data
        }


        selectedList.clear();
        ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getMedicineList();
        for (int i = 0; i < temp.size(); i++) {
            listDataChild.get(temp.get(i).getCategory()).add(temp.get(i));
            selectedList.add(temp.get(i));
        }
    }

    @Override
    public void removeChildAtPos(int pos, DataObj dataObj) {
        listDataChild.get(dataObj.getCategory()).remove(pos);
        selectedList.remove(dataObj);
        listAdapter.notifyDataSetChanged();
        new CustomToast().showToast(getActivity(), "Record deleted from List");
    }
}
