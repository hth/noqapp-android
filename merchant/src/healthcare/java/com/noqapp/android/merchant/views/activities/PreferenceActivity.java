package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class PreferenceActivity extends AppCompatActivity {

    private ListView lv_tests;
    private AutoCompleteTextView actv_search;
    private ArrayList<String> listData = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        lv_tests = findViewById(R.id.lv_tests);
        actv_search = findViewById(R.id.actv_search);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.search_array));
        actv_search.setAdapter(adapter);
        actv_search.setThreshold(1);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                if (!listData.contains(value)) {
                    listData.add(value);
                    listAdapter.notifyDataSetChanged();
                    actv_search.setText("");
                } else {
                    Toast.makeText(PreferenceActivity.this, "Already selected", Toast.LENGTH_LONG).show();
                }
            }
        });
        actv_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (actv_search.getRight() - actv_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        actv_search.setText("");
                        return true;
                    }
                    if (event.getRawX() <= (20 + actv_search.getLeft() + actv_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        //performSearch();
                        return true;
                    }
                }
                return false;
            }
        });
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listData);
        lv_tests.setAdapter(listAdapter);
    }

}
