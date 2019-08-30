package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;

import java.util.List;

public class WorkDoneAdapter extends BaseAdapter {
    private Context context;
    private List<ToothWorkDone> workDoneList;

    public void setWorkDoneList(List<ToothWorkDone> workDoneList) {
        this.workDoneList = workDoneList;
        notifyDataSetChanged();
    }

    public WorkDoneAdapter(Context context, List<ToothWorkDone> workDoneList) {
        this.context = context;
        this.workDoneList = workDoneList;
    }

    public int getCount() {
        return this.workDoneList.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        RecordHolder recordHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            recordHolder = new RecordHolder();
            view = layoutInflater.inflate(R.layout.list_item_workdone, viewGroup, false);
            recordHolder.tv_tooth_no = view.findViewById(R.id.tv_tooth_no);
            recordHolder.tv_procedure = view.findViewById(R.id.tv_procedure);
            recordHolder.tv_summary = view.findViewById(R.id.tv_summary);
            recordHolder.tv_created_date = view.findViewById(R.id.tv_created_date);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        ToothWorkDone toothWorkDone = workDoneList.get(position);
        recordHolder.tv_tooth_no.setText(toothWorkDone.getToothNumber());
        String ps = "<b>" + "Procedure: " + "</b> " + toothWorkDone.getProcedure();
        recordHolder.tv_procedure.setText(Html.fromHtml(ps));
        String ts = "<b>" + "Summary: " + "</b> " + toothWorkDone.getSummary();
        recordHolder.tv_summary.setText(Html.fromHtml(ts));
        recordHolder.tv_created_date.setText(toothWorkDone.getCreatedDate());

        return view;
    }

    static class RecordHolder {
        TextView tv_tooth_no;
        TextView tv_summary;
        TextView tv_procedure;
        TextView tv_created_date;

        RecordHolder() {
        }
    }
}

