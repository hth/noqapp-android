package com.noqapp.client.views.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.noqapp.client.R;
import com.noqapp.client.views.activities.InviteActivity;
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
    ToggleButton toggelAutoJoin;

    @BindView(R.id.iv_invite)
    ImageView iv_invite;

    @BindView(R.id.btn_register_login_logout)
    Button btn_register_login_logout;
    private String inviteCode;
    private final String TAG = "UserInfoFragment";

    public MeFragment() {

    }


    public static MeFragment getInstance() {
        MeFragment fragment = new MeFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Me");
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = preferences.getString(NoQueueBaseActivity.PREKEY_NAME, "Guest User");
        String phone = preferences.getString(NoQueueBaseActivity.PREKEY_PHONE, "");
        String gender = preferences.getString(NoQueueBaseActivity.PREKEY_GENDER, "");
        int remoteScanCount = preferences.getInt(NoQueueBaseActivity.PREKEY_REMOTESCAN, 0);
        boolean isAutoScanAvail = preferences.getBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, false);
        inviteCode = preferences.getString(NoQueueBaseActivity.PREKEY_INVITECODE, "");

        tv_firstName.setText(name);
        tv_phoneNo.setText(phone);
        tv_scanCount.setText(String.valueOf(remoteScanCount));
        toggelAutoJoin.setChecked(isAutoScanAvail);
        if (!phone.equals("")) {
            btn_register_login_logout.setText("Logout");
            tv_phoneNo.setVisibility(View.VISIBLE);
        } else {
            btn_register_login_logout.setText("Login/Register");
            tv_phoneNo.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.iv_invite)
    public void action_Invite(View view) {
        Intent in = new Intent(getActivity(), InviteActivity.class);
        in.putExtra("invite_code", inviteCode);
        startActivity(in);
    }

    @OnClick(R.id.btn_register_login_logout)
    public void actionLogout(View view) {
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
            replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, new RegistrationFormFragment(), TAG);
        }


    }
}
