package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.fragments.MapFragment;

public class MapsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initActionsViews(true);
        tv_toolbar_title.setText("Route");
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_map, new MapFragment()).commit();
    }
}
