package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileModel;
import com.noqapp.android.merchant.model.PreferredBusinessModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.PreferredStoreFragment;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;
import com.noqapp.android.merchant.views.pojos.PreferredStoreInfo;

import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PreferredStoreActivity extends AppCompatActivity implements PreferredBusinessPresenter, MenuHeaderAdapter.OnItemClickListener, IntellisensePresenter {

    private long lastPress;
    private Toast backPressToast;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private ArrayList<String> data = new ArrayList<>();
    private PreferredStoreFragment frag_mri_and_scan;
    private PreferredStoreFragment frag_sono_and_xray;
    private PreferredStoreFragment frag_path_and_spec;
    private PreferredStoreFragment frag_physio_medic;
    private ProgressDialog progressDialog;
    public PreferenceObjects preferenceObjects;

    public static PreferredStoreActivity getPreferredStoreActivity() {
        return preferredStoreActivity;
    }

    private static PreferredStoreActivity preferredStoreActivity;

    public List<JsonPreferredBusiness> getJsonPreferredBusiness() {
        return jsonPreferredBusiness;
    }

    private List<JsonPreferredBusiness> jsonPreferredBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        preferredStoreActivity = this;
        try {
            preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), PreferenceObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        if (null == preferenceObjects) {
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        initProgress();
        setContentView(R.layout.activity_preferred_business);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Preferred Stores");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = findViewById(R.id.pager);
        rcv_header = findViewById(R.id.rcv_header);
        data.add("MRI & CT Scan");
        data.add("Sonography & X-RAY");
        data.add("Pathology & Special");
        data.add("Physiotherapy & Medicine");


        if (null != LaunchActivity.merchantListFragment && null != LaunchActivity.merchantListFragment.getTopics() && LaunchActivity.merchantListFragment.getTopics().size() > 0) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                new PreferredBusinessModel(this)
                        .getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), LaunchActivity.merchantListFragment.getTopics().get(0).getCodeQR());
            }
        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
        // saveAllData();
    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList) {
        if (null != jsonPreferredBusinessList && jsonPreferredBusinessList.getPreferredBusinesses() != null && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0) {
            this.jsonPreferredBusiness = jsonPreferredBusinessList.getPreferredBusinesses();
            frag_mri_and_scan = new PreferredStoreFragment();
            frag_mri_and_scan.setArguments(getBundle(0));
            frag_sono_and_xray = new PreferredStoreFragment();
            frag_sono_and_xray.setArguments(getBundle(1));
            frag_path_and_spec = new PreferredStoreFragment();
            frag_path_and_spec.setArguments(getBundle(2));
            frag_physio_medic = new PreferredStoreFragment();
            frag_physio_medic.setArguments(getBundle(3));

            TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(frag_mri_and_scan, "FRAG" + 0);
            adapter.addFragment(frag_sono_and_xray, "FRAG" + 1);
            adapter.addFragment(frag_path_and_spec, "FRAG" + 2);
            adapter.addFragment(frag_physio_medic, "FRAG" + 3);

            rcv_header.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rcv_header.setLayoutManager(horizontalLayoutManagaer);
            rcv_header.setItemAnimator(new DefaultItemAnimator());


            menuAdapter = new MenuHeaderAdapter(data, this, this, true);
            rcv_header.setAdapter(menuAdapter);
            menuAdapter.notifyDataSetChanged();
            viewPager.setOffscreenPageLimit(data.size());
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            adapter.notifyDataSetChanged();
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //saveAllData();
                    rcv_header.smoothScrollToPosition(position);
                    menuAdapter.setSelected_pos(position);
                    menuAdapter.notifyDataSetChanged();

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            Log.e("Pref business list: ", jsonPreferredBusinessList.toString());
        }
        dismissProgress();
    }

    @Override
    public void preferredBusinessError() {
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
        AppUtils.authenticationProcessing();
    }

    private Bundle getBundle(int pos) {
        Bundle b = new Bundle();
        b.putInt("type", pos);
        return b;
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(this, getString(R.string.exit_medical_screen), Toast.LENGTH_LONG);
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
        }
        frag_mri_and_scan.saveData();
        frag_sono_and_xray.saveData();
        frag_path_and_spec.saveData();
        frag_physio_medic.saveData();
        LaunchActivity.getLaunchActivity().setSuggestionsPrefs(preferenceObjects);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching stores...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void initStores() {
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            preferenceObjects.getPreferredStoreInfoHashMap().put(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR(), new PreferredStoreInfo());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        M_MerchantProfileModel m_merchantProfileModel = new M_MerchantProfileModel(this);
        m_merchantProfileModel.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(LaunchActivity.getLaunchActivity().getSuggestionsPrefs()));
    }

    @Override
    public void intellisenseResponse(JsonResponse jsonResponse) {
        Log.v("IntelliSense upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void intellisenseError() {
        Log.v("IntelliSense upload: ", "error");
    }
}
