package com.noqapp.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.utils.Constants;
import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.RegistrationFormFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

import org.w3c.dom.Text;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends NoQueueBaseActivity implements OnClickListener {


    public static final String DID = UUID.randomUUID().toString();
    private RelativeLayout rl_list,rl_home,rl_me;
    private TextView tv_home,tv_list,tv_me;
    private ImageView iv_home,iv_list,iv_me;
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

        iv_home=(ImageView) findViewById(R.id.iv_home);
        iv_list=(ImageView) findViewById(R.id.iv_list);
        iv_me=(ImageView) findViewById(R.id.iv_me);

        tv_home=(TextView) findViewById(R.id.tv_home);
        tv_list=(TextView) findViewById(R.id.tv_list);
        tv_me=(TextView) findViewById(R.id.tv_me);
        iv_home.setBackgroundResource(R.mipmap.home_select);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
        replaceFragmentWithoutBackStack(R.id.frame_layout, ScanQueueFragment.getInstance());


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
        tv_title.setText(title);
    }

    private void  resetButtons(){

        iv_home.setBackgroundResource(R.mipmap.home_inactive);
        iv_list.setBackgroundResource(R.mipmap.list_inactive);
        iv_me.setBackgroundResource(R.mipmap.me_inactive);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_list.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeJoinQActiviy) {
            if (resultCode == Activity.RESULT_OK) {
                int qrCode = data.getExtras().getInt(JoinQueueActivity.KEY_CODEQR);
                Log.d("QR Code :: ", String.valueOf(qrCode));
                onClick(rl_list);
            }
        }

    }
}
