package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.Map;

public class HospitalVisitScheduleListAdapter extends BaseAdapter {
    private Context context;
    private final OnItemClickListener listener;
    private Map<String, BooleanReplacementEnum> visitingFor;
    private String[] mKeys;
    private JsonHospitalVisitSchedule jsonHospitalVisitSchedule;

    public interface OnItemClickListener {
        void onImmuneItemClick();
    }

    public HospitalVisitScheduleListAdapter(
        Context context,
        Map<String, BooleanReplacementEnum> visitingFor,
        OnItemClickListener onItemClickListener,
        JsonHospitalVisitSchedule jsonHospitalVisitSchedule
    ) {
        this.context = context;
        this.visitingFor = visitingFor;
        this.listener = onItemClickListener;
        mKeys = visitingFor.keySet().toArray(new String[visitingFor.size()]);
        this.jsonHospitalVisitSchedule = jsonHospitalVisitSchedule;
    }

    public int getCount() {
        return this.visitingFor.size();
    }

    @Override
    public Object getItem(int position) {
        return visitingFor.get(mKeys[position]);
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.rcv_hvs_item, parent, false);
            recordHolder.tv_hvs_name = view.findViewById(R.id.tv_hvs_name);
            recordHolder.tv_hvs_visitedDate = view.findViewById(R.id.tv_hvs_visitedDate);
            recordHolder.tv_hvs_status = view.findViewById(R.id.tv_hvs_status);
            recordHolder.card_view = view.findViewById(R.id.card_view);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        BooleanReplacementEnum booleanReplacementEnum = (BooleanReplacementEnum) getItem(position);
        recordHolder.tv_hvs_name.setText(mKeys[position]);
//        String visitedDate = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI,jsonHospitalVisitSchedule.getVisitedDate());
        String expectedDate = CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonHospitalVisitSchedule.getExpectedDate());
        recordHolder.tv_hvs_visitedDate.setText(expectedDate);
        recordHolder.tv_hvs_status.setText(BooleanReplacementEnum.getDisplayDescription(booleanReplacementEnum));
        recordHolder.card_view.setCardBackgroundColor(Color.parseColor(booleanReplacementEnum.getColor()));
        recordHolder.card_view.setOnClickListener(v -> {
            if (null != listener) {
                listener.onImmuneItemClick();
            }
        });
        return view;
    }

    static class RecordHolder {
        TextView tv_hvs_name;
        TextView tv_hvs_visitedDate;
        TextView tv_hvs_status;
        CardView card_view;

        RecordHolder() {
        }
    }
}
