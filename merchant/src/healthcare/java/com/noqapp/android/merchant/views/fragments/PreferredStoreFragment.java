package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.PreferredStoreActivity;
import com.noqapp.android.merchant.views.adapters.PreferredListAdapter;
import com.noqapp.android.merchant.views.pojos.CheckBoxObj;
import com.noqapp.android.merchant.views.pojos.ParentCheckBoxObj;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class PreferredStoreFragment extends Fragment {

    private RecyclerView rcv_one, rcv_two;
    private TextView tv_label_one, tv_label_two;
    private ProgressDialog progressDialog;
    int pos = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_preferred_store, container, false);
        initProgress();
        rcv_one = v.findViewById(R.id.rcv_one);
        rcv_two = v.findViewById(R.id.rcv_two);
        tv_label_one = v.findViewById(R.id.tv_label_one);
        tv_label_two = v.findViewById(R.id.tv_label_two);
        rcv_one.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcv_two.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        pos = getArguments().getInt("type");
        PreferredListAdapter preferredListAdapter1 = null;
        PreferredListAdapter preferredListAdapter2 = null;
        if (pos == 0) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.MRI));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SCAN));
            tv_label_one.setText(HealthCareServiceEnum.MRI.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.SCAN.getDescription());
        } else if (pos == 1) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SONO));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.XRAY));
            tv_label_one.setText(HealthCareServiceEnum.SONO.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.XRAY.getDescription());
        } else if (pos == 2) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.PATH));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SPEC));
            tv_label_one.setText(HealthCareServiceEnum.PATH.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.SPEC.getDescription());
        } else if (pos == 3) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.PHYS));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(null));
            tv_label_one.setText(HealthCareServiceEnum.PHYS.getDescription());
            tv_label_two.setText("Pharmacy");
        }
        rcv_one.setAdapter(preferredListAdapter1);
        rcv_two.setAdapter(preferredListAdapter2);
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

    private List<CheckBoxObj> checkBoxObjListScan = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListSono = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListPath = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListXray = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListSpec = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListMri = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListPhysio = new ArrayList<>();
    private List<CheckBoxObj> checkBoxObjListMedicine = new ArrayList<>();


    private List<ParentCheckBoxObj> initCheckBoxList(HealthCareServiceEnum healthCareServiceEnum) {
        checkBoxObjListScan.clear();
        checkBoxObjListSono.clear();
        checkBoxObjListPath.clear();
        checkBoxObjListXray.clear();
        checkBoxObjListSpec.clear();
        checkBoxObjListMri.clear();
        checkBoxObjListPhysio.clear();
        checkBoxObjListMedicine.clear();
        for (int i = 0; i < PreferredStoreActivity.getPreferredStoreActivity().getJsonPreferredBusiness().size(); i++) {
            JsonPreferredBusiness jpb = PreferredStoreActivity.getPreferredStoreActivity().getJsonPreferredBusiness().get(i);
            if (jpb.getBusinessType() == BusinessTypeEnum.PH) {
                checkBoxObjListMedicine.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
            } else if (jpb.getBusinessType() == BusinessTypeEnum.HS) {

                HealthCareServiceEnum hcse = HealthCareServiceEnum.valueOf(jpb.getBizCategoryId());
                switch (hcse) {
                    case SONO:
                        checkBoxObjListSono.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case SCAN:
                        checkBoxObjListScan.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case MRI:
                        checkBoxObjListMri.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case PATH:
                        checkBoxObjListPath.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case XRAY:
                        checkBoxObjListXray.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case SPEC:
                        checkBoxObjListSpec.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                    case PHYS:
                        checkBoxObjListPhysio.add(new CheckBoxObj().setJsonPreferredBusiness(jpb).setSelect(false));
                        break;
                }
            }
        }
        List<CheckBoxObj> tempList = getList(healthCareServiceEnum);
        List<ParentCheckBoxObj> parentCheckBoxObjs = new ArrayList<>();
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            List<CheckBoxObj> temp = new ArrayList<>();
            temp.addAll(tempList);
            parentCheckBoxObjs.add(new ParentCheckBoxObj().setCheckBoxObjList(temp).setJsonTopic(LaunchActivity.merchantListFragment.getTopics().get(i)).setSelectedPos(-1));
        }

        return parentCheckBoxObjs;
    }

    private List<CheckBoxObj> getList(HealthCareServiceEnum hcse) {
        if (null == hcse)
            return checkBoxObjListMedicine;
        switch (hcse) {
            case SONO:
                return checkBoxObjListSono;
            case SCAN:
                return checkBoxObjListScan;
            case MRI:
                return checkBoxObjListMri;
            case PATH:
                return checkBoxObjListPath;
            case XRAY:
                return checkBoxObjListXray;
            case SPEC:
                return checkBoxObjListSpec;
            case PHYS:
                return checkBoxObjListPhysio;
            default:
                return checkBoxObjListMedicine;

        }
    }
}