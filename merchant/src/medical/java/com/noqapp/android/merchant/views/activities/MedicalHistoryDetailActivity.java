package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class MedicalHistoryDetailActivity extends AppCompatActivity {
    private final String packageName = "com.google.android.apps.handwriting.ime";
    private ImageView actionbarBack;
    private HashMap<String, ArrayList<String>> mHashmapTemp = null;
    private String qCodeQR = "";
    private AutoCompleteTextView edt_complaints;
    protected AutoCompleteTextView edt_past_history;
    private final String xray = "X-ray";
    private final String medicine = "Medicine";
    private final String mri = "MRI";
    private JsonQueuedPerson jsonQueuedPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);

        edt_complaints = (AutoCompleteTextView) findViewById(R.id.edt_complaints);
        edt_past_history = (AutoCompleteTextView) findViewById(R.id.edt_past_history);
        qCodeQR = getIntent().getStringExtra("qCodeQR");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Medical History");
        if (!appInstalledOrNot(packageName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = (Button) customDialogView.findViewById(R.id.btn_yes);
            Button btn_no = (Button) customDialogView.findViewById(R.id.btn_no);
            TextView tv_title = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            btn_yes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                    mAlertDialog.dismiss();

                }
            });
            mAlertDialog.show();
            btn_yes.setText("Download");
            btn_no.setText("Cancel");
            tv_msg.setText("Download the Scribble writer app to make your life easy.");
            tv_title.setText("Scribble app missing");
        }

//        HashMap<String, ArrayList<String>> mHashmap = new HashMap<>();
//        ArrayList<String> aa =new ArrayList<>();
//        aa.add("lower elbow");
//        aa.add("upper hand");
//        ArrayList<String> bb =new ArrayList<>();
//        bb.add("Amoxo");
//        bb.add("Paracetamol");
//
//        ArrayList<String> cc =new ArrayList<>();
//        cc.add("qwerty");
//        cc.add("ytrewq");
//        mHashmap.put("X-ray",aa);
//        mHashmap.put("Medicine",bb);
//        mHashmap.put("MRI",cc);

//        LaunchActivity.getLaunchActivity().setSuggestions(mHashmap);
//

        String strOutput = LaunchActivity.getLaunchActivity().getSuggestions();
        Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {
        }.getType();
        Gson gson = new Gson();
        if (StringUtils.isBlank(strOutput)) {
            Log.v("JSON", "empty json");
        } else {
            try {
                mHashmapTemp = gson.fromJson(strOutput, type);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(xray));
                edt_past_history.setAdapter(adapter);

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(medicine));
                edt_complaints.setAdapter(adapter1);
                edt_complaints.setThreshold(1);
                edt_past_history.setThreshold(1);
                Log.v("JSON", mHashmapTemp.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!edt_past_history.getText().toString().equals(""))
            if (!mHashmapTemp.get(xray).contains(edt_past_history.getText().toString())) {
                mHashmapTemp.get(xray).add(edt_past_history.getText().toString());
            }
        if (!edt_complaints.getText().toString().equals(""))
            if (!mHashmapTemp.get(medicine).contains(edt_complaints.getText().toString())) {
                mHashmapTemp.get(medicine).add(edt_complaints.getText().toString());
            }
        LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);

    }
}
