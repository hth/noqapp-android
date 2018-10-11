package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.StoreModel;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonHour;
import com.noqapp.android.common.beans.order.JsonStoreProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.DeliveryTypeEnum;
import com.noqapp.android.common.model.types.order.PaymentTypeEnum;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StoreDetailActivity extends BaseActivity implements StorePresenter {

    private JsonStore jsonStore = null;
    private JsonQueue jsonQueue = null;
    private TextView tv_contact_no, tv_address, tv_address_title, tv_known_for,  tv_store_name, tv_store_address, tv_store_timings, tv_header_menu, tv_header_famous;
    private BizStoreElastic bizStoreElastic;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView rv_thumb_images, rv_photos;
    private AppBarLayout appBarLayout;
    private ImageView iv_business_icon;
    private boolean canAddItem = true;
    private RelativeLayout rl_mid_content;
    private TextView tv_rating, tv_rating_review;
    private SegmentedControl sc_amenities, sc_delivery_types, sc_payment_mode;
    private Button tv_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        tv_address = findViewById(R.id.tv_address);
        tv_address_title = findViewById(R.id.tv_address_title);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_store_address = findViewById(R.id.tv_store_address);
        tv_contact_no = findViewById(R.id.tv_contact_no);
        tv_known_for = findViewById(R.id.tv_known_for);
        tv_store_timings = findViewById(R.id.tv_store_timings);
        tv_header_menu = findViewById(R.id.tv_header_menu);
        tv_header_famous = findViewById(R.id.tv_header_famous);
        tv_menu = findViewById(R.id.tv_menu);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        tv_rating = findViewById(R.id.tv_rating);

        sc_payment_mode = findViewById(R.id.sc_payment_mode);
        sc_delivery_types = findViewById(R.id.sc_delivery_types);
        sc_amenities = findViewById(R.id.sc_amenities);
        ImageView collapseImageView = findViewById(R.id.backdrop);
        rl_mid_content = findViewById(R.id.rl_mid_content);
        iv_business_icon = findViewById(R.id.iv_business_icon);
        appBarLayout = findViewById(R.id.appbar);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    rl_mid_content.setVisibility(View.VISIBLE);
                } else {
                    rl_mid_content.setVisibility(View.GONE);
                }
            }
        });
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle(" ");
        bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
        ratingBar.setRating(bizStoreElastic.getRating());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_thumb_images = findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(horizontalLayoutManager);

        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_photos = findViewById(R.id.rv_photos);
        rv_photos.setHasFixedSize(true);
        rv_photos.setLayoutManager(horizontalLayoutManager1);

        tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_rating_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(StoreDetailActivity.this, ShowAllReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
                bundle.putString("storeName", jsonQueue.getDisplayName());
                bundle.putString("storeAddress", tv_store_address.getText().toString());
                in.putExtras(bundle);
                startActivity(in);
            }
        });

        if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
            Picasso.with(this)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(collapseImageView);
        else {
            Picasso.with(this).load(ImageUtils.getBannerPlaceholder()).into(collapseImageView);
        }
        progressDialog.setMessage("Loading " + bizStoreElastic.getBusinessName() + "...");
        if (NetworkUtils.isConnectingToInternet(this)) {
            progressDialog.show();
            new StoreModel(this).getStoreService(UserUtils.getDeviceId(), bizStoreElastic.getCodeQR());
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
                if (canAddItem) {
                    item.setIcon(R.drawable.heart_fill);
                    Toast.makeText(this, "added to favourite", Toast.LENGTH_LONG).show();
                    canAddItem = false;
                } else {
                    item.setIcon(R.drawable.ic_heart);
                    Toast.makeText(this, "remove from favourite", Toast.LENGTH_LONG).show();
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
        //@TODO Chandra enable when the feature add on server
        menu.findItem(R.id.menu_favourite).setVisible(false);
        return true;
    }

    @Override
    public void storeResponse(JsonStore tempjsonStore) {
        this.jsonStore = tempjsonStore;
        dismissProgress();
        switch (jsonStore.getJsonQueue().getBusinessType()) {
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
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_address.getText().toString());
            }
        });
        tv_address_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_address.getText().toString());
            }
        });
        tv_store_address.setText(AppUtilities.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
        tv_store_name.setText(jsonQueue.getDisplayName());
        tv_known_for.setText(jsonQueue.getFamousFor());
        if (TextUtils.isEmpty(jsonQueue.getFamousFor()))
            tv_header_famous.setVisibility(View.GONE);
        List<PaymentTypeEnum> temp = jsonQueue.getPaymentTypes();
        ArrayList<String> payment_data = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            payment_data.add(temp.get(i).getDescription());
        }
        sc_payment_mode.removeAllSegments();
        sc_payment_mode.addSegments(payment_data);
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


        tv_rating.setText(String.valueOf(AppUtilities.round(jsonQueue.getRating())));
        if (tv_rating.getText().toString().equals("0.0")) {
            tv_rating.setVisibility(View.INVISIBLE);
        } else {
            tv_rating.setVisibility(View.VISIBLE);
        }
        if (jsonQueue.getRatingCount() == 0) {
            tv_rating_review.setText("No Review");
        } else if (jsonQueue.getRatingCount() == 1) {
            tv_rating_review.setText("1 Review");
        } else {
            tv_rating_review.setText(String.valueOf(jsonQueue.getRatingCount()) + " Reviews");
        }
        AppUtilities.setStoreDrawable(this, iv_business_icon, bizStoreElastic.getBusinessType(), tv_rating);
        //
        List<AmenityEnum> amenities = jsonQueue.getAmenities();
        ArrayList<String> amenitiesdata = new ArrayList<>();
        for (int j = 0; j < amenities.size(); j++) {
            amenitiesdata.add(amenities.get(j).getDescription());
        }
        sc_amenities.removeAllSegments();
        sc_amenities.addSegments(amenitiesdata);

        List<DeliveryTypeEnum> deliveryTypes = jsonQueue.getDeliveryTypes();
        ArrayList<String> deliveryTypesdata = new ArrayList<>();
        for (int j = 0; j < deliveryTypes.size(); j++) {
            deliveryTypesdata.add(deliveryTypes.get(j).getDescription());
        }
        sc_delivery_types.removeAllSegments();
        sc_delivery_types.addSegments(deliveryTypesdata);


        //
        ArrayList<String> storeServiceImages = new ArrayList<>(jsonQueue.getStoreServiceImages());
        for (int i = 0; i < storeServiceImages.size(); i++) {
            storeServiceImages.set(i, jsonQueue.getCodeQR() + "/" + storeServiceImages.get(i));
        }
        ThumbnailGalleryAdapter serviceAdapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
        rv_thumb_images.setAdapter(serviceAdapter);
        //
        ArrayList<String> storeInteriorImages = new ArrayList<>(jsonQueue.getStoreInteriorImages());
        for (int i = 0; i < storeInteriorImages.size(); i++) {
            storeInteriorImages.set(i, jsonQueue.getCodeQR() + "/" + storeInteriorImages.get(i));
        }
        ThumbnailGalleryAdapter interiorAdapter = new ThumbnailGalleryAdapter(this, storeInteriorImages);
        rv_photos.setAdapter(interiorAdapter);
        //
        String defaultCategory = "Un-Categorized";
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
                if (null == listDataChild.get(defaultCategory)) {
                    listDataChild.put(defaultCategory, new ArrayList<ChildData>());
                }
                listDataChild.get(defaultCategory).add(new ChildData(0, jsonStoreProducts.get(k)));
            }
        }

        if (null != listDataChild.get(defaultCategory)) {
            jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
        }

        tv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonQueue.getBusinessType() != BusinessTypeEnum.PH) {
                    Intent in = new Intent(StoreDetailActivity.this, StoreMenuActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("jsonStoreCategories", jsonStoreCategories);
                    bundle.putSerializable("listDataChild", listDataChild);
                    bundle.putSerializable("jsonQueue", jsonQueue);
                    in.putExtras(bundle);
                    startActivity(in);
                } else {
                    //Do nothing
                    Toast.makeText(StoreDetailActivity.this, "Please visit store to purchase.", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (isStoreOpenToday(jsonStore)) {
            tv_menu.setClickable(true);
            if (jsonQueue.getBusinessType() != BusinessTypeEnum.PH) {
                tv_menu.setText("Order Now");
            } else {
                tv_menu.setText("Visit Store");
                tv_header_menu.setVisibility(View.GONE);
            }
        } else {
            tv_menu.setClickable(false);
            tv_menu.setText("Closed");
        }
        tv_store_timings.setText(Html.fromHtml(new AppUtilities().orderTheTimings(this, jsonStore.getJsonHours())));
    }


    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    private boolean isStoreOpenToday(JsonStore jsonStore) {
        List<JsonHour> jsonHourList = jsonStore.getJsonHours();
        JsonHour jsonHour = AppUtilities.getJsonHour(jsonHourList);
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());
        int timeData = Integer.parseInt(time.replace(":", ""));
        return jsonHour.getStartHour() <= timeData && timeData <= jsonHour.getEndHour();
    }
}
