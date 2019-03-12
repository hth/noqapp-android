package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.model.response.api.ManageQueueApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.presenter.beans.body.ChangeUserInQueue;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.DispenseTokenPresenter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.android.merchant.views.interfaces.TopicPresenter;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */
public class ManageQueueModel {
    private static final String TAG = ManageQueueModel.class.getSimpleName();

    private static final ManageQueueApiUrls manageQueueApiUrls;
    private ManageQueuePresenter manageQueuePresenter;
    private DispenseTokenPresenter dispenseTokenPresenter;
    private TopicPresenter topicPresenter;
    private QueuePersonListPresenter queuePersonListPresenter;


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

    static {
        manageQueueApiUrls = RetrofitClient.getClient().create(ManageQueueApiUrls.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void getQueues(String did, String mail, String auth) {
        manageQueueApiUrls.getQueues(did, Constants.DEVICE_TYPE, Constants.appVersion(), mail, auth).enqueue(new Callback<JsonTopicList>() {
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
        manageQueueApiUrls.served(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
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
        manageQueueApiUrls.acquire(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
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
        manageQueueApiUrls.showClients(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
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

    public void getAllQueuePersonListHistory(String did, String mail, String auth, String codeQR) {
        manageQueueApiUrls.showClientsHistorical(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
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


    public void dispenseToken(String did, String mail, String auth, String codeQR) {
        manageQueueApiUrls.dispenseTokenWithoutClientInfo(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonToken>() {
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


    public void dispenseTokenWithClientInfo(String did, String mail, String auth, JsonBusinessCustomerLookup jsonBusinessCustomerLookup) {
        manageQueueApiUrls.dispenseTokenWithClientInfo(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomerLookup).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("dispTokenWithClientInfo", String.valueOf(response.body()));
                        dispenseTokenPresenter.dispenseTokenResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error dispenseTokenWithClientInfo");
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
                Log.e("dispTokenWithClientInfo", t.getLocalizedMessage(), t);
                dispenseTokenPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void changeUserInQueue(String did, String mail, String auth, ChangeUserInQueue changeUserInQueue) {
        manageQueueApiUrls.changeUserInQueue(did, Constants.DEVICE_TYPE, mail, auth, changeUserInQueue).enqueue(new Callback<JsonQueuePersonList>() {
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

}
