package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.customviews.MeterView;
import com.noqapp.android.merchant.views.utils.DescreteProgressChangeListner;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

public class PrimaryCheckupFragment extends Fragment implements MeterView.MeterViewValueChanged {
    private MeterView mv_weight1, mv_weight2, mv_pulse, mv_temperature1, mv_temperature2, mv_oxygen;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_oxygen, tv_bp_high, tv_bp_low, tv_rr, tv_height;
    private DiscreteSeekBar dsb_bp_low, dsb_bp_high, dsb_rr, dsb_height;
    private SwitchCompat sc_enable_pulse, sc_enable_temp, sc_enable_weight, sc_enable_oxygen, sc_enable_bp, sc_enable_rr, sc_enable_height;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_primary_checkup, container, false);

        mv_weight1 = v.findViewById(R.id.mv_weight1);
        mv_weight2 = v.findViewById(R.id.mv_weight2);
        mv_pulse = v.findViewById(R.id.mv_pulse);
        mv_temperature2 = v.findViewById(R.id.mv_temperature2);
        mv_temperature1 = v.findViewById(R.id.mv_temperature1);
        mv_oxygen = v.findViewById(R.id.mv_oxygen);

        tv_weight = v.findViewById(R.id.tv_weight);
        tv_pulse = v.findViewById(R.id.tv_pulse);
        tv_temperature = v.findViewById(R.id.tv_temperature);
        tv_oxygen = v.findViewById(R.id.tv_oxygen);
        tv_bp_high = v.findViewById(R.id.tv_bp_high);
        tv_bp_low = v.findViewById(R.id.tv_bp_low);
        tv_rr = v.findViewById(R.id.tv_rr);
        tv_height = v.findViewById(R.id.tv_height);

        sc_enable_pulse = v.findViewById(R.id.sc_enable_pulse);
        sc_enable_temp = v.findViewById(R.id.sc_enable_temp);
        sc_enable_weight = v.findViewById(R.id.sc_enable_weight);
        sc_enable_oxygen = v.findViewById(R.id.sc_enable_oxygen);
        sc_enable_bp = v.findViewById(R.id.sc_enable_bp);
        sc_enable_rr = v.findViewById(R.id.sc_enable_rr);
        sc_enable_height = v.findViewById(R.id.sc_enable_height);

        sc_enable_pulse.setChecked(false);
        sc_enable_temp.setChecked(false);
        sc_enable_weight.setChecked(false);
        sc_enable_oxygen.setChecked(false);
        sc_enable_bp.setChecked(false);
        sc_enable_rr.setChecked(false);
        sc_enable_height.setChecked(false);

        final Button ll_pulse_disable = v.findViewById(R.id.ll_pulse_disable);
        final Button ll_temp_disable = v.findViewById(R.id.ll_temp_disable);
        final Button ll_weight_disable = v.findViewById(R.id.ll_weight_disable);
        final Button ll_oxygen_disable = v.findViewById(R.id.ll_oxygen_disable);
        final Button ll_bp_disable = v.findViewById(R.id.ll_bp_disable);
        final Button ll_rr_disable = v.findViewById(R.id.ll_rr_disable);
        final Button ll_height_disable = v.findViewById(R.id.ll_height_disable);

        sc_enable_rr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_rr_disable.setVisibility(View.GONE);
                } else {
                    ll_rr_disable.setVisibility(View.VISIBLE);
                }
            }
        });

        sc_enable_height.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_height_disable.setVisibility(View.GONE);
                } else {
                    ll_height_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_pulse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_pulse_disable.setVisibility(View.GONE);
                } else {
                    ll_pulse_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_temp_disable.setVisibility(View.GONE);
                } else {
                    ll_temp_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_oxygen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_oxygen_disable.setVisibility(View.GONE);
                } else {
                    ll_oxygen_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_weight_disable.setVisibility(View.GONE);
                } else {
                    ll_weight_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_bp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_bp_disable.setVisibility(View.GONE);
                } else {
                    ll_bp_disable.setVisibility(View.VISIBLE);
                }
            }
        });

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

        dsb_bp_low = v.findViewById(R.id.dsb_bp_low);
        dsb_bp_high = v.findViewById(R.id.dsb_bp_high);
        dsb_rr = v.findViewById(R.id.dsb_rr);
        dsb_height = v.findViewById(R.id.dsb_height);

        dsb_height.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_height,tv_height,"Height: "));
        dsb_bp_low.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_bp_low,tv_bp_low,"Diastolic: "));
        dsb_bp_high.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_bp_high,tv_bp_high,"Systolic: "));
        dsb_rr.setOnProgressChangeListener(new DescreteProgressChangeListner(dsb_rr,tv_rr,"Respiratory: "));


        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        updatePhysicalUI(jsonMedicalRecord);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void updatePhysicalUI(JsonMedicalRecord jsonMedicalRecord) {
        if (null != jsonMedicalRecord && null != jsonMedicalRecord.getMedicalPhysical()) {
            Log.e("Physical Data=", jsonMedicalRecord.getMedicalPhysical().toString());
            try {
                if (null != jsonMedicalRecord.getMedicalPhysical().getOxygen()) {
                    mv_oxygen.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getOxygen()));
                    sc_enable_oxygen.setChecked(true);
                } else {
                    sc_enable_oxygen.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                    mv_pulse.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getPulse()));
                    sc_enable_pulse.setChecked(true);
                } else {
                    sc_enable_pulse.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    dsb_bp_high.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0]));
                    dsb_bp_low.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]));
                    sc_enable_bp.setChecked(true);
                } else {
                    sc_enable_bp.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical().getHeight()) {
                    dsb_height.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getHeight()));
                    sc_enable_height.setChecked(true);
                } else {
                    sc_enable_height.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical().getRespiratory()) {
                    dsb_rr.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getRespiratory()));
                    sc_enable_rr.setChecked(true);
                } else {
                    sc_enable_rr.setChecked(false);
                }

                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
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
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
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
                Log.e("Failed on physical=", e.getLocalizedMessage(), e);
            }
        }
    }


    public void saveData() {
        if (sc_enable_pulse.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPulse(mv_pulse.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPulse(null);
        }

        if (sc_enable_bp.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setBloodPressure(new String[]{String.valueOf(dsb_bp_high.getProgress()), String.valueOf(dsb_bp_low.getProgress())});
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setBloodPressure(null);
        }

        if (sc_enable_rr.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setRespiratory(String.valueOf(dsb_rr.getProgress()));
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setRespiratory(null);
        }

        if (sc_enable_height.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHeight(String.valueOf(dsb_height.getProgress()));
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setHeight(null);
        }

        if (sc_enable_weight.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setWeight(mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setWeight(null);
        }

        if (sc_enable_temp.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setTemperature(mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setTemperature(null);
        }

        if (sc_enable_oxygen.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setOxygenLevel(mv_oxygen.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setOxygenLevel(null);
        }

        if (null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getPulse()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getBloodPressure()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getRespiratory()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getHeight()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getWeight()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getTemperature()
                || null != MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().getOxygenLevel()) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPhysicalFilled(true);
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setPhysicalFilled(false);
        }
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
}
