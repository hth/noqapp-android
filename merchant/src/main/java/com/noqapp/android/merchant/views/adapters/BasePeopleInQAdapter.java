package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonBusinessCustomerPriority;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;
import com.noqapp.android.common.model.types.DataVisibilityEnum;
import com.noqapp.android.common.model.types.PaymentPermissionEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.CustomProgressBar;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonDataVisibility;
import com.noqapp.android.merchant.presenter.beans.JsonPaymentPermission;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.List;

public abstract class BasePeopleInQAdapter extends RecyclerView.Adapter implements QueuePersonListPresenter {
    private static final String TAG = BasePeopleInQAdapter.class.getSimpleName();
    protected final Context context;
    private List<JsonQueuedPerson> dataSet;
    private int glowPosition = -1;
    protected String codeQR;
    protected String bizCategoryId;
    protected ManageQueueApiCalls manageQueueApiCalls;
    protected BusinessCustomerApiCalls businessCustomerApiCalls;
    private QueueStatusEnum queueStatusEnum;
    private JsonDataVisibility jsonDataVisibility;
    private JsonPaymentPermission jsonPaymentPermission;
    protected CustomProgressBar customProgressBar;
    protected String userAccountType;
    private CustomerPriorityLevelEnum customerPriorityLevelEnum;
    private BusinessTypeEnum businessTypeEnum;

    public void updateDataSet(List<JsonQueuedPerson> dataSet, JsonTopic jsonTopic) {
        if (jsonTopic.getServingNumber() > 0) {
            glowPosition = jsonTopic.getServingNumber() - 1;
        }
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    // for medical Only
    abstract void changePatient(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void editBusinessCustomerId(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void uploadDocument(Context context, JsonQueuedPerson jsonQueuedPerson);

    // for medical Only
    abstract void createCaseHistory(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId);

    abstract void approveCustomer(Context context, JsonQueuedPerson jsonQueuedPerson, String bizCategoryId, String action, String codeQR);

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
        customProgressBar.dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        customProgressBar.dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        customProgressBar.dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        customProgressBar.dismissProgress();
        new ErrorResponseHandler().processError(context, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        customProgressBar.dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(context, errorCode);
    }

    public interface PeopleInQAdapterClick {
        void peopleInQClick(int position);
        void viewOrderClick(Context context, JsonQueuedPerson jsonQueuedPerson, boolean isPaymentNotAllowed);
        void actionOnBusinessCustomer(Context context, JsonQueuedPerson jsonQueuedPerson, CustomerPriorityLevelEnum customerPriorityLevel, ActionTypeEnum action, String codeQR);
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
        TextView tv_token_time_slot;
        TextView tv_join_timing;
        TextView tv_last_visit;
        TextView tv_payment_stat;
        RelativeLayout rl_sequence_new_time;
        ImageView iv_new;
        CardView cardview;
        LinearLayout ll_account_authentication;
        RadioGroup account_type;
        Button authenticate_approve;
        Button authenticate_reject;
        Button authenticate_reset;


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
            this.tv_token_time_slot = itemView.findViewById(R.id.tv_token_time_slot);
            this.tv_join_timing = itemView.findViewById(R.id.tv_join_timing);
            this.tv_last_visit = itemView.findViewById(R.id.tv_last_visit);
            this.tv_payment_stat = itemView.findViewById(R.id.tv_payment_stat);
            this.rl_sequence_new_time = itemView.findViewById(R.id.rl_sequence_new_time);
            this.iv_new = itemView.findViewById(R.id.iv_new);
            this.cardview = itemView.findViewById(R.id.cardview);
            this.ll_account_authentication = itemView.findViewById(R.id.account_authentication);
            this.account_type = itemView.findViewById(R.id.account_type);
            this.authenticate_approve = itemView.findViewById(R.id.authenticate_approve);
            this.authenticate_reject= itemView.findViewById(R.id.authenticate_reject);
            this.authenticate_reset = itemView.findViewById(R.id.authenticate_reset);

        }
    }

    protected BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context,
                                   PeopleInQAdapterClick peopleInQAdapterClick, String codeQR,
                                   JsonDataVisibility jsonDataVisibility, JsonPaymentPermission jsonPaymentPermission) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.codeQR = codeQR;
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setQueuePersonListPresenter(this);
        this.jsonDataVisibility = jsonDataVisibility;
        this.jsonPaymentPermission = jsonPaymentPermission;
        customProgressBar = new CustomProgressBar(context);
    }


    protected BasePeopleInQAdapter(List<JsonQueuedPerson> data, Context context,
                                   PeopleInQAdapterClick peopleInQAdapterClick, JsonTopic jsonTopic) {
        this.dataSet = data;
        this.context = context;
        this.peopleInQAdapterClick = peopleInQAdapterClick;
        this.codeQR = jsonTopic.getCodeQR();
        this.glowPosition = jsonTopic.getServingNumber();
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setQueuePersonListPresenter(this);
        this.queueStatusEnum = jsonTopic.getQueueStatus();
        this.jsonDataVisibility = jsonTopic.getJsonDataVisibility();
        this.jsonPaymentPermission = jsonTopic.getJsonPaymentPermission();
        this.bizCategoryId = jsonTopic.getBizCategoryId();
        this.businessTypeEnum = jsonTopic.getBusinessType();
        customProgressBar = new CustomProgressBar(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_people_queue_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder recordHolder = (MyViewHolder) viewHolder;
        final JsonQueuedPerson jsonQueuedPerson = dataSet.get(position);
        final String phoneNo = jsonQueuedPerson.getCustomerPhone();

        recordHolder.tv_sequence_number.setText(String.valueOf(jsonQueuedPerson.getToken()));
        recordHolder.tv_last_visit.setText(TextUtils.isEmpty(
                jsonQueuedPerson.getClientVisitedThisStoreDate())
                ? ""
                : "Last visit: " + CommonHelper.formatStringDate(CommonHelper.SDF_DOB_FROM_UI, jsonQueuedPerson.getClientVisitedThisStoreDate()));
        recordHolder.tv_customer_name.setText(
                TextUtils.isEmpty(jsonQueuedPerson.getCustomerName())
                        ? context.getString(R.string.unregister_user)
                        : jsonQueuedPerson.getCustomerName());
        recordHolder.tv_business_customer_id.setText(
                TextUtils.isEmpty(jsonQueuedPerson.getBusinessCustomerId())
                        ? Html.fromHtml("<b>Reg. Id: </b>" + context.getString(R.string.unregister_user))
                        : Html.fromHtml("<b>Reg. Id: </b>" + jsonQueuedPerson.getBusinessCustomerId()));
        // Time slot should be visible only for CD or CDQ.
        if((businessTypeEnum == BusinessTypeEnum.CD || businessTypeEnum == BusinessTypeEnum.CDQ)
                && !TextUtils.isEmpty(jsonQueuedPerson.getTimeSlotMessage())) {
            recordHolder.tv_token_time_slot.setVisibility(View.VISIBLE);
            recordHolder.tv_token_time_slot.setText(context.getString(R.string.time_slot, jsonQueuedPerson.getTimeSlotMessage()));
        }
        if (null != LaunchActivity.getLaunchActivity() && null != LaunchActivity.getLaunchActivity().getUserProfile()) {
            recordHolder.tv_customer_mobile.setText(TextUtils.isEmpty(phoneNo) ? context.getString(R.string.unregister_user) :
                    PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
        }

        recordHolder.tv_join_timing.setText(Formatter.getTime(jsonQueuedPerson.getCreated()));
        if (DataVisibilityEnum.H == jsonDataVisibility.getDataVisibilities().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
            recordHolder.tv_customer_mobile.setOnClickListener(v -> {
                if (!recordHolder.tv_customer_mobile.getText().equals(context.getString(R.string.unregister_user)))
                    AppUtils.makeCall(LaunchActivity.getLaunchActivity(), PhoneFormatterUtil.formatNumber(LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName(), phoneNo));
            });
        } else {
            recordHolder.tv_customer_mobile.setText(AppUtils.hidePhoneNumberWithX(phoneNo));
        }
        recordHolder.tv_status_msg.setOnClickListener(v -> peopleInQAdapterClick.peopleInQClick(position));
        // check parameter to show client is new or has previously visited
        recordHolder.iv_new.setVisibility(jsonQueuedPerson.isClientVisitedThisStore() ? View.INVISIBLE : View.VISIBLE);

        // Get colorCode from priority for each user card
        String priorityColorCode = "";
        if (jsonQueuedPerson.getCustomerPriorityLevel() != null && jsonQueuedPerson.getCustomerPriorityLevel().getColorCode() != null) {
            priorityColorCode = jsonQueuedPerson.getCustomerPriorityLevel().getColorCode();
        } else {
            //Set Default value
            priorityColorCode = CustomerPriorityLevelEnum.I.getColorCode();
        }

        if (jsonQueuedPerson.isClientVisitedThisBusiness()) {
            //recordHolder.rl_sequence_new_time.setBackgroundColor(Color.TRANSPARENT);
            recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#" + priorityColorCode));
            recordHolder.tv_sequence_number.setTextColor(Color.BLACK);
            recordHolder.tv_join_timing.setTextColor(Color.BLACK);
        } else {
            recordHolder.rl_sequence_new_time.setBackgroundColor(Color.parseColor("#" + priorityColorCode));
            recordHolder.tv_sequence_number.setTextColor(Color.WHITE);
            recordHolder.tv_join_timing.setTextColor(Color.WHITE);
        }


        // Don't show radio buttons when merchant priority access is false
        // or customer has already been approved.
        if(LaunchActivity.getLaunchActivity().getPriorityAccess()
                && jsonQueuedPerson.getBusinessCustomerAttributes() != null
                &&  !jsonQueuedPerson.getBusinessCustomerAttributes().contains(BusinessCustomerAttributeEnum.AP)
                && !jsonQueuedPerson.getBusinessCustomerAttributes().contains(BusinessCustomerAttributeEnum.RJ)) {

            // Get the business customer priorities from sharedPreference set in loginActivity
            List<JsonBusinessCustomerPriority> businessCustomerPriorities = LaunchActivity.getLaunchActivity().getBusinessCustomerPriority();
            recordHolder.ll_account_authentication.setVisibility(View.VISIBLE);
            RadioGroup.LayoutParams rprms;
            // Clean up the radio group view
            recordHolder.account_type.removeAllViews();

            // Dynamically add radio buttons based on the businessCustomerPriorities
            for(int i=0; i< businessCustomerPriorities.size(); i++){
                JsonBusinessCustomerPriority jsonBusinessCustomerPriority = businessCustomerPriorities.get(i);
                RadioButton radioButton = new RadioButton(this.context);
                radioButton.setText(jsonBusinessCustomerPriority.getPriorityName());
                radioButton.setId(View.generateViewId());
                radioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#E07E3D")));
                rprms= new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                recordHolder.account_type.addView(radioButton, rprms);
            }

            // Get the selected account type be listening to click on radio button
            // and reverse look up which businessCustomerPriorities enum does this belong
            recordHolder.account_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                    // Remove error from last child
                    int lastChildPos = group.getChildCount()-1;
                    ((RadioButton)group.getChildAt(lastChildPos)).setError(null);

                    boolean isChecked = checkedRadioButton.isChecked();
                    if (isChecked)
                    {
                        userAccountType = checkedRadioButton.getText().toString();
                        for (JsonBusinessCustomerPriority j: businessCustomerPriorities){
                            if(j.getPriorityName().equals(userAccountType)) {
                                customerPriorityLevelEnum = j.getCustomerPriorityLevel();
                                break;
                            }
                        }
                    }
                }
            });

            // Show the radio buttons
            recordHolder.ll_account_authentication.setVisibility(View.VISIBLE);
            recordHolder.authenticate_approve.setOnClickListener(v -> {
                if (recordHolder.account_type.getCheckedRadioButtonId() == -1)
                {
                    // No radio buttons checked, show error and return.
                    int lastChildPos = recordHolder.account_type.getChildCount()-1;
                    ((RadioButton)recordHolder.account_type.getChildAt(lastChildPos)).setError("Please select one of " +
                            "the choices");
                    return;
                }
                else
                {
                    peopleInQAdapterClick.actionOnBusinessCustomer(
                            context, jsonQueuedPerson, customerPriorityLevelEnum, ActionTypeEnum.APPROVE, this.codeQR);
                }
            });
            recordHolder.authenticate_reject.setOnClickListener(v -> peopleInQAdapterClick.actionOnBusinessCustomer(context, jsonQueuedPerson, customerPriorityLevelEnum, ActionTypeEnum.REJECT, this.codeQR));
            recordHolder.authenticate_reset.setOnClickListener(v -> peopleInQAdapterClick.actionOnBusinessCustomer(context, jsonQueuedPerson, customerPriorityLevelEnum, ActionTypeEnum.CLEAR, this.codeQR));
        }
        else {
            // Hide the radio buttons
            recordHolder.ll_account_authentication.setVisibility(View.GONE);
        }

        switch (jsonQueuedPerson.getQueueUserState()) {
            case Q:
                recordHolder.tv_customer_name.setText(jsonQueuedPerson.getCustomerName());
                recordHolder.tv_customer_mobile.setText(jsonQueuedPerson.getCustomerPhone());
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
                recordHolder.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.disable_list));
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
        if (!TextUtils.isEmpty(jsonQueuedPerson.getTransactionId())) {
            recordHolder.tv_payment_stat.setVisibility(View.VISIBLE);
            switch (jsonQueuedPerson.getJsonPurchaseOrder().getPaymentStatus()) {
                case PA:
                    recordHolder.tv_payment_stat.setText("Paid");
                    recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.bg_nogradient_round);
                    if (jsonQueuedPerson.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                        recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                        recordHolder.tv_payment_stat.setText("Refund Due");
                    }
                    if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.N) {
                        recordHolder.tv_payment_stat.setText("Refund Due");
                    }
                    break;
                case PR:
                    recordHolder.tv_payment_stat.setText("Payment Refunded");
                    recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                    break;
                case PC:
                    recordHolder.tv_payment_stat.setText("Payment Cancelled");
                    recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                    break;
                case PP:
                    if (jsonQueuedPerson.getJsonPurchaseOrder().getPresentOrderState() == PurchaseOrderStateEnum.CO) {
                        recordHolder.tv_payment_stat.setText("No Payment Due");
                        recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                    } else {
                        recordHolder.tv_payment_stat.setText("Accept Payment");
                        if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q || jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {
                            recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.bg_unpaid);
                        } else {
                            recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                        }
                    }
                    break;
                default:
                    recordHolder.tv_payment_stat.setText("Accept Payment");
                    if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q ||
                            jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {
                        recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.bg_unpaid);
                    } else {
                        recordHolder.tv_payment_stat.setBackgroundResource(R.drawable.grey_background);
                    }
                    break;
            }
        } else {
            recordHolder.tv_payment_stat.setVisibility(View.GONE);
        }
        recordHolder.tv_payment_stat.setOnClickListener(v -> {
            if (PaymentPermissionEnum.A == jsonPaymentPermission.getPaymentPermissions().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
                peopleInQAdapterClick.viewOrderClick(context, jsonQueuedPerson, false);
            } else {
                new CustomToast().showToast(context, context.getString(R.string.payment_not_allowed));
                peopleInQAdapterClick.viewOrderClick(context, jsonQueuedPerson, true);
            }
        });
        recordHolder.tv_create_case.setOnClickListener(v -> createCaseHistory(context, jsonQueuedPerson, bizCategoryId));
        recordHolder.tv_change_name.setOnClickListener(v -> changePatient(context, jsonQueuedPerson));
        recordHolder.tv_upload_document.setOnClickListener(v -> uploadDocument(context, jsonQueuedPerson));
        recordHolder.tv_business_customer_id.setOnClickListener(v -> editBusinessCustomerId(context, jsonQueuedPerson));
        try {
            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
                if (glowPosition > 0 && glowPosition - 1 == position && jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.Q || jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S && queueStatusEnum == QueueStatusEnum.N) {
                    recordHolder.tv_create_case.setClickable(true);
                    recordHolder.tv_create_case.setBackgroundResource(R.drawable.bg_nogradient_round);
                } else if (jsonQueuedPerson.getQueueUserState() == QueueUserStateEnum.S) {
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
