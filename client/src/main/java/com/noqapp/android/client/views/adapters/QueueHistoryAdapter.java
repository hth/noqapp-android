package com.noqapp.android.client.views.adapters;


import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueueHistorical;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class QueueHistoryAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final OnItemClickListener listener;
    private ArrayList<JsonQueueHistorical> dataSet;

    public QueueHistoryAdapter(ArrayList<JsonQueueHistorical> data, Context context, OnItemClickListener listener) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_queue_history, parent, false);
        RecyclerView.ViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final JsonQueueHistorical jsonQueueHistorical = dataSet.get(listPosition);
        holder.tv_name.setText(jsonQueueHistorical.getDisplayName());
        holder.tv_address.setText(AppUtilities.getStoreAddress(jsonQueueHistorical.getTown(), jsonQueueHistorical.getArea()));
        // holder.tv_store_rating.setText(String.valueOf(jsonQueueHistorical.getRatingCount()));
        holder.tv_business_name.setText(jsonQueueHistorical.getBusinessName());
        try {
            holder.tv_queue_join_date.setText(CommonHelper.SDF_DD_MMM_YY_HH_MM_A.
                    format(new SimpleDateFormat(Constants.ISO8601_FMT, Locale.getDefault()).parse(jsonQueueHistorical.getCreated())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tv_queue_status.setText(jsonQueueHistorical.getQueueUserState().getDescription());
        holder.tv_business_category.setText(jsonQueueHistorical.getBizCategoryName());
        holder.btn_rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStoreItemClick(jsonQueueHistorical);
            }
        });
        holder.iv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to detail screen
                Toast.makeText(context,"View Detail screen",Toast.LENGTH_LONG).show();
            }
        });

        switch (jsonQueueHistorical.getQueueUserState()) {
            case A:
            case N:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.colorMobile));
                break;
            case Q:
                holder.tv_queue_status.setText(QueueUserStateEnum.Q.getDescription() + " (Expired)");
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
                break;
            case S:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            default:
                holder.tv_queue_status.setTextColor(ContextCompat.getColor(context, R.color.text_header_color));
        }

//        if(0 == jsonQueueHistorical.getRatingCount()) {
//            holder.tv_add_review.setVisibility(View.VISIBLE);
//            if (jsonQueueHistorical.getQueueUserState() != QueueUserStateEnum.S) {
//                holder.tv_add_review.setVisibility(View.GONE);
//            }
//            holder.tv_store_rating.setVisibility(View.GONE);
//        }else{
//            holder.tv_add_review.setVisibility(View.GONE);
//            holder.tv_store_rating.setVisibility(View.VISIBLE);
//        }
//        holder.tv_add_review.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
//                jsonTokenAndQueue.setQueueUserId(jsonQueueHistorical.getQueueUserId());
//                jsonTokenAndQueue.setDisplayName(jsonQueueHistorical.getDisplayName());
//                jsonTokenAndQueue.setStoreAddress(jsonQueueHistorical.getStoreAddress());
//                jsonTokenAndQueue.setBusinessType(jsonQueueHistorical.getBusinessType());
//                jsonTokenAndQueue.setCodeQR(jsonQueueHistorical.getCodeQR());
//                jsonTokenAndQueue.setToken(jsonQueueHistorical.getTokenNumber());
//                Intent in = new Intent(context, ReviewActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("object", jsonTokenAndQueue);
//                in.putExtras(bundle);
//                context.startActivity(in);
//            }
//        });
//        holder.tv_support.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Feedback feedback = new Feedback();
//                feedback.setMessageOrigin(MessageOriginEnum.Q);
//                feedback.setCodeQR(jsonQueueHistorical.getCodeQR());
//                feedback.setToken(jsonQueueHistorical.getTokenNumber());
//                Intent in = new Intent(context, ContactUsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("object", feedback);
//                in.putExtras(bundle);
//                context.startActivity(in);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onStoreItemClick(JsonQueueHistorical item);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_support;
        private TextView tv_address;
        private TextView tv_queue_join_date;
        private TextView tv_queue_status;
        // private TextView tv_store_rating;
        // private TextView tv_add_review;
        private TextView tv_business_name;
        private TextView tv_business_category;
        private Button btn_rejoin;
        private ImageView iv_details;
        private CardView card_view;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
        //    this.tv_support = itemView.findViewById(R.id.tv_support);
            this.tv_address = itemView.findViewById(R.id.tv_address);
            this.tv_queue_join_date = itemView.findViewById(R.id.tv_queue_join_date);
            this.tv_queue_status = itemView.findViewById(R.id.tv_queue_status);
            // this.tv_store_rating = itemView.findViewById(R.id.tv_store_rating);
            // this.tv_add_review = itemView.findViewById(R.id.tv_add_review);
            this.tv_business_name = itemView.findViewById(R.id.tv_business_name);
            this.tv_business_category = itemView.findViewById(R.id.tv_business_category);
            this.btn_rejoin = itemView.findViewById(R.id.btn_rejoin);
            this.iv_details = itemView.findViewById(R.id.iv_details);
            this.card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
