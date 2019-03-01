package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.model.database.DatabaseTable;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NoQueueBaseFragment extends Fragment {
    public static final String KEY_CODE_QR = DatabaseTable.TokenQueue.CODE_QR;
    public static final String KEY_FROM_LIST = "fromList";
    public static final String KEY_JSON_TOKEN_QUEUE = "jsonTokenQueue";

    public static void replaceFragmentWithBackStack(FragmentActivity activity, int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        // transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(container, fragment, tag).addToBackStack(tag).commitAllowingStateLoss();

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
