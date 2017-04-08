package com.noqapp.client.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends AppCompatActivity implements OnClickListener {


    public static final String DID = UUID.randomUUID().toString();
    private RelativeLayout rl_list,rl_home,rl_me;
    private static LaunchActivity launchActivity;
    private Toolbar actionBarToolbar;
    private  TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        launchActivity=this;
        actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        tv_title =(TextView)findViewById(R.id.tv_title);
        setSupportActionBar(actionBarToolbar);
        getSupportActionBar().setTitle("");
        rl_home=(RelativeLayout) findViewById(R.id.rl_home);
        rl_home.setOnClickListener(this);
        rl_list=(RelativeLayout) findViewById(R.id.rl_list);
        rl_list.setOnClickListener(this);
        rl_me=(RelativeLayout) findViewById(R.id.rl_me);
        rl_me.setOnClickListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, new ScanQueueFragment()).commit();

    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        Fragment fragment=null;
        switch (id) {
            case R.id.rl_home:
                fragment = new ScanQueueFragment();
                break;

            case R.id.rl_list:
                fragment = ListQueueFragment.getInstance();
                break;

            case R.id.rl_me:
                fragment = MeFragment.getInstance();
                break;
            default:
                break;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();



    }

    public static LaunchActivity getLaunchActivity(){
        return  launchActivity;
    }
    public void setActionBarTitle(String title){
        tv_title.setText(title);
    }
}
