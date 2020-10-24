package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ViewAllOrderExpandableListAdapter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ViewAllPeopleInQOrderActivity extends BaseActivity implements PurchaseOrderPresenter {
    private Map<Date, List<JsonPurchaseOrderList>> expandableListDetail = new HashMap<>();
    private ExpandableListView listview;
    private RelativeLayout rl_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_people);
        initActionsViews(false);
        listview = findViewById(R.id.exp_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        tv_toolbar_title.setText(getString(R.string.screen_order_q_history));
        setProgressMessage("Fetching data...");
        if (new NetworkUtil(this).isOnline()) {
            showProgress();
            PurchaseOrderApiCalls purchaseOrderApiCalls = new PurchaseOrderApiCalls();
            purchaseOrderApiCalls.setPurchaseOrderPresenter(this);
            purchaseOrderApiCalls.showOrdersHistorical(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), getIntent().getStringExtra("codeQR"));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    private void createData(List<JsonPurchaseOrder> temp) {
        if (null != temp && temp.size() > 0) {
            HashMap<Date, List<JsonPurchaseOrderList>> tempList = new HashMap<>();
            for (int i = 0; i < temp.size(); i++) {
                try {
                    Date key = new Date(CommonHelper.SDF_YYYY_MM_DD.parse(temp.get(i).getCreated()).getTime());
                    if (null == tempList.get(key)) {
                        tempList.put(key, new ArrayList<JsonPurchaseOrderList>());

                        tempList.get(key).add(new JsonPurchaseOrderList());
                    }
                    tempList.get(key).get(0).getPurchaseOrders().add(temp.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            expandableListDetail = new TreeMap(tempList).descendingMap();
        }
    }

    @Override
    public void purchaseOrderListResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        if (null != jsonPurchaseOrderList) {
            Log.e("data", jsonPurchaseOrderList.toString());
            Log.e("data size", "" + jsonPurchaseOrderList.getPurchaseOrders().size());
            createData(jsonPurchaseOrderList.getPurchaseOrders());
            List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
            ViewAllOrderExpandableListAdapter adapter = new ViewAllOrderExpandableListAdapter(ViewAllPeopleInQOrderActivity.this, expandableListTitle,
                    expandableListDetail, getIntent().getBooleanExtra("visibility", false));
            listview.setAdapter(adapter);
            if (expandableListTitle.size() <= 0) {
                listview.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                listview.setVisibility(View.VISIBLE);
                rl_empty.setVisibility(View.GONE);
            }

        }
        dismissProgress();
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        // do nothing
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }
}
