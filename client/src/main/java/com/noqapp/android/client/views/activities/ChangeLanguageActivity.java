package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.LanguageAdapter;
import com.noqapp.android.client.views.pojos.LanguageInfo;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;

public class ChangeLanguageActivity extends BaseActivity implements LanguageAdapter.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_change_language);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.lang_dialog_title));
        RecyclerView rv_indian_language = findViewById(R.id.rv_indian_language);
        rv_indian_language.setHasFixedSize(true);
        rv_indian_language.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_indian_language.setItemAnimator(new DefaultItemAnimator());

        RecyclerView rv_foreign_language = findViewById(R.id.rv_foreign_language);
        rv_foreign_language.setHasFixedSize(true);
        rv_foreign_language.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_foreign_language.setItemAnimator(new DefaultItemAnimator());

        LanguageAdapter indianLanguageAdapter = new LanguageAdapter(this, getEnabledIndianLanguage(), this, true);
        rv_indian_language.setAdapter(indianLanguageAdapter);

        LanguageAdapter foreignLanguageAdapter = new LanguageAdapter(this, getEnabledForeignLanguage(), this, false);
        rv_foreign_language.setAdapter(foreignLanguageAdapter);
    }

    @Override
    public void onLanguageSelected(LanguageInfo languageInfo) {
        new CustomToast().showToast(this, " Language changed successfully");
        finish();
    }

    private ArrayList<LanguageInfo> getEnabledIndianLanguage() {
        ArrayList<LanguageInfo> indianLanguages = new ArrayList<>();
        indianLanguages.add(new LanguageInfo(getString(R.string.hindi), "hi", R.drawable.flag_india, LaunchActivity.language.equals("hi")));
        indianLanguages.add(new LanguageInfo(getString(R.string.kannada), "kn", R.drawable.flag_india, LaunchActivity.language.equals("kn")));
        return indianLanguages;
    }

    private ArrayList<LanguageInfo> getEnabledForeignLanguage() {
        ArrayList<LanguageInfo> foreignLanguages = new ArrayList<>();
        foreignLanguages.add(new LanguageInfo(getString(R.string.french), "fr", R.drawable.flag_france, LaunchActivity.language.equals("fr")));
        foreignLanguages.add(new LanguageInfo(getString(R.string.english), "en", R.drawable.flag_united_kingdom, LaunchActivity.language.equals("en_US")));
        return foreignLanguages;
    }
}
