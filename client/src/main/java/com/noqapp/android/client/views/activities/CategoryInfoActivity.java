package com.noqapp.android.client.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.common.cache.Cache;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.FacilityEnum;
import com.noqapp.android.client.presenter.AuthorizeResponsePresenter;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.body.QueueAuthorize;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowKioskModeDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AccreditionAdapter;
import com.noqapp.android.client.views.adapters.LevelUpQueueAdapter;
import com.noqapp.android.client.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.client.views.pojos.KioskModeInfo;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.cache.CacheBuilder.newBuilder;

/**
 * Created by chandra on 5/7/17.
 */
public class CategoryInfoActivity extends BaseActivity implements QueuePresenter,
        LevelUpQueueAdapter.OnItemClickListener, AuthorizeResponsePresenter {

    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = newBuilder()
            .maximumSize(1)
            .build();
    private final Cache<String, Map<String, ArrayList<BizStoreElastic>>> cacheQueue = newBuilder()
            .maximumSize(1)
            .build();

    private final String QUEUE = "queue";
    private final String CATEGORY = "category";
    private TextView tv_store_name;
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_complete_address;
    private TextView tv_rating_review;
    private TextView tv_rating;
    private RecyclerView rv_thumb_images;
    private ImageView iv_category_banner;
    private Button btn_join_queues;
    private Button btn_register;
    private CardView cv_announcement;
    private RecyclerView rcv_amenities;
    private RecyclerView rcv_facility;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private float rating = 0;
    private int reviewCount = 0;
    private String title = "";
    private View view_loader;
    private RecyclerView rcv_accreditation;
    private LinearLayout ll_top_header;
    private ExpandableListView expandableListView;
    private QueueApiAuthenticCall queueApiAuthenticCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_complete_address = findViewById(R.id.tv_complete_address);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        tv_rating = findViewById(R.id.tv_rating);
        RecyclerView rv_categories = findViewById(R.id.rv_categories);
        rv_thumb_images = findViewById(R.id.rv_thumb_images);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        btn_join_queues = findViewById(R.id.btn_join_queues);
        btn_register = findViewById(R.id.btn_pre_approve);
        cv_announcement = findViewById(R.id.cv_announcement);
        rcv_amenities = findViewById(R.id.rcv_amenities);
        rcv_facility = findViewById(R.id.rcv_facility);
        rcv_accreditation = findViewById(R.id.rcv_accreditation);
        ll_top_header = findViewById(R.id.ll_top_header);
        view_loader = findViewById(R.id.view_loader);
        expandableListView = findViewById(R.id.expandableListView);
        initActionsViews(true);
        queueApiAuthenticCall = new QueueApiAuthenticCall();
        tv_mobile.setOnClickListener((View v) -> {
            AppUtils.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
        });

        btn_join_queues.setOnClickListener((View v) -> {
            joinClick();
        });
        btn_register.setOnClickListener((View v) -> {
            register();
        });
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
            BizStoreElastic bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            if (null != bizStoreElastic) {
                switch (bizStoreElastic.getBusinessType()) {
                    case DO:
                    case BK:
                    case CD:
                    case CDQ:
                        setProgressMessage("Loading " + bizStoreElastic.getBusinessName() + "...");
                        break;
                    default:
                        setProgressMessage("Loading ...");
                }
            } else {
                setProgressMessage("Loading ...");
            }
            if (NetworkUtils.isConnectingToInternet(this)) {
                showProgress();
                QueueApiUnAuthenticCall queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
                queueApiUnAuthenticCall.setQueuePresenter(this);
                if (bundle.getBoolean(IBConstant.KEY_CALL_CATEGORY, false)) {
                    queueApiUnAuthenticCall.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
                } else {
                    queueApiUnAuthenticCall.getAllQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }

            if (UserUtils.isLogin() && (BusinessTypeEnum.CDQ == bizStoreElastic.getBusinessType() || BusinessTypeEnum.CD == bizStoreElastic.getBusinessType())) {
                SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                boolean registered = prefs.getBoolean(Constants.PRE_REGISTER, false);
                if (registered) {
                    btn_register.setVisibility(View.GONE);
                } else {
                    btn_register.setVisibility(View.VISIBLE);
                }
            }
        }
        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rv_categories.setLayoutManager(recyclerViewLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void queueError() {
        dismissProgress();
    }


    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        dismissProgress();
    }


    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        if (!bizStoreElasticList.getBizStoreElastics().isEmpty()) {
            view_loader.setVisibility(View.GONE);
            populateAndSortedCache(bizStoreElasticList);
            bizStoreElastic = bizStoreElasticList.getBizStoreElastics().get(0);
            dismissProgress();

            if (AppUtils.showKioskMode(bizStoreElastic)) {
                populateKioskMode();
            } else {
                TextView tv_enable_kiosk = findViewById(R.id.tv_enable_kiosk);
                tv_enable_kiosk.setVisibility(View.GONE);
            }

            tv_store_name.setText(bizStoreElastic.getBusinessName());
            tv_address.setText(AppUtils.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
            tv_complete_address.setText(bizStoreElastic.getAddress());
            tv_complete_address.setOnClickListener((View v) -> AppUtils.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_complete_address.getText().toString()));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            tv_rating.setText(AppUtils.round(rating) + " -");
            if (tv_rating.getText().toString().equals("0.0")) {
                tv_rating.setVisibility(View.INVISIBLE);
            } else {
                tv_rating.setVisibility(View.VISIBLE);
            }

            AppUtils.setReviewCountText(reviewCount, tv_rating_review);
            tv_rating_review.setOnClickListener((View v) -> {
                if (null != bizStoreElastic && reviewCount > 0) {
                    Intent in = new Intent(CategoryInfoActivity.this, AllReviewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(IBConstant.KEY_CODE_QR, codeQR);
                    bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getBusinessName());
                    bundle.putString(IBConstant.KEY_STORE_ADDRESS, tv_address.getText().toString());
                    bundle.putBoolean("isLevelUp", true);
                    in.putExtras(bundle);
                    startActivity(in);
                }
            });

            codeQR = bizStoreElastic.getCodeQR();

            //TODO: This information need to come from bizStoreElastic along with formatted announcement text
            if (BusinessTypeEnum.CDQ == bizStoreElastic.getBusinessType() ||
                    BusinessTypeEnum.CD == bizStoreElastic.getBusinessType()) {
                cv_announcement.setVisibility(View.VISIBLE);
            }

            List<AmenityEnum> amenityEnums = bizStoreElastic.getAmenities();
            List<String> amenities = new ArrayList<>();
            for (int j = 0; j < amenityEnums.size(); j++) {
                amenities.add(amenityEnums.get(j).getDescription());
            }
            rcv_amenities.setLayoutManager(getFlexBoxLayoutManager());
            rcv_amenities.setAdapter(new StaggeredGridAdapter(amenities));
            List<FacilityEnum> facilityEnums = bizStoreElastic.getFacilities();
            List<String> facilities = new ArrayList<>();
            for (int j = 0; j < facilityEnums.size(); j++) {
                facilities.add(facilityEnums.get(j).getDescription());
            }
            rcv_facility.setLayoutManager(getFlexBoxLayoutManager());
            rcv_facility.setAdapter(new StaggeredGridAdapter(facilities));
            Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
            rv_thumb_images.setHasFixedSize(true);
            rv_thumb_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            List<String> storeServiceImages = new ArrayList<>();
            // initialize list if we are receiving urls from server
            if (bizStoreElastic.getBizServiceImages().size() > 0) {
                storeServiceImages = (ArrayList<String>) bizStoreElastic.getBizServiceImages();
                // load first image default
                Picasso.get()
                        .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getBizServiceImages().get(0)))
                        .placeholder(ImageUtils.getBannerPlaceholder(this))
                        .error(ImageUtils.getBannerErrorPlaceholder(this))
                        .into(iv_category_banner);
            }

            ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
            rv_thumb_images.setAdapter(adapter);
            rcv_accreditation.setHasFixedSize(true);
            rcv_accreditation.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            List<String> accreditationImages = new ArrayList<>();
            if (bizStoreElastic.getAccreditation().size() > 0) {
                for (int i = 0; i < bizStoreElastic.getAccreditation().size(); i++) {
                    accreditationImages.add(bizStoreElastic.getAccreditation().get(i).getFilename());
                }
            }
            AccreditionAdapter accreditionAdapter = new AccreditionAdapter(this, accreditationImages);
            rcv_accreditation.setAdapter(accreditionAdapter);
            if (null != storeServiceImages && storeServiceImages.size() > 0) {
                iv_category_banner.setOnClickListener((View v) -> {
                    Intent intent = new Intent(CategoryInfoActivity.this, SliderActivity.class);
                    intent.putExtra("pos", 0);
                    intent.putExtra("imageurls", (ArrayList<String>) bizStoreElastic.getBizServiceImages());
                    startActivity(intent);
                });
            }
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                    btn_join_queues.setText(getString(R.string.find_doctor));
                    tv_toolbar_title.setText(getString(R.string.medical));
                    title = getString(R.string.select_a_doctor);
                    break;
                case CD:
                case CDQ:
                    btn_join_queues.setText(getString(R.string.csd_token));
                    tv_toolbar_title.setText(getString(R.string.canteen_store));
                    title = getString(R.string.select_a_queue);
                    break;
                case BK:
                    btn_join_queues.setText(getString(R.string.view_services));
                    tv_toolbar_title.setText(getString(R.string.bank));
                    title = getString(R.string.select_a_service);
                    break;
                case HS:
                    btn_join_queues.setText(getString(R.string.view_services));
                    tv_toolbar_title.setText(getString(R.string.health_service));
                    title = getString(R.string.select_a_service);
                    break;
                // TODO(hth)
                default:
                    btn_join_queues.setText(getString(R.string.get_token));
                    tv_toolbar_title.setText(getString(R.string.departments));
                    title = getString(R.string.select_a_queue);
            }
        } else {
            //TODO(chandra) when its empty do something nice
        }
        checkForSingleEntry();
        dismissProgress();
    }

    /**
     * Call when confirmed to show the kiosk mode.
     */
    private void populateKioskMode() {
        TextView tv_enable_kiosk = findViewById(R.id.tv_enable_kiosk);
        tv_enable_kiosk.setVisibility(View.VISIBLE);
        tv_enable_kiosk.setOnClickListener(v -> {
            ShowKioskModeDialog showKioskModeDialog = new ShowKioskModeDialog(CategoryInfoActivity.this);
            showKioskModeDialog.setDialogClickListener(new ShowKioskModeDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick(boolean isFeedBackScreen) {
                    LaunchActivity.isLockMode = true;
                    KioskModeInfo kioskModeInfo = new KioskModeInfo();
                    kioskModeInfo.setKioskCodeQR(bizStoreElastic.getCodeQR());
                    kioskModeInfo.setKioskModeEnable(true);
                    kioskModeInfo.setLevelUp(true);
                    kioskModeInfo.setBizNameId(bizStoreElastic.getBizNameId());
                    kioskModeInfo.setBizName(bizStoreElastic.getBusinessName());
                    kioskModeInfo.setFeedbackScreen(isFeedBackScreen);
                    NoQueueBaseActivity.setKioskModeInfo(kioskModeInfo);

                    if (NoQueueBaseActivity.getKioskModeInfo().isFeedbackScreen()) {
                        Intent in = new Intent(CategoryInfoActivity.this, SurveyKioskModeActivity.class);
                        in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                        startActivity(in);
                    } else {
                        NoQueueBaseActivity.clearPreferences();
                        Intent in = new Intent(CategoryInfoActivity.this, CategoryInfoKioskModeActivity.class);
                        in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                        startActivity(in);
                    }
                    finish();
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showKioskModeDialog.displayDialog(LaunchActivity.getUserProfile().getUserLevel().getDescription());
        });
    }

    /**
     * Populated cache and sorted based on current time.
     *
     * @param bizStoreElasticList
     */
    private void populateAndSortedCache(BizStoreElasticList bizStoreElasticList) {
        Map<String, JsonCategory> categoryMap = new LinkedHashMap<>();
        for (JsonCategory jsonCategory : bizStoreElasticList.getJsonCategories()) {
            categoryMap.put(jsonCategory.getBizCategoryId(), jsonCategory);
        }
        categoryMap.put("", new JsonCategory().setBizCategoryId("").setCategoryName(bizStoreElasticList.getBizStoreElastics().get(0).getBusinessName()));
        cacheCategory.put(CATEGORY, categoryMap);

        Map<String, ArrayList<BizStoreElastic>> queueMap = new HashMap<>();
        float ratingQueue = 0;
        int ratingCountQueue = 0, queueWithRating = 0;
        for (BizStoreElastic bizStoreElastic : bizStoreElasticList.getBizStoreElastics()) {

            //Likely hood of blank bizCategoryId
            String bizCategoryId = bizStoreElastic.getBizCategoryId() == null ? "" : bizStoreElastic.getBizCategoryId();

            /* When level up, check if queue is offline. */
            if (bizStoreElastic.isActive()) {
                if (!queueMap.containsKey(bizCategoryId)) {
                    ArrayList<BizStoreElastic> bizStoreElasticArrayList = new ArrayList<>();
                    bizStoreElasticArrayList.add(bizStoreElastic);
                    queueMap.put(bizCategoryId, bizStoreElasticArrayList);
                } else {
                    ArrayList<BizStoreElastic> jsonQueues = queueMap.get(bizCategoryId);
                    jsonQueues.add(bizStoreElastic);
                    // AppUtilities.sortJsonQueues(systemHourMinutes, jsonQueues);
                }

                if (bizStoreElastic.getReviewCount() != 0) {
                    ratingQueue += bizStoreElastic.getRating();
                    ratingCountQueue += bizStoreElastic.getReviewCount();
                    queueWithRating++;
                }
            }
        }
        rating = ratingQueue / queueWithRating;
        reviewCount = ratingCountQueue;
        cacheQueue.put(QUEUE, queueMap);
    }

    public List<JsonCategory> getCategoryThatArePopulated() {
        Map<String, JsonCategory> categoryMap = new HashMap<>();
        Map<String, ArrayList<BizStoreElastic>> queueMap = new HashMap<>();
        if (null != cacheCategory.getIfPresent(CATEGORY)) {
            categoryMap = cacheCategory.getIfPresent(CATEGORY);
        }

        if (null != cacheQueue.getIfPresent(QUEUE)) {
            queueMap = cacheQueue.getIfPresent(QUEUE);
        }

        Set<String> categoryKey = categoryMap.keySet();
        Set<String> queueKey = queueMap.keySet();
        categoryKey.retainAll(queueKey);
        return new ArrayList<>(categoryMap.values());
    }

    private void joinClick() {
        if (null != getCategoryThatArePopulated() && null != cacheQueue.getIfPresent("queue")) {
            Intent in = new Intent(this, QueueListActivity.class);
            in.putExtra("list", (Serializable) getCategoryThatArePopulated());
            in.putExtra("hashmap", (Serializable) cacheQueue.getIfPresent("queue"));
            in.putExtra("title", title);
            in.putExtra("position", 0);
            in.putExtra("isCanteenStore",(BusinessTypeEnum.CDQ == bizStoreElastic.getBusinessType() || BusinessTypeEnum.CD == bizStoreElastic.getBusinessType()));
            startActivity(in);
        }
    }

    private void checkForSingleEntry() {
        expandableListView.setVisibility(View.GONE);
        ll_top_header.setVisibility(View.VISIBLE);
        List<JsonCategory> jsonCategories = getCategoryThatArePopulated();
        Map<String, ArrayList<BizStoreElastic>> entryData = cacheQueue.getIfPresent("queue");
        if ((null != jsonCategories && jsonCategories.size() == 1) && (null != entryData && entryData.size() == 1)) {
            Map.Entry<String, ArrayList<BizStoreElastic>> entry = cacheQueue.getIfPresent("queue").entrySet().iterator().next();
            ArrayList<BizStoreElastic> bizStoreElastics = entry.getValue();
            if (bizStoreElastics.size() == 1) {
                expandableListView.setVisibility(View.VISIBLE);
                LevelUpQueueAdapter expandableListAdapter = new LevelUpQueueAdapter(this, jsonCategories, cacheQueue.getIfPresent("queue"), this, true);
                expandableListView.setAdapter(expandableListAdapter);
                for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                    expandableListView.expandGroup(i);
                }
                btn_join_queues.setVisibility(View.GONE);
                ll_top_header.setVisibility(View.GONE);
            } else {
                // Do nothing
            }
        } else {
            // Do nothing
        }

    }

    private FlexboxLayoutManager getFlexBoxLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item) {
        Intent in;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            case DO:
            case CD:
            case CDQ:
            case BK:
                // open hospital profile
                in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_IS_DO, item.getBusinessType() == BusinessTypeEnum.DO);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                startActivity(in);
                break;
            case HS:
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

    private void register() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_two_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        EditText edtGroceryCard = dialog.findViewById(R.id.edt_grocery_card);
        EditText edtLiquorCard = dialog.findViewById(R.id.edt_liquor_card);

        edtGroceryCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtGroceryCard.getText().toString().length() < 19) {
                    edtGroceryCard.setError(getString(R.string.grocery_card_number_entry_error));
                } else if (edtGroceryCard.getText().toString().charAt(0) != 'G') {
                    edtGroceryCard.setError(getString(R.string.grocery_card_number_prefix_error));
                } else {
                    edtGroceryCard.setError(null);
                }
            }
        });

        edtLiquorCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtLiquorCard.getText().toString().length() < 19) {
                    edtLiquorCard.setError(getString(R.string.liquor_card_number_entry_error));
                } else if (edtLiquorCard.getText().toString().charAt(0) != 'L') {
                    edtLiquorCard.setError(getString(R.string.liquor_card_number_prefix_error));
                } else {
                    edtLiquorCard.setError(null);
                }
            }
        });

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        btn_positive.setOnClickListener(v -> {
            if (btn_positive.getText().equals(this.getString(R.string.submit_button))) {
                String gCard = null;
                String lCard = null;
                if (edtGroceryCard.getError() == null && !TextUtils.isEmpty(edtGroceryCard.getText())) {
                    gCard = edtGroceryCard.getText().toString();
                }
                if (edtLiquorCard.getError() == null && !TextUtils.isEmpty(edtLiquorCard.getText())) {
                    lCard = edtLiquorCard.getText().toString();
                }

                if (!TextUtils.isEmpty(gCard) || !TextUtils.isEmpty(lCard)) {
                    QueueAuthorize queueAuthorize = new QueueAuthorize()
                            .setCodeQR(codeQR)
                            .setFirstCustomerId(gCard)
                            .setAdditionalCustomerId(lCard);
                    queueApiAuthenticCall.setAuthorizeResponsePresenter(this);
                    queueApiAuthenticCall.businessApprove(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), queueAuthorize);
                    AppUtils.hideKeyBoard(this);
                    new CustomToast().showToast(this, "Successfully submitted pre-approval.");
                    dialog.dismiss();
                } else {
                    if (edtGroceryCard.getText().toString().length() < 19) {
                        edtGroceryCard.setError(getString(R.string.grocery_card_number_entry_error));
                    } else if (edtLiquorCard.getText().toString().length() < 19) {
                        edtLiquorCard.setError(getString(R.string.liquor_card_number_entry_error));
                    }
                }
            }
        });
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        btn_negative.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            dialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
        }
    }

    @Override
    public void authorizePresenterResponse(JsonResponse response) {
        Log.d("CategoryInfoActivity", "AuthorizePresenterResponse is" + response);
        SharedPreferences prefs = this.getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.PRE_REGISTER, true).apply();
        btn_register.setVisibility(View.GONE);
    }

    @Override
    public void authorizePresenterError() {
        Log.d("CategoryInfoActivity", "ERROR");
    }
}
