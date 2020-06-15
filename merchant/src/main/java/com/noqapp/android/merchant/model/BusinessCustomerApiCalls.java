package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;
import com.noqapp.android.merchant.model.response.api.BusinessCustomerApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CustomerPriority;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ApproveCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */
public class BusinessCustomerApiCalls {
    private static final String TAG = BusinessCustomerApiCalls.class.getSimpleName();

    private static final BusinessCustomerApiUrls businessCustomerApiUrls;
    private QueuePersonListPresenter queuePersonListPresenter;
    private FindCustomerPresenter findCustomerPresenter;
    private ApproveCustomerPresenter approveCustomerPresenter;

    public void setQueuePersonListPresenter(QueuePersonListPresenter queuePersonListPresenter) {
        this.queuePersonListPresenter = queuePersonListPresenter;
    }

    public void setFindCustomerPresenter(FindCustomerPresenter findCustomerPresenter) {
        this.findCustomerPresenter = findCustomerPresenter;
    }

    public void setApproveCustomerPresenter( ApproveCustomerPresenter approveCustomerPresenter) {
        this.approveCustomerPresenter = approveCustomerPresenter;
    }

    static {
        businessCustomerApiUrls = RetrofitClient.getClient().create(BusinessCustomerApiUrls.class);
    }

    public void addId(String did, String mail, String auth, JsonBusinessCustomer jsonBusinessCustomer) {
        businessCustomerApiUrls.addId(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomer).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("addId", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while addId");
                        queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePersonListPresenter.authenticationFailure();
                    } else {
                        queuePersonListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("addId", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }


    public void editId(String did, String mail, String auth, JsonBusinessCustomer jsonBusinessCustomer) {
        businessCustomerApiUrls.editId(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomer).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("editId", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while editId");
                        queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePersonListPresenter.authenticationFailure();
                    } else {
                        queuePersonListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("editId", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    public void findCustomer(String did, String mail, String auth, JsonBusinessCustomerLookup jsonBusinessCustomerLookup) {
        businessCustomerApiUrls.findCustomer(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomerLookup).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("findCustomer", String.valueOf(response.body()));
                        findCustomerPresenter.findCustomerResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while findCustomer");
                        findCustomerPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        findCustomerPresenter.authenticationFailure();
                    } else {
                        findCustomerPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("findCustomer fail", t.getLocalizedMessage(), t);
                findCustomerPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void accessCustomer(String did, String mail, String auth, CustomerPriority customerPriority) {
        businessCustomerApiUrls.accessAction(did, Constants.DEVICE_TYPE, mail, auth, customerPriority).enqueue(new Callback<JsonQueuedPerson>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuedPerson> call, @NonNull Response<JsonQueuedPerson> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        approveCustomerPresenter.approveCustomerResponse(response.body());
                    } else {
                        approveCustomerPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    approveCustomerPresenter.responseErrorPresenter(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuedPerson> call, @NonNull Throwable t) {
                Log.e("accessCustomer Failure", t.getLocalizedMessage(), t);
            }
        });
    }
}
