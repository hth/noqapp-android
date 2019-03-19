package com.noqapp.android.merchant.views.activities;



import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private String url = "";
    private ProgressDialog progressDialog;
    private boolean isPdf = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };
    private boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        webView = findViewById(R.id.webView);
        if (null != getIntent().getStringExtra("url")) {
            url = getIntent().getStringExtra("url");
        }
        if (null != getIntent().getStringExtra("title")) {
            tv_toolbar_title.setText(getIntent().getStringExtra("title"));
        }
        isPdf = getIntent().getBooleanExtra("isPdf",false);
        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if(isPdf){
            webView.loadUrl("http://docs.google.com/viewer?url="+url);
        }else {
            webView.loadUrl(url);
        }
        webView.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }

        });
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void webViewGoBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (progressDialog == null && active) {
                progressDialog = new ProgressDialog(WebViewActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //super.onPageFinished(view, url);
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
