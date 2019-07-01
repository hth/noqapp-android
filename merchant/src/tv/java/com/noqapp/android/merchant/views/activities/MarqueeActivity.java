package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.adapters.MarqueeAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;

import java.util.ArrayList;
import java.util.List;

public class MarqueeActivity
        extends BaseActivity
        implements MarqueeAdapter.OnItemClickListener {
    private FixedHeightListView fh_list_view;
    public MarqueeAdapter adapter;
    private List<String> marqueeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText("Marquee Settings");
        EditText edt_marquee = findViewById(R.id.edt_marquee);
        Button btn_add_marquee = findViewById(R.id.btn_add_marquee);
        marqueeList = LaunchActivity.getLaunchActivity().getMarquee();
        btn_add_marquee.setOnClickListener(View -> {
            //TODO error on add
            if (!TextUtils.isEmpty(edt_marquee.getText().toString())) {
                marqueeList.add(edt_marquee.getText().toString());
                adapter.notifyDataSetChanged();
                edt_marquee.setText("");
                LaunchActivity.getLaunchActivity().saveMarquee(marqueeList);
            }
        });
        fh_list_view = findViewById(R.id.fh_list_view);
        adapter = new MarqueeAdapter(MarqueeActivity.this, marqueeList, this);
        fh_list_view.setAdapter(adapter);

    }

    @Override
    public void deleteMarquee(String item) {
        ShowCustomDialog showDialog = new ShowCustomDialog(this);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                new CustomToast().showToast(MarqueeActivity.this, "Deleted from Marquee List");
                marqueeList.remove(item);
                adapter.notifyDataSetChanged();
                LaunchActivity.getLaunchActivity().saveMarquee(marqueeList);
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog("Delete Marquee Item", "Do you want to delete it from Marquee List?");

    }
}
