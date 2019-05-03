package com.noqapp.android.merchant.views.model;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.model.response.api.store.PurchaseOrderApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.store.LabFile;
import com.noqapp.android.merchant.presenter.beans.body.store.OrderServed;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.AcquireOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.LabFilePresenter;
import com.noqapp.android.merchant.views.interfaces.ModifyOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.OrderProcessedPresenter;
import com.noqapp.android.merchant.views.interfaces.PaymentProcessPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;

import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseOrderApiCalls {

    private static final String TAG = PurchaseOrderApiCalls.class.getSimpleName();
    private static final PurchaseOrderApiUrls purchaseOrderService;
    private PurchaseOrderPresenter purchaseOrderPresenter;
    private OrderProcessedPresenter orderProcessedPresenter;
    private AcquireOrderPresenter acquireOrderPresenter;
    private ImageUploadPresenter imageUploadPresenter;
    private LabFilePresenter labFilePresenter;
    private PaymentProcessPresenter paymentProcessPresenter;
    private ModifyOrderPresenter modifyOrderPresenter;

    public void setPaymentProcessPresenter(PaymentProcessPresenter paymentProcessPresenter) {
        this.paymentProcessPresenter = paymentProcessPresenter;
    }

    public void setImageUploadPresenter(ImageUploadPresenter imageUploadPresenter) {
        this.imageUploadPresenter = imageUploadPresenter;
    }

    public void setOrderProcessedPresenter(OrderProcessedPresenter orderProcessedPresenter) {
        this.orderProcessedPresenter = orderProcessedPresenter;
    }

    public void setPurchaseOrderPresenter(PurchaseOrderPresenter purchaseOrderPresenter) {
        this.purchaseOrderPresenter = purchaseOrderPresenter;
    }

    public void setAcquireOrderPresenter(AcquireOrderPresenter acquireOrderPresenter) {
        this.acquireOrderPresenter = acquireOrderPresenter;
    }

    public void setLabFilePresenter(LabFilePresenter labFilePresenter) {
        this.labFilePresenter = labFilePresenter;
    }

    public void setModifyOrderPresenter(ModifyOrderPresenter modifyOrderPresenter) {
        this.modifyOrderPresenter = modifyOrderPresenter;
    }

    static {
        purchaseOrderService = RetrofitClient.getClient().create(PurchaseOrderApiUrls.class);
    }

    public void showOrders(String did, String mail, String auth, String codeQR) {
        purchaseOrderService.showOrders(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Get order list", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while Get order list");
                        purchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        purchaseOrderPresenter.authenticationFailure();
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("Order list error", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.purchaseOrderError();
            }
        });
    }

    public void showOrdersHistorical(String did, String mail, String auth, String codeQR) {
        purchaseOrderService.showOrdersHistorical(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Get history order list", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while Get history order list");
                        purchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        purchaseOrderPresenter.authenticationFailure();
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("HistoryOrder list error", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.purchaseOrderError();
            }
        });
    }

    public void actionOnOrder(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.actionOnOrder(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("actionOnOrder", String.valueOf(response.body()));
                        orderProcessedPresenter.orderProcessedResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while actionOnOrder");
                        orderProcessedPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        orderProcessedPresenter.authenticationFailure();
                    } else {
                        orderProcessedPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("actionOnOrder fail", t.getLocalizedMessage(), t);
                orderProcessedPresenter.orderProcessedError();
            }
        });
    }

    public void cancel(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.cancel(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response Order cancel", String.valueOf(response.body()));
                        orderProcessedPresenter.orderProcessedResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while Order cancel");
                        orderProcessedPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        orderProcessedPresenter.authenticationFailure();
                    } else {
                        orderProcessedPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("Order cancel fail", t.getLocalizedMessage(), t);
                orderProcessedPresenter.orderProcessedError();
            }
        });
    }

    public void acquire(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.acquire(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("order acquire", String.valueOf(response.body()));
                        acquireOrderPresenter.acquireOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while order acquire");
                        acquireOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        acquireOrderPresenter.authenticationFailure();
                    } else {
                        acquireOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Order acquire fail", t.getLocalizedMessage(), t);
                acquireOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void served(String did, String mail, String auth, OrderServed orderServed) {
        purchaseOrderService.served(did, Constants.DEVICE_TYPE, mail, auth, orderServed).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("order served", String.valueOf(response.body()));
                        acquireOrderPresenter.acquireOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while order served");
                        acquireOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        acquireOrderPresenter.authenticationFailure();
                    } else {
                        acquireOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Order served fail", t.getLocalizedMessage(), t);
                acquireOrderPresenter.responseErrorPresenter(null);
            }
        });
    }


    public void purchase(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.purchase(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("purchase", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while purchase");
                        purchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        purchaseOrderPresenter.authenticationFailure();
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("purchase fail", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void medicalPurchase(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.medicalPurchase(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrderList>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Response<JsonPurchaseOrderList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("purchase", String.valueOf(response.body()));
                        purchaseOrderPresenter.purchaseOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while purchase");
                        purchaseOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        purchaseOrderPresenter.authenticationFailure();
                    } else {
                        purchaseOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrderList> call, @NonNull Throwable t) {
                Log.e("purchase fail", t.getLocalizedMessage(), t);
                purchaseOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void modify(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.modify(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("modify", String.valueOf(response.body()));
                        modifyOrderPresenter.modifyOrderResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while modify");
                        modifyOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        modifyOrderPresenter.authenticationFailure();
                    } else {
                        modifyOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("modify fail", t.getLocalizedMessage(), t);
                modifyOrderPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void partialCounterPayment(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.partialCounterPayment(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("partialCounterPayment", String.valueOf(response.body()));
                        paymentProcessPresenter.paymentProcessResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while partialCounterPayment");
                        paymentProcessPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        paymentProcessPresenter.authenticationFailure();
                    } else {
                        paymentProcessPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("partialCPayment fail", t.getLocalizedMessage(), t);
                paymentProcessPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void counterPayment(String did, String mail, String auth, JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderService.counterPayment(did, Constants.DEVICE_TYPE, mail, auth, jsonPurchaseOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("counterPayment", String.valueOf(response.body()));
                        paymentProcessPresenter.paymentProcessResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while counterPayment");
                        paymentProcessPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        paymentProcessPresenter.authenticationFailure();
                    } else {
                        paymentProcessPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("counterPayment fail", t.getLocalizedMessage(), t);
                paymentProcessPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void addAttachment(String did, String mail, String auth, MultipartBody.Part profileImageFile, RequestBody transactionId) {
        purchaseOrderService.addAttachment(did, Constants.DEVICE_TYPE, mail, auth, profileImageFile, transactionId).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("upload", String.valueOf(response.body()));
                        imageUploadPresenter.imageUploadResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed image upload");
                        imageUploadPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        imageUploadPresenter.authenticationFailure();
                    } else {
                        imageUploadPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("upload", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

    public void removeAttachment(String did, String mail, String auth, LabFile labFile) {
        purchaseOrderService.removeAttachment(did, Constants.DEVICE_TYPE, mail, auth, labFile).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("upload", String.valueOf(response.body()));
                        imageUploadPresenter.imageRemoveResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed image upload");
                        imageUploadPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        imageUploadPresenter.authenticationFailure();
                    } else {
                        imageUploadPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("upload", t.getLocalizedMessage(), t);
                imageUploadPresenter.imageUploadError();
            }
        });
    }

    public void showAttachment(String did, String mail, String auth, LabFile labFile) {
        purchaseOrderService.showAttachment(did, Constants.DEVICE_TYPE, mail, auth, labFile).enqueue(new Callback<LabFile>() {
            @Override
            public void onResponse(@NonNull Call<LabFile> call, @NonNull Response<LabFile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("showAttachment", String.valueOf(response.body()));
                        labFilePresenter.showAttachmentResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed showAttachment");
                        labFilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        labFilePresenter.authenticationFailure();
                    } else {
                        labFilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LabFile> call, @NonNull Throwable t) {
                Log.e("onFailureShowAttachment", t.getLocalizedMessage(), t);
                labFilePresenter.responseErrorPresenter(null);
            }
        });
    }
}
