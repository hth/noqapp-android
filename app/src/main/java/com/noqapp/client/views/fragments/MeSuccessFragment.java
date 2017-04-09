package com.noqapp.client.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.noqapp.client.R;
import com.noqapp.client.views.activities.InviteActivity;
import com.noqapp.client.views.activities.NoQueueBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MeSuccessFragment extends NoQueueBaseFragment {

    @BindView(R.id.tv_firstLastName)
    TextView tv_firstName;

    @BindView(R.id.tv_phoneno)
    TextView tv_phoneNo;

   @BindView(R.id.tv_RemoteScanCount)
   TextView tv_scanCount;

    @BindView(R.id.toggleAutojoin)
    ToggleButton toggelAutoJoin;

    @BindView(R.id.btn_invite)
    Button btn_Invite;
    private String inviteCode;

    public MeSuccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeLoginSuccesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeSuccessFragment newInstance(String param1, String param2) {
        MeSuccessFragment fragment = new MeSuccessFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mesuccess, container, false);
        ButterKnife.bind(this,view);
        return view;
     }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = preferences.getString(NoQueueBaseActivity.PREKEY_NAME,"");
        String phone = preferences.getString(NoQueueBaseActivity.PREKEY_PHONE,"");
        String gender = preferences.getString(NoQueueBaseActivity.PREKEY_GENDER,"");
        int remoteScanCount = preferences.getInt(NoQueueBaseActivity.PREKEY_REMOTESCAN,0);
        boolean isAutoScanAvail = preferences.getBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN,false);
        inviteCode = preferences.getString(NoQueueBaseActivity.PREKEY_INVITECODE,"");

        tv_firstName.setText(name);
        tv_phoneNo.setText(phone);
        tv_scanCount.setText(String.valueOf(remoteScanCount));
        toggelAutoJoin.setChecked(isAutoScanAvail);

    }

    @OnClick(R.id.btn_invite)
    public void action_Invite(View view)
    {
        Intent in =new Intent(getActivity(),InviteActivity.class);
        in.putExtra("invite_code",inviteCode);
        startActivity(in);
    }
}
