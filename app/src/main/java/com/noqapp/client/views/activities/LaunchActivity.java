package com.noqapp.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.utils.Constants;
import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.RegistrationFormFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;


import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends NoQueueBaseActivity implements OnClickListener {


    public static final String DID = UUID.randomUUID().toString();
    private static LaunchActivity launchActivity;

    @BindView(R.id.rl_list)
    protected RelativeLayout rl_list;
    @BindView(R.id.rl_home)
    protected RelativeLayout rl_home;
    @BindView(R.id.rl_me)
    protected RelativeLayout rl_me;
    @BindView(R.id.tv_home)
    protected  TextView tv_home;
    @BindView(R.id.tv_list)
    protected TextView tv_list;
    @BindView(R.id.tv_me)
    protected TextView tv_me;
    @BindView(R.id.iv_home)
    protected ImageView iv_home;
    @BindView(R.id.iv_list)
    protected ImageView iv_list;
    @BindView(R.id.iv_me)
    protected ImageView iv_me;
    @BindView(R.id.toolbar)
    protected  Toolbar toolbar;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        launchActivity=this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        rl_home.setOnClickListener(this);
        rl_list.setOnClickListener(this);
        rl_me.setOnClickListener(this);
        iv_home.setBackgroundResource(R.mipmap.home_select);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
        replaceFragmentWithoutBackStack(R.id.frame_layout, new ScanQueueFragment());


    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        Fragment fragment=null;
        resetButtons();
        switch (id) {
            case R.id.rl_home:
                fragment = new ScanQueueFragment();
                iv_home.setBackgroundResource(R.mipmap.home_select);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;

            case R.id.rl_list:
                fragment = ListQueueFragment.getInstance();
                ListQueueFragment.isCurrentQueueCall = true;
                ((ListQueueFragment)fragment).callQueue();
                iv_list.setBackgroundResource(R.mipmap.list_select);
                tv_list.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;

            case R.id.rl_me:
                fragment = MeFragment.getInstance();
                iv_me.setBackgroundResource(R.mipmap.me_select);
                tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;
            default:
                break;

        }
        replaceFragmentWithoutBackStack(R.id.frame_layout,fragment);

    }

    public static LaunchActivity getLaunchActivity(){
        return  launchActivity;
    }
    public void setActionBarTitle(String title){
        tv_toolbar_title.setText(title);
    }

    private void  resetButtons(){

        iv_home.setBackgroundResource(R.mipmap.home_inactive);
        iv_list.setBackgroundResource(R.mipmap.list_inactive);
        iv_me.setBackgroundResource(R.mipmap.me_inactive);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_list.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.requestCodeJoinQActiviy) {
//            if (resultCode == Activity.RESULT_OK) {
//                int qrCode = data.getExtras().getInt(JoinQueueActivity.KEY_CODEQR);
//                Log.d("QR Code :: ", String.valueOf(qrCode));
//                onClick(rl_list);
//            }
//        }
//
//    }
}
