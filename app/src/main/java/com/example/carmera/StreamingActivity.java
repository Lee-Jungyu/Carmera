package com.example.carmera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class StreamingActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.setPadding(0,0,0,0);
        //webView.setInitialScale(100);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        String url ="http://192.168.0.32:8090/javascript_simple.html";
        webView.loadUrl(url);
        //String imgSrcHtml = "<html><img src='" + url + "' /></html>";
        // String imgSrcHtml = url;
        //webView.loadData(imgSrcHtml, "text/html", "UTF-8");
    }
}