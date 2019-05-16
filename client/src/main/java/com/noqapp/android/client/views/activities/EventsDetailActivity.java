package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.common.beans.JsonAdvertisement;
import com.squareup.picasso.Picasso;


public class EventsDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        initActionsViews(true);
        tv_toolbar_title.setText("Event & Offer Details");
        Intent bundle = getIntent();
        if (null != bundle) {
            JsonAdvertisement jsonEvent = (JsonAdvertisement) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA_OBJECT);
            ImageView iv_event = findViewById(R.id.iv_event);
            if (jsonEvent.getImageUrls().size()==0) {
                Picasso.get().load(R.drawable.noqbanner).into(iv_event);
            } else {
                Picasso.get().load(jsonEvent.getImageUrls().get(0)).into(iv_event);
            }
            TextView tv_sub_title = findViewById(R.id.tv_sub_title);
            TextView tv_title = findViewById(R.id.tv_title);
            TextView tv_tac = findViewById(R.id.tv_tac);
            tv_sub_title.setText(jsonEvent.getShortDescription());
            tv_title.setText(jsonEvent.getTitle());
            String term = "";
            for (int i = 0; i < jsonEvent.getTermAndConditions().size(); i++) {
                term += getString(R.string.circle_bullet) + " "+jsonEvent.getTermAndConditions().get(i) + " \n";
            }
            tv_tac.setText(term);
        }
    }

}
