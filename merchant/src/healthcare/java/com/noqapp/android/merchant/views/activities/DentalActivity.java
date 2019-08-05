package com.noqapp.android.merchant.views.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

public class DentalActivity extends BaseActivity implements View.OnClickListener {
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    public final int[] DENTAl_DRAWABLES = new int[32];
    public final ImageView[] imageViews = new ImageView[32];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dental);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(v -> onBackPressed());
        tv_toolbar_title.setText("Dental Chart");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        for (int i = 0; i < imageViews.length; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("iv_" + (i + 1)), "id", this.getPackageName());
            imageViews[i] = findViewById(id);
            DENTAl_DRAWABLES[i] = R.drawable.tooth;
            imageViews[i].setBackgroundResource(DENTAl_DRAWABLES[i]);
            imageViews[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DentalActivity.this);
        LayoutInflater inflater = LayoutInflater.from(DentalActivity.this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        ImageView[] imageViewsDialog = new ImageView[8];
        int[] DENTAl_DRAWABLES_DIALOG = new int[8];
        for (int i = 0; i < imageViewsDialog.length; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("iv_dialog_" + (i + 1)), "id", this.getPackageName());
            imageViewsDialog[i] = customDialogView.findViewById(id);
            DENTAl_DRAWABLES_DIALOG[i] = R.drawable.tooth_decay;
            imageViewsDialog[i].setBackgroundResource(DENTAl_DRAWABLES_DIALOG[i]);
            imageViewsDialog[i].setOnClickListener(v13 -> {
                mAlertDialog.dismiss();
                v.setBackgroundResource(R.drawable.tooth_decay);
            });
        }

        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v1 -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v12 -> mAlertDialog.dismiss());
        mAlertDialog.show();

    }
}