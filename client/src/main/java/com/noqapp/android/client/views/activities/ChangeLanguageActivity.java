package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.LocaleHelper;
import com.noqapp.android.client.views.adapters.LanguageAdapter;
import com.noqapp.android.client.views.pojos.LanguageInfo;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;
import java.util.Objects;

public class ChangeLanguageActivity extends BaseActivity implements LanguageAdapter.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
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
        String languageCode = languageInfo.getLanguageCode();
        changeLanguage(this, languageCode);
        new CustomToast().showToast(this, " Language changed successfully");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    public static void changeLanguage(Context context, String language) {
        if (!language.equals("")) {
            switch (language) {
                case "en":
                    LocaleHelper.INSTANCE.setLocale(context, "en_US");
                    break;
                case "kn":
                    LocaleHelper.INSTANCE.setLocale(context, "kn");
                    break;
                case "fr":
                    LocaleHelper.INSTANCE.setLocale(context, "fr");
                    break;
                case "hi":
                    LocaleHelper.INSTANCE.setLocale(context, "hi");
                    break;
            }
        } else {
            LocaleHelper.INSTANCE.setLocale(context, "en_US");
        }
    }

    private ArrayList<LanguageInfo> getEnabledIndianLanguage() {
        ArrayList<LanguageInfo> indianLanguages = new ArrayList<>();
        indianLanguages.add(new LanguageInfo(getString(R.string.hindi), "hi", R.drawable.flag_india, Objects.equals(LocaleHelper.INSTANCE.getLocaleLanguage(this), "hi")));
        indianLanguages.add(new LanguageInfo(getString(R.string.kannada), "kn", R.drawable.flag_india, Objects.equals(LocaleHelper.INSTANCE.getLocaleLanguage(this), "kn")));
        return indianLanguages;
    }

    private ArrayList<LanguageInfo> getEnabledForeignLanguage() {
        ArrayList<LanguageInfo> foreignLanguages = new ArrayList<>();
        foreignLanguages.add(new LanguageInfo(getString(R.string.french), "fr", R.drawable.flag_france, Objects.equals(LocaleHelper.INSTANCE.getLocaleLanguage(this), "fr")));
        foreignLanguages.add(new LanguageInfo(getString(R.string.english), "en", R.drawable.flag_united_kingdom, Objects.equals(LocaleHelper.INSTANCE.getLocaleLanguage(this), "en_US")));
        return foreignLanguages;
    }
}
