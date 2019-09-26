package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.pojos.CheckBoxObj;

import java.util.List;

public class RadioRecyclerAdapter extends RecyclerView.Adapter {

    private int lastCheckedPos = -1;
    private Context context;
    private List<CheckBoxObj> checkBoxObjList;
    private int parentPos;
    private UpdateGrid updateGrid;
    private int selectPos;

    interface UpdateGrid {
        void notifyView(int parentPos, int checkedPos);
    }

    public RadioRecyclerAdapter(Context context, List<CheckBoxObj> checkBoxObjList, int parentPos,
                                UpdateGrid updateGrid, int selectPos) {
        this.context = context;
        this.checkBoxObjList = checkBoxObjList;
        this.parentPos = parentPos;
        this.updateGrid = updateGrid;
        this.selectPos = selectPos;
        lastCheckedPos = selectPos;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_checkbox, parent, false);
        return new MyViewHolder(v);
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        JsonPreferredBusiness jpb = checkBoxObjList.get(position).getJsonPreferredBusiness();
        holder.checkBox.setChecked(false);
        String address = AppUtils.getStoreAddress(jpb.getTown(), jpb.getArea());
        String s = jpb.getDisplayName() + "\n" + address;
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, jpb.getDisplayName().length(), 0); // set size
        ss1.setSpan(new RelativeSizeSpan(0.8f), jpb.getDisplayName().length(), jpb.getDisplayName().length() + address.length(), 0);
        holder.checkBox.setText(ss1);
        if (selectPos != -1 && selectPos == position) {
            holder.checkBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_select_square));
            holder.checkBox.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        } else {
            holder.checkBox.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_unselect_square));
            holder.checkBox.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (lastCheckedPos == -1) {
                            lastCheckedPos = position;
                        } else if (lastCheckedPos > -1 && lastCheckedPos == position) {
                            lastCheckedPos = -1;
                        } else if (lastCheckedPos > -1 && lastCheckedPos != position) {
                            lastCheckedPos = position;
                        }
                        if (null != updateGrid) {
                            updateGrid.notifyView(parentPos, lastCheckedPos);
                        }

                    }
                }
        );

    }


    @Override
    public int getItemCount() {
        return checkBoxObjList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            this.setIsRecyclable(false);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
