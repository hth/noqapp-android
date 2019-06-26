package com.noqapp.android.client.views.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public abstract class BaseFragment extends Fragment implements ResponseErrorPresenter {
    private Dialog dialog;
    private TextView tv_loading_msg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initProgress();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.lay_progress, null, false);
        tv_loading_msg = view.findViewById(R.id.tv_loading_msg);
        builder.setView(view);
        dialog = builder.create();
    }

    protected void dismissProgress() {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    protected void showProgress() {
        if (null != dialog)
            dialog.show();
    }

    protected void setProgressCancel(boolean isCancelled) {
        if (null != dialog && dialog.isShowing()) {
            dialog.setCanceledOnTouchOutside(isCancelled);
            dialog.setCancelable(isCancelled);
        }
    }

    protected void setProgressMessage(String msg) {
        tv_loading_msg.setText(msg);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(getActivity());
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }
}
