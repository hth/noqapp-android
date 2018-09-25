package com.noqapp.android.merchant.views.fragments;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.PatientProfileModel;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PatientProfileFragment extends Fragment implements PatientProfilePresenter {

    private ImageView iv_profile;
    private String gender = "";
    private TextView tv_name;
    private EditText edt_birthday;
    private EditText edt_address;
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private EditText tv_male;
    private EditText tv_female;

    private LinearLayout ll_dependent;
    private SimpleDateFormat dateFormatter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_patient_profile, container, false);
        tv_name = view.findViewById(R.id.tv_name);
        edt_birthday = view.findViewById(R.id.edt_birthday);
        edt_address = view.findViewById(R.id.edt_address);
        edt_phoneNo = view.findViewById(R.id.edt_phone);
        edt_Name = view.findViewById(R.id.edt_name);
        edt_Mail = view.findViewById(R.id.edt_email);
        tv_male = view.findViewById(R.id.tv_male);
        tv_female = view.findViewById(R.id.tv_female);
        ll_dependent = view.findViewById(R.id.ll_dependent);

        iv_profile = view.findViewById(R.id.iv_profile);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);
        edt_Name.setEnabled(false);
        edt_birthday.setEnabled(false);
        edt_address.setEnabled(false);
        initProgress();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            PatientProfileModel profileModel = new PatientProfileModel(this);
            profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(getArguments().getString("qCodeQR")).setQueueUserId(getArguments().getString("qUserId")));
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }

    private void loadProfilePic(String url) {
        //Picasso.with(getActivity()).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(getActivity())
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + url)
                        //.placeholder(ImageUtils.getProfilePlaceholder(getActivity()))
                        //.error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        updateUI(jsonProfile);
        dismissProgress();
    }

    @Override
    public void patientProfileError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
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

    private void updateUI(JsonProfile jsonProfile) {
        edt_Name.setText(jsonProfile.getName());
        tv_name.setText(jsonProfile.getName());
        edt_phoneNo.setText(jsonProfile.getPhoneRaw());
        edt_Mail.setText(jsonProfile.getMail().contains("noqapp.com") ? "" :
                jsonProfile.getMail());
        edt_address.setText(jsonProfile.getAddress());
        int id = 0;
        if (jsonProfile.getGender().equals("M")) {
            id = R.id.tv_male;
        } else {
            id = R.id.tv_female;
        }
        switch (id) {
            case R.id.tv_male:
                gender = "M";
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setBackgroundResource(R.drawable.gender_redbg);
                SpannableString ss = new SpannableString("Male  ");
                Drawable d = getResources().getDrawable(R.drawable.check_white);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ss.setSpan(span, 5, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_male.setText(ss);
                tv_male.setTextColor(Color.WHITE);
                tv_female.setTextColor(Color.BLACK);
                tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
            case R.id.tv_female:
                gender = "F";
                tv_female.setBackgroundResource(R.drawable.gender_redbg);
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_female.setCompoundDrawablePadding(0);
                tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.WHITE);
                SpannableString ss1 = new SpannableString("Female  ");
                Drawable d1 = getResources().getDrawable(R.drawable.check_white);
                d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight());
                ImageSpan span1 = new ImageSpan(d1, ImageSpan.ALIGN_BASELINE);
                ss1.setSpan(span1, 7, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_female.setText(ss1);
                break;


        }
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = dateFormatter.format(fromUser.parse(jsonProfile.getBirthday()));
            edt_birthday.setText(reformattedStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JsonProfile> jsonProfiles = jsonProfile.getDependents();
        ll_dependent.removeAllViews();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfileTemp = jsonProfiles.get(j);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View listitem_dependent = inflater.inflate(R.layout.listitem_dependent, null);
                TextView tv_title = listitem_dependent.findViewById(R.id.tv_title);
                tv_title.setText(jsonProfileTemp.getName());
                ll_dependent.addView(listitem_dependent);
            }
        }
        loadProfilePic(jsonProfile.getProfileImage());
    }


    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
