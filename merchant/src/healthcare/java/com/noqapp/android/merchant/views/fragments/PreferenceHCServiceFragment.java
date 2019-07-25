package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MasterLabApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.SelectItemListAdapter;
import com.noqapp.android.merchant.views.adapters.TestListAdapter;
import com.noqapp.android.merchant.views.adapters.TestListAutoComplete;
import com.noqapp.android.merchant.views.interfaces.MasterLabPresenter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PreferenceHCServiceFragment extends BaseFragment implements
        SelectItemListAdapter.RemoveListItem, TestListAdapter.FlagListItem, MasterLabPresenter {

    private ListView lv_tests, lv_all_tests;
    private AutoCompleteTextView actv_search;
    private EditText edt_add;
    private TestListAdapter testListAdapter;
    private ArrayList<JsonMasterLab> masterLabArrayList = new ArrayList<>();

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
    private TestListAutoComplete testListAutoComplete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_preference_hc_service, container, false);
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
                JsonMasterLab value = (JsonMasterLab) parent.getItemAtPosition(position);
                DataObj dataObj = new DataObj();
                dataObj.setShortName(value.getProductShortName());
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    selectItemListAdapter.notifyDataSetChanged();
                    actv_search.setText("");
                } else {
                    new CustomToast().showToast(getActivity(), "Already selected");
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
                dataObj.setShortName(masterLabArrayList.get(position).getProductShortName());
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    selectItemListAdapter.notifyDataSetChanged();
                }
            }
        });

        Button btn_add = v.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v1 -> {
            new AppUtils().hideKeyBoard(getActivity());
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
                new CustomToast().showToast(getActivity(), "Test updated Successfully");
            }
        });
        return v;
    }

    public void setData(ArrayList<JsonMasterLab> tempList) {
        masterLabArrayList = tempList;
        Collections.sort(masterLabArrayList, new Comparator<JsonMasterLab>() {
            @Override
            public int compare(JsonMasterLab item1, JsonMasterLab item2) {
                return item1.getProductShortName().compareToIgnoreCase(item2.getProductShortName());
            }
        });
        testListAdapter = new TestListAdapter(getActivity(), masterLabArrayList, this);
        lv_all_tests.setAdapter(testListAdapter);
        testListAutoComplete = new TestListAutoComplete(getActivity(), masterLabArrayList);
        actv_search.setAdapter(testListAutoComplete);
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
        if (null == PreferenceActivity.getPreferenceActivity().preferenceObjects)
            return null;
        else {
            switch (pos) {
                case 0: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getMriList();
                    sortListData(temp);
                    return temp;
                }
                case 1: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getScanList();
                    sortListData(temp);
                    return temp;
                }
                case 2: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getSonoList();
                    sortListData(temp);
                    return temp;
                }
                case 3: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getXrayList();
                    sortListData(temp);
                    return temp;
                }
                case 4: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getPathologyList();
                    sortListData(temp);
                    return temp;
                }
                case 5: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getSpecList();
                    sortListData(temp);
                    return temp;
                }
                default: {
                    ArrayList<DataObj> temp = PreferenceActivity.getPreferenceActivity().preferenceObjects.getPathologyList();
                    sortListData(temp);
                    return temp;
                }
            }
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

    @Override
    public void flagItem(int pos) {
        new CustomToast().showToast(getActivity(), "Record flagged");
        setProgressMessage("Flag the data...");
        showProgress();
        MasterLabApiCalls masterLabApiCalls = new MasterLabApiCalls();
        masterLabApiCalls.setMasterLabPresenter(this);
        masterLabApiCalls.flag(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), masterLabArrayList.get(pos));
    }

    @Override
    public void masterLabUploadResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(getActivity(), "Data flagged successfully!");
        } else {
            new CustomToast().showToast(getActivity(), "Failed to flag data");
        }
        dismissProgress();
    }
}
