package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.views.cutomviews.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryListAdapter1  extends RecyclerView.Adapter<CategoryListAdapter1.MyViewHolder> {
    private final Context context;
    private List<BizStoreElastic> dataSet;
    //private List<JsonQueue> dataSet;
    public interface OnItemClickListener {
        void onCategoryItemClick(BizStoreElastic item, View view, int pos);
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_detail;
        private TextView tv_store_rating;
        private TextView tv_category;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public CategoryListAdapter1(List<BizStoreElastic> jsonQueues, Context context, OnItemClickListener listener) {
        this.dataSet = jsonQueues;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_view_all, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final BizStoreElastic jsonQueue = dataSet.get(listPosition);
        holder.tv_name.setText(dataSet.get(listPosition).getDisplayName());
        holder.tv_detail.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getPhone()));
        holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(dataSet.get(listPosition).getRating())));
        holder.tv_address.setText(jsonQueue.getAddress());
//        if (jsonQueue.isDayClosed()) {
//            holder.tv_address.setText(context.getString(R.string.store_closed));
//        } else {
//            holder.tv_address.setText(
//                    context.getString(R.string.store_hour)
//                            + " "
//                            + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
//                            + " - "
//                            + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
//        }
//
//
//        int timeIn24HourFormat = AppUtilities.getTimeIn24HourFormat();
//        if (!jsonQueue.isDayClosed()) {
//            // Before Token Available Time
//            if (timeIn24HourFormat < jsonQueue.getTokenAvailableFrom()) {
//                if (jsonQueue.getBusinessType() != null) {
//                    switch (jsonQueue.getBusinessType()) {
//                        case DO:
//                            holder.tv_category.setText("Closed Now. Appointment booking starts at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
//                            break;
//                        default:
//                            holder.tv_category.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
//                            break;
//                    }
//                } else {
//                    holder.tv_category.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom()));
//                }
//            }
//
//            // Between Token Available and Start Hour
//            if (timeIn24HourFormat >= jsonQueue.getTokenAvailableFrom() && timeIn24HourFormat < jsonQueue.getStartHour()) {
//                if (jsonQueue.getBusinessType() != null) {
//                    switch (jsonQueue.getBusinessType()) {
//                        case DO:
//                            holder.tv_category.setText("Now accepting appointments for today");
//                            break;
//                        default:
//                            holder.tv_category.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()));
//                            break;
//                    }
//                } else {
//                    holder.tv_category.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()));
//                }
//                holder.tv_category.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
//            }
//
//            // After Start Hour and Before Token Not Available From
//            if (timeIn24HourFormat >= jsonQueue.getStartHour() && timeIn24HourFormat < jsonQueue.getTokenNotAvailableFrom()) {
//                if (jsonQueue.getBusinessType() != null) {
//                    switch (jsonQueue.getBusinessType()) {
//                        case DO:
//                            holder.tv_category.setText("Open now. Book your appointment for today.");
//                            break;
//                        default:
//                            holder.tv_category.setText("Open now. Join the queue.");
//                            break;
//                    }
//                } else {
//                    holder.tv_category.setText("Open Now. Join the queue.");
//                }
//                holder.tv_category.setTextColor(context.getResources().getColor(R.color.open_queue));
//            }
//
//            // When between Token Not Available From and End Hour
//            if (timeIn24HourFormat >= jsonQueue.getTokenNotAvailableFrom() && timeIn24HourFormat < jsonQueue.getEndHour()) {
//                holder.tv_category.setText("Closing soon");
//                holder.tv_category.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
//            }
//
//            // When after End Hour
//            if (timeIn24HourFormat >= jsonQueue.getEndHour()) {
//                holder.tv_category.setText("Closed now");
//                holder.tv_category.setTextColor(context.getResources().getColor(R.color.color_btn_select));
//            }
//
//            holder.tv_category.setTypeface(null, Typeface.BOLD);
//        } else {
//            //TODO(hth) Show when will this be open next. For now hide it.
//            //holder.tv_category.setText("Show some smart message");
//            //holder.tv_category.setTextColor(Color.DKGRAY);
//            //holder.tv_category.setTypeface(null, Typeface.BOLD);
//            holder.tv_category.setVisibility(View.GONE);
//        }
        Picasso.with(context)
                .load(dataSet.get(listPosition).getDisplayImage())
                .transform(new RoundedTransformation(10, 4))
                .into(holder.iv_main);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryItemClick(dataSet.get(listPosition), v, listPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        





//        recordHolder.tv_mobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), jsonQueue.getStorePhone());
//            }
//        });

//
//        recordHolder.cardview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle b = new Bundle();
//                b.putString(NoQueueBaseFragment.KEY_CODE_QR, "");
//                b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, true);
//                b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, true);
//                b.putSerializable("object", jsonQueue);
//                JoinFragment jf = new JoinFragment();
//                jf.setArguments(b);
//                NoQueueBaseFragment.replaceFragmentWithBackStack(
//                        LaunchActivity.getLaunchActivity(),
//                        R.id.frame_layout,
//                        jf,
//                        JoinFragment.class.getName(),
//                         LaunchActivity.tabHome);
//            }
//        });


