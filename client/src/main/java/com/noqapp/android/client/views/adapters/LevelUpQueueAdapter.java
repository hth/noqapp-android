package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AllReviewsActivity;
import com.noqapp.android.client.views.activities.BookAppointmentActivity;
import com.noqapp.android.client.views.activities.LoginActivity;
import com.noqapp.android.client.views.activities.ManagerProfileActivity;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.WalkInStateEnum;
import com.noqapp.android.common.model.types.category.CanteenStoreDepartmentEnum;
import com.noqapp.android.common.utils.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelUpQueueAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<JsonCategory> listDataHeader;
    private Map<String, ArrayList<BizStoreElastic>> listDataChild;
    private final OnItemClickListener listener;
    private boolean isSingleEntry = false;
    private boolean isTemple = false;

    public interface OnItemClickListener {
        void onCategoryItemClick(BizStoreElastic item);
    }

    public LevelUpQueueAdapter(
        Context context,
        List<JsonCategory> listDataHeader,
        Map<String, ArrayList<BizStoreElastic>> listDataChild,
        OnItemClickListener listener
    ) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.listener = listener;
        if (listDataChild.size() > 0) {
            BizStoreElastic bizStoreElastic = (BizStoreElastic) getChild(0, 0);
            isTemple = bizStoreElastic.getBusinessType() == BusinessTypeEnum.PW;
        }
    }

    public LevelUpQueueAdapter(
        Context context,
        List<JsonCategory> listDataHeader,
        Map<String, ArrayList<BizStoreElastic>> listDataChild,
        OnItemClickListener listener,
        boolean isSingleEntry
    ) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.listener = listener;
        this.isSingleEntry = isSingleEntry;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getBizCategoryId()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(
        final int groupPosition,
        final int childPosition,
        boolean isLastChild,
        View convertView,
        ViewGroup parent
    ) {
        try {
            final ChildViewHolder childViewHolder;
            final BizStoreElastic bizStoreElastic = (BizStoreElastic) getChild(groupPosition, childPosition);
            if (convertView == null) {
                if (isSingleEntry) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_single_entry_item, parent, false);
                } else {
                    if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_category1, parent, false);
                    } else {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_category_store, parent, false);
                    }
                }
                childViewHolder = new ChildViewHolder();
                childViewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                childViewHolder.tv_address = convertView.findViewById(R.id.tv_address);
                childViewHolder.tv_store_rating = convertView.findViewById(R.id.tv_store_rating);
                childViewHolder.tv_specialization = convertView.findViewById(R.id.tv_specialization);
                childViewHolder.tv_store_special = convertView.findViewById(R.id.tv_store_special);
                childViewHolder.tv_store_review = convertView.findViewById(R.id.tv_store_review);
                childViewHolder.tv_store_timing = convertView.findViewById(R.id.tv_store_timing);
                childViewHolder.tv_time_label = convertView.findViewById(R.id.tv_time_label);
                childViewHolder.tv_status = convertView.findViewById(R.id.tv_status);
                childViewHolder.tv_lunch_time = convertView.findViewById(R.id.tv_lunch_time);
                childViewHolder.iv_main = convertView.findViewById(R.id.iv_main);
                childViewHolder.tv_join = convertView.findViewById(R.id.tv_join);
                childViewHolder.btn_book_appointment = convertView.findViewById(R.id.btn_book_appointment);
                childViewHolder.tv_consult_fees = convertView.findViewById(R.id.tv_consult_fees);
                childViewHolder.tv_consult_fees_header = convertView.findViewById(R.id.tv_consult_fees_header);
                childViewHolder.card_view = convertView.findViewById(R.id.card_view);
                convertView.setTag(R.layout.rcv_item_category1, childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.rcv_item_category1);
            }

            childViewHolder.tv_name.setText(bizStoreElastic.getDisplayName());
            childViewHolder.tv_store_rating.setText(String.valueOf(AppUtils.round(bizStoreElastic.getRating())));
            childViewHolder.tv_address.setText(AppUtils.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
            childViewHolder.tv_address.setVisibility(View.GONE);
            AppUtils.setReviewCountText(bizStoreElastic.getReviewCount(), childViewHolder.tv_store_review);
            childViewHolder.tv_store_review.setOnClickListener((View v) -> {
                if (bizStoreElastic.getReviewCount() > 0) {
                    Intent in = new Intent(context, AllReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                    bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getDisplayName());
                    bundle.putString(IBConstant.KEY_STORE_ADDRESS, AppUtils.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
                    in.putExtras(bundle);
                    context.startActivity(in);
                }
            });
            AppUtils.showAllDaysTiming(context, childViewHolder.tv_store_timing, bizStoreElastic.getCodeQR());
            String specialization = bizStoreElastic.getCompleteEducation();
            childViewHolder.tv_specialization.setText(specialization);
            childViewHolder.tv_specialization.setVisibility(TextUtils.isEmpty(specialization) ? View.GONE : View.VISIBLE);
            StoreHourElastic storeHourElastic = AppUtils.getStoreHourElastic(bizStoreElastic.getStoreHourElasticList());
            childViewHolder.tv_join.setEnabled(!storeHourElastic.isDayClosed());
            if (storeHourElastic.isDayClosed()) {
                childViewHolder.tv_status.setText(context.getString(R.string.store_closed));
                childViewHolder.tv_store_timing.setVisibility(View.VISIBLE);
                childViewHolder.tv_status.setVisibility(View.GONE);
                childViewHolder.tv_store_timing.setText(context.getResources().getString(R.string.closed));
                childViewHolder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bg_inactive));
                childViewHolder.tv_join.setTextColor(context.getResources().getColor(R.color.button_color));
                childViewHolder.tv_join.setText(context.getResources().getString(R.string.closed));
            } else {
                childViewHolder.tv_store_timing.setVisibility(View.VISIBLE);
                childViewHolder.tv_status.setVisibility(View.VISIBLE);
                String time = new AppUtils().formatTodayStoreTiming(
                    context,
                    storeHourElastic.isDayClosed(),
                    storeHourElastic.getStartHour(),
                    storeHourElastic.getEndHour());

                String lunchTime = new AppUtils().formatTodayStoreLunchTiming(context, storeHourElastic.getLunchTimeStart(), storeHourElastic.getLunchTimeEnd());
                if (!TextUtils.isEmpty(lunchTime)) {
                    childViewHolder.tv_lunch_time.setText(lunchTime);
                    childViewHolder.tv_lunch_time.setVisibility(View.VISIBLE);
                }
                Log.e("value: " + childPosition, time);
                childViewHolder.tv_store_timing.setText(time);
                childViewHolder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_gradient));
                childViewHolder.tv_join.setTextColor(context.getResources().getColor(R.color.white));
                childViewHolder.tv_join.setText(context.getResources().getString(R.string.get_token));
                if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.HS) {
                    childViewHolder.tv_join.setText(context.getResources().getString(R.string.visit_store));
                }
            }

            int timeIn24HourFormat = AppUtils.getTimeIn24HourFormat();
            if (!storeHourElastic.isDayClosed()) {
                // Before Token Available Time
                if (timeIn24HourFormat < storeHourElastic.getTokenAvailableFrom()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                childViewHolder.tv_status.setText(
                                    context.getResources().getString(R.string.closed_now_booking_start_at)
                                        + " "
                                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                                break;
                            default:
                                childViewHolder.tv_status.setText(
                                    context.getResources().getString(R.string.closed_now_token_available_after)
                                        + " "
                                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                                break;
                        }
                    } else {
                        childViewHolder.tv_status.setText(
                            context.getResources().getString(R.string.closed_now_token_available_after)
                                + " "
                                + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getTokenAvailableFrom()));
                    }
                }

                // Between Token Available and Start Hour
                if (timeIn24HourFormat >= storeHourElastic.getTokenAvailableFrom() && timeIn24HourFormat < storeHourElastic.getStartHour()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                childViewHolder.tv_status.setText(context.getResources().getString(R.string.accepting_today_appointment));
                                break;
                            default:
                                childViewHolder.tv_status.setText(
                                    context.getResources().getString(R.string.open_serving_at)
                                        + " "
                                        + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                                break;
                        }
                    } else {
                        childViewHolder.tv_status.setText(
                            context.getResources().getString(R.string.open_serving_at)
                                + " "
                                + Formatter.convertMilitaryTo12HourFormat(storeHourElastic.getStartHour()));
                    }
                    childViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.before_opening_queue));
                }

                // After Start Hour and Before Token Not Available From
                if (timeIn24HourFormat >= storeHourElastic.getStartHour() && timeIn24HourFormat < storeHourElastic.getTokenNotAvailableFrom()) {
                    if (bizStoreElastic.getBusinessType() != null) {
                        switch (bizStoreElastic.getBusinessType()) {
                            case DO:
                                childViewHolder.tv_status.setText(context.getResources().getString(R.string.open_for_walkin));
                                break;
                            default:
                                childViewHolder.tv_status.setText(context.getResources().getString(R.string.open_token_available));
                                break;
                        }
                    } else {
                        childViewHolder.tv_status.setText(context.getResources().getString(R.string.open_token_available));
                    }
                    childViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.open_queue));
                }

                // When between Token Not Available From and End Hour
                if (timeIn24HourFormat >= storeHourElastic.getTokenNotAvailableFrom() && timeIn24HourFormat < storeHourElastic.getEndHour()) {
                    childViewHolder.tv_status.setText(context.getResources().getString(R.string.closing_soon));
                    childViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
                }

                // When after End Hour
                if (timeIn24HourFormat >= storeHourElastic.getEndHour()) {
                    childViewHolder.tv_status.setText(context.getResources().getString(R.string.now_closed));
                    childViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
                }
            } else {
                //TODO(hth) Show when will this be open next. For now hide it.
                childViewHolder.tv_status.setText(context.getResources().getString(R.string.closed));
                childViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.button_color));
            }
            AppUtils.loadProfilePic(childViewHolder.iv_main, bizStoreElastic.getDisplayImage(), context);
            if (bizStoreElastic.getProductPrice() == 0) {
                childViewHolder.tv_consult_fees.setVisibility(View.GONE);
                childViewHolder.tv_consult_fees_header.setVisibility(View.GONE);
            } else {
                // String feeString = "<font color=#000000><b>"+ AppUtilities.getCurrencySymbol(bizStoreElastic.getCountryShortName()) + String.valueOf(bizStoreElastic.getProductPrice() / 100) + "</b></font>  Consultation fee";
                String feeString = "fees <font color=#000000><b>" + AppUtils.getCurrencySymbol(bizStoreElastic.getCountryShortName()) + String.valueOf(bizStoreElastic.getProductPrice() / 100) + "</b></font>";
                childViewHolder.tv_consult_fees.setText(Html.fromHtml(feeString));
                childViewHolder.tv_consult_fees.setVisibility(View.VISIBLE);
                childViewHolder.tv_consult_fees_header.setVisibility(View.VISIBLE);
            }
            String special = bizStoreElastic.getFamousFor();
            childViewHolder.tv_store_special.setText(special);
            childViewHolder.tv_store_special.setVisibility(TextUtils.isEmpty(special) ? View.GONE : View.VISIBLE);
            // for safety null check added for walking state
            if (null == bizStoreElastic.getWalkInState() || bizStoreElastic.getWalkInState() == WalkInStateEnum.E) {
                childViewHolder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_gradient));
                childViewHolder.tv_join.setTextColor(Color.WHITE);
                childViewHolder.tv_join.setOnClickListener((View v) -> {
                    if (bizStoreElastic.getBusinessType() != BusinessTypeEnum.HS) {
                        listener.onCategoryItemClick(bizStoreElastic);
                    } else {
                        new CustomToast().showToast(context, context.getResources().getString(R.string.visit_store_for_service));
                    }
                });
            } else {
                childViewHolder.tv_join.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bg_inactive));
                childViewHolder.tv_join.setTextColor(Color.BLACK);
                childViewHolder.tv_join.setOnClickListener((View v) -> new CustomToast().showToast(context, bizStoreElastic.getDisplayName() + " " + context.getResources().getString(R.string.no_walkin)));
            }

            /* Booking of Appointment. */
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                    switch (bizStoreElastic.getAppointmentState()) {
                        case O:
                            childViewHolder.btn_book_appointment.setVisibility(View.GONE);
                            break;
                        case A:
                        case S:
                            childViewHolder.btn_book_appointment.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                case CDQ:
                    childViewHolder.tv_specialization.setText(CanteenStoreDepartmentEnum.valueOf(bizStoreElastic.getBizCategoryId()).getDescription());
                    childViewHolder.tv_specialization.setVisibility(View.VISIBLE);

                    childViewHolder.tv_store_rating.setVisibility(View.GONE);
                    childViewHolder.tv_store_review.setVisibility(View.GONE);
                    childViewHolder.tv_store_special.setVisibility(View.GONE);
                    childViewHolder.tv_consult_fees.setVisibility(View.GONE);
                    childViewHolder.tv_consult_fees_header.setVisibility(View.GONE);
                    switch (bizStoreElastic.getAppointmentState()) {
                        case O:
                            childViewHolder.btn_book_appointment.setVisibility(View.GONE);
                            break;
                        case A:
                        case S:
                            childViewHolder.btn_book_appointment.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                case PW:
                    childViewHolder.tv_specialization.setVisibility(View.GONE);
                    childViewHolder.tv_store_rating.setVisibility(View.GONE);
                    childViewHolder.tv_store_review.setVisibility(View.GONE);
                    childViewHolder.tv_store_special.setVisibility(View.GONE);
                    childViewHolder.tv_consult_fees.setVisibility(View.GONE);
                    childViewHolder.tv_consult_fees_header.setVisibility(View.GONE);
                    switch (bizStoreElastic.getAppointmentState()) {
                        case O:
                            childViewHolder.btn_book_appointment.setVisibility(View.GONE);
                            break;
                        case A:
                        case S:
                            childViewHolder.btn_book_appointment.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                default:
                    childViewHolder.btn_book_appointment.setVisibility(View.GONE);
            }

            childViewHolder.btn_book_appointment.setOnClickListener((View v) -> {
                if (UserUtils.isLogin()) {
                    Intent in = new Intent(context, BookAppointmentActivity.class);
                    in.putExtra(IBConstant.KEY_DATA_OBJECT, bizStoreElastic);
                    context.startActivity(in);
                } else {
                    new CustomToast().showToast(context, context.getResources().getString(R.string.login_for_appt_booking));
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                }
            });
            childViewHolder.iv_main.setOnClickListener((View v) -> {
                if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO) {
                    if (TextUtils.isEmpty(bizStoreElastic.getWebProfileId())) {
                        new CustomToast().showToast(context, context.getResources().getString(R.string.no_doctor_profile));
                    } else {
                        Intent intent = new Intent(context, ManagerProfileActivity.class);
                        intent.putExtra("webProfileId", bizStoreElastic.getWebProfileId());
                        intent.putExtra("managerName", bizStoreElastic.getDisplayName());
                        intent.putExtra("managerImage", bizStoreElastic.getDisplayImage());
                        intent.putExtra("bizCategoryId", bizStoreElastic.getBizCategoryId());
                        context.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).getBizCategoryId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (isSingleEntry || isTemple) {
            convertView = layoutInflater.inflate(R.layout.blank_group_view, null);
        } else {
            String headerTitle = ((JsonCategory) getGroup(groupPosition)).getCategoryName();
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item_menu_group, parent, false);
            }
            TextView tv_list_header = convertView.findViewById(R.id.tv_list_header);
            tv_list_header.setText(headerTitle);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public final class ChildViewHolder {
        private TextView tv_name;
        private TextView tv_address;
        private TextView tv_store_rating;
        private TextView tv_specialization;
        private TextView tv_store_special;
        private TextView tv_store_review;
        private TextView tv_store_timing;
        private TextView tv_time_label;
        private TextView tv_status;
        private TextView tv_lunch_time;
        private Button tv_join;
        private Button btn_book_appointment;
        private TextView tv_consult_fees;
        private TextView tv_consult_fees_header;
        private ImageView iv_main;
        private CardView card_view;
    }
}