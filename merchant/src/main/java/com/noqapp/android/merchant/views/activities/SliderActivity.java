package com.noqapp.android.merchant.views.activities;



import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.adapters.SliderPagerAdapter;

import android.graphics.Color;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_slider_pager);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        page_position = getIntent().getIntExtra("pos", 0);
        init();
        addBottomDots(page_position);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void init() {
        ViewPager vp_slider = findViewById(R.id.vp_slider);
        ll_dots = findViewById(R.id.ll_dots);
        Bundle b = getIntent().getExtras();
        if (null != b)
            slider_image_list = b.getStringArrayList("imageurls");
        else
            slider_image_list = new ArrayList<>();
        SliderPagerAdapter sliderPagerAdapter ;
        if(b.getBoolean("isDocument")){
            sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list,true,b.getString("recordReferenceId"));
        }else{
            sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list);
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
        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#b71c1c"));
    }
}
