package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.CustomExpandListAdapter;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicineFragment extends Fragment {
    private CustomExpandListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ListView lv_tests;
    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private MultiSelectListAdapter multiSelectListAdapter;

    public ArrayList<DataObj> getSelectedList() {
        return selectedList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_medicine, container, false);
        expListView = v.findViewById(R.id.lvExp);
        lv_tests = v.findViewById(R.id.lv_tests);
        prepareListData();
        try {
            Log.e("medicine", LaunchActivity.getLaunchActivity().getFavouriteMedicines().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null != PreferenceActivity.getPreferenceActivity().testCaseObjects)
         selectedList = PreferenceActivity.getPreferenceActivity().testCaseObjects.getMedicineList();
        if (null == selectedList)
            selectedList = new ArrayList<>();
        multiSelectListAdapter = new MultiSelectListAdapter(getActivity(), selectedList);
        lv_tests.setAdapter(multiSelectListAdapter);
        listAdapter = new CustomExpandListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getActivity(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                DataObj dataObj = new DataObj();
                dataObj.setName(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                dataObj.setSelect(false);
                dataObj.setCategory(getCategory(listDataHeader.get(groupPosition)));
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    multiSelectListAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return v;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Tablet");
        listDataHeader.add("Capsule");
        listDataHeader.add("Syrup");
        listDataHeader.add("Powder");
        listDataHeader.add("Injection");

        // Adding child data
        List<String> tablet = new ArrayList<>();
        tablet.add("Paracetamol");
        tablet.add("Ibrufen");
        tablet.add("Numofine");

        List<String> capsule = new ArrayList<>();
        capsule.add("Becasule");
        capsule.add("Zinc 100");

        List<String> syrup = new ArrayList<>();
        syrup.add("Digine");
        syrup.add("Corex");
        List<String> powder = new ArrayList<>();
        powder.add("Calicum");
        powder.add("Zincocide");
        List<String> injection = new ArrayList<>();
        injection.add("Dilona 30ml");
        injection.add("Biotax 500");

        listDataChild.put(listDataHeader.get(0), tablet); // Header, Child data
        listDataChild.put(listDataHeader.get(1), capsule);
        listDataChild.put(listDataHeader.get(2), syrup);
        listDataChild.put(listDataHeader.get(3), powder);
        listDataChild.put(listDataHeader.get(4), injection);
    }

    private String getCategory(String header) {
        switch (header) {
            case "Tablet":
                return PharmacyCategoryEnum.TA.getDescription();
            case "Capsule":
                return PharmacyCategoryEnum.CA.getDescription();
            case "Syrup":
                return PharmacyCategoryEnum.SY.getDescription();
            case "Powder":
                return PharmacyCategoryEnum.PW.getDescription();
            case "Injection":
                return PharmacyCategoryEnum.IJ.getDescription();
            default:
                return PharmacyCategoryEnum.TA.getDescription();
        }
    }
}
