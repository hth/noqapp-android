package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SearchBusinessStoreApiCalls;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;
import com.noqapp.android.client.views.adapters.SearchAdapter;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by chandra on 5/7/17.
 */
public class SearchActivity extends BaseActivity implements SearchAdapter.OnItemClickListener,
        SearchBusinessStorePresenter {
    private ArrayList<BizStoreElastic> listData = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String scrollId = "";
    private String city = "";
    private String lat = "";
    private String lng = "";
    private SearchBusinessStoreApiCalls searchBusinessStoreModels;
    private EditText edt_search;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout ll_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView rv_search = findViewById(R.id.rv_search);
        edt_search = findViewById(R.id.edt_search);
        TextView tv_auto = findViewById(R.id.tv_auto);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        ll_search = findViewById(R.id.ll_search);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_search));
        searchBusinessStoreModels = new SearchBusinessStoreApiCalls(this);
        city = getIntent().getStringExtra("city");
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        initDefaultLatLng();
        scrollId = "";
        AppUtils.setAutoCompleteText(autoCompleteTextView, city);
        rv_search.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_search.setLayoutManager(horizontalLayoutManager);
        rv_search.setItemAnimator(new DefaultItemAnimator());
        searchAdapter = new SearchAdapter(listData, this, this, Double.parseDouble(lat), Double.parseDouble(lng));
        rv_search.setAdapter(searchAdapter);

        edt_search.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_LEFT = 0;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_search.getRight() - edt_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    AppUtils.hideKeyBoard(SearchActivity.this);
                    edt_search.setText("");
                    return true;
                }
                if (event.getRawX() <= (20 + edt_search.getLeft() + edt_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    performSearch();
                    return true;
                }
            }
            return false;
        });
        edt_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });


        tv_auto.setOnClickListener((View v) -> {
            lat = String.valueOf(LaunchActivity.getLaunchActivity().latitude);
            lng = String.valueOf(LaunchActivity.getLaunchActivity().longitude);
            city = LaunchActivity.getLaunchActivity().cityName;
            AppUtils.setAutoCompleteText(autoCompleteTextView, city);
            AppUtils.hideKeyBoard(SearchActivity.this);
        });

        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String city_name = (String) parent.getItemAtPosition(position);
            LatLng latLng = AppUtils.getLocationFromAddress(SearchActivity.this, city_name);
            if (null != latLng) {
                lat = String.valueOf(latLng.latitude);
                lng = String.valueOf(latLng.longitude);
            }
            city = city_name;
            AppUtils.hideKeyBoard(SearchActivity.this);
            initDefaultLatLng();

        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    autoCompleteTextView.setText("");
                    return true;
                }
            }
            return false;
        });
        if (LaunchActivity.isLockMode) {
            tv_auto.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.GONE);
            edt_search.setText(getIntent().getStringExtra("searchString"));
            performSearch();
        }
        if (AppUtils.isRelease()) {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.SEARCH_TERM, FabricEvents.EVENT_SEARCH);
            LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(FabricEvents.EVENT_SEARCH, params);
        }
    }

    private void initDefaultLatLng() {
        if (null == lat || lng == null) {
            lat = String.valueOf(Constants.DEFAULT_LATITUDE);
            lng = String.valueOf(Constants.DEFAULT_LONGITUDE);
            city = Constants.DEFAULT_CITY;
        }
    }

    private void performSearch() {
        if (StringUtils.isNotBlank(edt_search.getText().toString())) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                SearchStoreQuery searchStoreQuery = new SearchStoreQuery()
                        .setCityName(city)
                        .setLatitude(lat)
                        .setLongitude(lng)
                        .setQuery(edt_search.getText().toString())
                        .setFilters("")
                        .setScrollId(""); //Scroll id - fresh search pass blank
                searchBusinessStoreModels.search(UserUtils.getDeviceId(), searchStoreQuery);
            } else {
                ShowAlertInformation.showNetworkDialog(SearchActivity.this);
            }
        }
        AppUtils.hideKeyBoard(SearchActivity.this);
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item) {
        Intent in = null;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            //Level up
            case DO:
            case BK:
            case HS:
                if(LaunchActivity.isLockMode){
                    in = new Intent(this, KioskJoinActivity.class);
                }else {
                    in = new Intent(this, BeforeJoinActivity.class);
                }
                in.putExtra(IBConstant.KEY_IS_DO,item.getBusinessType()== BusinessTypeEnum.DO);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                startActivity(in);
                break;
            case PH: {
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
            break;
            default: {
                // open order screen
                in = new Intent(this, StoreWithMenuActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {
        ArrayList<BizStoreElastic> nearMeData = new ArrayList<>();
        nearMeData.addAll(bizStoreElasticList.getBizStoreElastics());
        scrollId = bizStoreElasticList.getScrollId();
        if (scrollId == null) {
            scrollId = "";
        }
        //sort the list, give the Comparator the current location
        // Collections.sort(nearMeData, new SortPlaces(new LatLng(Double.parseDouble(lat), Double.parseDouble(longitude))));
        listData.clear();
        listData.addAll(nearMeData);
        searchAdapter.notifyDataSetChanged();
        if (bizStoreElasticList.getBizStoreElastics().size() == 0) {
            new ShowAlertInformation().showSnakeBar(ll_search, "Search result empty");
        }
        dismissProgress();

    }

    @Override
    public void nearMeError() {
        dismissProgress();
    }

    @Override
    public void nearMeHospitalResponse(BizStoreElasticList bizStoreElasticList) {
        // Do nothing
    }

    @Override
    public void nearMeHospitalError() {
        dismissProgress();
    }
}
