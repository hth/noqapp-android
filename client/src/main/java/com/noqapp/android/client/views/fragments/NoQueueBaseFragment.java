package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.views.activities.LaunchActivity;

import org.apache.commons.lang3.StringUtils;

/**
 * User: omkar
 * Date: 4/8/17 11:31 AM
 */
public class NoQueueBaseFragment extends Fragment {
    public static final String KEY_CODE_QR = DatabaseTable.TokenQueue.CODE_QR;
    public static final String KEY_FROM_LIST = "fromList";
    public static final String KEY_IS_HISTORY = "isHistory";
    public static final String KEY_IS_REJOIN = "isRejoin";
    public static final String KEY_JSON_TOKEN_QUEUE = "jsonTokenQueue";

    public static void replaceFragmentWithBackStack(FragmentActivity activity, int container, Fragment fragment, String tag, String selectedTab) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        // transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(container, fragment, tag).addToBackStack(tag).commitAllowingStateLoss();
        // Added to maintaining the stack
        if (StringUtils.isNotBlank(selectedTab)) {
            LaunchActivity.getLaunchActivity().setCurrentSelectedTabTag(selectedTab);
            LaunchActivity.getLaunchActivity().addFragmentToStack(fragment);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void replaceFragmentWithoutBackStack(FragmentActivity activity, int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment, tag).commit();

    }

    public void addFragment(FragmentActivity activity, int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(container, fragment, tag).commit();

    }
}
