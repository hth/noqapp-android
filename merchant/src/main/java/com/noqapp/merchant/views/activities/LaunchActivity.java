package com.noqapp.merchant.views.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.merchant.R;
import com.noqapp.merchant.views.fragments.LoginFragment;
import com.noqapp.merchant.views.fragments.MerchantListFragment;
import com.noqapp.merchant.helper.NetworkHelper;

public class LaunchActivity extends AppCompatActivity {
    private static LaunchActivity launchActivity;
    protected TextView tv_toolbar_title;
    public NetworkHelper networkHelper;
    public static final String MyPREFERENCES = "AppPref";
    private static SharedPreferences sharedpreferences;
    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_USER_ID = "userID";
    private ImageView iv_logout;
    private long lastPress;
    private Toast backpressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launchActivity=this;
        networkHelper=new NetworkHelper(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tv_toolbar_title=(TextView) findViewById(R.id.tv_toolbar_title);
        iv_logout =(ImageView)findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(launchActivity)
                        .setTitle("Logout")
                        .setMessage("Would you like to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // logout
                                sharedpreferences.edit().clear().commit();
                                //navigate to signup/login
                                replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
            }
        });
        if (isLoggedIn()) {
            replaceFragmentWithoutBackStack(R.id.frame_layout, new MerchantListFragment());
        }
        else {
            replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
        }

    }


    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
    }

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
        enableLogout();
    }


    public void replaceFragmentWithBackStack( int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment, tag).addToBackStack(tag).commit();

    }

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    public boolean isOnline() {
        return networkHelper.isOnline();
    }

    public static SharedPreferences getSharePreferance() {
        return sharedpreferences;
    }

    public String getUserName() {
        return sharedpreferences.getString(KEY_USER_NAME, "");
    }

    public String getUSerID() {
        return sharedpreferences.getString(KEY_USER_ID, "");
    }

    public boolean isLoggedIn() {
        return sharedpreferences.getBoolean(IS_LOGIN, false);
    }

    public void setSharPreferancename(String userName, String userID, String emailno, boolean isLogin) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userID);
        editor.putString(KEY_USER_EMAIL, emailno);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    private void enableLogout(){

        if (isLoggedIn()) {
            iv_logout.setVisibility(View.VISIBLE);
        }
        else {
            iv_logout.setVisibility(View.INVISIBLE);
        }
    }


    private void restartApp(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount()==0){
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPress > 3000) {
                    backpressToast = Toast.makeText(launchActivity, "Press back again to exit", Toast.LENGTH_LONG);
                    backpressToast.show();
                    lastPress = currentTime;
                } else {
                    if (backpressToast != null) backpressToast.cancel();
                    super.onBackPressed();
                }
        }else{
            super.onBackPressed();
        }
    }
}
