package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.SelectItemListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PreferenceHCServiceFragment extends Fragment implements SelectItemListAdapter.RemoveListItem {

    private ListView lv_tests, lv_all_tests;
    private AutoCompleteTextView actv_search;
    private EditText edt_add;
    private ArrayAdapter<String> listAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<String> masterDataString = new ArrayList<>();

    public ArrayList<DataObj> getSelectedList() {
        return selectedList;
    }

    public ArrayList<DataObj> clearListSelection() {
        ArrayList<DataObj> temp = new ArrayList<>();
        for (DataObj d :
                selectedList) {
            temp.add(d.setSelect(false));
        }
        return temp;
    }

    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private SelectItemListAdapter selectItemListAdapter;
    private ArrayAdapter<String> actvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_preference_hc_service, container, false);
        initProgress();
        edt_add = v.findViewById(R.id.edt_add);
        lv_tests = v.findViewById(R.id.lv_tests);
        lv_all_tests = v.findViewById(R.id.lv_all_tests);
        actv_search = v.findViewById(R.id.actv_search);
        selectedList = getPreviousList(getArguments().getInt("type"));
        if (null == selectedList)
            selectedList = new ArrayList<>();
        actv_search.setThreshold(1);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                DataObj dataObj = new DataObj();
                dataObj.setShortName(value);
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    selectItemListAdapter.notifyDataSetChanged();
                    actv_search.setText("");
                } else {
                    Toast.makeText(getActivity(), "Already selected", Toast.LENGTH_LONG).show();
                }
                new AppUtils().hideKeyBoard(getActivity());
            }
        });
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

        selectItemListAdapter = new SelectItemListAdapter(getActivity(), selectedList, this);
        lv_tests.setAdapter(selectItemListAdapter);
        lv_all_tests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataObj dataObj = new DataObj();
                dataObj.setShortName(masterDataString.get(position));
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    selectItemListAdapter.notifyDataSetChanged();
                }
            }
        });

        Button btn_add = v.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(getActivity());
                edt_add.setError(null);
                if (TextUtils.isEmpty(edt_add.getText().toString())) {
                    edt_add.setError(getString(R.string.error_field_required));
                } else {
                    masterDataString.add(edt_add.getText().toString());
                    listAdapter.notifyDataSetChanged();
                    actvAdapter.notifyDataSetChanged();
                    DataObj dataObj = new DataObj();
                    dataObj.setShortName(edt_add.getText().toString());
                    dataObj.setSelect(false);
                    selectedList.add(dataObj);
                    selectItemListAdapter.notifyDataSetChanged();
                    edt_add.setText("");
                    Toast.makeText(getActivity(), "Test updated Successfully", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void setData(ArrayList<String> tempList) {
        masterDataString = tempList;
        Collections.sort(masterDataString);
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, masterDataString);
        lv_all_tests.setAdapter(listAdapter);
        actvAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, masterDataString);
        actv_search.setAdapter(actvAdapter);
    }

    private HealthCareServiceEnum getHealthCareEnum(int pos) {
        switch (pos) {
            case 0:
                return HealthCareServiceEnum.MRI;
            case 1:
                return HealthCareServiceEnum.SCAN;
            case 2:
                return HealthCareServiceEnum.SONO;
            case 3:
                return HealthCareServiceEnum.XRAY;
            case 4:
                return HealthCareServiceEnum.PATH;
            case 5:
                return HealthCareServiceEnum.SPEC;
            default:
                return HealthCareServiceEnum.PATH;
        }
    }

    private ArrayList<DataObj> getPreviousList(int pos) {
        if (null == PreferenceActivity.getPreferenceActivity().testCaseObjects)
            return null;
        else {

            switch (pos) {
                case 0: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getMriList();
                    sortListData(temp);
                    return temp;
                }
                case 1: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getScanList();
                    sortListData(temp);
                    return temp;
                }
                case 2: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getSonoList();
                    sortListData(temp);
                    return temp;
                }
                case 3: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getXrayList();
                    sortListData(temp);
                    return temp;
                }
                case 4: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getPathologyList();
                    sortListData(temp);
                    return temp;
                }
                case 5: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getSpecList();
                    sortListData(temp);
                    return temp;
                }
                default: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().testCaseObjects.getPathologyList();
                    sortListData(temp);
                    return temp;
                }
            }


        }
    }

    private void sortListData(ArrayList<DataObj> dataObjs){
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
        Toast.makeText(getActivity(), "Record deleted from List", Toast.LENGTH_LONG).show();

    }
}
