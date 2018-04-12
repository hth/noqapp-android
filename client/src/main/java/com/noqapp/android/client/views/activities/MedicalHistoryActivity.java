package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.NotificationBeans;
import com.noqapp.android.client.views.adapters.MedicalHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicalHistoryActivity extends AppCompatActivity  {

    @BindView(R.id.listview)
    protected ListView listview;
    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_empty)
    protected TextView tv_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_toolbar_title.setText(getString(R.string.medical_history));

        List<NotificationBeans> notificationsList = dummyData();
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, notificationsList);
        listview.setAdapter(adapter);
        if(notificationsList.size()<=0){
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }else{
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(MedicalHistoryActivity.this,MedicalHistoryDetailActivity.class);
                startActivity(in);
            }
        });

    }

    private List<NotificationBeans> dummyData(){
        List<NotificationBeans> list = new ArrayList<>();
        for(int i = 0; i< 5;i++){
            list.add(new NotificationBeans("title "+i,"This is medical history of the patient.","1",""));
        }
        return list;
    }

}
