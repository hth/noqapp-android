package com.noqapp.android.client.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.NoQueueDBPresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AfterJoinActivity;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.DoctorProfile1Activity;
import com.noqapp.android.client.views.activities.JoinActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.SearchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.ViewAllListActivity;
import com.noqapp.android.client.views.adapters.CurrentActivityAdapter;
import com.noqapp.android.client.views.adapters.RecentActivityAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.customviews.CirclePagerIndicatorDecoration;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanQueueFragment extends Scanner implements CurrentActivityAdapter.OnItemClickListener, RecentActivityAdapter.OnItemClickListener, NearMePresenter, StoreInfoAdapter.OnItemClickListener, TokenAndQueuePresenter, TokenQueueViewInterface {
    private final String TAG = ScanQueueFragment.class.getSimpleName();

    @BindView(R.id.cv_scan)
    protected CardView cv_scan;

    @BindView(R.id.rv_recent_activity)
    protected RecyclerView rv_recent_activity;
    @BindView(R.id.rv_current_activity)
    protected RecyclerView rv_current_activity;
    @BindView(R.id.tv_current_title)
    protected TextView tv_current_title;
    private static final int MSG_CURRENT_QUEUE = 0;
    private static final int MSG_HISTORY_QUEUE = 1;
    private static TokenQueueViewInterface tokenQueueViewInterface;
    @BindView(R.id.rv_merchant_around_you)
    protected RecyclerView rv_merchant_around_you;
    private boolean fromList = false;
    private CurrentActivityAdapter currentActivityAdapter;
    private StoreInfoAdapter storeInfoAdapter;
    private RecentActivityAdapter recentActivityAdapter;
    private ArrayList<BizStoreElastic> nearMeData;
    private CurrentActivityAdapter.OnItemClickListener currentClickListner;
    private RecentActivityAdapter.OnItemClickListener recentClickListner;
    private StoreInfoAdapter.OnItemClickListener storeListener;

    @BindView(R.id.tv_recent_view_all)
    protected TextView tv_recent_view_all;
    @BindView(R.id.tv_near_view_all)
    protected TextView tv_near_view_all;

    private static QueueHandler mHandler;
    @BindView(R.id.btn_temp)
    protected Button btn_temp;

    @BindView(R.id.spinner)
    protected Spinner spinner;


    @BindView(R.id.pb_current)
    protected ProgressBar pb_current;

    @BindView(R.id.pb_recent)
    protected ProgressBar pb_recent;

    @BindView(R.id.pb_near)
    protected ProgressBar pb_near;

    @BindView(R.id.autoCompleteTextView)
    protected AutoCompleteTextView autoCompleteTextView;

    //TODO(chandra) temp code
    private String[] city = {"Mumbai", "Delhi", "Calcutta"};
    private String[] lat_array = {"19.004550", "28.553399", "22.572645"};
    private String[] log_array = {"73.014529", "77.194165", "88.363892"};
    private int city_select = 0 ;
    private String scrollId = "";



    private static final String LOG_TAG = "ExampleApp";

    private final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private final String OUT_JSON = "/json";
    private final String COUNTRY_CODE = "IN";
    private final String API_KEY = "AIzaSyA9eHl3SHvjXmHFq9q5yPjRy0uqBd5awSc";


    public ScanQueueFragment() {

    }

    private static class QueueHandler extends Handler {
        private boolean isCurrentExecute = false;
        private boolean isHistoryExecute = false;

        // This method is used to handle received messages
        public void handleMessage(Message msg) {
            // switch to identify the message by its code
            switch (msg.what) {
                case MSG_CURRENT_QUEUE:
                    //doSomething();
                    isCurrentExecute = true;
                    if (isHistoryExecute && isCurrentExecute) {
                        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(LaunchActivity.getLaunchActivity());
                        dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
                    }
                    break;

                case MSG_HISTORY_QUEUE:
                    //doMoreThings();
                    isHistoryExecute = true;
                    if (isHistoryExecute && isCurrentExecute) {
                        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(LaunchActivity.getLaunchActivity());
                        dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                fromList = true;
            } else {
                startScanningBarcode();
            }
        } else {
            // startScanningBarcode();
            // commented due to last discussion that barcode should not start automatically
        }


        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, city);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);


        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        autoCompleteTextView.setText("");
                        return true;
                    }
                    if (event.getRawX() <= (autoCompleteTextView.getLeft() + autoCompleteTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        // your action here
                        autoCompleteTextView.setText(city_select);
                        return true;
                    }
                }
                return false;
            }
        });

        tokenQueueViewInterface = this;
        currentClickListner = this;
        recentClickListner = this;
        storeListener = this;
        rv_recent_activity.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recent_activity.setLayoutManager(horizontalLayoutManagaer);
        rv_recent_activity.setItemAnimator(new DefaultItemAnimator());


        //

        rv_current_activity.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer2
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_current_activity.setLayoutManager(horizontalLayoutManagaer2);
        rv_current_activity.setItemAnimator(new DefaultItemAnimator());
        rv_current_activity.addItemDecoration(new CirclePagerIndicatorDecoration());


        //
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer1
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManagaer1);
        // rv_merchant_around_you.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());


        getNearMeInfo(city[0], lat_array[0], log_array[0]);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getNearMeInfo(city[position], lat_array[position], log_array[position]);
                city_select = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            mHandler = new QueueHandler();

            if (UserUtils.isLogin()) { // Call secure API if user is loggedIn else normal API
                //Call the current queue
                QueueApiModel.tokenAndQueuePresenter = this;
                QueueApiModel.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());

                //Call the history queue
                DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
                QueueApiModel.allHistoricalJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
            } else {
                //Call the current queue
                QueueModel.tokenAndQueuePresenter = this;
                QueueModel.getAllJoinedQueue(UserUtils.getDeviceId());

                //Call the history queue
                DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
                QueueModel.getHistoryQueueList(UserUtils.getDeviceId(), deviceToken);
            }
            pb_current.setVisibility(View.VISIBLE);
            pb_recent.setVisibility(View.VISIBLE);

        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.tab_scan));
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
        fetchCurrentAndHistoryList();
    }

    @OnClick(R.id.cv_scan)
    public void scanQR() {
        startScanningBarcode();
    }

    @Override
    protected void barcodeResult(String codeQR, boolean isCategoryData) {
        Bundle b = new Bundle();
        b.putString(KEY_CODE_QR, codeQR);
        b.putBoolean(KEY_FROM_LIST, fromList);
        b.putBoolean(KEY_IS_HISTORY, false);
        if (isCategoryData) {
            Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
            in.putExtra("bundle", b);
            getActivity().startActivity(in);

        } else {
            Intent in = new Intent(getActivity(), JoinActivity.class);
            in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
            in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
            in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
            in.putExtra("isCategoryData", false);
            startActivity(in);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    private void getNearMeInfo(String city, String lat, String longitute) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            StoreInfoParam storeInfoParam = new StoreInfoParam();
            storeInfoParam.setCityName(city);
            storeInfoParam.setLatitude(lat);
            storeInfoParam.setLongitude(longitute);
            storeInfoParam.setFilters("xyz");
            storeInfoParam.setScrollId("");
           // LaunchActivity.getLaunchActivity().progressDialog.show();
            pb_near.setVisibility(View.VISIBLE);
            NearMeModel.nearMePresenter = this;
            NearMeModel.nearMeStore(UserUtils.getDeviceId(), storeInfoParam);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {

        nearMeData = new ArrayList<>();
        nearMeData.addAll(bizStoreElasticList.getBizStoreElastics());
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(Double.parseDouble(lat_array[city_select]),Double.parseDouble(log_array[city_select]))));
        storeInfoAdapter = new StoreInfoAdapter(nearMeData, getActivity(), storeListener);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe", bizStoreElasticList.toString());
        scrollId = bizStoreElasticList.getScrollId();
       // LaunchActivity.getLaunchActivity().dismissProgress();
        pb_near.setVisibility(View.GONE);
    }

    @Override
    public void nearMeError() {
        //LaunchActivity.getLaunchActivity().dismissProgress();
        pb_near.setVisibility(View.GONE);
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(KEY_FROM_LIST, fromList);
                b.putBoolean(KEY_IS_HISTORY, false);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
        }
    }

    @Override
    public void currentItemClick(JsonTokenAndQueue item, View view, int pos) {
        Intent in = new Intent(getActivity(), AfterJoinActivity.class);
        in.putExtra(KEY_CODE_QR, item.getCodeQR());
        in.putExtra(KEY_FROM_LIST, true);
        in.putExtra(KEY_JSON_TOKEN_QUEUE, item);
        in.putExtra(KEY_IS_AUTOJOIN_ELIGIBLE, true);
        in.putExtra(KEY_IS_HISTORY, false);
        startActivity(in);

//        Bundle b = new Bundle();
//        b.putString(KEY_CODE_QR, jsonQueue.getCodeQR());
//        b.putBoolean(KEY_FROM_LIST, true);
//        if (groupPosition == 0) {
//            b.putSerializable(KEY_JSON_TOKEN_QUEUE, jsonQueue);
//            //  AfterJoinFragment ajf = new AfterJoinFragment();
//            //  ajf.setArguments(b);
//            //  replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, ajf, TAG, LaunchActivity.tabList);
//        } else {
//            b.putBoolean(KEY_IS_HISTORY, true);
//            b.putBoolean(KEY_IS_AUTOJOIN_ELIGIBLE, false);
//            b.putBoolean("isCategoryData", false);
//            // JoinFragment jf = new JoinFragment();
//            // jf.setArguments(b);
//            // replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, jf, TAG, LaunchActivity.tabList);
//        }
    }

    @Override
    public void recentItemClick(JsonTokenAndQueue item, View view, int pos) {
        Intent in = new Intent(getActivity(), JoinActivity.class);
        in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
        in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, true);
        in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, true);
        in.putExtra(KEY_IS_AUTOJOIN_ELIGIBLE, false);
        in.putExtra("isCategoryData", false);
        startActivity(in);
    }

    @OnClick(R.id.tv_near_view_all)
    public void nearClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeData);
        intent.putExtra("scrollId",scrollId);
        intent.putExtra("lat",lat_array[0]);
        intent.putExtra("long",log_array[0]);
        intent.putExtra("city",city[0]);
        startActivity(intent);

    }

    @OnClick(R.id.tv_recent_view_all)
    public void recentClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        Bundle bundle = new Bundle();
        // bundle.putSerializable("data", data1);
        intent.putExtras(bundle);
        // startActivity(intent);
    }

    @OnClick(R.id.btn_temp)
    public void tempClick() {
        Intent intent = new Intent(getActivity(), DoctorProfile1Activity.class);
        // startActivity(intent);

        Location mylocation = new Location("");
        Location dest_location = new Location("");

        dest_location.setLatitude(19.077065);
        dest_location.setLongitude(72.998993);
        mylocation.setLatitude(19.0068);
        mylocation.setLongitude(73.0147);
        float distance = mylocation.distanceTo(dest_location);//in meters
        Toast.makeText(getActivity(), "Distance" + Double.toString(distance / 1000),
                Toast.LENGTH_LONG).show();

        Log.v("distance :", AppUtilities.calculateDistanceInKm(19.0068f, 73.0147f, 19.077065f, 72.998993f));
//
//        // Extract Bitmap from ImageView drawable
//        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.launcher);
//        if (drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//
//            // Store image to default external storage directory
//            Uri bitmapUri = null;
//            try {
//                File file = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
//                file.getParentFile().mkdirs();
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//                out.close();
//                bitmapUri = Uri.fromFile(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (bitmapUri != null) {
//                Intent shareIntent = new Intent();
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am inviting you to join our app. A simple and secure app developed by us. https://www.google.co.in/");
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                shareIntent.setType("*/*");
//                startActivity(Intent.createChooser(shareIntent, "Share my app"));
//            }
//        }


    }

    @Override
    public void currentQueueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(getActivity());
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveTokenQueue(tokenAndQueues, true, false);
        pb_current.setVisibility(View.GONE);
    }

    @Override
    public void historyQueueResponse(List<JsonTokenAndQueue> tokenAndQueues, boolean sinceBeginning) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(getActivity());
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveTokenQueue(tokenAndQueues, false, sinceBeginning);
        pb_recent.setVisibility(View.GONE);
    }

    @Override
    public void historyQueueError() {
        Log.d(TAG, "Token and queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(false);
        pb_recent.setVisibility(View.GONE);
    }

    @Override
    public void currentQueueError() {
        Log.d(TAG, "Token and queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(true);
        pb_current.setVisibility(View.GONE);
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(getActivity());
        }
    }

    @Override
    public void currentQueueSaved() {
        passMsgToHandler(true);
    }

    @Override
    public void historyQueueSaved() {
        passMsgToHandler(false);
    }

    private void passMsgToHandler(boolean isCurrentQueue) {
        // pass msg to handler to load the data from DB
        if (isCurrentQueue) {
            Message msg = new Message();
            msg.what = MSG_CURRENT_QUEUE;
            mHandler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = MSG_HISTORY_QUEUE;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void tokenQueueList(List<JsonTokenAndQueue> currentlist, List<JsonTokenAndQueue> historylist) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentlist.size()) + ":History Queue Count:" + String.valueOf(historylist.size()));

        currentActivityAdapter = new CurrentActivityAdapter(currentlist, getActivity(), currentClickListner);
        rv_current_activity.setAdapter(currentActivityAdapter);
        tv_current_title.setText(getString(R.string.active_queue)+" ("+String.valueOf(currentlist.size())+")");
        currentActivityAdapter.notifyDataSetChanged();

        recentActivityAdapter = new RecentActivityAdapter(historylist, getActivity(), recentClickListner);
        rv_recent_activity.setAdapter(recentActivityAdapter);
        recentActivityAdapter.notifyDataSetChanged();
    }

    public void updateListFromNotification(JsonTokenAndQueue jq, String go_to) {
        TokenAndQueueDB.updateCurrentListQueueObject(jq.getCodeQR(), "" + jq.getServingNumber(), "" + jq.getToken());
        //fetch the
        fetchCurrentAndHistoryList();
    }

    public void fetchCurrentAndHistoryList() {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter(getActivity());
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.getCurrentAndHistoryTokenQueueListFromDB();
    }

    public void callSearch() {
        Intent in_search = new Intent(getActivity(), SearchActivity.class);
        in_search.putExtra("scrollId","");
        in_search.putExtra("lat",lat_array[0]);
        in_search.putExtra("long",log_array[0]);
        in_search.putExtra("city",city[0]);
        startActivity(in_search);
    }

    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:"+COUNTRY_CODE);
            sb.append("&types=(regions)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
