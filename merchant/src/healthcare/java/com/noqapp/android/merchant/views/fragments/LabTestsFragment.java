package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.ShowAddDialog;

import java.util.ArrayList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class LabTestsFragment extends BaseFragment implements AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView rcv_mri, rcv_scan, rcv_sono, rcv_xray, rcv_pathology, rcv_special;
    private TextView tv_add_pathology, tv_add_new;
    private StaggeredGridAdapter mriAdapter, scanAdapter, sonoAdapter, xrayAdapter, specAdapter, pathalogyAdapter;
    private int selectionPos = -1;
    private AutoCompleteTextView actv_search_path;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_recomand_lab_test, container, false);
        rcv_mri = v.findViewById(R.id.rcv_mri);
        rcv_scan = v.findViewById(R.id.rcv_scan);
        rcv_sono = v.findViewById(R.id.rcv_sono);
        rcv_xray = v.findViewById(R.id.rcv_xray);
        rcv_special = v.findViewById(R.id.rcv_special);
        actv_search_path = v.findViewById(R.id.actv_search_path);
        actv_search_path.setThreshold(1);
        ImageView iv_clear_actv_path = v.findViewById(R.id.iv_clear_actv_path);
        iv_clear_actv_path.setOnClickListener(v1 -> {
            actv_search_path.setText("");
            AppUtils.hideKeyBoard(getActivity());
        });

        rcv_pathology = v.findViewById(R.id.rcv_pathology);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_pathology = v.findViewById(R.id.tv_add_pathology);
        tv_add_new.setOnClickListener(v12 -> AddItemDialog(getActivity(), "Add Radiology"));
        tv_add_pathology.setOnClickListener(v13 -> AddPathologyItemDialog(getActivity()));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rcv_mri.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        mriAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList());
        rcv_mri.setAdapter(mriAdapter);

        rcv_scan.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        scanAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getScanList());
        rcv_scan.setAdapter(scanAdapter);

        rcv_sono.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        sonoAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSonoList());
        rcv_sono.setAdapter(sonoAdapter);

        rcv_xray.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        xrayAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getXrayList());
        rcv_xray.setAdapter(xrayAdapter);


        rcv_pathology.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        pathalogyAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
        rcv_pathology.setAdapter(pathalogyAdapter);

        rcv_special.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        specAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSpecList());
        rcv_special.setAdapter(specAdapter);

        updateLabSelection();
        updatePathologySelection();
        AutoCompleteAdapterNew adapterSearchPath = new AutoCompleteAdapterNew(getActivity(), R.layout.layout_autocomplete, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList(), null, this);
        actv_search_path.setAdapter(adapterSearchPath);
    }

    private void updateLabSelection() {
        try {
            List<JsonMedicalRadiologyList> temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalRadiologyLists();
            for (int i = 0; i < temp.size(); i++) {
                JsonMedicalRadiologyList jsonMedicalRadiologyList = temp.get(i);
                if (jsonMedicalRadiologyList.getLabCategory() == LabCategoryEnum.MRI) {
                    mriAdapter.updateSelectList(jsonMedicalRadiologyList.getJsonMedicalRadiologies());
                } else if (jsonMedicalRadiologyList.getLabCategory() == LabCategoryEnum.SCAN) {
                    scanAdapter.updateSelectList(jsonMedicalRadiologyList.getJsonMedicalRadiologies());
                } else if (jsonMedicalRadiologyList.getLabCategory() == LabCategoryEnum.SONO) {
                    sonoAdapter.updateSelectList(jsonMedicalRadiologyList.getJsonMedicalRadiologies());
                } else if (jsonMedicalRadiologyList.getLabCategory() == LabCategoryEnum.XRAY) {
                    xrayAdapter.updateSelectList(jsonMedicalRadiologyList.getJsonMedicalRadiologies());
                } else if (jsonMedicalRadiologyList.getLabCategory() == LabCategoryEnum.SPEC) {
                    specAdapter.updateSelectList(jsonMedicalRadiologyList.getJsonMedicalRadiologies());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePathologySelection() {
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalPathologiesLists() &&
                    MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalPathologiesLists().size() > 0) {
                List<JsonMedicalPathology> temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getMedicalPathologiesLists().get(0).getJsonMedicalPathologies();
                pathalogyAdapter.updatePathSelectList(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AddPathologyItemDialog(Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList();
                temp.add(new DataObj(str, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setPathologyList(temp);
                rcv_pathology.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                StaggeredGridAdapter customAdapter1 = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
                rcv_pathology.setAdapter(customAdapter1);
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getPathologyList().add(new DataObj(str, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
            }
        });
        showDialog.displayDialog("Add Pathology");
    }


    private void AddItemDialog(final Context mContext, String title) {
        selectionPos = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.add_item_with_category, null, false);
        final EditText edt_item = customDialogView.findViewById(R.id.edt_item);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        SegmentedControl sc_category = customDialogView.findViewById(R.id.sc_category);
        final List<String> category_data = new ArrayList<>();
        category_data.add("SONO");
        category_data.add("MRI");
        category_data.add("SCAN");
        category_data.add("X-RAY");
        category_data.add("SPEC");
        sc_category.addSegments(category_data);
        sc_category.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    selectionPos = segmentViewHolder.getAbsolutePosition();
                }
            }
        });

        tvtitle.setText(title);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ImageView iv_close = customDialogView.findViewById(R.id.iv_close);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        iv_close.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_add.setOnClickListener(v -> {
            edt_item.setError(null);
            if (edt_item.getText().toString().equals("")) {
                edt_item.setError("Empty field not allowed");
            } else if (selectionPos == -1) {
                new CustomToast().showToast(getActivity(), "please select a category");
            } else {
                if (selectionPos == 0) {
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSonoList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSonoList(temp);
                    rcv_sono.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSonoList());
                    rcv_sono.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getSonoList().add(new DataObj(edt_item.getText().toString(), false));
                } else if (selectionPos == 1) {
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMriList(temp);
                    rcv_mri.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMriList());
                    rcv_mri.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getMriList().add(new DataObj(edt_item.getText().toString(), false));
                } else if (selectionPos == 2) {
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getScanList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setScanList(temp);
                    rcv_scan.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getScanList());
                    rcv_scan.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getScanList().add(new DataObj(edt_item.getText().toString(), false));
                } else if (selectionPos == 3) {
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getXrayList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setXrayList(temp);
                    rcv_xray.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getXrayList());
                    rcv_xray.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getXrayList().add(new DataObj(edt_item.getText().toString(), false));
                } else {
                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSpecList();
                    temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSpecList(temp);
                    rcv_special.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                    StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSpecList());
                    rcv_special.setAdapter(customAdapter);
                    MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getSpecList().add(new DataObj(edt_item.getText().toString(), false));
                }
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list");
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setMriList(mriAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setScanList(scanAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSonoList(sonoAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setXrayList(xrayAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setSpecList(specAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPathologyList(pathalogyAdapter.getSelectedDataList());
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        AppUtils.hideKeyBoard(getActivity());
        actv_search_path.setText("");
        pathalogyAdapter.selectItem(dataObj);
        pathalogyAdapter.notifyDataSetChanged();
    }

}
