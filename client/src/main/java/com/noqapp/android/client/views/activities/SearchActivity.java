package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.R;
import com.noqapp.android.client.location.LocationManager;
import com.noqapp.android.client.model.api.SearchApiImpl;
import com.noqapp.android.client.model.open.SearchImpl;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.body.SearchQuery;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;
import com.noqapp.android.client.views.adapters.SearchAdapter;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.model.types.BusinessSupportEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.GeoIP;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function9;

/**
 * Created by chandra on 5/7/17.
 */
public class SearchActivity
    extends BaseActivity
    implements
        SearchAdapter.OnItemClickListener,
        SearchBusinessStorePresenter,
        Function9<String, String, String, String, String, String, String, Double, Double, Unit>
{
    private ArrayList<BizStoreElastic> listData = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String scrollId = "";
    private String city = "";
    private String area = "";
    private String town = "";
    private String lat = "";
    private String lng = "";
    private SearchImpl searchImpl;
    private SearchApiImpl searchApiImpl;
    private EditText edt_search;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout ll_search;
    private final String TAG = SearchActivity.class.getSimpleName();
    private RelativeLayout rl_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView rv_search = findViewById(R.id.rv_search);
        edt_search = findViewById(R.id.edt_search);
        rl_empty = findViewById(R.id.rl_empty);
        TextView tv_auto = findViewById(R.id.tv_auto);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        ll_search = findViewById(R.id.ll_search);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_search));
        searchImpl = new SearchImpl(this);
        searchApiImpl = new SearchApiImpl(this);
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
                    applySearch();
                    return true;
                }
            }
            return false;
        });
        edt_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applySearch();
                return true;
            }
            return false;
        });

        tv_auto.setOnClickListener((View v) -> LocationManager.INSTANCE.fetchCurrentLocationAddress(this, this));

        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String city = (String) parent.getItemAtPosition(position);
            GeoIP geoIP = AppUtils.getLocationFromAddress(SearchActivity.this, city);
            if (null != geoIP) {
                lat = String.valueOf(geoIP.getLatitude());
                lng = String.valueOf(geoIP.getLongitude());
                area = geoIP.getArea();
                town = geoIP.getTown();
            }
            this.city = city;
            AppUtils.hideKeyBoard(SearchActivity.this);
            initDefaultLatLng();
            edt_search.setText("");
            edt_search.requestFocus();

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
        if (NoQueueClientApplication.isLockMode) {
            tv_auto.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.GONE);
            edt_search.setText(getIntent().getStringExtra("searchString"));
            performKioskSearch();
        }
        if (AppUtils.isRelease()) {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.SEARCH_TERM, AnalyticsEvents.EVENT_SEARCH);
            NoQueueClientApplication.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_SEARCH, params);
        }
        edt_search.requestFocus();
    }

    private void initDefaultLatLng() {
        if (null == lat || lng == null) {
            LocationPref locationPref = NoQueueClientApplication.getLocationPreference();
            lat = String.valueOf(locationPref.getLatitude());
            lng = String.valueOf(locationPref.getLongitude());
            city = locationPref.getLocationAsString();
            area = locationPref.getArea();
            town = locationPref.getTown();
        }
    }

    private void performSearch() {
        if (StringUtils.isNotBlank(edt_search.getText().toString())) {
            if (isOnline()) {
                showProgress();
                SearchQuery searchQuery = new SearchQuery()
                        .setCityName(city)
                        .setLatitude(lat)
                        .setLongitude(lng)
                        .setQuery(edt_search.getText().toString())
                        .setFilters("")
                        .setScrollId(""); //Scroll id - fresh search pass blank
                if (UserUtils.isLogin()) {
                    searchApiImpl.search(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), searchQuery);
                } else {
                    searchImpl.search(UserUtils.getDeviceId(), searchQuery);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(SearchActivity.this);
            }
        }
        AppUtils.hideKeyBoard(SearchActivity.this);
    }

    private void performKioskSearch() {
        if (StringUtils.isNotBlank(edt_search.getText().toString())) {
            if (isOnline()) {
                showProgress();
                SearchQuery searchQuery = new SearchQuery()
                        .setCityName(city)
                        .setLatitude(lat)
                        .setLongitude(lng)
                        .setQuery(edt_search.getText().toString())
                        .setCodeQR(getIntent().getStringExtra("codeQR"))
                        .setFilters("")
                        .setScrollId(""); //Scroll id - fresh search pass blank
                searchImpl.kiosk(UserUtils.getDeviceId(), searchQuery);
            } else {
                ShowAlertInformation.showNetworkDialog(SearchActivity.this);
            }
        }
        AppUtils.hideKeyBoard(SearchActivity.this);
    }


    @Override
    public void onStoreItemClick(BizStoreElastic item) {
        Intent in;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            //Level up
            case DO:
            case CD:
            case CDQ:
            case BK:
            case HS:
            case PW:
                if (NoQueueClientApplication.isLockMode) {
                    in = new Intent(this, KioskActivity.class);
                } else {
                    in = new Intent(this, BeforeJoinActivity.class);
                }
                in.putExtra(IBConstant.KEY_IS_DO, item.getBusinessType() == BusinessTypeEnum.DO);
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
            case RSQ:
            case GSQ:
            case BAQ:
            case CFQ:
            case FTQ:
            case STQ:
                //@TODO Modification done due to corona crisis, Re-check all the functionality
                //proper testing required
                if (BusinessSupportEnum.OQ == item.getBusinessType().getBusinessSupport()) {
                    in = new Intent(this, BeforeJoinOrderQueueActivity.class);
                    b.putString(IBConstant.KEY_CODE_QR, item.getCodeQR());
                    b.putBoolean(IBConstant.KEY_FROM_LIST, false);
                    b.putBoolean(IBConstant.KEY_IS_CATEGORY, false);
                    b.putSerializable("BizStoreElastic", item);
                    in.putExtras(b);
                    startActivity(in);
                } else {
                    Log.d(TAG, "Reached un-supported condition");
                    FirebaseCrashlytics.getInstance().log("Reached un-supported condition  " + item.getBusinessType());
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
    public void nearMeMerchant(BizStoreElasticList bizStoreElasticList) {
        ArrayList<BizStoreElastic> nearMeData = new ArrayList<>(bizStoreElasticList.getBizStoreElastics());
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
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            rl_empty.setVisibility(View.GONE);
        }
        dismissProgress();

    }

    @Override
    public void nearMeMerchantError() {
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
    public void nearMeCanteenResponse(BizStoreElasticList bizStoreElasticList) {
        // Do nothing
    }

    @Override
    public void nearMeCanteenError() {
        // Do nothing
    }

    @Override
    public void nearMeRestaurantsResponse(BizStoreElasticList bizStoreElasticList) {
        // Do nothing
    }

    @Override
    public void nearMeRestaurantsError() {
        // Do nothing
    }

    @Override
    public void nearMeTempleResponse(BizStoreElasticList bizStoreElasticList) {
        // Do nothing
    }

    @Override
    public void nearMeTempleError() {
        // Do nothing
    }


    private void applySearch() {
        if (NoQueueClientApplication.isLockMode) {
            performKioskSearch();
        } else {
            performSearch();
        }
    }

    @Override
    public Unit invoke(String address, String countryShortName, String area, String town, String district, String state, String stateShortName, Double latitude, Double longitude) {
        if (latitude == 0 && longitude == 0) {
            LocationPref locationPref = NoQueueClientApplication.getLocationPreference();
            lat = String.valueOf(locationPref.getLatitude());
            lng = String.valueOf(locationPref.getLongitude());
            this.area = locationPref.getArea();
            this.town = locationPref.getTown();
        } else {
            lat = String.valueOf(latitude);
            lng = String.valueOf(longitude);
            this.area = area;
            this.town = town;
        }

        String city = town;
        if (StringUtils.isNotBlank(area)) {
            city = this.area + ", " + this.town;
        }
        AppUtils.setAutoCompleteText(autoCompleteTextView, city);
        AppUtils.hideKeyBoard(this);
        return null;
    }
}
