package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.utils.CustomProgressBar;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;

public class BaseFragment extends Fragment implements ResponseErrorPresenter {
    private CustomProgressBar customProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customProgressBar = new CustomProgressBar(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void dismissProgress() {
        if (null != customProgressBar) {
            customProgressBar.dismissProgress();
        }
    }

    protected void showProgress() {
        if (null != customProgressBar) {
            customProgressBar.showProgress();
        }
    }

    protected void setProgressCancel(boolean isCancelled) {
        if (null != customProgressBar) {
            customProgressBar.setProgressCancel(isCancelled);
        }
    }

    protected void setProgressMessage(String msg) {
        if (null != customProgressBar) {
            customProgressBar.setProgressMessage(msg);
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

    @Override
    public void onDestroy() {
        dismissProgress();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        dismissProgress();
        super.onDetach();
    }

    protected FragmentTransaction getFragmentTransaction(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        return fm.beginTransaction();
    }
}

