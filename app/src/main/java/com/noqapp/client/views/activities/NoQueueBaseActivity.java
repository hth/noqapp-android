package com.noqapp.client.views.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.noqapp.client.R;

/**
 * Created by omkar on 4/8/17.
 */

public class NoQueueBaseActivity extends AppCompatActivity{

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }
}
