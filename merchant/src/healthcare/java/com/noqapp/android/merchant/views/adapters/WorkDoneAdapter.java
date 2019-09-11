package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;

import java.util.List;

public class WorkDoneAdapter extends BaseAdapter {
    private Context context;
    private List<ToothWorkDone> workDoneList;
    private final OnItemClickListener listener;
    private boolean isDentalTreatment = false;

    public interface OnItemClickListener {
        void removeWorkDone(ToothWorkDone item, int pos);
    }

    public void setWorkDoneList(List<ToothWorkDone> workDoneList) {
        this.workDoneList = workDoneList;
        notifyDataSetChanged();
    }

    public WorkDoneAdapter(Context context, List<ToothWorkDone> workDoneList) {
        this.context = context;
        this.workDoneList = workDoneList;
        listener = null;
    }

    public WorkDoneAdapter(Context context, List<ToothWorkDone> workDoneList, boolean isDentalTreatment) {
        this.context = context;
        this.workDoneList = workDoneList;
        this.isDentalTreatment = isDentalTreatment;
        listener = null;
    }

    public WorkDoneAdapter(Context context, List<ToothWorkDone> workDoneList, OnItemClickListener listener) {
        this.context = context;
        this.workDoneList = workDoneList;
        this.listener = listener;
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
            recordHolder.tv_unit = view.findViewById(R.id.tv_unit);
            recordHolder.tv_status = view.findViewById(R.id.tv_status);
            recordHolder.tv_period = view.findViewById(R.id.tv_period);
            recordHolder.tv_summary = view.findViewById(R.id.tv_summary);
            recordHolder.iv_delete = view.findViewById(R.id.iv_delete);
            recordHolder.tv_created_date = view.findViewById(R.id.tv_created_date);
            recordHolder.ll_ups = view.findViewById(R.id.ll_ups);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        ToothWorkDone toothWorkDone = workDoneList.get(position);
        recordHolder.tv_tooth_no.setText(toothWorkDone.getToothNumber());
        String ps = "<b>" + "Procedure: " + "</b> " + toothWorkDone.getProcedure();
        recordHolder.tv_procedure.setText(Html.fromHtml(ps));


        recordHolder.iv_delete.setVisibility(null == listener ? View.GONE : View.VISIBLE);
        if (isDentalTreatment) {
            recordHolder.ll_ups.setVisibility(View.GONE);
            recordHolder.tv_summary.setVisibility(View.VISIBLE);
            recordHolder.tv_created_date.setVisibility(View.GONE);
        } else {
            recordHolder.ll_ups.setVisibility(View.VISIBLE);
            recordHolder.tv_summary.setVisibility(View.VISIBLE);
            String ts = "<b>" + "Summary: " + "</b> " + toothWorkDone.getSummary();
            recordHolder.tv_summary.setText(Html.fromHtml(ts));
            recordHolder.tv_created_date.setText(toothWorkDone.getCreatedDate());
            recordHolder.tv_created_date.setVisibility(TextUtils.isEmpty(toothWorkDone.getCreatedDate()) ? View.GONE : View.VISIBLE);
            String status = "<b>" + "Status: " + "</b> " + toothWorkDone.getTeethStatus();
            recordHolder.tv_status.setText(Html.fromHtml(status));
            String unit = "<b>" + "Unit: " + "</b> " + toothWorkDone.getTeethUnit();
            recordHolder.tv_unit.setText(Html.fromHtml(unit));
            String period = "<b>" + "Period: " + "</b> " + toothWorkDone.getTeethPeriod();
            recordHolder.tv_period.setText(Html.fromHtml(period));

            recordHolder.iv_delete.setOnClickListener(v -> {
                if (null != listener) {
                    ShowCustomDialog showDialog = new ShowCustomDialog(context);
                    showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                        @Override
                        public void btnPositiveClick() {
                            listener.removeWorkDone(toothWorkDone, position);
                        }

                        @Override
                        public void btnNegativeClick() {
                            //Do nothing
                        }
                    });
                    showDialog.displayDialog("Delete work done", "Do you want to delete it from work done list?");
                }
            });
        }
        return view;
    }

    static class RecordHolder {
        TextView tv_tooth_no;
        TextView tv_summary;
        TextView tv_procedure;
        TextView tv_status;
        TextView tv_unit;
        TextView tv_period;
        TextView tv_created_date;
        LinearLayout ll_ups;
        ImageView iv_delete;

        RecordHolder() {
        }
    }
}

