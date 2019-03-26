package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SearchBusinessStoreApiCall;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;
import com.noqapp.android.client.views.adapters.SearchAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
public class SearchActivity extends BaseActivity implements SearchAdapter.OnItemClickListener, SearchBusinessStorePresenter {
    private ArrayList<BizStoreElastic> listData = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String scrollId = "";
    private String city = "";
    private String lat = "";
    private String longitude = "";
    private SearchBusinessStoreApiCall searchBusinessStoreModel;
    private EditText edt_search;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout ll_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView rv_search = findViewById(R.id.rv_search);
        edt_search = findViewById(R.id.edt_search);
        TextView tv_auto = findViewById(R.id.tv_auto);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        ll_search = findViewById(R.id.ll_search);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_search));
        searchBusinessStoreModel = new SearchBusinessStoreApiCall(this);
        city = getIntent().getStringExtra("city");
        lat = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("long");
        scrollId = "";
        AppUtilities.setAutoCompleteText(autoCompleteTextView, city);
        rv_search.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_search.setLayoutManager(horizontalLayoutManager);
        rv_search.setItemAnimator(new DefaultItemAnimator());
        searchAdapter = new SearchAdapter(listData, this, this, Double.parseDouble(lat), Double.parseDouble(longitude));
        rv_search.setAdapter(searchAdapter);

        edt_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_search.getRight() - edt_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        new AppUtilities().hideKeyBoard(SearchActivity.this);
                        edt_search.setText("");
                        return true;
                    }
                    if (event.getRawX() <= (20 + edt_search.getLeft() + edt_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        performSearch();
                        return true;
                    }
                }
                return false;
            }
        });
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat = String.valueOf(LaunchActivity.getLaunchActivity().latitute);
                longitude = String.valueOf(LaunchActivity.getLaunchActivity().longitute);
                city = LaunchActivity.getLaunchActivity().cityName;
                AppUtilities.setAutoCompleteText(autoCompleteTextView, city);

                new AppUtilities().hideKeyBoard(SearchActivity.this);
            }
        });

        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city_name = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                LatLng latLng = AppUtilities.getLocationFromAddress(SearchActivity.this, city_name);
                lat = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);
                city = city_name;
                new AppUtilities().hideKeyBoard(SearchActivity.this);

            }
        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        autoCompleteTextView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void performSearch() {
        if (StringUtils.isNotBlank(edt_search.getText().toString())) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                SearchStoreQuery searchStoreQuery = new SearchStoreQuery()
                        .setCityName(city)
                        .setLatitude(lat)
                        .setLongitude(longitude)
                        .setQuery(edt_search.getText().toString())
                        .setFilters("")
                        .setScrollId(""); //Scroll id - fresh search pass blank
                searchBusinessStoreModel.search(UserUtils.getDeviceId(), searchStoreQuery);
            } else {
                ShowAlertInformation.showNetworkDialog(SearchActivity.this);
            }
        }
        new AppUtilities().hideKeyBoard(SearchActivity.this);
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                Intent in = new Intent(this, JoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(this, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
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
            new ShowAlertInformation().showSnakeBar(ll_search, "Search element not found");
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

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }
}
