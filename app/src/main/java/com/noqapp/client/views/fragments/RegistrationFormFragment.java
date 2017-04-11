package com.noqapp.client.views.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.noqapp.client.R;
import com.noqapp.client.presenter.MePresenter;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.views.activities.NoQueueBaseActivity;
import com.noqapp.client.views.interfaces.MeView;

import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFormFragment extends NoQueueBaseFragment implements MeView,RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.edt_phone)
    EditText edt_phoneNo;
    @BindView(R.id.edt_name)
    EditText edt_Name;
    @BindView(R.id.edt_mail)
    EditText edt_Mail;
    @BindView(R.id.edt_birthday)
    EditText edtBirthday;
    @BindView(R.id.generGroup)
    RadioGroup genderGroup;
    @BindView(R.id.rbMale)
    RadioButton rb_male;
    @BindView(R.id.rbfemale)
    RadioButton rb_female;

    private  static final String TAG = "RegistrationForm";
    public String gender = "";


    public RegistrationFormFragment() {
        // Required empty public constructor
    }

    public void callRegistrationAPI()
    {
        String phoneNo = edt_phoneNo.getText().toString();
        String name = edt_Name.getText().toString();
        String mail = edt_Mail.getText().toString();
        String birthday = edtBirthday.getText().toString();
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());



        // Todo :Change to 2character country code
        String country = Locale.getDefault().getISO3Country();

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        //String ic = e

        Registration registrationbean = new Registration();
        registrationbean.setPhone(phoneNo);
        registrationbean.setFirstName(name);
        registrationbean.setMail(mail);
        registrationbean.setBirthday(birthday);
        registrationbean.setGender(gender);
        registrationbean.setTimeZoneId(tz.getID());
        registrationbean.setCountryShortName("IN");
        registrationbean.setInviteCode("");

        MePresenter mePresenter = new MePresenter(getContext());
        mePresenter.meView = this;
        mePresenter.callProfile(registrationbean);

        if (accessToken != null) {
            //Handle Returning User
        } else {
            //Handle new or logged out user
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_form, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        genderGroup.setOnCheckedChangeListener(this);
    }

    @OnClick(R.id.btnContinueRegistration)
    public void action_Registration(View view)
    {



        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("FB accont res:: ",data.toString());
                callRegistrationAPI();
            }
        }
    }

    @Override
    public void queueResponse(JsonProfile profile) {
        Log.d(TAG,"profile :"+profile.toString());
        SharedPreferences.Editor editor = ((NoQueueBaseActivity)getActivity()).getSharedprefEdit(getActivity());
        editor.putString(NoQueueBaseActivity.PREKEY_PHONE,profile.getPhoneRaw());
        editor.putString(NoQueueBaseActivity.PREKEY_NAME,profile.getName());
        editor.putString(NoQueueBaseActivity.PREKEY_GENDER,profile.getGender());
        editor.putString(NoQueueBaseActivity.PREKEY_MAIL,profile.getMail());
        editor.putInt(NoQueueBaseActivity.PREKEY_REMOTESCAN,profile.getRemoteScanAvailable());
        editor.putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN,true);
        editor.putString(NoQueueBaseActivity.PREKEY_INVITECODE,profile.getInviteCode());
        editor.commit();

        replaceFragmentWithoutBackStack(getActivity(),R.id.frame_layout,new MeSuccessFragment(),TAG);

    }

    @Override
    public void queueError() {
        Log.d(TAG,"Error");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch (checkedId)
        {
            case R.id.rbMale:
                gender = "M";
                break;
            case R.id.rbfemale:
                gender = "F";
                break;
            default:
                gender = "";
        }
    }
}
