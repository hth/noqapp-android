package com.noqapp.client.views.activities;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chandra on 4/9/17.
 */

public class InviteActivity extends AppCompatActivity {

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tv_how_it_works)
    protected TextView tv_how_it_works;
    @BindView(R.id.tv_copy)
    protected TextView tv_copy;
    @BindView(R.id.tv_title)
    protected TextView tv_title;
    @BindView(R.id.tv_details)
    protected TextView tv_details;
    @BindView(R.id.tv_invite_code)
    protected TextView tv_invite_code;
    @BindView(R.id.btn_send_invite)
    protected Button btn_send_invite;

    private String selectedText;


    public InviteActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        tv_how_it_works.setPaintFlags(tv_how_it_works.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_copy.setPaintFlags(tv_copy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        tv_toolbar_title.setText("Invite");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString("title", "Hardcoded Title");
            String details = bundle.getString("details", "Hardcoded Details....");
            String invite_code = bundle.getString("invite_code", "");

            if (invite_code.isEmpty()) {
                btn_send_invite.setEnabled(false);
            }
            tv_title.setText(title);
            tv_details.setText(details);
            tv_invite_code.setText(invite_code);
        } else {
            tv_title.setText("Title is here");
            tv_details.setText("Details are .....");
            tv_invite_code.setText("");
        }
        selectedText = tv_invite_code.getText().toString();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }

    @OnClick(R.id.btn_send_invite)
    public void sendInvitation() {

        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app to share invitation", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_copy)
    public void copyText() {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", selectedText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv_how_it_works)
    public void howItWorks() {
        Intent in = new Intent(this, InviteDetailActivity.class);
        in.putExtra("title", tv_title.getText().toString());
        in.putExtra("details", tv_details.getText().toString());
        startActivity(in);
    }
}
