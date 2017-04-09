package com.noqapp.client.views.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by omkar on 4/8/17.
 */

public class NoQueueBaseFragment extends Fragment {

    public void replaceFragmentWithoutBackStack(FragmentActivity activity, int container, Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container,fragment,tag).commit();

    }

    public void replaceFragmentWithBackStack(FragmentActivity activity, int container, Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container,fragment,tag).addToBackStack(tag).commit();

    }

    public void addFragment(FragmentActivity activity, int container, Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(container,fragment,tag).commit();

    }
}
