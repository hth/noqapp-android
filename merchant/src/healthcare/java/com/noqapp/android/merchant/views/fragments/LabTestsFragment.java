package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class LabTestsFragment extends Fragment {

    private RecyclerView rcv_mri, rcv_scan, rcv_sono, rcv_xray, rcv_pathology;
    private TextView tv_add_dia, tv_add_new;
    private CustomAdapter mriAdapter, scanAdapter, sonoAdapter, xrayAdapter, pathalogyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_recomand_lab_test, container, false);
        rcv_mri = v.findViewById(R.id.rcv_mri);
        rcv_scan = v.findViewById(R.id.rcv_scan);
        rcv_sono = v.findViewById(R.id.rcv_sono);
        rcv_xray = v.findViewById(R.id.rcv_xray);

        rcv_pathology = v.findViewById(R.id.rcv_pathology);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_dia = v.findViewById(R.id.tv_add_dia);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Radiology", true);
            }
        });
        tv_add_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Pathology", false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_mri.setLayoutManager(staggeredGridLayoutManager);
        mriAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList(),R.drawable.bg_unselect_mri);
        rcv_mri.setAdapter(mriAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getScanList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_scan.setLayoutManager(staggeredGridLayoutManager1);
        scanAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getScanList(),R.drawable.bg_unselect_scan);
        rcv_scan.setAdapter(scanAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSonoList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_sono.setLayoutManager(staggeredGridLayoutManager2);
        sonoAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSonoList(),R.drawable.bg_unselect_sono);
        rcv_sono.setAdapter(sonoAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager3 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getXrayList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_xray.setLayoutManager(staggeredGridLayoutManager3);
        xrayAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getXrayList(),R.drawable.bg_unselect_xray);
        rcv_xray.setAdapter(xrayAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager4 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_pathology.setLayoutManager(staggeredGridLayoutManager4);
        pathalogyAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
        rcv_pathology.setAdapter(pathalogyAdapter);
    }

    private void AddItemDialog(final Context mContext, String title, final boolean isMedicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.add_item, null, false);
        final EditText edt_item = customDialogView.findViewById(R.id.edt_item);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        tvtitle.setText(title);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_item.setError(null);
                if (edt_item.getText().toString().equals("")) {
                    edt_item.setError("Empty field not allowed");
                } else {
                    if (isMedicine) {
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList();
                        temp.add(new DataObj(edt_item.getText().toString(), false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMriList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        rcv_mri.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList());
                        rcv_mri.setAdapter(customAdapter);
                    } else {
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList();
                        temp.add(new DataObj(edt_item.getText().toString(), false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setPathologyList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        rcv_pathology.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        CustomAdapter customAdapter1 = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
                        rcv_pathology.setAdapter(customAdapter1);
                    }
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setMriList(mriAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setScanList(scanAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setSonoList(sonoAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setXrayList(xrayAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPathologyList(pathalogyAdapter.getSelectedDataList());
    }
}
