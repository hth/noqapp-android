package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ToothInfo;

import java.util.List;

public class ToothAdapter extends RecyclerView.Adapter {
    private List<ToothInfo> dataSet;
    private Context context;
    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;

    public List<ToothInfo> getDataSet() {
        return dataSet;
    }

    public ToothAdapter(List<ToothInfo> data, Context context) {
        this.dataSet = data;
        this.context = context;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        ToothInfo item = dataSet.get(listPosition);
        holder.tv_count.setText(String.valueOf(item.getToothNumber()));
        holder.iv_top.setBackground(ContextCompat.getDrawable(context, item.getToothTopView()));
        holder.iv_front.setBackground(ContextCompat.getDrawable(context, item.getToothFrontView()));
        holder.iv_front.setOnClickListener(v -> {
                onToothFrontViewSelected(listPosition,item);
        });
        holder.iv_top.setOnClickListener(v -> {
                onToothTopViewSelected(listPosition,item);
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

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_count = itemView.findViewById(R.id.tv_count);
            this.iv_front = itemView.findViewById(R.id.iv_front);
            this.iv_top = itemView.findViewById(R.id.iv_top);
        }
    }

    private void onToothTopViewSelected(int pos,ToothInfo toothInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        builder.setView(dialogView);
        final AlertDialog mAlertDialog = builder.create();
        RecyclerView rcv_tooth = dialogView.findViewById(R.id.rcv_tooth_option);
        rcv_tooth.setLayoutManager(new GridLayoutManager(context, 4));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        ToothOptionAdapter toothAdapter = new ToothOptionAdapter(toothInfo.getTopViewDrawables(), item -> {
            mAlertDialog.dismiss();
            toothInfo.setToothTopView(item);
            dataSet.set(pos,toothInfo);
            notifyDataSetChanged();
        },context);
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

    private void onToothFrontViewSelected(int pos,ToothInfo toothInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(null);
        View dialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        builder.setView(dialogView);
        final AlertDialog mAlertDialog = builder.create();
        RecyclerView rcv_tooth = dialogView.findViewById(R.id.rcv_tooth_option);
        rcv_tooth.setLayoutManager(new GridLayoutManager(context, 4));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        ToothOptionAdapter toothAdapter = new ToothOptionAdapter(toothInfo.getFrontViewDrawables(), item -> {
            mAlertDialog.dismiss();
            toothInfo.setToothFrontView(item);
            dataSet.set(pos,toothInfo);
            notifyDataSetChanged();
        },context);
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
}
