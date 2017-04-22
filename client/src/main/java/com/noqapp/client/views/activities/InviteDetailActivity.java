package com.noqapp.client.views.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.noqapp.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteDetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tv_title)
    protected TextView tv_title;
    @BindView(R.id.tv_details)
    protected TextView tv_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitedetail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        tv_toolbar_title.setText("Invite Details");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            String title = extras.getString("title", "");
            String details = extras.getString("details", "");
            tv_title.setText(title);
            tv_details.setText(details);
            //added to check the content
            tv_title.setText("How it works?");
            tv_details.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                    " Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer" +
                    " took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries," +
                    " but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s " +
                    "with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                    "like Aldus PageMaker including versions of Lorem Ipsum.\n\n" +
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text " +
                    "ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                    "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
                    "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
                    "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        } else {
            tv_title.setText("");
            tv_details.setText("");
        }
    }


}
