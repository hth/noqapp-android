package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.model.types.StoreModel;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.utils.ViewAnimationUtils;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.client.views.toremove.MyData;


public class StoreDetailActivity extends AppCompatActivity implements StorePresenter {

    private JsonStore jsonStore = null;
    private JsonQueue jsonQueue = null;
    private TextView tv_contact_no,tv_address,tv_known_for;
    private LinearLayout ll_store_open_status;
    private boolean isUp;
    private ImageView iv_store_open_status;
    private BizStoreElastic bizStoreElastic;
    private CollapsingToolbarLayout collapsingToolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_contact_no = (TextView) findViewById(R.id.tv_contact_no);
        tv_known_for = (TextView) findViewById(R.id.tv_known_for);
        ll_store_open_status = (LinearLayout)findViewById(R.id.ll_store_open_status);
        iv_store_open_status = (ImageView) findViewById(R.id.iv_store_open_status);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        bizStoreElastic = (BizStoreElastic)bundle.getSerializable("BizStoreElastic");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        loadBackdrop();

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView rv_thumb_images = (RecyclerView) findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(horizontalLayoutManager);
        ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, MyData.imageList());
        rv_thumb_images.setAdapter(adapter);



        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView rv_photos = (RecyclerView) findViewById(R.id.rv_photos);
        rv_photos.setHasFixedSize(true);
        rv_photos.setLayoutManager(horizontalLayoutManager1);
        ThumbnailGalleryAdapter adapter1 = new ThumbnailGalleryAdapter(this, MyData.imageList1());
        rv_photos.setAdapter(adapter1);
        
        

        for(int j=0;j<6;j++) {
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
            mType.setText("9:30 am - 10:30 pm");
            childLayout.addView(mType, 0);
            ll_store_open_status.addView(childLayout);
        }

        iv_store_open_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUp) {
                    ViewAnimationUtils.expand(ll_store_open_status);
                } else {
                    ViewAnimationUtils.collapse(ll_store_open_status);
                }
                isUp = !isUp;
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            // LaunchActivity.getLaunchActivity().progressDialog.show();
            StoreModel.storePresenter = this;
            StoreModel.getStoreService(UserUtils.getDeviceId(), bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        //Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void storeResponse(JsonStore tempjsonStore) {
        this.jsonStore = tempjsonStore;
       // Toast.makeText(getActivity(),"jsonStore response success",Toast.LENGTH_LONG).show();
        Log.v("jsonStore response :", jsonStore.toString());

        if(jsonStore.getJsonQueue().getBusinessType() == BusinessTypeEnum.DO||
                jsonStore.getJsonQueue().getBusinessType() == BusinessTypeEnum.HO){
            // open hospital profile
        }else{

            jsonQueue = jsonStore.getJsonQueue();
            tv_contact_no.setText(jsonQueue.getStorePhone());
            tv_address.setText(jsonQueue.getStoreAddress());
            collapsingToolbar.setTitle(jsonQueue.getDisplayName());
        }
    }

    @Override
    public void storeError() {

    }

    @Override
    public void authenticationFailure(int errorCode) {

    }
}
