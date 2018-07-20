package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.AdapterCommunicate;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MedicalRecordFavouriteAdapter extends BaseAdapter {

    private Context context;
    private List<JsonMedicalMedicine> medicalRecordList;
    private AdapterCommunicate adapterCommunicate;

    public MedicalRecordFavouriteAdapter(Context context, List<JsonMedicalMedicine> medicalRecordList,AdapterCommunicate adapterCommunicate) {
        this.context = context;
        this.medicalRecordList = medicalRecordList;
        this.adapterCommunicate = adapterCommunicate;
    }

    public int getCount() {
        return this.medicalRecordList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int pos, View view, ViewGroup viewGroup) {
        final int position = pos;
        final RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.medical_item, null);
            recordHolder.tv_medicine_name = view.findViewById(R.id.tv_medicine_name);
            recordHolder.tv_medication = view.findViewById(R.id.tv_medication);
            recordHolder.tv_dose = view.findViewById(R.id.tv_dose);
            recordHolder.tv_frequency = view.findViewById(R.id.tv_frequency);
            recordHolder.tv_dose_timing = view.findViewById(R.id.tv_dose_timing);
            recordHolder.tv_course = view.findViewById(R.id.tv_course);
            recordHolder.iv_delete = view.findViewById(R.id.iv_delete);
            recordHolder.iv_favourite = view.findViewById(R.id.iv_favourite);
            recordHolder.cardview = view.findViewById(R.id.cardview);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }

        final JsonMedicalMedicine medicalRecord = medicalRecordList.get(position);
        recordHolder.tv_medication.setText(medicalRecord.getMedicationType());
        recordHolder.tv_dose.setText(medicalRecord.getStrength());
        recordHolder.tv_frequency.setText(medicalRecord.getDailyFrequency());
        recordHolder.tv_dose_timing.setText(medicalRecord.getMedicationWithFood());
        recordHolder.tv_course.setText(medicalRecord.getCourse());
        recordHolder.tv_medicine_name.setText(medicalRecord.getName());
        recordHolder.iv_delete.setBackgroundResource(R.drawable.add_medic);
        recordHolder.iv_favourite.setBackgroundResource(medicalRecord.isFavourite()?R.drawable.ic_favorite:R.drawable.ic_favorite_border);
        recordHolder.iv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medicalRecord.isFavourite()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    builder.setTitle(null);
                    View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
                    builder.setView(customDialogView);
                    final AlertDialog mAlertDialog = builder.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                    TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                    tvtitle.setText("Delete Favourite");
                    tv_msg.setText("Do you want to delete it from favroite list.");
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
                            Toast.makeText(context,"Deleted from Favourite",Toast.LENGTH_LONG).show();
                            medicalRecordList.remove(position);
                            notifyDataSetChanged();
                            adapterCommunicate.updateNonFavouriteList(medicalRecord,false);
                            LaunchActivity.getLaunchActivity().setFavouriteMedicines(medicalRecordList);
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                }else{
                    medicalRecord.setFavourite(true);
                    notifyDataSetChanged();
                }
            }
        });
        recordHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //medicalRecordList.remove(position);
                //notifyDataSetChanged();
                adapterCommunicate.updateNonFavouriteList(medicalRecord,true);
            }
        });
        return view;
    }

    static class RecordHolder {
        TextView tv_medicine_name;
        TextView tv_medication;
        TextView tv_frequency;
        TextView tv_dose_timing;
        TextView tv_course;
        TextView tv_dose;
        ImageView iv_delete;
        ImageView iv_favourite;
        CardView cardview;

        RecordHolder() {
        }
    }
}
