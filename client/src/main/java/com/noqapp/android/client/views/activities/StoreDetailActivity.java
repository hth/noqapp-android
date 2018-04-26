package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.DeliveryTypeEnum;
import com.noqapp.android.client.model.types.PaymentTypeEnum;
import com.noqapp.android.client.model.types.StoreModel;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonHour;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.presenter.beans.JsonStoreProduct;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.utils.ViewAnimationUtils;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class StoreDetailActivity extends BaseActivity implements StorePresenter {

    private JsonStore jsonStore = null;
    private JsonQueue jsonQueue = null;
    private TextView tv_contact_no, tv_address, tv_known_for, tv_store_rating, tv_payment_mode, tv_amenities, tv_menu, tv_delivery_types, tv_store_name, tv_store_address,tv_store_open_status;
    private LinearLayout ll_store_open_status;
    private boolean isUp;
    private ImageView iv_store_open_status;
    private BizStoreElastic bizStoreElastic;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView rv_thumb_images, rv_photos;
    private ImageView collapseImageView;
    private AppBarLayout appBarLayout;
    private boolean canAddItem = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_store_name = (TextView) findViewById(R.id.tv_store_name);
        tv_store_address = (TextView) findViewById(R.id.tv_store_address);
        tv_contact_no = (TextView) findViewById(R.id.tv_contact_no);
        tv_known_for = (TextView) findViewById(R.id.tv_known_for);
        tv_payment_mode = (TextView) findViewById(R.id.tv_payment_mode);
        tv_amenities = (TextView) findViewById(R.id.tv_amenities);
        tv_delivery_types = (TextView) findViewById(R.id.tv_delivery_types);
        tv_store_rating = (TextView) findViewById(R.id.tv_store_rating);
        tv_store_open_status = (TextView) findViewById(R.id.tv_store_open_status);
        tv_menu = (TextView) findViewById(R.id.tv_menu);
        ll_store_open_status = (LinearLayout) findViewById(R.id.ll_store_open_status);
        iv_store_open_status = (ImageView) findViewById(R.id.iv_store_open_status);
        collapseImageView = (ImageView) findViewById(R.id.backdrop);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle(" ");
        bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_thumb_images = (RecyclerView) findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(horizontalLayoutManager);

        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_photos = (RecyclerView) findViewById(R.id.rv_photos);
        rv_photos.setHasFixedSize(true);
        rv_photos.setLayoutManager(horizontalLayoutManager1);

        Picasso.with(this)
                .load(bizStoreElastic.getDisplayImage())
                .into(collapseImageView);



        iv_store_open_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUp) {
                    ViewAnimationUtils.expand(ll_store_open_status);
                    iv_store_open_status.setBackground(ContextCompat.getDrawable(StoreDetailActivity.this, R.drawable.arrow_down));
                } else {
                    ViewAnimationUtils.collapse(ll_store_open_status);
                    iv_store_open_status.setBackground(ContextCompat.getDrawable(StoreDetailActivity.this, R.drawable.arrow_up));
                }
                isUp = !isUp;
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            StoreModel.storePresenter = this;
            StoreModel.getStoreService(UserUtils.getDeviceId(), bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_favourite:
                if(canAddItem){
                    item.setIcon(R.drawable.heart_fill);
                    Toast.makeText(this,"added to favourite",Toast.LENGTH_LONG).show();
                    canAddItem = false;
                }
                else{
                    item.setIcon(R.drawable.ic_heart);
                    Toast.makeText(this,"remove from favourite",Toast.LENGTH_LONG).show();
                    canAddItem = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_doc_profile, menu);
        return true;
    }
    @Override
    public void storeResponse(JsonStore tempjsonStore) {
        this.jsonStore = tempjsonStore;
        dismissProgress();
        // Toast.makeText(getActivity(),"jsonStore response success",Toast.LENGTH_LONG).show();
        Log.v("jsonStore response :", jsonStore.toString());

        switch (jsonStore.getJsonQueue().getBusinessType()) {
            case HO:
            case DO:
                // open hospital profile
                break;
            default:
                populateStore();

        }
    }

    private void populateStore() {
        jsonQueue = jsonStore.getJsonQueue();
        tv_contact_no.setText(jsonQueue.getStorePhone());
        tv_address.setText(jsonQueue.getStoreAddress());
        tv_store_address.setText(jsonQueue.getStoreAddress());
        tv_store_name.setText(jsonQueue.getDisplayName());
        tv_store_rating.setText(String.valueOf(AppUtilities.round(jsonQueue.getRating())));
        tv_known_for.setText(jsonQueue.getFamousFor());
        List<PaymentTypeEnum> temp = jsonQueue.getPaymentTypes();
        StringBuilder paymentMode = new StringBuilder();
        for (int i = 0; i < temp.size(); i++) {
            paymentMode.append(temp.get(i).getDescription()).append(i < (temp.size() - 1) ? ", " : "");
        }
        tv_payment_mode.setText(paymentMode.toString());

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(jsonQueue.getDisplayName());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");//be careful there should be a space between double quote otherwise it won't work
                    isShow = false;
                }
            }
        });
        List<AmenityEnum> amenities = jsonQueue.getAmenities();
        StringBuilder amenity = new StringBuilder();
        for (int j = 0; j < amenities.size(); j++) {
            amenity.append(amenities.get(j).getDescription()).append(j < (amenities.size() - 1) ? ", " : "");
        }
        tv_amenities.setText(amenity.toString());


        List<DeliveryTypeEnum> deliveryTypes = jsonQueue.getDeliveryTypes();
        StringBuilder deliveryMode = new StringBuilder();
        for (int j = 0; j < deliveryTypes.size(); j++) {
            deliveryMode.append(deliveryTypes.get(j).getDescription()).append(j < (deliveryTypes.size() - 1) ? ", " : "");
        }
        tv_delivery_types.setText(deliveryMode.toString());


        //
        ArrayList<String> storeServiceImages = (ArrayList<String>) jsonQueue.getStoreServiceImages();
        ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
        rv_thumb_images.setAdapter(adapter);
        //
        ArrayList<String> storeInteriorImages = (ArrayList<String>) jsonQueue.getStoreInteriorImages();
        ThumbnailGalleryAdapter adapter1 = new ThumbnailGalleryAdapter(this, storeInteriorImages);
        rv_photos.setAdapter(adapter1);
        //

        //  {
        //TODO @Chandra Optimize the loop
        final ArrayList<JsonStoreCategory> jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();

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
            }
        }
        //  }

        tv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(StoreDetailActivity.this, StoreMenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("jsonStoreCategories", jsonStoreCategories);
                bundle.putSerializable("listDataChild", listDataChild);
                bundle.putSerializable("jsonQueue", jsonQueue);
                in.putExtras(bundle);
                startActivity(in);
            }
        });
        if(isStoreOpenToday(jsonStore)){
            tv_menu.setClickable(true);
            tv_menu.setText("Order Now");
        }else{
            tv_menu.setClickable(false);
            tv_menu.setText("Closed");
        }
        ll_store_open_status.removeAllViews();
        JsonHour jsonHourt = jsonStore.getJsonHours().get(6);
        tv_store_open_status.setText(Formatter.convertMilitaryTo12HourFormat(jsonHourt.getStartHour())+" - "+Formatter.convertMilitaryTo12HourFormat(jsonHourt.getEndHour()));
        for (int j = 0; j < 6; j++) {
            JsonHour jsonHour = jsonStore.getJsonHours().get(j);
            LinearLayout childLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayout.setLayoutParams(linearParams);
            TextView mType = new TextView(this);
            mType.setTextSize(17);
            mType.setPadding(5, 3, 0, 3);
            mType.setTypeface(Typeface.DEFAULT_BOLD);
            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
            mType.setText(Formatter.convertMilitaryTo12HourFormat(jsonHour.getStartHour())+" - "+Formatter.convertMilitaryTo12HourFormat(jsonHour.getEndHour()));
            childLayout.addView(mType, 0);
            ll_store_open_status.addView(childLayout);
        }

    }

    @Override
    public void storeError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
    }


    private boolean isStoreOpenToday(JsonStore jsonStore) {
        List<JsonHour> jsonHourList = jsonStore.getJsonHours();
        JsonHour jsonHour = jsonHourList.get(//3);
                AppUtilities.getTodayDay());
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(Calendar.getInstance().getTime());
        int timedata = Integer.valueOf(time.replace(":",""));
        if(jsonHour.getStartHour()<= timedata&&
                timedata <=jsonHour.getEndHour()){
            return true;
        }else{
            return false;
        }
    }
}
