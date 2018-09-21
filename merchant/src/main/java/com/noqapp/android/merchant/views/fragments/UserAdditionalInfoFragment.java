package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.MerchantProfessionalPresenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UserAdditionalInfoFragment extends Fragment implements MerchantProfessionalPresenter {
    private EditText edt_about_me;
    private TextView edt_practice_start;
    private EditText edt_edu_name;
    private TextView tv_edu_date;
    private EditText edt_award_name;
    private TextView tv_award_date;
    private EditText edt_license_name;
    private TextView tv_license_date;
    private LinearLayout ll_education;
    private LinearLayout ll_experience;
    private LinearLayout ll_license;
    private JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal;


    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        initProgress();

        edt_about_me = view.findViewById(R.id.edt_about_me);
        edt_practice_start = view.findViewById(R.id.edt_practice_start);
        edt_edu_name = view.findViewById(R.id.edt_edu_name);
        tv_edu_date = view.findViewById(R.id.tv_edu_date);
        ImageView iv_edu_add = view.findViewById(R.id.iv_edu_add);
        edt_award_name = view.findViewById(R.id.edt_award_name);
        tv_award_date = view.findViewById(R.id.tv_award_date);
        ImageView iv_award_add = view.findViewById(R.id.iv_award_add);
        edt_license_name = view.findViewById(R.id.edt_license_name);
        tv_license_date = view.findViewById(R.id.tv_license_date);
        ImageView iv_license_add = view.findViewById(R.id.iv_license_add);
        ll_education = view.findViewById(R.id.ll_education);
        ll_experience = view.findViewById(R.id.ll_experience);
        ll_license = view.findViewById(R.id.ll_license);
        Button btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfessionalInfo();
            }
        });
        if (null != jsonProfessionalProfilePersonal)
            updateUI(jsonProfessionalProfilePersonal);

        edt_practice_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(edt_practice_start);
            }
        });

        tv_edu_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(tv_edu_date);
            }
        });
        iv_edu_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_edu_name.getText().toString()) || TextUtils.isEmpty(tv_edu_date.getText().toString()))
                    Toast.makeText(getActivity(), "Both fields are mandatory", Toast.LENGTH_LONG).show();
                else {
                    JsonNameDatePair jsonNameDatePair = new JsonNameDatePair();
                    jsonNameDatePair.setName(edt_edu_name.getText().toString());
                    jsonNameDatePair.setMonthYear(tv_edu_date.getText().toString());
                    jsonProfessionalProfilePersonal.getEducation().add(jsonNameDatePair);
                    updateUI(jsonProfessionalProfilePersonal);
                    edt_edu_name.setText("");
                    tv_edu_date.setText("");
                }
            }
        });
        tv_award_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(tv_award_date);
            }
        });
        iv_award_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_award_name.getText().toString()) || TextUtils.isEmpty(tv_award_date.getText().toString()))
                    Toast.makeText(getActivity(), "Both fields are mandatory", Toast.LENGTH_LONG).show();
                else {
                    JsonNameDatePair jsonNameDatePair = new JsonNameDatePair();
                    jsonNameDatePair.setName(edt_award_name.getText().toString());
                    jsonNameDatePair.setMonthYear(tv_award_date.getText().toString());
                    jsonProfessionalProfilePersonal.getAwards().add(jsonNameDatePair);
                    updateUI(jsonProfessionalProfilePersonal);
                    edt_award_name.setText("");
                    tv_award_date.setText("");

                }
            }
        });

        tv_license_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(tv_license_date);
            }
        });
        iv_license_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_license_name.getText().toString()) || TextUtils.isEmpty(tv_license_date.getText().toString()))
                    Toast.makeText(getActivity(), "Both fields are mandatory", Toast.LENGTH_LONG).show();
                else {
                    JsonNameDatePair jsonNameDatePair = new JsonNameDatePair();
                    jsonNameDatePair.setName(edt_license_name.getText().toString());
                    jsonNameDatePair.setMonthYear(tv_license_date.getText().toString());
                    jsonProfessionalProfilePersonal.getLicenses().add(jsonNameDatePair);
                    updateUI(jsonProfessionalProfilePersonal);
                    edt_license_name.setText("");
                    tv_license_date.setText("");
                }
            }
        });
        return view;
    }


    public void updateUI(JsonProfessionalProfilePersonal temp) {
        this.jsonProfessionalProfilePersonal = temp;
        edt_about_me.setText(jsonProfessionalProfilePersonal.getAboutMe());
        edt_practice_start.setText(jsonProfessionalProfilePersonal.getPracticeStart());
        List<JsonNameDatePair> experience = jsonProfessionalProfilePersonal.getAwards();
        List<JsonNameDatePair> education = jsonProfessionalProfilePersonal.getEducation();
        List<JsonNameDatePair> license = jsonProfessionalProfilePersonal.getLicenses();
        ll_experience.removeAllViews();
        ll_education.removeAllViews();
        ll_license.removeAllViews();
        for (int i = 0; i < education.size(); i++) {
            final JsonNameDatePair jsonNameDatePair = education.get(i);
            final View inflatedLayout = getLayoutInflater().inflate(R.layout.list_item_name_date, null);
            inflatedLayout.setId(i);
            EditText tv_name = inflatedLayout.findViewById(R.id.edt_name);
            tv_name.setEnabled(false);
            final TextView tv_date = inflatedLayout.findViewById(R.id.tv_date);
            ImageView iv_delete = inflatedLayout.findViewById(R.id.iv_delete);
            tv_name.setText(jsonNameDatePair.getName());
            tv_date.setText(TextUtils.isEmpty(jsonNameDatePair.getMonthYear()) ? "" : jsonNameDatePair.getMonthYear());
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete Eductaion");
                    tv_msg.setText("Do you want to delete it from education list?");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Deleted from Education list", Toast.LENGTH_LONG).show();
                            jsonProfessionalProfilePersonal.getEducation().remove(jsonNameDatePair);
                            ll_education.removeView(inflatedLayout);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();

                }
            });
            ll_education.addView(inflatedLayout);
        }


        for (int i = 0; i < experience.size(); i++) {
            final View inflatedLayout = getLayoutInflater().inflate(R.layout.list_item_name_date, null);
            final JsonNameDatePair jsonNameDatePair = experience.get(i);
            inflatedLayout.setId(i);
            EditText tv_name = inflatedLayout.findViewById(R.id.edt_name);
            tv_name.setEnabled(false);
            final TextView tv_date = inflatedLayout.findViewById(R.id.tv_date);
            ImageView iv_delete = inflatedLayout.findViewById(R.id.iv_delete);
            tv_name.setText(jsonNameDatePair.getName());
            tv_date.setText(TextUtils.isEmpty(jsonNameDatePair.getMonthYear()) ? "" : jsonNameDatePair.getMonthYear());
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete Award");
                    tv_msg.setText("Do you want to delete it from award list?");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Deleted from award list", Toast.LENGTH_LONG).show();
                            jsonProfessionalProfilePersonal.getAwards().remove(jsonNameDatePair);
                            ll_experience.removeView(inflatedLayout);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();

                }
            });
            ll_experience.addView(inflatedLayout);
        }

        for (int i = 0; i < license.size(); i++) {
            final View inflatedLayout = getLayoutInflater().inflate(R.layout.list_item_name_date, null);
            final JsonNameDatePair jsonNameDatePair = license.get(i);
            inflatedLayout.setId(i);
            EditText tv_name = inflatedLayout.findViewById(R.id.edt_name);
            tv_name.setEnabled(false);
            final TextView tv_date = inflatedLayout.findViewById(R.id.tv_date);
            ImageView iv_delete = inflatedLayout.findViewById(R.id.iv_delete);
            tv_name.setText(jsonNameDatePair.getName());
            tv_date.setText(TextUtils.isEmpty(jsonNameDatePair.getMonthYear()) ? "" : jsonNameDatePair.getMonthYear());
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete License");
                    tv_msg.setText("Do you want to delete it from license list?");
                    Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                    Button btn_no = customDialogView.findViewById(R.id.btn_no);
                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    btn_yes.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Deleted from license list", Toast.LENGTH_LONG).show();
                            jsonProfessionalProfilePersonal.getLicenses().remove(jsonNameDatePair);
                            ll_license.removeView(inflatedLayout);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();

                }
            });
            ll_license.addView(inflatedLayout);
        }

    }

    public void updateProfessionalInfo() {
        MerchantProfileModel merchantProfileModel = new MerchantProfileModel();
        merchantProfileModel.setMerchantProfessionalPresenter(this);
        if (jsonProfessionalProfilePersonal.getLicenses().size() == 0 && jsonProfessionalProfilePersonal.getEducation().size() == 0) {
            Toast.makeText(getActivity(), "Please add one record in education or License", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.show();
            jsonProfessionalProfilePersonal.setAboutMe(edt_about_me.getText().toString());
            jsonProfessionalProfilePersonal.setPracticeStart(edt_practice_start.getText().toString());
            merchantProfileModel.updateProfessionalProfile(UserUtils.getEmail(), UserUtils.getAuth(), jsonProfessionalProfilePersonal);
        }
    }


    @Override
    public void merchantProfessionalResponse(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        Log.v("JsonProfessionalProfile", jsonProfessionalProfilePersonal.toString());
        Toast.makeText(getActivity(), "Professional profile updated", Toast.LENGTH_LONG).show();
        updateUI(jsonProfessionalProfilePersonal);
        dismissProgress();
    }

    @Override
    public void merchantProfessionalError() {
        dismissProgress();
        Toast.makeText(getActivity(), "Professional profile updated failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(),eej);
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

    private void openDatePickerDialog(final TextView edt) {
        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(getActivity(), getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    edt.setText("");
                } else {
                    edt.setText(CommonHelper.SDF_DOB_FROM_UI.format(newDate.getTime()));
                }

            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
