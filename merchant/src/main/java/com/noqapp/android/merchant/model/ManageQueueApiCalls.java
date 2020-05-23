package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.model.response.api.queue.QueueApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.ChangeUserInQueue;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.presenter.beans.body.merchant.Served;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.DispenseTokenPresenter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePaymentPresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.android.merchant.views.interfaces.QueueRefundPaymentPresenter;
import com.noqapp.android.merchant.views.interfaces.TopicPresenter;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */
public class ManageQueueApiCalls {
    private static final String TAG = ManageQueueApiCalls.class.getSimpleName();

    private static final QueueApiUrls queueApiUrls;
    private ManageQueuePresenter manageQueuePresenter;
    private DispenseTokenPresenter dispenseTokenPresenter;
    private TopicPresenter topicPresenter;
    private QueuePersonListPresenter queuePersonListPresenter;
    private QueuePaymentPresenter queuePaymentPresenter;
    private QueueRefundPaymentPresenter queueRefundPaymentPresenter;


    public void setManageQueuePresenter(ManageQueuePresenter manageQueuePresenter) {
        this.manageQueuePresenter = manageQueuePresenter;
    }

    public void setDispenseTokenPresenter(DispenseTokenPresenter dispenseTokenPresenter) {
        this.dispenseTokenPresenter = dispenseTokenPresenter;
    }

    public void setTopicPresenter(TopicPresenter topicPresenter) {
        this.topicPresenter = topicPresenter;
    }

    public void setQueuePersonListPresenter(QueuePersonListPresenter queuePersonListPresenter) {
        this.queuePersonListPresenter = queuePersonListPresenter;
    }

    public void setQueuePaymentPresenter(QueuePaymentPresenter queuePaymentPresenter) {
        this.queuePaymentPresenter = queuePaymentPresenter;
    }

    public void setQueueRefundPaymentPresenter(QueueRefundPaymentPresenter queueRefundPaymentPresenter) {
        this.queueRefundPaymentPresenter = queueRefundPaymentPresenter;
    }

    static {
        queueApiUrls = RetrofitClient.getClient().create(QueueApiUrls.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void getQueues(String did, String mail, String auth) {
        queueApiUrls.getQueues(did, Constants.DEVICE_TYPE, Constants.appVersion(), mail, auth).enqueue(new Callback<JsonTopicList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTopicList> call, @NonNull Response<JsonTopicList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Get all assigned queues", String.valueOf(response.body()));
                        topicPresenter.topicPresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while getting all queues assigned");
                        topicPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        topicPresenter.authenticationFailure();
                    } else {
                        topicPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonTopicList> call, @NonNull Throwable t) {
                Log.e("getQueues", t.getLocalizedMessage(), t);
                topicPresenter.topicPresenterError();
            }
        });
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void served(String did, String mail, String auth, Served served) {
        queueApiUrls.served(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "served" + response.body().toString());
                            manageQueuePresenter.manageQueueResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to served");
                            manageQueuePresenter.responseErrorPresenter(null);
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        Log.e(TAG, "Got error served" + errorEncounteredJson.getReason());
                        manageQueuePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        manageQueuePresenter.authenticationFailure();
                    } else {
                        manageQueuePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("served", t.getLocalizedMessage(), t);
                manageQueuePresenter.manageQueueError();
            }
        });
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void acquire(String did, String mail, String auth, Served served) {
        queueApiUrls.acquire(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                            Log.d(TAG, "acquire" + response.body().toString());
                            manageQueuePresenter.manageQueueResponse(response.body());
                        } else {
                            Log.e(TAG, "Failed to acquire");
                            manageQueuePresenter.responseErrorPresenter(null);
                        }
                    } else if (response.body() != null && response.body().getError() != null) {
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        manageQueuePresenter.responseErrorPresenter(response.body().getError());
                        Log.e(TAG, "Got error acquire" + errorEncounteredJson.getReason());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        manageQueuePresenter.authenticationFailure();
                    } else {
                        manageQueuePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("acquire", t.getLocalizedMessage(), t);
                manageQueuePresenter.manageQueueError();
            }
        });
    }

    public void getAllQueuePersonList(String did, String mail, String auth, String codeQR) {
        queueApiUrls.showClients(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("showClients", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while showClients");
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
                Log.e("showClients", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    public void getAllQueuePersonListHistory(String did, String mail, String auth, CodeQRDateRangeLookup codeQRDateRangeLookup) {
        queueApiUrls.showClientsHistorical(did, Constants.DEVICE_TYPE, mail, auth, codeQRDateRangeLookup).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("showClientsHistorical", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while showClientsHistorical");
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
                Log.e("showClientsHistorical", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    @Deprecated
    public void dispenseToken(String did, String mail, String auth, String codeQR) {
        queueApiUrls.dispenseTokenWithoutClientInfo(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("dispTokenWithoutCInfo", String.valueOf(response.body()));
                        dispenseTokenPresenter.dispenseTokenResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while dispenseTokenWithoutClientInfo");
                        ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                        dispenseTokenPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        dispenseTokenPresenter.authenticationFailure();
                    } else {
                        dispenseTokenPresenter.responseErrorPresenter(response.code());
                    }
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("dispTokenWithoutCInfo", t.getLocalizedMessage(), t);
                dispenseTokenPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void dispenseTokenWithClientInfo(String did, String mail, String auth, JsonBusinessCustomer jsonBusinessCustomer) {
        queueApiUrls.dispenseTokenWithClientInfo(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomer).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("dispTokenWithClientInfo", String.valueOf(response.body()));
                        dispenseTokenPresenter.dispenseTokenResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error dispenseTokenWithClientInfo");
                        dispenseTokenPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        dispenseTokenPresenter.authenticationFailure();
                    } else {
                        dispenseTokenPresenter.responseErrorPresenter(response.code());
                    }
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("dispTokenWithClientInfo", t.getLocalizedMessage(), t);
                dispenseTokenPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void changeUserInQueue(String did, String mail, String auth, ChangeUserInQueue changeUserInQueue) {
        queueApiUrls.changeUserInQueue(did, Constants.DEVICE_TYPE, mail, auth, changeUserInQueue).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("changeUserInQueue", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while changeUserInQueue");
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
                Log.e("changeUserInQueue", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    public void counterPayment(String did, String mail, String auth, JsonQueuedPerson jsonQueuedPerson) {
        queueApiUrls.counterPayment(did, Constants.DEVICE_TYPE, mail, auth, jsonQueuedPerson).enqueue(new Callback<JsonQueuedPerson>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuedPerson> call, @NonNull Response<JsonQueuedPerson> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("changeUserInQueue", String.valueOf(response.body()));
                        queuePaymentPresenter.queuePaymentResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while changeUserInQueue");
                        queuePaymentPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePaymentPresenter.authenticationFailure();
                    } else {
                        queuePaymentPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuedPerson> call, @NonNull Throwable t) {
                Log.e("changeUserInQueue", t.getLocalizedMessage(), t);
                queuePaymentPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void cancel(String did, String mail, String auth, JsonQueuedPerson jsonQueuedPerson) {
        queueApiUrls.cancel(did, Constants.DEVICE_TYPE, mail, auth, jsonQueuedPerson).enqueue(new Callback<JsonQueuedPerson>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuedPerson> call, @NonNull Response<JsonQueuedPerson> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("refund Q payment", String.valueOf(response.body()));
                        queueRefundPaymentPresenter.queueRefundPaymentResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while refund Q payment");
                        queueRefundPaymentPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueRefundPaymentPresenter.authenticationFailure();
                    } else {
                        queueRefundPaymentPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuedPerson> call, @NonNull Throwable t) {
                Log.e("onFail refund Q payment", t.getLocalizedMessage(), t);
                queueRefundPaymentPresenter.responseErrorPresenter(null);
            }
        });
    }

}
