package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.utils.CustomProgressBar;

public abstract class BaseFragment extends Fragment implements ResponseErrorPresenter {
    private CustomProgressBar customProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customProgressBar = new CustomProgressBar(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void dismissProgress() {
        customProgressBar.dismissProgress();
    }

    protected void showProgress() {
        customProgressBar.showProgress();
    }

    protected void setProgressCancel(boolean isCancelled) {
        customProgressBar.setProgressCancel(isCancelled);
    }

    protected void setProgressMessage(String msg) {
        customProgressBar.setProgressMessage(msg);
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
