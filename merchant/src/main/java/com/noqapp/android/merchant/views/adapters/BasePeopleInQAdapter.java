package com.noqapp.android.merchant.views.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerModel;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.common.model.types.UserLevelEnum;
import com.noqapp.common.utils.PhoneFormatterUtil;

import java.util.List;

public abstract class BasePeopleInQAdapter extends RecyclerView.Adapter<BasePeopleInQAdapter.MyViewHolder> implements QueuePersonListPresenter {

    private static final String TAG = BasePeopleInQAdapter.class.getSimpleName();
    private final Context context;
    private List<JsonQueuedPerson> dataSet;
    private int glowPostion = -1;
    protected String qCodeQR = "";
    protected ManageQueueModel manageQueueModel;
    protected BusinessCustomerModel businessCustomerModel;
    private QueueStatusEnum queueStatusEnum;
    // for medical Only
    abstract void changePatient(Context context, JsonQueuedPerson jsonQueuedPerson);
    // for medical Only
    abstract void editBusinessCustomerId(Context context, JsonQueuedPerson jsonQueuedPerson);
    // for medical Only
    abstract void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson);

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        // Only the updated record will be returned from WebService
        if(jsonQueuePersonList.getQueuedPeople().size()>0) {
            //Server will return only updated objects list
            //Hence update only that objects
            for (int j = 0; j < jsonQueuePersonList.getQueuedPeople().size(); j++) {
                JsonQueuedPerson jsonQueuedPerson = jsonQueuePersonList.getQueuedPeople().get(j);
                for (int i = 0; i < dataSet.size(); i++) {
                    if (dataSet.get(i).getToken() == jsonQueuedPerson.getToken()) {
                        dataSet.set(i, jsonQueuedPerson);
                        break;
                    }
                }
            }
        }
        notifyDataSetChanged();
        LaunchActivity.getLaunchActivity().dismissProgress();

    }

    @Override
    public void queuePersonListError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    public interface PeopleInQAdapterClick {

        void PeopleInQClick(int position);
    }

    private PeopleInQAdapterClick peopleInQAdapterClick;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_customer_name;
        TextView tv_customer_mobile;
        TextView tv_sequence_number;
        TextView tv_status_msg;
        TextView tv_create_case;
        TextView tv_change_name;
        TextView tv_business_customer_id;
        ImageView iv_info;
        ImageView iv_new;
        CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_status_msg = itemView.findViewById(R.id.tv_status_msg);
            this.tv_create_case = itemView.findViewById(R.id.tv_create_case);
            this.tv_change_name = itemView.findViewById(R.id.tv_change_name);
            this.tv_business_customer_id = itemView.findViewById(R.id.tv_business_customer_id);
            this.iv_info = itemView.findViewById(R.id.iv_info);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }

    public BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.qCodeQR = qCodeQR;
        manageQueueModel = new ManageQueueModel(this);
        businessCustomerModel =  new BusinessCustomerModel(this);
    }

    public BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPostion, QueueStatusEnum queueStatusEnum) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.qCodeQR = qCodeQR;
        this.glowPostion = glowPostion;
        manageQueueModel = new ManageQueueModel(this);
        businessCustomerModel =  new BusinessCustomerModel(this);
        this.queueStatusEnum = queueStatusEnum;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_people_queue_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder recordHolder, final int position) {
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(position);
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();

        recordHolder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        recordHolder.tv_business_customer_id.setText(TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId()) ? context.getString(R.string.unregister_user) :
                Html.fromHtml("<b>Reg. Id: </b>"+jsonQueuedPerson.getBusinessCustomerId()));
        recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                //TODO : @ Chandra Please change the country code dynamically, country code you can get it from TOPIC
                PhoneFormatterUtil.formatNumber("IN", phoneNo));
        recordHolder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    new AppUtils().makeCall(LaunchActivity.getLaunchActivity(), phoneNo);
            }
        });
        recordHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQAdapterClick.PeopleInQClick(position);
            }
        });
        // check parameter to show client is new or has previously visited
        if (jsonQueuedPerson.isClientVisitedThisStore()) {
            recordHolder.iv_new.setVisibility(View.VISIBLE);
        } else {
            recordHolder.iv_new.setVisibility(View.GONE);
        }
        switch (jsonQueuedPerson.getQueueUserState()) {
            case Q:
                if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId())) {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_available);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_available));
                } else if (jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquired_by_you);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_acquired_by_you));
                } else {
                    recordHolder.iv_info.setBackgroundResource(R.drawable.acquired_already);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_already_acquired));
                }
                recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
                break;
            case A:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_left_queue));
                break;
            case N:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_skip));
                break;
            case S:
                recordHolder.iv_info.setBackgroundResource(R.drawable.acquire_cancel_by_user);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_served));
                break;
            default:
                Log.e(TAG, "Reached unsupported condition state=" + jsonQueuedPerson.getQueueUserState());
                throw new UnsupportedOperationException("Reached unsupported condition");
        }

        recordHolder.tv_create_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCaseHistory(context, jsonQueuedPerson);
            }
        });

        recordHolder.tv_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePatient(context, jsonQueuedPerson);
            }
        });
        recordHolder.tv_business_customer_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBusinessCustomerId(context, jsonQueuedPerson);
            }
        });

        if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
            if(glowPostion > 0 && glowPostion - 1 == position && jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q && queueStatusEnum == QueueStatusEnum.N) {
                recordHolder.tv_create_case.setVisibility(View.VISIBLE);
            }else{
                recordHolder.tv_create_case.setVisibility(View.GONE);
            }
        }else {
            recordHolder.tv_create_case.setVisibility(View.GONE);
        }

        if (jsonQueuedPerson.getDependents().size()>0)
            recordHolder.tv_change_name.setVisibility(View.VISIBLE);
        else
            recordHolder.tv_change_name.setVisibility(View.GONE);

        if (glowPostion > 0 && glowPostion - 1 == position && jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q && queueStatusEnum == QueueStatusEnum.N) {
            Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.show_anim);
            recordHolder.cardview.startAnimation(startAnimation);
            Log.v("animation true: ",""+position);
        }else{
            Animation removeAnimation = AnimationUtils.loadAnimation(context, R.anim.remove_anim);
            recordHolder.cardview.startAnimation(removeAnimation);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public void setViewAnimation(View view) {
        AlphaAnimation blinkanimation = new AlphaAnimation(1, 0.5f); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(300); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        view.setAnimation(blinkanimation);
    }

    private void setAnim(final CardView view) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 1.0f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float fractionAnim = (float) valueAnimator.getAnimatedValue();

                view.setCardBackgroundColor(ColorUtils.blendARGB(Color.parseColor("#CD334E")
                        , Color.parseColor("#FFFFFF")
                        , fractionAnim));
            }
        });
        valueAnimator.start();
    }

    private void setAnimationOfView(final View view) {
        int colorFrom = context.getResources().getColor(R.color.color_action_bar);
        int colorTo = context.getResources().getColor(R.color.color_separator);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }


}
