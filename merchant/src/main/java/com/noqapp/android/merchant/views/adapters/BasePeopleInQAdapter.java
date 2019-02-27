package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerModel;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public abstract class BasePeopleInQAdapter extends RecyclerView.Adapter<BasePeopleInQAdapter.MyViewHolder> implements QueuePersonListPresenter {
    private static final String TAG = BasePeopleInQAdapter.class.getSimpleName();

    private final Context context;
    private List<JsonQueuedPerson> dataSet;
    private int glowPosition = -1;
    protected String qCodeQR;
    protected String bizCategoryId;
    protected ManageQueueModel manageQueueModel;
    protected BusinessCustomerModel businessCustomerModel;
    private QueueStatusEnum queueStatusEnum;
    private JsonDataVisibility jsonDataVisibility;

    // for medical Only
    abstract void changePatient(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void editBusinessCustomerId(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId);

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        // Only the updated record will be returned from WebService
        if (jsonQueuePersonList.getQueuedPeople().size() > 0) {
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
    public void authenticationFailure() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processError(context, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(context, errorCode);
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
        TextView tv_upload_document;
        TextView tv_business_customer_id;
        TextView tv_join_timing;
        TextView tv_last_visit;
        RelativeLayout rl_sequence_new_time;
        ImageView iv_new;
        CardView cardview;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            this.tv_customer_mobile = itemView.findViewById(R.id.tv_customer_mobile);
            this.tv_sequence_number = itemView.findViewById(R.id.tv_sequence_number);
            this.tv_status_msg = itemView.findViewById(R.id.tv_status_msg);
            this.tv_create_case = itemView.findViewById(R.id.tv_create_case);
            this.tv_change_name = itemView.findViewById(R.id.tv_change_name);
            this.tv_upload_document = itemView.findViewById(R.id.tv_upload_document);
            this.tv_business_customer_id = itemView.findViewById(R.id.tv_business_customer_id);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.tv_last_visit = itemView.findViewById(R.id.tv_last_visit);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }

    protected BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, JsonDataVisibility jsonDataVisibility) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.qCodeQR = qCodeQR;
        manageQueueModel = new ManageQueueModel();
        manageQueueModel.setQueuePersonListPresenter(this);
        businessCustomerModel = new BusinessCustomerModel(this);
        this.jsonDataVisibility = jsonDataVisibility;
    }

    protected BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context, PeopleInQAdapterClick peopleInQAdapterClick, String qCodeQR, int glowPosition, QueueStatusEnum queueStatusEnum, JsonDataVisibility jsonDataVisibility, String bizCategoryId) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.qCodeQR = qCodeQR;
        this.glowPosition = glowPosition;
        manageQueueModel = new ManageQueueModel();
        manageQueueModel.setQueuePersonListPresenter(this);
        businessCustomerModel = new BusinessCustomerModel(this);
        this.queueStatusEnum = queueStatusEnum;
        this.jsonDataVisibility = jsonDataVisibility;
        this.bizCategoryId = bizCategoryId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_people_queue_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder recordHolder, final int position) {
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(position);
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();

        recordHolder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        recordHolder.tv_last_visit.setText(TextUtils.isEmpty(jsonQueuedPerson.getClientVisitedThisStoreDate()) ? "" : "Last visit: " + CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonQueuedPerson.getClientVisitedThisStoreDate()));
        recordHolder.tv_customer_name.setText(TextUtils.isEmpty(jsonQueuedPerson.getCustomerName()) ? context.getString(R.string.unregister_user) : jsonQueuedPerson.getCustomerName());
        recordHolder.tv_business_customer_id.setText(TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId()) ? Html.fromHtml("<b>Reg. Id: </b>" + context.getString(R.string.unregister_user)) :
                Html.fromHtml("<b>Reg. Id: </b>" + jsonQueuedPerson.getBusinessCustomerId()));
        recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        recordHolder.tv_join_timing.setText(Formatter.getTime(jsonQueuedPerson.getCreated()));
        if (DataVisibilityEnum.H.getName().equals(jsonDataVisibility.getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name()).name())) {
            recordHolder.tv_customer_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                        new AppUtils().makeCall(LaunchActivity.getLaunchActivity(), PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
                }
            });
        } else {
            recordHolder.tv_customer_mobile.setText(new AppUtils().hidePhoneNumberWithX(phoneNo));
        }
        recordHolder.tv_status_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleInQAdapterClick.PeopleInQClick(position);
            }
        });
        // check parameter to show client is new or has previously visited
        recordHolder.iv_new.setVisibility(jsonQueuedPerson.isClientVisitedThisStore() ? View.INVISIBLE : View.VISIBLE);

        if (jsonQueuedPerson.isClientVisitedThisBusiness()) {
            recordHolder.rl_sequence_new_time.setBackgroundColor(Color.TRANSPARENT);
            recordHolder.tv_sequence_number.setTextColor(Color.BLACK);
            recordHolder.tv_join_timing.setTextColor(Color.BLACK);
        } else {
            recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#e07e3d"));
            recordHolder.tv_sequence_number.setTextColor(Color.WHITE);
            recordHolder.tv_join_timing.setTextColor(Color.WHITE);
        }

        switch (jsonQueuedPerson.getQueueUserState()) {
            case Q:
                if (TextUtils.isEmpty(jsonQueuedPerson.getServerDeviceId())) {
                    recordHolder.tv_status_msg.setBackgroundResource(R.drawable.bg_nogradient_round);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_available));
                } else if (jsonQueuedPerson.getServerDeviceId().equals(UserUtils.getDeviceId())) {
                    recordHolder.tv_status_msg.setBackgroundResource(R.drawable.bg_nogradient_round);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_acquired_by_you));
                } else {
                    recordHolder.tv_status_msg.setBackgroundResource(R.drawable.grey_background);
                    recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_already_acquired));
                }
                recordHolder.cardview.setCardBackgroundColor(Color.WHITE);
                break;
            case A:
                recordHolder.tv_status_msg.setBackgroundResource(R.drawable.grey_background);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(
                        context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_client_left_queue));
                break;
            case N:
                recordHolder.tv_status_msg.setBackgroundResource(R.drawable.grey_background);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_skip));
                break;
            case S:
                recordHolder.tv_status_msg.setBackgroundResource(R.drawable.grey_background);
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.disable_list));
                recordHolder.tv_status_msg.setText(context.getString(R.string.msg_merchant_served));
                break;
            default:
                Log.e(TAG, "Reached unsupported condition state=" + jsonQueuedPerson.getQueueUserState());
                throw new UnsupportedOperationException("Reached unsupported condition");
        }

        recordHolder.tv_create_case.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCaseHistory(context, jsonQueuedPerson, bizCategoryId);
            }
        });
        recordHolder.tv_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePatient(context, jsonQueuedPerson);
            }
        });
        recordHolder.tv_upload_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDocument(context, jsonQueuedPerson);
            }
        });
        recordHolder.tv_business_customer_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBusinessCustomerId(context, jsonQueuedPerson);
            }
        });
        try {
            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
                if (glowPosition > 0 && glowPosition - 1 == position && jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q|| jsonQueuedPerson.getQueueUserState() ==QueueUserStateEnum.S && queueStatusEnum == QueueStatusEnum.N) {
                    recordHolder.tv_create_case.setClickable(true);
                    recordHolder.tv_create_case.setBackgroundResource(R.drawable.bg_nogradient_round);
                } else {
                    recordHolder.tv_create_case.setClickable(false);
                    recordHolder.tv_create_case.setBackgroundResource(R.drawable.grey_background);
                }
            } else if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.Q_SUPERVISOR) {
                if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q || jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {
                    recordHolder.tv_create_case.setClickable(true);
                    recordHolder.tv_create_case.setBackgroundResource(R.drawable.bg_nogradient_round);
                } else {
                    recordHolder.tv_create_case.setClickable(false);
                    recordHolder.tv_create_case.setBackgroundResource(R.drawable.grey_background);
                }
            } else {
                recordHolder.tv_create_case.setClickable(false);
                recordHolder.tv_create_case.setBackgroundResource(R.drawable.grey_background);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonQueuedPerson.getDependents().size() > 0) {
            recordHolder.tv_change_name.setBackgroundResource(R.drawable.bg_nogradient_round);
            recordHolder.tv_change_name.setClickable(true);
        } else {
            recordHolder.tv_change_name.setBackgroundResource(R.drawable.grey_background);
            recordHolder.tv_change_name.setClickable(false);
        }

        if (glowPosition > 0 && glowPosition - 1 == position && jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q && queueStatusEnum == QueueStatusEnum.N) {
            Animation startAnimation = AnimationUtils.loadAnimation(context, R.anim.show_anim);
            recordHolder.cardview.startAnimation(startAnimation);
            Log.v("Animation true: ", String.valueOf(position));
        } else {
            Animation removeAnimation = AnimationUtils.loadAnimation(context, R.anim.remove_anim);
            recordHolder.cardview.startAnimation(removeAnimation);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
