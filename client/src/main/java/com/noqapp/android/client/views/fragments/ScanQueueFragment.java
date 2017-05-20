package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ScanQueueFragment extends Scanner {

    private final String TAG = ScanQueueFragment.class.getSimpleName();

    @BindView(R.id.rl_empty)
    protected RelativeLayout rl_empty;

    @BindView(R.id.btnScanQRCode)
    protected Button btnScanQRCode;

    private String currentTab = "";
    private boolean fromList = false;

    public ScanQueueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentTab = LaunchActivity.tabHome;
        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                // don 't start the scanner
                currentTab = LaunchActivity.tabList;
                fromList = true;
            } else {
                startScanningBarcode();
            }

        } else {
            startScanningBarcode();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!fromList)// to not modify the actionbar if it is coming from list
            LaunchActivity.getLaunchActivity().setActionBarTitle("ScanQ");
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @OnClick(R.id.btnScanQRCode)
    public void scanQR() {
        startScanningBarcode();
    }


    @Override
    protected void barcodeResult(String codeqr) {
        Bundle b = new Bundle();
        b.putString(KEY_CODEQR, codeqr);
        b.putBoolean(KEY_FROM_LIST, fromList);
        JoinFragment jf = new JoinFragment();
        jf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, currentTab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

}
