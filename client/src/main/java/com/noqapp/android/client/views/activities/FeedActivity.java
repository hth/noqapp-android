package com.noqapp.android.client.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.squareup.picasso.Picasso;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_back_new);
        mToolbar.setNavigationOnClickListener(v -> finish());
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");


        ImageView iv_bg = findViewById(R.id.expandedImage);
        TextView tv_details = findViewById(R.id.tv_details);
        TextView tv_title = findViewById(R.id.tv_title);
        final JsonFeed jsonFeed = getIntent().getParcelableExtra(IBConstant.KEY_DATA_OBJECT);
        tv_title.setText(jsonFeed.getTitle());
        RelativeLayout rl_author = findViewById(R.id.rl_author);
        if (TextUtils.isEmpty(jsonFeed.getAuthor())) {
            rl_author.setVisibility(View.GONE);
        } else {
            rl_author.setVisibility(View.VISIBLE);
            ImageView iv_main = findViewById(R.id.iv_main);
            TextView tv_author_name = findViewById(R.id.tv_author_name);
            TextView tv_author_profession = findViewById(R.id.tv_author_profession);
            Picasso.get().load(jsonFeed.getAuthorThumbnail()).into(iv_main);
            tv_author_name.setText(jsonFeed.getAuthor());
            tv_author_profession.setText(jsonFeed.getProfession());

        }

        Picasso.get().load(jsonFeed.getImageUrl()).into(iv_bg);
        String data = jsonFeed.getContent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_details.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_details.setText(Html.fromHtml(data));
        }

        if (AppUtils.isRelease()) {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.CONTENT, jsonFeed.getTitle());
            params.putString(FirebaseAnalytics.Param.ITEM_ID, jsonFeed.getContentId());
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, jsonFeed.getContentType());
            LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(FabricEvents.EVENT_FEED, params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_favourite) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
