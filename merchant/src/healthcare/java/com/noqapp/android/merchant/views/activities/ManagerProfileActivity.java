package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.views.fragments.MerchantReviewQListFragment;
import com.noqapp.android.merchant.views.fragments.UserAdditionalInfoFragment;

import androidx.viewpager.widget.ViewPager;

public class ManagerProfileActivity extends BaseManagerProfileActivity {

    private UserAdditionalInfoFragment userAdditionalInfoFragment;

    @Override
    protected void setupViewPager(ViewPager viewPager) {
        super.setupViewPager(viewPager);
        if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
            // Additional profile will be only visible to store manager
            switch (LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
                case DO:
                    userAdditionalInfoFragment = new UserAdditionalInfoFragment();
                    adapter.addFragment(userAdditionalInfoFragment, "Professional Profile");
                    break;
                default:
                    //Do nothing
            }
        }
        merchantReviewQListFragment = new MerchantReviewQListFragment();
        adapter.addFragment(merchantReviewQListFragment, "Reviews");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void merchantResponse(JsonMerchant jsonMerchant) {
        super.merchantResponse(jsonMerchant);
        if (null != jsonMerchant) {
            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
                // Additional profile will be only visible to store manager
                switch (jsonMerchant.getJsonProfile().getBusinessType()) {
                    case DO:
                        if(null != jsonMerchant.getJsonProfessionalProfile()) {
                            LaunchActivity.getLaunchActivity().setUserProfessionalProfile(jsonMerchant.getJsonProfessionalProfile());
                            userAdditionalInfoFragment.updateUI(jsonMerchant.getJsonProfessionalProfile());
                            merchantReviewQListFragment.updateUI(jsonMerchant.getJsonProfessionalProfile());
                        }
                        break;
                    default:
                        //Do nothing
                }
            }
        }
    }
}
