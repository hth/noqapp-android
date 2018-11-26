package com.noqapp.android.merchant.views.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.MasterLabPresenter;
import com.noqapp.android.merchant.model.MasterLabModel;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.PreferenceActivity;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;

public class PreferenceHCServiceFragment extends Fragment implements MasterLabPresenter {

    private ListView lv_tests, lv_all_tests;
    private AutoCompleteTextView actv_search;
    private EditText edt_add;
    private ArrayAdapter<String> listAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<String> masterDataString = new ArrayList<>();

    public ArrayList<DataObj> getSelectedList() {
        return selectedList;
    }

    private ArrayList<DataObj> selectedList = new ArrayList<>();
    private MultiSelectListAdapter multiSelectListAdapter;
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
                dataObj.setName(value);
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    multiSelectListAdapter.notifyDataSetChanged();
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

        multiSelectListAdapter = new MultiSelectListAdapter(getActivity(), selectedList);
        lv_tests.setAdapter(multiSelectListAdapter);
        lv_all_tests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataObj dataObj = new DataObj();
                dataObj.setName(masterDataString.get(position));
                dataObj.setSelect(false);
                if (!selectedList.contains(dataObj)) {
                    selectedList.add(dataObj);
                    multiSelectListAdapter.notifyDataSetChanged();
                }
            }
        });

        Button btn_add = v.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                MasterLabModel masterLabModel = new MasterLabModel();
                masterLabModel.setMasterLabPresenter(PreferenceHCServiceFragment.this);
                masterLabModel.add(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new JsonMasterLab().
                        setProductName(edt_add.getText().toString()).setProductShortName(edt_add.getText().toString()).setHealthCareService(getHealthCareEnum(getArguments().getInt("type"))));
            }
        });
        return v;
    }

    @Override
    public void masterLabUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            masterDataString.add(edt_add.getText().toString());
            listAdapter.notifyDataSetChanged();
            actvAdapter.notifyDataSetChanged();
            DataObj dataObj = new DataObj();
            dataObj.setName(edt_add.getText().toString());
            dataObj.setSelect(false);
            selectedList.add(dataObj);
            multiSelectListAdapter.notifyDataSetChanged();
            edt_add.setText("");
            Toast.makeText(getActivity(), "Test updated Successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Failed to update test", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
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
        listAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, masterDataString);
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
            default:
                return HealthCareServiceEnum.PATH;
        }
    }

    private ArrayList<DataObj> getPreviousList(int pos) {
        switch (pos) {
            case 0:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getMriList();
            case 1:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getScanList();
            case 2:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getSonoList();
            case 3:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getXrayList();
            case 4:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getPathologyList();
            default:
                return PreferenceActivity.getPreferenceActivity().testCaseObjects.getPathologyList();
        }
    }


}
