package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.model.ReceiptInfoApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.customviews.MeterView;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordPresenter;
import com.noqapp.android.merchant.views.interfaces.ReceiptInfoPresenter;
import com.noqapp.android.merchant.views.pojos.Receipt;
import com.noqapp.android.merchant.views.utils.CompoundCheckChangedListener;
import com.noqapp.android.merchant.views.utils.DescreteProgressChangeListner;
import com.noqapp.android.merchant.views.utils.PdfSkeletonGenerator;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class PhysicalActivity extends BaseActivity implements
        MedicalRecordPresenter, JsonMedicalRecordPresenter,
        MeterView.MeterViewValueChanged, ReceiptInfoPresenter {
    private MeterView mv_weight1, mv_weight2, mv_pulse, mv_temperature1, mv_temperature2, mv_oxygen;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_oxygen, tv_bp_high, tv_bp_low, tv_followup, tv_rr, tv_height;
    private DiscreteSeekBar dsb_bp_low, dsb_bp_high, dsb_rr, dsb_height;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private JsonQueuedPerson jsonQueuedPerson;
    protected boolean isDialog = false;
    private SwitchCompat sc_enable_pulse, sc_enable_temp, sc_enable_weight, sc_enable_oxygen, sc_enable_bp, sc_enable_rr, sc_enable_height;
    private SegmentedControl sc_follow_up;
    private ArrayList<String> follow_up_data = new ArrayList<>();
    private String followup;
    private JsonMedicalRecord jsonMedicalRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDialog) {
            setScreenOrientation();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.60);
            int height = (int) (metrics.heightPixels * 0.70);
            getWindow().setLayout(screenWidth, height);
        }
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        initActionsViews(false);
        actionbarBack.setBackgroundResource(R.drawable.cross);
        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setScrollBarFadeDuration(0);
        scroll_view.setScrollbarFadingEnabled(false);
        setProgressMessage("Loading Patient data...");
        tv_toolbar_title.setText(getString(R.string.screen_physical));
        TextView tv_title = findViewById(R.id.tv_title);
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        final String codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        TextView tv_hospital_schedule = findViewById(R.id.tv_hospital_schedule);
        tv_hospital_schedule.setOnClickListener(v -> {
            if (null == jsonQueuedPerson || null == jsonMedicalRecord) {
                new CustomToast().showToast(PhysicalActivity.this, "Please wait while patient data is loading...");
            } else {
                Intent intent = new Intent(PhysicalActivity.this, HospitalVisitScheduleActivity.class);
                intent.putExtra(IBConstant.KEY_CODE_QR, codeQR);
                intent.putExtra("data", jsonQueuedPerson);
                intent.putExtra("jsonMedicalRecord", jsonMedicalRecord);
                startActivity(intent);
                finish();
            }

        });

        tv_title.setText(jsonQueuedPerson.getCustomerName());
        mv_weight1 = findViewById(R.id.mv_weight1);
        mv_weight2 = findViewById(R.id.mv_weight2);
        mv_pulse = findViewById(R.id.mv_pulse);
        mv_temperature2 = findViewById(R.id.mv_temperature2);
        mv_temperature1 = findViewById(R.id.mv_temperature1);
        mv_oxygen = findViewById(R.id.mv_oxygen);


        tv_followup = findViewById(R.id.tv_followup);
        tv_weight = findViewById(R.id.tv_weight);
        tv_pulse = findViewById(R.id.tv_pulse);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_oxygen = findViewById(R.id.tv_oxygen);
        tv_bp_high = findViewById(R.id.tv_bp_high);
        tv_bp_low = findViewById(R.id.tv_bp_low);
        tv_rr = findViewById(R.id.tv_rr);
        tv_height = findViewById(R.id.tv_height);

        sc_enable_pulse = findViewById(R.id.sc_enable_pulse);
        sc_enable_temp = findViewById(R.id.sc_enable_temp);
        sc_enable_weight = findViewById(R.id.sc_enable_weight);
        sc_enable_oxygen = findViewById(R.id.sc_enable_oxygen);
        sc_enable_bp = findViewById(R.id.sc_enable_bp);
        sc_enable_rr = findViewById(R.id.sc_enable_rr);
        sc_enable_height = findViewById(R.id.sc_enable_height);

        sc_enable_pulse.setChecked(false);
        sc_enable_temp.setChecked(false);
        sc_enable_weight.setChecked(false);
        sc_enable_oxygen.setChecked(false);
        sc_enable_bp.setChecked(false);
        sc_enable_rr.setChecked(false);
        sc_enable_height.setChecked(false);

        sc_follow_up = findViewById(R.id.sc_follow_up);

        follow_up_data.add("1");
        follow_up_data.add("2");
        follow_up_data.add("3");
        follow_up_data.add("4");
        follow_up_data.add("5");
        follow_up_data.add("6");
        follow_up_data.add("7");
        follow_up_data.add("10");
        follow_up_data.add("15");
        follow_up_data.add("30");
        follow_up_data.add("45");
        follow_up_data.add("60");
        follow_up_data.add("90");
        follow_up_data.add("180");
        sc_follow_up.addSegments(follow_up_data);

        sc_follow_up.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    followup = follow_up_data.get(segmentViewHolder.getAbsolutePosition());
                    tv_followup.setText("in " + followup + " days");
                }
            }
        });

        final Button ll_pulse_disable = findViewById(R.id.ll_pulse_disable);
        final Button ll_temp_disable = findViewById(R.id.ll_temp_disable);
        final Button ll_weight_disable = findViewById(R.id.ll_weight_disable);
        final Button ll_oxygen_disable = findViewById(R.id.ll_oxygen_disable);
        final Button ll_bp_disable = findViewById(R.id.ll_bp_disable);
        final Button ll_rr_disable = findViewById(R.id.ll_rr_disable);
        final Button ll_height_disable = findViewById(R.id.ll_height_disable);

        sc_enable_rr.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_rr_disable));
        sc_enable_height.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_height_disable));
        sc_enable_pulse.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_pulse_disable));
        sc_enable_temp.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_temp_disable));
        sc_enable_oxygen.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_oxygen_disable));
        sc_enable_weight.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_weight_disable));
        sc_enable_bp.setOnCheckedChangeListener(new CompoundCheckChangedListener(ll_bp_disable));

        mv_pulse.setMeterViewValueChanged(this);
        mv_temperature2.setMeterViewValueChanged(this);
        mv_temperature1.setMeterViewValueChanged(this);
        mv_weight1.setMeterViewValueChanged(this);
        mv_weight2.setMeterViewValueChanged(this);
        mv_oxygen.setMeterViewValueChanged(this);


        meterViewValueChanged(mv_pulse);
        meterViewValueChanged(mv_weight1);
        meterViewValueChanged(mv_temperature1);
        meterViewValueChanged(mv_oxygen);

        dsb_bp_low = findViewById(R.id.dsb_bp_low);
        dsb_bp_high = findViewById(R.id.dsb_bp_high);
        dsb_rr = findViewById(R.id.dsb_rr);
        dsb_height = findViewById(R.id.dsb_height);
        dsb_height.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_height, tv_height, "Height: "));
        dsb_bp_low.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_bp_low, tv_bp_low, "Diastolic: "));
        dsb_bp_high.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_bp_high, tv_bp_high, "Systolic: "));
        dsb_rr.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_rr, tv_rr, "Respiratory: "));
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(v -> {
            setProgressMessage("Updating Patient data...");
            showProgress();
            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
            jsonMedicalRecord.setCodeQR(codeQR);
            jsonMedicalRecord.setQueueUserId(jsonQueuedPerson.getQueueUserId());
            JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical();

            if (sc_enable_pulse.isChecked()) {
                jsonMedicalPhysical.setPulse(mv_pulse.getValueAsString());
            } else {
                jsonMedicalPhysical.setPulse(null);
            }
            if (sc_enable_bp.isChecked()) {
                jsonMedicalPhysical.setBloodPressure(new String[]{String.valueOf(dsb_bp_high.getProgress()), String.valueOf(dsb_bp_low.getProgress())});
            } else {
                jsonMedicalPhysical.setBloodPressure(null);
            }
            if (sc_enable_weight.isChecked()) {
                jsonMedicalPhysical.setWeight(mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString());
            } else {
                jsonMedicalPhysical.setWeight(null);
            }
            if (sc_enable_temp.isChecked()) {
                jsonMedicalPhysical.setTemperature(mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
            } else {
                jsonMedicalPhysical.setTemperature(null);
            }
            if (sc_enable_oxygen.isChecked()) {
                jsonMedicalPhysical.setOxygen(mv_oxygen.getValueAsString());
            } else {
                jsonMedicalPhysical.setOxygen(null);
            }
            if (sc_enable_rr.isChecked()) {
                jsonMedicalPhysical.setRespiratory(String.valueOf(dsb_rr.getProgress()));
            } else {
                jsonMedicalPhysical.setRespiratory(null);
            }
            if (sc_enable_height.isChecked()) {
                jsonMedicalPhysical.setHeight(String.valueOf(dsb_height.getProgress()));
            } else {
                jsonMedicalPhysical.setHeight(null);
            }
            if (null != jsonMedicalPhysical.getPulse()
                    || null != jsonMedicalPhysical.getBloodPressure()
                    || null != jsonMedicalPhysical.getRespiratory()
                    || null != jsonMedicalPhysical.getHeight()
                    || null != jsonMedicalPhysical.getWeight()
                    || null != jsonMedicalPhysical.getTemperature()
                    || null != jsonMedicalPhysical.getOxygen()) {
                jsonMedicalPhysical.setPhysicalFilled(true);
            }
            jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
            if (!TextUtils.isEmpty(followup)) {
                jsonMedicalRecord.setFollowUpInDays(followup);
            }
            medicalHistoryApiCalls.update(
                    AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(),
                    jsonMedicalRecord);
        });
        if (new NetworkUtil(this).isOnline()) {
            showProgress();
            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setCodeQR(codeQR);
            medicalHistoryApiCalls.setJsonMedicalRecordPresenter(this);
            medicalHistoryApiCalls.retrieveMedicalRecord(AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(), jsonMedicalRecord);

        } else {
            ShowAlertInformation.showNetworkDialog(PhysicalActivity.this);
        }
        ReceiptInfoApiCalls receiptInfoApiCalls = new ReceiptInfoApiCalls();
        receiptInfoApiCalls.setReceiptInfoPresenter(this);
        PermissionHelper permissionHelper = new PermissionHelper(this);
        Button btn_print_pdf = findViewById(R.id.btn_print_pdf);
        btn_print_pdf.setOnClickListener(v -> {
            if (permissionHelper.isStoragePermissionAllowed()) {
                if (TextUtils.isEmpty(jsonQueuedPerson.getTransactionId())) {
                    ShowCustomDialog showDialog = new ShowCustomDialog(PhysicalActivity.this, false);
                    showDialog.displayDialog("Alert", "Transaction Id is empty. Receipt can't be generated");
                } else {
                    showProgress();
                    setProgressMessage("Fetching receipt info...");
                    Receipt receipt = new Receipt();
                    receipt.setCodeQR(codeQR);
                    receipt.setQueueUserId(jsonQueuedPerson.getQueueUserId());
                    receipt.setTransactionId(jsonQueuedPerson.getTransactionId());
                    receiptInfoApiCalls.detail(AppInitialize.getDeviceID(),
                            AppInitialize.getEmail(), AppInitialize.getAuth(), receipt);
                }
            } else {
                permissionHelper.requestStoragePermission();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "Medical History updated successfully");
            this.finish();
        } else {
            new CustomToast().showToast(this, "Failed to update");
        }
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        new CustomToast().showToast(this, "Failed to update");
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        this.jsonMedicalRecord = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.i("data", jsonMedicalRecord.toString());
            try {
                if (!TextUtils.isEmpty(jsonMedicalRecord.getFollowUpInDays())) {
                    int index = follow_up_data.indexOf(jsonMedicalRecord.getFollowUpInDays());
                    if (-1 != index) {
                        sc_follow_up.setSelectedSegment(index);
                    }
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getOxygen()) {
                    mv_oxygen.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getOxygen()));
                    sc_enable_oxygen.setChecked(true);
                } else {
                    sc_enable_oxygen.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                    mv_pulse.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getPulse()));
                    sc_enable_pulse.setChecked(true);
                } else {
                    sc_enable_pulse.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    dsb_bp_high.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0]));
                    dsb_bp_low.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]));
                    sc_enable_bp.setChecked(true);
                } else {
                    sc_enable_bp.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getHeight()) {
                    dsb_height.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getHeight()));
                    sc_enable_height.setChecked(true);
                } else {
                    sc_enable_height.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getRespiratory()) {
                    dsb_rr.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getRespiratory()));
                    sc_enable_rr.setChecked(true);
                } else {
                    sc_enable_rr.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    if (jsonMedicalRecord.getMedicalPhysical().getWeight().contains(".")) {
                        String[] temp = jsonMedicalRecord.getMedicalPhysical().getWeight().split("\\.");
                        mv_weight1.setValue(Integer.parseInt(temp[0]));
                        mv_weight2.setValue(Integer.parseInt(temp[1]));
                        sc_enable_weight.setChecked(true);
                    } else {
                        sc_enable_weight.setChecked(false);
                    }
                } else {
                    sc_enable_weight.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical() && null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    if (jsonMedicalRecord.getMedicalPhysical().getTemperature().contains(".")) {
                        String[] temp = jsonMedicalRecord.getMedicalPhysical().getTemperature().split("\\.");
                        mv_temperature1.setValue(Integer.parseInt(temp[0]));
                        mv_temperature2.setValue(Integer.parseInt(temp[1]));
                        sc_enable_temp.setChecked(true);
                    } else {
                        sc_enable_temp.setChecked(false);
                    }
                } else {
                    sc_enable_temp.setChecked(false);
                }
                meterViewValueChanged(mv_pulse);
                meterViewValueChanged(mv_weight1);
                meterViewValueChanged(mv_temperature1);
                meterViewValueChanged(mv_oxygen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dismissProgress();
    }

    @Override
    public void meterViewValueChanged(View v) {
        switch (v.getId()) {
            case R.id.mv_pulse:
                tv_pulse.setText("Pulse: " + mv_pulse.getValueAsString());
                break;
            case R.id.mv_weight1:
            case R.id.mv_weight2:
                tv_weight.setText("Weight: " + mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString());
                break;
            case R.id.mv_temperature1:
            case R.id.mv_temperature2:
                tv_temperature.setText("Temp: " + mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
                break;
            case R.id.mv_oxygen:
                tv_oxygen.setText("Oxygen: " + mv_oxygen.getValueAsString());
                break;

            default:
                break;
        }
    }

    @Override
    public void receiptInfoResponse(Receipt receipt) {
        try {
            Log.e("Data", receipt.toString());
            PdfSkeletonGenerator pdfGenerator = new PdfSkeletonGenerator(PhysicalActivity.this);
            pdfGenerator.createPdf(receipt, jsonMedicalRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismissProgress();
    }
}
