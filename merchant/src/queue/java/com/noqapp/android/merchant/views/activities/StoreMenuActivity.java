package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.MenuOrderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.FragmentDummyMenu;
import com.noqapp.android.merchant.views.interfaces.DispenseTokenPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.StoreProductModel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreMenuActivity extends AppCompatActivity implements StoreProductPresenter, MenuHeaderAdapter.OnItemClickListener,
        MenuOrderAdapter.CartOrderUpdate, DispenseTokenPresenter, RegistrationActivity.RegisterCallBack, LoginActivity.LoginCallBack {


    private Button tv_place_order;
    private RecyclerView rcv_header;
    // private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private HashMap<String, ChildData> orders = new HashMap<>();
    private ProgressDialog progressDialog;
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();
    private ManageQueueModel manageQueueModel;
    private EditText edt_mobile;

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        initProgress();
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Menu");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        rcv_header = findViewById(R.id.rcv_header);
        tv_place_order = findViewById(R.id.tv_place_order);
        //  jsonQueue = (JsonQueue) getIntent().getSerializableExtra("jsonQueue");
        final String codeQR = getIntent().getStringExtra("codeQR");

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            StoreProductModel storeProductModel = new StoreProductModel();
            storeProductModel.setStoreProductPresenter(this);
            storeProductModel.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
        manageQueueModel = new ManageQueueModel();
        manageQueueModel.setDispenseTokenPresenter(this);

        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (UserUtils.isLogin()) {
//                    if (LaunchActivity.getLaunchActivity().isOnline()) {
//                        //HashMap<String, ChildData> getOrder = expandableListAdapter.getOrders();  old one
//                        HashMap<String, ChildData> getOrder = getOrders();
//
//                        List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
//                        int price = 0;
//                        for (ChildData value : getOrder.values()) {
//                            ll.add(new JsonPurchaseOrderProduct()
//                                    .setProductId(value.getJsonStoreProduct().getProductId())
//                                    .setProductPrice(value.getFinalDiscountedPrice() * 100)
//                                    .setProductQuantity(value.getChildInput())
//                                    .setProductName(value.getJsonStoreProduct().getProductName()));
//                            price += value.getChildInput() * value.getFinalDiscountedPrice() * 100;
//                        }
//                        if (price / 100 >= jsonQueue.getMinimumDeliveryOrder()) {
//                            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
//                                    .setBizStoreId(jsonQueue.getBizStoreId())
//                                    .setBusinessType(jsonQueue.getBusinessType())
//                                    .setOrderPrice(String.valueOf(price));
//                            jsonPurchaseOrder.setPurchaseOrderProducts(ll);
//
//                            Intent intent = new Intent(StoreMenuActivity.this, OrderActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("data", jsonPurchaseOrder);
//                            bundle.putString("storeName", jsonQueue.getDisplayName());
//                            bundle.putString("storeAddress", jsonQueue.getStoreAddress());
//                            bundle.putInt("deliveryRange", jsonQueue.getDeliveryRange());
//                            bundle.putString("topic", jsonQueue.getTopic());
//                            bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
//                            bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(StoreMenuActivity.this, "Minimum cart amount is " + jsonQueue.getMinimumDeliveryOrder(), Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        ShowAlertInformation.showNetworkDialog(StoreMenuActivity.this);
//                    }
//                } else {
//                    // Navigate to login screen
//                    Intent loginIntent = new Intent(StoreMenuActivity.this, LoginActivity.class);
//                    startActivity(loginIntent);
//                }

                showCreateTokenDialogWithMobile(StoreMenuActivity.this, codeQR);
            }
        });

    }

    @Override
    public void storeProductResponse(JsonStore jsonStore) {
        dismissProgress();
        if (null != jsonStore) {
            String defaultCategory = "Un-Categorized";
            jsonStoreCategories.clear();
            jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<ChildData>> listDataChild = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                listDataChild.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<ChildData>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    listDataChild.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new ChildData(0, jsonStoreProducts.get(k)));
                } else {
                    //TODO(hth) when product without category else it will drop
                    if (null == listDataChild.get(defaultCategory)) {
                        listDataChild.put(defaultCategory, new ArrayList<ChildData>());
                    }
                    listDataChild.get(defaultCategory).add(new ChildData(0, jsonStoreProducts.get(k)));
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


//    @Override
//    public void updateCartInfo(int amountString) {
//        updateCartOrderInfo(amountString);
//    }

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

    public HashMap<String, ChildData> getOrders() {
        return orders;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej && eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            Toast.makeText(this, eej.getReason(), Toast.LENGTH_LONG).show();
            Intent in = new Intent(this, LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else {
            new ErrorResponseHandler().processError(this, eej);
        }
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_create_token_with_mobile, null, false);
        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        TextView tv_create_token = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_toolbar_title = customDialogView.findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Create order");
        tv_create_token.setText("Click button to create order");
        ImageView iv_banner = customDialogView.findViewById(R.id.iv_banner);
        TextView tvcount = customDialogView.findViewById(R.id.tvcount);
        edt_mobile = customDialogView.findViewById(R.id.edt_mobile);
        final EditText edt_id = customDialogView.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = customDialogView.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = customDialogView.findViewById(R.id.rb_mobile);
        builder.setView(customDialogView);
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
        final Button btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setText("Create order");
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
                    if (btn_create_token.getText().equals(mContext.getString(R.string.create_token))) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();


                        String phone = "";
                        String cid = "";
                        if (rb_mobile.isChecked()) {
                            edt_id.setText("");
                            phone = "91" + edt_mobile.getText().toString();
                        } else {
                            cid = edt_id.getText().toString();
                            edt_mobile.setText("");// set blank so that wrong phone no. not pass to login screen
                        }
                        manageQueueModel.dispenseTokenWithClientInfo(
                                BaseLaunchActivity.getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                        btn_create_token.setClickable(false);
                        mAlertDialog.dismiss();
                    } else {
                        mAlertDialog.dismiss();
                    }
                }
            }
        });

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    @Override
    public void passPhoneNo(String phoneNo, String countryShortName) {
        dismissProgress();
    }

    @Override
    public void dispenseTokenResponse(JsonToken token) {
        dismissProgress();
    }
}