package com.noqapp.android.client.views.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;

public class WebViewActivity extends BaseActivity {
    private WebView webView;
    private String url = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initActionsViews(true);
        webView = findViewById(R.id.webView);
        if (null != getIntent().getStringExtra(IBConstant.KEY_URL)) {
            url = getIntent().getStringExtra(IBConstant.KEY_URL);
        }
        isPdf = getIntent().getBooleanExtra(IBConstant.KEY_IS_PDF, false);
        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (isPdf) {
            webView.loadUrl("http://docs.google.com/viewer?url=" + url);
        } else {
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
        switch (url) {
            case Constants.URL_HOW_IT_WORKS:
                tv_toolbar_title.setText(getString(R.string.screen_invite_details));
                break;
            case Constants.URL_MERCHANT_LOGIN:
            case Constants.URL_MERCHANT_REGISTER:
                tv_toolbar_title.setText(getString(R.string.merchant_account));
                break;
            default:
                if (null != getIntent().getStringExtra("title")) {
                    tv_toolbar_title.setText(getIntent().getStringExtra("title"));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void webViewGoBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    private class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            dismissProgress();
            setProgressMessage("Loading...");
            showProgress();

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
                dismissProgress();
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
