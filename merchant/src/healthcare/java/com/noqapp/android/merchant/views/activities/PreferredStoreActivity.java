package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileApiCalls;
import com.noqapp.android.merchant.model.PreferredBusinessApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessBucket;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.fragments.PreferredStoreCategoryList;
import com.noqapp.android.merchant.views.fragments.PreferredStoreFragment;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;
import com.noqapp.android.merchant.views.pojos.PreferredStoreInfo;

import java.util.List;

public class PreferredStoreActivity extends BaseActivity implements View.OnClickListener,
        PreferredStoreCategoryList.CategoryListener, PreferredBusinessPresenter, IntellisensePresenter {

    private PreferredStoreFragment frag_mri;
    private PreferredStoreFragment frag_scan;
    private PreferredStoreFragment frag_sono;
    private PreferredStoreFragment frag_xray;
    private PreferredStoreFragment frag_path;
    private PreferredStoreFragment frag_spec;
    private PreferredStoreFragment frag_physio;
    private PreferredStoreFragment frag_medic;
    public PreferenceObjects preferenceObjects;
    private PreferredStoreCategoryList preferredStoreCategoryList;

    public static PreferredStoreActivity getPreferredStoreActivity() {
        return preferredStoreActivity;
    }

    private static PreferredStoreActivity preferredStoreActivity;
    private List<JsonPreferredBusinessList> jsonPreferredBusinessLists;

    public List<JsonPreferredBusinessList> getJsonPreferredBusinessLists() {
        return jsonPreferredBusinessLists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        preferredStoreActivity = this;
        try {
            preferenceObjects = new Gson().fromJson(AppInitialize.getSuggestionsPrefs(), PreferenceObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        if (null == preferenceObjects) {
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        setProgressMessage("Fetching stores...");
        setContentView(R.layout.activity_preferred_business);
        initActionsViews(false);
        tv_toolbar_title.setText("Preferred Stores");
        actionbarBack.setOnClickListener(v -> onBackPressed());
        preferredStoreCategoryList = new PreferredStoreCategoryList();
        preferredStoreCategoryList.setCategoryListener(this);

        replaceFragmentWithoutBackStack(R.id.frame_list, preferredStoreCategoryList);

        if (null != LaunchActivity.merchantListFragment && null != LaunchActivity.merchantListFragment.getTopics() && LaunchActivity.merchantListFragment.getTopics().size() > 0) {
            if (new NetworkUtil(this).isOnline()) {
                showProgress();
                new PreferredBusinessApiCalls(this)
                        .getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
            }
        }
    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessBucket jsonPreferredBusinessBucket) {
        if (null != jsonPreferredBusinessBucket && jsonPreferredBusinessBucket.getJsonPreferredBusinessLists() != null && jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().size() > 0) {
            //this.jsonPreferredBusiness = jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().get(0).getPreferredBusinesses();
            this.jsonPreferredBusinessLists = jsonPreferredBusinessBucket.getJsonPreferredBusinessLists();
            frag_mri = new PreferredStoreFragment();
            frag_mri.setArguments(getBundle(0));
            frag_scan = new PreferredStoreFragment();
            frag_scan.setArguments(getBundle(1));

            frag_sono = new PreferredStoreFragment();
            frag_sono.setArguments(getBundle(2));
            frag_xray = new PreferredStoreFragment();
            frag_xray.setArguments(getBundle(3));


            frag_path = new PreferredStoreFragment();
            frag_path.setArguments(getBundle(4));
            frag_spec = new PreferredStoreFragment();
            frag_spec.setArguments(getBundle(5));

            frag_physio = new PreferredStoreFragment();
            frag_physio.setArguments(getBundle(6));
            frag_medic = new PreferredStoreFragment();
            frag_medic.setArguments(getBundle(7));

            preferredStoreCategoryList.updateProgress();

            Log.e("Pref business list: ", jsonPreferredBusinessBucket.toString());
        }
        dismissProgress();
    }


    @Override
    public void preferredBusinessError() {
        dismissProgress();
    }

    private Bundle getBundle(int pos) {
        Bundle b = new Bundle();
        b.putInt("type", pos);
        return b;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppInitialize.setSuggestionsPrefs(preferenceObjects);
    }


    private void initStores() {
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            preferenceObjects.getPreferredStoreInfoHashMap().put(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR(), new PreferredStoreInfo());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        M_MerchantProfileApiCalls m_merchantProfileApiCalls = new M_MerchantProfileApiCalls(this);
        m_merchantProfileApiCalls.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(AppInitialize.getSuggestionsPrefs()));
    }

    @Override
    public void intellisenseResponse(JsonResponse jsonResponse) {
        Log.v("IntelliSense upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void intellisenseError() {
        Log.v("IntelliSense upload: ", "error");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void categorySelected(int id) {

        switch (id) {
            case R.id.cv_mri:
                replaceFragmentWithBackStack(R.id.frame_list, frag_mri, "MRI");
                break;
            case R.id.cv_ctscan:
                replaceFragmentWithBackStack(R.id.frame_list, frag_scan, "Scan");
                break;
            case R.id.cv_sono:
                replaceFragmentWithBackStack(R.id.frame_list, frag_sono, "Sono");
                break;
            case R.id.cv_xray:
                replaceFragmentWithBackStack(R.id.frame_list, frag_xray, "Xray");
                break;
            case R.id.cv_path:
                replaceFragmentWithBackStack(R.id.frame_list, frag_path, "Path");
                break;
            case R.id.cv_special:
                replaceFragmentWithBackStack(R.id.frame_list, frag_spec, "Special");
                break;
            case R.id.cv_physio:
                replaceFragmentWithBackStack(R.id.frame_list, frag_physio, "Physcio");
                break;
            case R.id.cv_medicine:
                replaceFragmentWithBackStack(R.id.frame_list, frag_medic, "Medicine");
                break;
        }

    }
}
