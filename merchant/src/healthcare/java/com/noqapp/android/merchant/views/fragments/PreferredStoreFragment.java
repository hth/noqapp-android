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
import com.noqapp.android.merchant.views.pojos.PreferredStoreInfo;

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
    private TextView tv_label_one, tv_label_two, tv_sublabel_one, tv_sublabel_two;
    private ProgressDialog progressDialog;
    private int pos = -1;
    private PreferredListAdapter preferredListAdapter1 = null;
    private PreferredListAdapter preferredListAdapter2 = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_preferred_store, container, false);
        initProgress();
        rcv_one = v.findViewById(R.id.rcv_one);
        rcv_two = v.findViewById(R.id.rcv_two);
        tv_label_one = v.findViewById(R.id.tv_label_one);
        tv_label_two = v.findViewById(R.id.tv_label_two);
        tv_sublabel_one = v.findViewById(R.id.tv_sublabel_one);
        tv_sublabel_two = v.findViewById(R.id.tv_sublabel_two);
        rcv_one.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcv_two.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        pos = getArguments().getInt("type");

        if (pos == 0) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.MRI));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SCAN));
            tv_label_one.setText(HealthCareServiceEnum.MRI.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.SCAN.getDescription());
            tv_sublabel_one.setText(getMessage(HealthCareServiceEnum.MRI.getDescription()));
            tv_sublabel_two.setText(getMessage(HealthCareServiceEnum.SCAN.getDescription()));
        } else if (pos == 1) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SONO));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.XRAY));
            tv_label_one.setText(HealthCareServiceEnum.SONO.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.XRAY.getDescription());
            tv_sublabel_one.setText(getMessage(HealthCareServiceEnum.SONO.getDescription()));
            tv_sublabel_two.setText(getMessage(HealthCareServiceEnum.XRAY.getDescription()));
        } else if (pos == 2) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.PATH));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.SPEC));
            tv_label_one.setText(HealthCareServiceEnum.PATH.getDescription());
            tv_label_two.setText(HealthCareServiceEnum.SPEC.getDescription());
            tv_sublabel_one.setText(getMessage(HealthCareServiceEnum.PATH.getDescription()));
            tv_sublabel_two.setText(getMessage(HealthCareServiceEnum.SPEC.getDescription()));
        } else if (pos == 3) {
            preferredListAdapter1 = new PreferredListAdapter(getActivity(), initCheckBoxList(HealthCareServiceEnum.PHYS));
            preferredListAdapter2 = new PreferredListAdapter(getActivity(), initCheckBoxList(null));
            tv_label_one.setText(HealthCareServiceEnum.PHYS.getDescription());
            tv_label_two.setText("Pharmacy");
            tv_sublabel_one.setText(getMessage(HealthCareServiceEnum.PHYS.getDescription()));
            tv_sublabel_two.setText(getMessage("Pharmacy"));
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
            int selectedPos = getSelectionPos(temp, LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR(), healthCareServiceEnum);
            parentCheckBoxObjs.add(new ParentCheckBoxObj().setCheckBoxObjList(temp).setJsonTopic(LaunchActivity.merchantListFragment.getTopics().get(i)).setSelectedPos(selectedPos));
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

    public void saveData() {
        if (pos == 0) {
            List<ParentCheckBoxObj> temp1 = preferredListAdapter1.getParentCheckBoxObjs();
            for (int i = 0; i < temp1.size(); i++) {
                if (null == PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                        get(temp1.get(i).getJsonTopic().getCodeQR())) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().put(temp1.get(i).getJsonTopic().getCodeQR(), new PreferredStoreInfo());
                }

                if (temp1.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdMri(temp1.get(i).getCheckBoxObjList().get(temp1.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdMri("");
                }
            }
            List<ParentCheckBoxObj> temp2 = preferredListAdapter2.getParentCheckBoxObjs();
            for (int i = 0; i < temp2.size(); i++) {
                if (temp2.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdScan(temp2.get(i).getCheckBoxObjList().get(temp2.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdScan("");
                }
            }

        } else if (pos == 1) {
            List<ParentCheckBoxObj> temp1 = preferredListAdapter1.getParentCheckBoxObjs();
            for (int i = 0; i < temp1.size(); i++) {
                if (temp1.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdSono(temp1.get(i).getCheckBoxObjList().get(temp1.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdSono("");
                }
            }
            List<ParentCheckBoxObj> temp2 = preferredListAdapter2.getParentCheckBoxObjs();
            for (int i = 0; i < temp2.size(); i++) {
                if (temp2.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdXray(temp2.get(i).getCheckBoxObjList().get(temp2.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdXray("");
                }
            }

        } else if (pos == 2) {
            List<ParentCheckBoxObj> temp1 = preferredListAdapter1.getParentCheckBoxObjs();
            for (int i = 0; i < temp1.size(); i++) {
                if (temp1.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPath(temp1.get(i).getCheckBoxObjList().get(temp1.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPath("");
                }
            }
            List<ParentCheckBoxObj> temp2 = preferredListAdapter2.getParentCheckBoxObjs();
            for (int i = 0; i < temp2.size(); i++) {
                if (temp2.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdSpecial(temp2.get(i).getCheckBoxObjList().get(temp2.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdSpecial("");
                }
            }

        } else if (pos == 3) {
            List<ParentCheckBoxObj> temp1 = preferredListAdapter1.getParentCheckBoxObjs();
            for (int i = 0; i < temp1.size(); i++) {
                if (temp1.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPhysio(temp1.get(i).getCheckBoxObjList().get(temp1.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp1.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPhysio("");
                }
            }
            List<ParentCheckBoxObj> temp2 = preferredListAdapter2.getParentCheckBoxObjs();
            for (int i = 0; i < temp2.size(); i++) {
                if (temp2.get(i).getSelectedPos() > -1) {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPharmacy(temp2.get(i).getCheckBoxObjList().get(temp2.get(i).
                            getSelectedPos()).getJsonPreferredBusiness().getBizStoreId());
                } else {
                    PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                            get(temp2.get(i).getJsonTopic().getCodeQR()).setBizStoreIdPharmacy("");
                }
            }

        }
    }

    private int getSelectionPos(List<CheckBoxObj> temp, String codeQR, HealthCareServiceEnum hcse) {
        PreferredStoreInfo preferredStoreInfo = PreferredStoreActivity.getPreferredStoreActivity().preferenceObjects.getPreferredStoreInfoHashMap().
                get(codeQR);
        if (null == preferredStoreInfo)
            return -1;

        if (null == hcse) {
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPharmacy())) {
                    return i;
                }
            }
            return -1;
        }
        switch (hcse) {
            case SONO: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdSono())) {
                        return i;
                    }
                }
                return -1;
            }
            case SCAN: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdScan())) {
                        return i;
                    }
                }
                return -1;
            }
            case MRI: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdMri())) {
                        return i;
                    }
                }
                return -1;
            }
            case PATH: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPath())) {
                        return i;
                    }
                }
                return -1;
            }
            case XRAY: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdXray())) {
                        return i;
                    }
                }
                return -1;
            }
            case SPEC: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdSpecial())) {
                        return i;
                    }
                }
                return -1;
            }
            case PHYS: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPhysio())) {
                        return i;
                    }
                }
                return -1;
            }
            default: {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getJsonPreferredBusiness().getBizStoreId().equals(preferredStoreInfo.getBizStoreIdPharmacy())) {
                        return i;
                    }
                }
                return -1;
            }

        }
    }

    private String getMessage(String str){
        return  "Select preferred store to receive "+str+" requested for patients";
    }
}