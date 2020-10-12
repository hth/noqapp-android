package com.noqapp.android.client.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.ProductSliderPagerAdapter;
import com.noqapp.android.common.beans.store.JsonStoreProduct;

import java.util.ArrayList;
import java.util.List;

public class ProductSliderActivity extends BaseActivity {
    private LinearLayout ll_dots;
    private List<JsonStoreProduct> slider_product_list = null;
    private int page_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_slider_pager);
        initActionsViews(true);
        page_position = getIntent().getIntExtra("pos", 0);
        init();
        addBottomDots(page_position);
    }

    private void init() {
        ViewPager vp_slider = findViewById(R.id.vp_slider);
        ll_dots = findViewById(R.id.ll_dots);
        Bundle b = getIntent().getExtras();
        if (null != b) {
            slider_product_list = (List<JsonStoreProduct>) b.getSerializable("storeProduct");
        } else {
            slider_product_list = new ArrayList<>();
        }
        ProductSliderPagerAdapter sliderPagerAdapter = new ProductSliderPagerAdapter(this, slider_product_list);
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
        TextView[] dots = new TextView[slider_product_list.size()];
        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#999999"));
            ll_dots.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(Color.parseColor("#b71c1c"));
        }
    }
}
