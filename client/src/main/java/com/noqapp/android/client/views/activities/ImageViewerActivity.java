package com.noqapp.android.client.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.customviews.TouchImageView;
import com.noqapp.android.common.customviews.CustomToast;
import com.squareup.picasso.Picasso;

/**
 * Created by chandra on 3/26/18.
 */

public class ImageViewerActivity extends AppCompatActivity {
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.layout_image_viewer);
        TouchImageView im_slider = findViewById(R.id.im_slider);

        if (null != getIntent().getStringExtra(IBConstant.KEY_URL)) {
            url = getIntent().getStringExtra(IBConstant.KEY_URL);
            Picasso.get().load(url).into(im_slider);
        } else {
            new CustomToast().showToast(this, "Image url is missing");
        }

        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener((View v) -> {
            finish();
        });
    }

}
