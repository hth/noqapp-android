package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.FeedObj;

import com.squareup.picasso.Picasso;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initActionsViews(true);

        tv_toolbar_title.setText("");
        ImageView iv_bg = findViewById(R.id.iv_bg);
        TextView tv_details = findViewById(R.id.tv_details);
        TextView tv_title = findViewById(R.id.tv_title);
        FeedObj feedObj = (FeedObj) getIntent().getSerializableExtra("object");
        tv_title.setText(feedObj.getTitle());
        Picasso.with(this).load(feedObj.getImageUrl()).into(iv_bg);
        String data = feedObj.getContent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_details.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_details.setText(Html.fromHtml(data));
        }

    }
}
