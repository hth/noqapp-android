package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.WebViewActivity;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeFragment extends NoQueueBaseFragment {
    private final String TAG = MeFragment.class.getSimpleName();

    @BindView(R.id.tv_firstLastName)
    protected TextView tv_firstName;

    @BindView(R.id.tv_phoneno)
    protected TextView tv_phoneNo;

    @BindView(R.id.tv_RemoteScanCount)
    protected TextView tv_scanCount;

    @BindView(R.id.toggleAutojoin)
    protected ToggleButton toggleAutoJoin;

    @BindView(R.id.btn_register_login_logout)
    protected Button btn_register_login_logout;

    @BindView(R.id.ll_rate_app)
    protected LinearLayout ll_rate_app;

    @BindView(R.id.ll_invite)
    protected LinearLayout ll_invite;

    @BindView(R.id.ll_legal)
    protected LinearLayout ll_legal;

    @BindView(R.id.ll_referal)
    protected LinearLayout ll_referal;


    private String inviteCode;

    public static MeFragment getInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.tab_me));
        LaunchActivity.getLaunchActivity().enableDisableBack(false);

        String name = NoQueueBaseActivity.getUserName();
        String phone = NoQueueBaseActivity.getPhoneNo();
        String gender = NoQueueBaseActivity.getGender();
        //  int remoteScanCount = NoQueueBaseActivity.getRemoteJoinCount();
        // boolean isAutoScanAvail = NoQueueBaseActivity.getAutoJoinStatus();
        inviteCode = NoQueueBaseActivity.getInviteCode();
        tv_firstName.setText(name);
        if (!phone.equals("")) {
            tv_phoneNo.setText(PhoneFormatterUtil.formatNumber(NoQueueBaseActivity.getCountryShortName(), phone));
        }
        //  tv_scanCount.setText(String.valueOf(remoteScanCount) + " ");
//        toggleAutoJoin.setChecked(isAutoScanAvail);
//        toggleAutoJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
//                NoQueueBaseActivity.setAutoJoinStatus(isChecked);
//            }
//        });

        if (!phone.equals("")) {
            btn_register_login_logout.setText(getString(R.string.logout));
            tv_phoneNo.setVisibility(View.VISIBLE);
        } else {
            btn_register_login_logout.setText(getString(R.string.login_register));
            tv_phoneNo.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_invite})
    public void action_Invite() {
        Bundle b = new Bundle();
        b.putString("invite_code", inviteCode);
        InviteFragment inf = new InviteFragment();
        inf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, inf, TAG);
    }


    @OnClick({R.id.ll_legal})
    public void action_Legal() {
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, new LegalFragment(), TAG);
    }

    @OnClick({R.id.ll_referal})
    public void promotionalClick() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            Intent in = new Intent(getActivity(), WebViewActivity.class);
            in.putExtra("url", Constants.URL_ABOUT_US);
            getActivity().startActivity(in);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }
}
