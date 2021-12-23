package com.noqapp.android.client.views.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.SliderPagerAdapter;

import java.util.ArrayList;

/**
 * Created by chandra on 3/26/18.
 */
public class SliderActivity extends AppCompatActivity {
    private LinearLayout ll_dots;
    private ArrayList<String> slider_image_list = null;
    private int page_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_slider_pager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        page_position = getIntent().getIntExtra("pos", 0);
        init();
        addBottomDots(page_position);
        actionbarBack.setOnClickListener((View v) -> finish());
    }

    private void init() {
        ViewPager vp_slider = findViewById(R.id.vp_slider);
        ll_dots = findViewById(R.id.ll_dots);
        Bundle b = getIntent().getExtras();
        if (null != b)
            slider_image_list = b.getStringArrayList("imageurls");
        else
            slider_image_list = new ArrayList<>();
        SliderPagerAdapter sliderPagerAdapter;
        if (b.getBoolean("isDocument")) {
            sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list, true, b.getString("recordReferenceId"));
        } else {
            if (TextUtils.isEmpty(b.getString("bucket"))) {
                sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list);
            } else {
                sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list, b.getString("bucket"));
            }
        }

        vp_slider.setAdapter(sliderPagerAdapter);
        vp_slider.setCurrentItem(page_position);
        vp_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slider_image_list.size()];
        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#ffffff"));
            ll_dots.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(Color.parseColor("#b71c1c"));
        }
    }
}
