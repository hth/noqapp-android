package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class UserProfileFragment extends Fragment implements View.OnClickListener, ProfilePresenter {

    private EditText edt_birthday;
    private EditText edt_address;
    private EditText edt_phoneNo;
    private EditText edt_name;
    private EditText edt_email;

    private String gender = "";
    private DatePickerDialog fromDatePickerDialog;
    private MerchantProfileModel merchantProfileModel;
    private ProgressDialog progressDialog;
    private String qUserId = "";
    private SegmentedControl sc_gender;
    private ArrayList<String> gender_list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        merchantProfileModel = new MerchantProfileModel();
        initProgress();
        edt_email = view.findViewById(R.id.edt_email);
        edt_name = view.findViewById(R.id.edt_name);
        edt_phoneNo = view.findViewById(R.id.edt_phone);

        Button btn_update = view.findViewById(R.id.btn_update);
        edt_address = view.findViewById(R.id.edt_address);
        edt_birthday = view.findViewById(R.id.edt_birthday);
        edt_birthday.setInputType(InputType.TYPE_NULL);
        gender_list.clear();
        gender_list.add("Male");
        gender_list.add("Female");
        gender_list.add("Transgender");
        sc_gender = view.findViewById(R.id.sc_gender);
        sc_gender.addSegments(gender_list);
        sc_gender.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    int selection = segmentViewHolder.getAbsolutePosition();
                    switch (selection) {
                        case 0:
                            gender = "M";
                            break;
                        case 1:
                            gender = "F";
                            break;
                        case 2:
                            gender = "T";
                            break;
                        default:
                            gender = "M";
                    }
                }
            }
        });
        btn_update.setOnClickListener(this);
        edt_birthday.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(getActivity(), getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    edt_birthday.setText("");
                } else {
                    edt_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edt_birthday:
                fromDatePickerDialog.show();
                break;
            case R.id.btn_update:
                updateProfile();
                break;
        }
    }

    public void updateProfile() {

        if (validate()) {
            // btn_update.setBackgroundResource(R.drawable.button_drawable_red);
            // btn_update.setTextColor(Color.WHITE);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = edt_birthday.getText().toString();
                String address = edt_address.getText().toString();
                UpdateProfile updateProfile = new UpdateProfile();
                updateProfile.setAddress(address);
                updateProfile.setFirstName(name);
                updateProfile.setBirthday(convertDOBToValidFormat(birthday));
                updateProfile.setGender(gender);
                updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                updateProfile.setQueueUserId(qUserId);
                merchantProfileModel.setProfilePresenter(this);
                merchantProfileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = CommonHelper.SDF_DOB_FROM_UI.parse(dob);
            return CommonHelper.SDF_YYYY_MM_DD.format(date);
        } catch (ParseException e) {
            Log.e(UserProfileFragment.class.getSimpleName(), "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        // NoQueueBaseActivity.commitProfile(profile, email, auth);
        Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_LONG).show();
        dismissProgress();
        //updateUI();
    }

    @Override
    public void profileError() {
        dismissProgress();
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

    public void updateUI(JsonProfile jsonProfile) {
        edt_name.setText(jsonProfile.getName());
        edt_phoneNo.setText(jsonProfile.getPhoneRaw());
        edt_email.setText(jsonProfile.getMail());
        edt_phoneNo.setEnabled(false);
        edt_email.setEnabled(false);
        edt_address.setText(jsonProfile.getAddress());
        gender = jsonProfile.getGender().name();
        if (jsonProfile.getGender().name().equals("M")) {
            sc_gender.setSelectedSegment(0);
        } else if (jsonProfile.getGender().name().equals("F")) {
            sc_gender.setSelectedSegment(1);
        } else {
            sc_gender.setSelectedSegment(2);
        }
        try {
            edt_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(jsonProfile.getBirthday())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        qUserId = jsonProfile.getQueueUserId();
    }


    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean validate() {
        // btn_update.setBackgroundResource(R.drawable.button_drawable);
        // btn_update.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        boolean isValid = true;
        edt_name.setError(null);
        edt_email.setError(null);
        edt_birthday.setError(null);
        new AppUtils().hideKeyBoard(getActivity());
        if (TextUtils.isEmpty(edt_name.getText())) {
            edt_name.setError(getString(R.string.error_name_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_name.getText()) && edt_name.getText().length() < 4) {
            edt_name.setError(getString(R.string.error_name_length));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_email.getText()) && !isValidEmail(edt_email.getText())) {
            edt_email.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(edt_birthday.getText())) {
            edt_birthday.setError(getString(R.string.error_dob_blank));
            isValid = false;
        }
        return isValid;
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

