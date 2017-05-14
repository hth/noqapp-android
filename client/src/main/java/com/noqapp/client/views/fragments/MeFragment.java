package com.noqapp.client.views.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.noqapp.client.R;
import com.noqapp.client.helper.PhoneFormatterUtil;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.activities.NoQueueBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MeFragment extends NoQueueBaseFragment {

    @BindView(R.id.tv_firstLastName)
    TextView tv_firstName;

    @BindView(R.id.tv_phoneno)
    TextView tv_phoneNo;

    @BindView(R.id.tv_RemoteScanCount)
    TextView tv_scanCount;

    @BindView(R.id.toggleAutojoin)
    ToggleButton toggleAutoJoin;

    @BindView(R.id.btn_register_login_logout)
    Button btn_register_login_logout;

    @BindView(R.id.ll_rate_app)
    LinearLayout ll_rate_app;

    @BindView(R.id.ll_invite)
    LinearLayout ll_invite;

    private String inviteCode;
    private final String TAG = MeFragment.class.getSimpleName();

    public static MeFragment getInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Me");
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = preferences.getString(NoQueueBaseActivity.PREKEY_NAME, "Guest User");
        String phone = preferences.getString(NoQueueBaseActivity.PREKEY_PHONE, "");
        String gender = preferences.getString(NoQueueBaseActivity.PREKEY_GENDER, "");
        int remoteScanCount = preferences.getInt(NoQueueBaseActivity.PREKEY_REMOTESCAN, 0);
        boolean isAutoScanAvail = preferences.getBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, false);
        inviteCode = preferences.getString(NoQueueBaseActivity.PREKEY_INVITECODE, "");
        String countryname = preferences.getString(NoQueueBaseActivity.PREKEY_COUNTRY_SHORT_NAME, "US");
        tv_firstName.setText(name);
        if (!phone.equals("")) {
            tv_phoneNo.setText(PhoneFormatterUtil.formatNumber(countryname, phone));
        }
        tv_scanCount.setText(String.valueOf(remoteScanCount));
        toggleAutoJoin.setChecked(isAutoScanAvail);
        toggleAutoJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                SharedPreferences.Editor editor = ((NoQueueBaseActivity) getActivity()).getSharedPreferencesEditor(getActivity());
                editor.putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, isChecked);
                editor.commit();
            }
        });
        if (!phone.equals("")) {
            btn_register_login_logout.setText("Logout");
            tv_phoneNo.setVisibility(View.VISIBLE);
        } else {
            btn_register_login_logout.setText("Login / Register");
            tv_phoneNo.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_invite})
    public void action_Invite() {
        Bundle b = new Bundle();
        b.putString("invite_code", inviteCode);
        InviteFragment inf = new InviteFragment();
        inf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, inf, TAG, LaunchActivity.tabMe);
    }

    @OnClick(R.id.btn_register_login_logout)
    public void actionLogout() {
        if (btn_register_login_logout.getText().equals("Logout")) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Would you like to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // logout
                            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                            preferences.edit().clear().commit();
                            //navigate to signup/login
                            replaceFragmentWithoutBackStack(getActivity(), R.id.frame_layout, new MeFragment(), TAG);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // user doesn't want to logout
                        }
                    })
                    .show();
        } else {
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, new LoginFragment(), TAG, LaunchActivity.tabMe);
        }
    }

    @OnClick({R.id.ll_rate_app})
    public void action_RateApp() {
        Uri uri = Uri.parse("market://details?id=" + "com.google.android.youtube");
        //getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }
}
