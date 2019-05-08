package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.customviews.TouchImageView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by chandra on 3/26/18.
 */

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_image_viewer);
        TouchImageView im_slider = findViewById(R.id.im_slider);
        Picasso.get()
                .load("https://noqapp.com/imgs/appmages/garbhasanskar-ssd-march-2019.png")
                .into(im_slider);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
