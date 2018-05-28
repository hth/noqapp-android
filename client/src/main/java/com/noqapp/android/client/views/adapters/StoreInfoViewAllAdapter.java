package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StoreInfoViewAllAdapter extends RecyclerView.Adapter {
    private final Context context;
    private ArrayList<BizStoreElastic> dataSet;
    private RecyclerView recyclerView;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnItemClickListener {
        void onStoreItemClick(BizStoreElastic item, View view, int pos);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private final OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_phoneno;
        private TextView tv_store_rating;
        private TextView tv_store_review;
        private TextView tv_category_name;
        private TextView tv_store_special;
        private TextView tv_status;
        private ImageView iv_main;
        private CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_phoneno = (TextView) itemView.findViewById(R.id.tv_phoneno);
            this.tv_store_rating = (TextView) itemView.findViewById(R.id.tv_store_rating);
            this.tv_store_review = (TextView) itemView.findViewById(R.id.tv_store_review);
            this.tv_category_name = (TextView) itemView.findViewById(R.id.tv_category_name);
            this.tv_store_special = (TextView) itemView.findViewById(R.id.tv_store_special);
            this.tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            this.iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            this.card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public StoreInfoViewAllAdapter(ArrayList<BizStoreElastic> data, Context context, OnItemClickListener listener, RecyclerView recyclerView) {
        this.dataSet = data;
        this.context = context;
        this.listener = listener;
        this.recyclerView = recyclerView;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            this.recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_view_all, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int listPosition) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            BizStoreElastic bizStoreElastic = dataSet.get(listPosition);
            String address = "";
            if (!TextUtils.isEmpty(bizStoreElastic.getTown())) {
                address = bizStoreElastic.getTown();
            }
            if (!TextUtils.isEmpty(bizStoreElastic.getArea())) {
                address = bizStoreElastic.getArea() + ", " + address;
            }
            holder.tv_address.setText(address);
            holder.tv_phoneno.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            holder.tv_store_special.setText(bizStoreElastic.getFamousFor());
            holder.tv_store_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
            holder.tv_store_review.setText(String.valueOf(bizStoreElastic.getRatingCount() == 0 ? "No" : bizStoreElastic.getRatingCount()) + " Reviews");
            if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
                Picasso.with(context)
                        .load(bizStoreElastic.getDisplayImage())
                        .into(holder.iv_main);
            else {
                //TODO load default image
            }
            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStoreItemClick(dataSet.get(listPosition), v, listPosition);
                }
            });

            // holder.tv_store_special.setText();
            StoreHourElastic storeHourElastic = bizStoreElastic.getStoreHourElasticList().get(AppUtilities.getDayOfWeek());
            if (storeHourElastic.isDayClosed()) {
                holder.tv_status.setText(context.getString(R.string.store_closed));
            } else {
                holder.tv_status.setText(
                        context.getString(R.string.store_hour)
                                + " "
                                + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour())
                                + " - "
                                + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getEndHour()));
            }


            int timeIn24HourFormat = AppUtilities.getTimeIn24HourFormat();
            if (!storeHourElastic.isDayClosed()) {
                // Before Token Available Time
                if (timeIn24HourFormat < storeHourElastic.getTokenAvailableFrom()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                holder.tv_status.setText("Closed Now. Appointment booking starts at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                                break;
                            default:
                                holder.tv_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                                break;
                        }
                    } else {
                        holder.tv_status.setText("Closed Now. You can join queue at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                    }
                }

                // Between Token Available and Start Hour
                if (timeIn24HourFormat >= storeHourElastic.getTokenAvailableFrom() && timeIn24HourFormat < storeHourElastic.getStartHour()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                holder.tv_status.setText("Now accepting appointments for today");
                                break;
                            default:
                                holder.tv_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                                break;
                        }
                    } else {
                        holder.tv_status.setText("Now you can join queue. Queue service will begin at " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                    }
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
                }

                // After Start Hour and Before Token Not Available From
                if (timeIn24HourFormat >= storeHourElastic.getStartHour() && timeIn24HourFormat < storeHourElastic.getTokenNotAvailableFrom()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                holder.tv_status.setText("Open now. Book your appointment for today.");
                                break;
                            default:
                                holder.tv_status.setText("Open now. Join the queue.");
                                break;
                        }
                    } else {
                        holder.tv_status.setText("Open Now. Join the queue.");
                    }
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.open_queue));
                }

                // When between Token Not Available From and End Hour
                if (timeIn24HourFormat >= storeHourElastic.getTokenNotAvailableFrom() && timeIn24HourFormat < storeHourElastic.getEndHour()) {
                    holder.tv_status.setText("Closing soon");
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }

                // When after End Hour
                if (timeIn24HourFormat >= storeHourElastic.getEndHour()) {
                    holder.tv_status.setText("Closed now");
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }


            } else {
                //TODO(hth) Show when will this be open next. For now hide it.
                holder.tv_status.setText("Closed Today");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                case BK:
                    holder.tv_store_special.setVisibility(View.GONE);
                    holder.tv_status.setVisibility(View.GONE);
                    holder.tv_category_name.setText("");
                    holder.tv_name.setText(bizStoreElastic.getBusinessName());
                    break;
                default:
                    holder.tv_store_special.setVisibility(View.VISIBLE);
                    holder.tv_status.setVisibility(View.VISIBLE);
                    String time =  Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()) +
                            " - " + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getEndHour());
                    holder.tv_category_name.setText(time);
                    holder.tv_name.setText(bizStoreElastic.getDisplayName());
                    break;
            }
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
