package com.ecaray.ecaradsdk.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ecaray.ecaradsdk.EcarAdSplManager;
import com.ecaray.ecaradsdk.R;

public class WebActivity extends Activity {
    WebView webView;
    ProgressBar progressbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecar_web);
        webView=(WebView)findViewById(R.id.webView);
        progressbar=(ProgressBar)findViewById(R.id.progressbar);
        Intent  intent=getIntent();
        String   url = intent.getStringExtra("url");
        //WebView加载web资源
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                Log.i("tagtag",  "shouldOverrideUrlLoading    ");
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("tagtag",  "onPageStarted    ");
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressbar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i("tagtag",  "onReceivedTitle    ");
//                currentUrlTitle = title;
//                mShowTitle.setText(currentUrlTitle);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(EcarAdSplManager.requestCode);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setResult(EcarAdSplManager.requestCode);
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }


    }
}
