package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.model.response.api.ManageQueueService;
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

import android.support.annotation.NonNull;
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

    private static final ManageQueueService manageQueueService;
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
        manageQueueService = RetrofitClient.getClient().create(ManageQueueService.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public void getQueues(String did, String mail, String auth) {
        manageQueueService.getQueues(did, Constants.DEVICE_TYPE, Constants.appVersion(), mail, auth).enqueue(new Callback<JsonTopicList>() {
            @Override
            public void onResponse(@NonNull Call<JsonTopicList> call, @NonNull Response<JsonTopicList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    topicPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get all assigned queues", String.valueOf(response.body()));
                    topicPresenter.topicPresenterResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while getting all queues assigned");
                    topicPresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonTopicList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
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
        manageQueueService.served(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    manageQueuePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                        Log.d(TAG, "After clicking Next, response jsonToken" + response.body().toString());
                        manageQueuePresenter.manageQueueResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to get response");
                        manageQueuePresenter.responseErrorPresenter(null);
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                    manageQueuePresenter.responseErrorPresenter(response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
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
        manageQueueService.acquire(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    manageQueuePresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                        Log.d(TAG, "After clicking Next, response jsonToken" + response.body().toString());
                        manageQueuePresenter.manageQueueResponse(response.body());
                    } else {
                        //TODO something logical
                        Log.e(TAG, "Failed to get response");
                        manageQueuePresenter.responseErrorPresenter(null);
                        ;
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    manageQueuePresenter.responseErrorPresenter(response.body().getError());
                    ;
                    Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                manageQueuePresenter.manageQueueError();
            }
        });
    }

    public void getAllQueuePersonList(String did, String mail, String auth, String codeQR) {
        manageQueueService.showClients(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    queuePersonListPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    queuePersonListPresenter.queuePersonListResponse(response.body());
                } else {
                    Log.e(TAG, "Found error while get queue setting");
                    queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    ;
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

    public void getAllQueuePersonListHistory(String did, String mail, String auth, String codeQR) {
        manageQueueService.showClientsHistorical(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    queuePersonListPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    queuePersonListPresenter.queuePersonListResponse(response.body());
                } else {
                    Log.e(TAG, "Found error while get queue setting");
                    queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    ;
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }


    public void dispenseToken(String did, String mail, String auth, String codeQR) {
        manageQueueService.dispenseTokenWithoutClientInfo(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    dispenseTokenPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    dispenseTokenPresenter.dispenseTokenResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while get queue setting");
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    dispenseTokenPresenter.responseErrorPresenter(response.body().getError());
                    ;
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                dispenseTokenPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void dispenseTokenWithClientInfo(String did, String mail, String auth, JsonBusinessCustomerLookup jsonBusinessCustomerLookup) {
        manageQueueService.dispenseTokenWithClientInfo(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomerLookup).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    dispenseTokenPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    dispenseTokenPresenter.dispenseTokenResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while get queue setting");
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    dispenseTokenPresenter.responseErrorPresenter(response.body().getError());
                    ;
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                dispenseTokenPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void changeUserInQueue(String did, String mail, String auth, ChangeUserInQueue changeUserInQueue) {
        manageQueueService.changeUserInQueue(did, Constants.DEVICE_TYPE, mail, auth, changeUserInQueue).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    queuePersonListPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    queuePersonListPresenter.queuePersonListResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while get queue setting");
                    queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    ;
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }

}
