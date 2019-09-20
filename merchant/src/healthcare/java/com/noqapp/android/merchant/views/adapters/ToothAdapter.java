package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.pojos.ToothInfo;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;

import java.util.List;

public class ToothAdapter extends RecyclerView.Adapter {
    private List<ToothInfo> dataSet;
    private Context context;
    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;
    private final String SPLIT_SYMBOL = ":";
    private boolean isClickEnable = true;

    public List<ToothInfo> getDataSet() {
        return dataSet;
    }

    public ToothAdapter(List<ToothInfo> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    public ToothAdapter(List<ToothInfo> data, Context context, boolean isClickEnable) {
        this.dataSet = data;
        this.context = context;
        this.isClickEnable = isClickEnable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == LAYOUT_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_tooth_item_1, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_tooth_item_2, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        ToothInfo item = dataSet.get(position);
        holder.tv_count.setText(String.valueOf(item.getToothNumber()));
        holder.iv_top.setBackground(ContextCompat.getDrawable(context, item.getToothTopView().getDrawable()));
        holder.iv_front.setBackground(ContextCompat.getDrawable(context, item.getToothFrontView().getDrawable()));
        if (position == 7 || position == 23) {
            holder.ll_header.setBackgroundResource(R.drawable.vertical_line);
        } else {
             holder.ll_header.setBackgroundResource(0);
        }

        holder.iv_front.setOnClickListener(v -> {
            if (isClickEnable)
                onToothFrontViewSelected(position, item);
        });
        holder.iv_top.setOnClickListener(v -> {
            if (isClickEnable)
                onToothTopViewSelected(position, item);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 16)
            return LAYOUT_ONE;
        else
            return LAYOUT_TWO;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_count;
        private ImageView iv_front;
        private ImageView iv_top;
        private LinearLayout ll_header;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_count = itemView.findViewById(R.id.tv_count);
            this.iv_front = itemView.findViewById(R.id.iv_front);
            this.iv_top = itemView.findViewById(R.id.iv_top);
            this.ll_header = itemView.findViewById(R.id.ll_header);
        }
    }

    private void onToothTopViewSelected(int pos, ToothInfo toothInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        builder.setView(dialogView);
        final AlertDialog mAlertDialog = builder.create();
        RecyclerView rcv_tooth = dialogView.findViewById(R.id.rcv_tooth_option);
        rcv_tooth.setLayoutManager(new GridLayoutManager(context, LaunchActivity.isTablet ? 6 : 4));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        ToothOptionAdapter toothAdapter = new ToothOptionAdapter(toothInfo.getTopViewDrawables(), item -> {
            mAlertDialog.dismiss();
            toothInfo.setToothTopView(item);
            toothInfo.setUpdated(true);
            dataSet.set(pos, toothInfo);
            notifyDataSetChanged();
        }, context);
        rcv_tooth.setAdapter(toothAdapter);

        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        Button btn_yes = dialogView.findViewById(R.id.btn_yes);
        Button btn_no = dialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v1 -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v12 -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    private void onToothFrontViewSelected(int pos, ToothInfo toothInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        builder.setView(dialogView);
        final AlertDialog mAlertDialog = builder.create();
        RecyclerView rcv_tooth = dialogView.findViewById(R.id.rcv_tooth_option);
        rcv_tooth.setLayoutManager(new GridLayoutManager(context, LaunchActivity.isTablet ? 6 : 4));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        ToothOptionAdapter toothAdapter = new ToothOptionAdapter(toothInfo.getFrontViewDrawables(), item -> {
            mAlertDialog.dismiss();
            toothInfo.setToothFrontView(item);
            toothInfo.setUpdated(true);
            dataSet.set(pos, toothInfo);
            notifyDataSetChanged();
        }, context);
        rcv_tooth.setAdapter(toothAdapter);

        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        Button btn_yes = dialogView.findViewById(R.id.btn_yes);
        Button btn_no = dialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v1 -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v12 -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }


    public String getSelectedData() {
        String data = "";
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).isUpdated()) {
                data += dataSet.get(i).getToothNumber() + ":"
                        + dataSet.get(i).getToothFrontView().getDrawableLabel() + ":"
                        + dataSet.get(i).getToothTopView().getDrawableLabel() + ":"
                        + dataSet.get(i).getToothFrontView().getDrawable() + ":"
                        + dataSet.get(i).getToothTopView().getDrawable() + "|";
            }
        }
        if (data.endsWith("| "))
            data = data.substring(0, data.length() - 2);
        return data;
    }


    public void updateToothObj(String str) {
        try {
            String[] temp = str.split("\\|");
            if (temp.length > 0) {
                for (String act : temp) {
                    if (act.contains(SPLIT_SYMBOL)) {
                        String[] strArray = act.split(":");
                        String toothNumber = strArray[0].trim();
                        String toothFrontLabel = strArray[1];
                        String toothTopLabel = strArray[2];
                        String toothFrontDrawable = strArray[3];
                        String toothTopDrawable = strArray[4];
                        for (int j = 0; j < dataSet.size(); j++) {
                            ToothInfo tf = dataSet.get(j);
                            if (tf.getToothNumber() == Integer.parseInt(toothNumber)) {
                                tf.setToothTopView(new ToothProcedure(Integer.parseInt(toothTopDrawable), toothTopLabel));
                                tf.setToothFrontView(new ToothProcedure(Integer.parseInt(toothFrontDrawable), toothFrontLabel));
                                tf.setUpdated(true);
                                dataSet.set(j, tf);
                                break;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }
}

