package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.FeedObj;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        final FeedObj feedObj = (FeedObj) getIntent().getSerializableExtra("object");
        tv_title.setText(feedObj.getTitle());

        RelativeLayout rl_author = findViewById(R.id.rl_author);
        // View view_separator = findViewById(R.id.view_separator);

        if (TextUtils.isEmpty(feedObj.getAuthor())) {
            rl_author.setVisibility(View.GONE);
            // view_separator.setVisibility(View.GONE);
        } else {
            rl_author.setVisibility(View.VISIBLE);
            //  view_separator.setVisibility(View.VISIBLE);
            ImageView iv_main = findViewById(R.id.iv_main);
            TextView tv_author_name = findViewById(R.id.tv_author_name);
            TextView tv_author_profession = findViewById(R.id.tv_author_profession);
            Picasso.with(this).load(feedObj.getAuthorPic()).into(iv_main);
            tv_author_name.setText(feedObj.getAuthor());
            tv_author_profession.setText(feedObj.getAuthorProfession());
            rl_author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FeedActivity.this, ManagerProfileActivity.class);
                    intent.putExtra("webProfileId", feedObj.getAuthorWebProfileId());
                    intent.putExtra("managerName", feedObj.getAuthor());
                    intent.putExtra("managerImage", feedObj.getAuthorPic());
                    intent.putExtra("bizCategoryId", MedicalDepartmentEnum.PHY.getName());
                    startActivity(intent);
                }
            });
        }

        Picasso.with(this).load(feedObj.getImageUrl()).into(iv_bg);
        String data = feedObj.getContent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_details.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_details.setText(Html.fromHtml(data));
        }

        if (BuildConfig.BUILD_TYPE.equals("release")) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(feedObj.getTitle())
                    .putContentType(feedObj.getContentType())
                    .putContentId("feed-dec-18"));
        }
    }
}
