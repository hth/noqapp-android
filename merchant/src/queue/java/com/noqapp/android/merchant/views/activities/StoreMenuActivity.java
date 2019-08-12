package com.noqapp.android.merchant.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.MenuOrderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.FragmentDummyMenu;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;
import com.noqapp.android.merchant.views.model.StoreProductApiCalls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreMenuActivity extends BaseActivity implements StoreProductPresenter,
        MenuHeaderAdapter.OnItemClickListener, MenuOrderAdapter.CartOrderUpdate,
        FindCustomerPresenter, PurchaseOrderPresenter, RegistrationActivity.RegisterCallBack,
        LoginActivity.LoginCallBack {

    private Button tv_place_order;
    private RecyclerView rcv_header;
    // private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private HashMap<String, StoreCartItem> orders = new HashMap<>();
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();
    private PurchaseOrderApiCalls purchaseOrderApiCalls;
    private BusinessCustomerApiCalls businessCustomerApiCalls;
    private EditText edt_mobile;
    private Spinner sp_patient_list;
    private TextView tv_select_patient;
    private Button btn_create_order, btn_create_token;
    private String codeQR = "";
    public static UpdateWholeList updateWholeList;
    private long lastPress;
    private Toast backPressToast;
    private ArrayList<String> ordersList= new ArrayList<>();

    public interface UpdateWholeList {
        void updateWholeList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        setProgressMessage("Fetching data...");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Menu");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> {
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        });
        rcv_header = findViewById(R.id.rcv_header);
        tv_place_order = findViewById(R.id.tv_place_order);
        codeQR = getIntent().getStringExtra("codeQR");

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            StoreProductApiCalls storeProductApiCalls = new StoreProductApiCalls();
            storeProductApiCalls.setStoreProductPresenter(this);
            storeProductApiCalls.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
        purchaseOrderApiCalls = new PurchaseOrderApiCalls();
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setFindCustomerPresenter(this);
        purchaseOrderApiCalls.setPurchaseOrderPresenter(this);

        tv_place_order.setOnClickListener(v -> showCreateTokenDialogWithMobile(StoreMenuActivity.this, codeQR));

    }

    @Override
    public void storeProductResponse(JsonStore jsonStore) {
        dismissProgress();
        if (null != jsonStore) {
            String defaultCategory = "Un-Categorized";
            jsonStoreCategories.clear();
            jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<StoreCartItem>> listDataChild = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                listDataChild.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<StoreCartItem>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    listDataChild.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                } else {
                    //TODO(hth) when product without category else it will drop
                    if (null == listDataChild.get(defaultCategory)) {
                        listDataChild.put(defaultCategory, new ArrayList<StoreCartItem>());
                    }
                    listDataChild.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                }
            }

            if (null != listDataChild.get(defaultCategory)) {
                jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
            }

            rcv_header = findViewById(R.id.rcv_header);
            viewPager = findViewById(R.id.pager);
            TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
            ArrayList<Integer> removeEmptyData = new ArrayList<>();
            for (int i = 0; i < jsonStoreCategories.size(); i++) {
                if (listDataChild.get(jsonStoreCategories.get(i).getCategoryId()).size() > 0)
                    adapter.addFragment(new FragmentDummyMenu(listDataChild.get(jsonStoreCategories.get(i).getCategoryId()), this, this, LaunchActivity.getCurrencySymbol()), "FRAG" + i);
                else
                    removeEmptyData.add(i);
            }
            // Remove the categories which having zero items
            for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
                jsonStoreCategories.remove((int) removeEmptyData.get(j));
            }
            rcv_header.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rcv_header.setLayoutManager(horizontalLayoutManagaer);
            rcv_header.setItemAnimator(new DefaultItemAnimator());

            menuAdapter = new MenuHeaderAdapter(jsonStoreCategories, this, this);
            rcv_header.setAdapter(menuAdapter);
            menuAdapter.notifyDataSetChanged();
            viewPager.setAdapter(null);
            orders.clear();
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    rcv_header.smoothScrollToPosition(position);
                    menuAdapter.setSelected_pos(position);
                    menuAdapter.notifyDataSetChanged();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void updateCartOrderInfo(int amountString) {
        if (amountString > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is: " + amountString);
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }

    @Override
    public void updateCartOrder(String item) {
        if(!ordersList.contains(item)){
            ordersList.add(item);
        }
        if (ordersList.size() > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is filled " );
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }

    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej && eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            btn_create_token.setClickable(true);
            new CustomToast().showToast(this, eej.getReason());
            Intent in = new Intent(this, LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else {
            new ErrorResponseHandler().processError(this, eej);
        }
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        new CustomToast().showToast(mContext,ordersList.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_create_order_with_mobile, null, false);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        TextView tv_create_token = view.findViewById(R.id.tvtitle);
        TextView tv_toolbar_title = view.findViewById(R.id.tv_toolbar_title);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);
        tv_toolbar_title.setText("Create Order");
        tv_create_token.setText("Click button to create order");
        edt_mobile = view.findViewById(R.id.edt_mobile);
        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        final RadioButton rb_customer_id = view.findViewById(R.id.rb_customer_id);
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    edt_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    edt_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        rb_customer_id.setVisibility(View.GONE);
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token.setText("Search Customer");
        btn_create_token.setOnClickListener(v -> {
            boolean isValid = true;
            edt_mobile.setError(null);
            edt_id.setError(null);
            new AppUtils().hideKeyBoard(StoreMenuActivity.this);
            int selectedId = rg_user_id.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_mobile) {
                if (TextUtils.isEmpty(edt_mobile.getText())) {
                    edt_mobile.setError(getString(R.string.error_mobile_blank));
                    isValid = false;
                }
            } else {
                if (TextUtils.isEmpty(edt_id.getText())) {
                    edt_id.setError(getString(R.string.error_customer_id));
                    isValid = false;
                }
            }
            if (isValid) {
                String phone = "";
                String cid = "";
                if (rb_mobile.isChecked()) {
                    edt_id.setText("");
                    phone = "91" + edt_mobile.getText().toString();
                } else {
                    cid = edt_id.getText().toString();
                    edt_mobile.setText("");// set blank so that wrong phone no. not pass to login screen
                }
                showProgress();
                setProgressMessage("Searching user...");
                setProgressCancel(false);
                businessCustomerApiCalls.findCustomer(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                btn_create_token.setClickable(false);
                // mAlertDialog.dismiss();

            }
        });
        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        // coming from login activity
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        // coming from registration activity
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void findCustomerResponse(final JsonProfile jsonProfile) {
        dismissProgress();
        if (null != jsonProfile) {
            List<JsonProfile> jsonProfileList = new ArrayList<>();
            jsonProfileList.add(jsonProfile);
            if (jsonProfile.getDependents().size() > 0) {
                jsonProfileList.addAll(jsonProfile.getDependents());
            }
            JsonProfileAdapter adapter = new JsonProfileAdapter(this, jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setEnabled(false);
            sp_patient_list.setVisibility(View.VISIBLE);
            edt_mobile.setEnabled(false);
            //tv_select_patient.setVisibility(View.VISIBLE);
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(v -> {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    setProgressMessage("Placing order....");
                    showProgress();
                    setProgressCancel(false);
                    HashMap<String, StoreCartItem> getOrder = getOrders();
                    List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                    int price = 0;
                    for (StoreCartItem value : getOrder.values()) {
                        ll.add(new JsonPurchaseOrderProduct()
                                .setProductId(value.getJsonStoreProduct().getProductId())
                                .setProductPrice(value.getFinalDiscountedPrice() * 100)
                                .setProductQuantity(value.getChildInput())
                                .setProductName(value.getJsonStoreProduct().getProductName()));
                        price += value.getChildInput() * value.getFinalDiscountedPrice() * 100;
                    }
                    JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                            .setCodeQR(codeQR)
                            .setQueueUserId(jsonProfile.getQueueUserId())
                            .setOrderPrice(String.valueOf(price));
                    jsonPurchaseOrder.setPurchaseOrderProducts(ll);
                    jsonPurchaseOrder.setDeliveryAddress(jsonProfile.getAddress());
                    jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.TO);
                    jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA);
                    jsonPurchaseOrder.setCustomerPhone(jsonProfile.getPhoneRaw());
                    jsonPurchaseOrder.setAdditionalNote("");
                    purchaseOrderApiCalls.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(StoreMenuActivity.this);
                }
            });
        }
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }


    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = new CustomToast().getToast(this, "Press back to exit");
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }
}