package com.example.testvedio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
//        webView.loadUrl("http://www.baidu.com/");
        webView.loadUrl("https://blog.csdn.net/Leslie_LN/article/details/91584144");

        /*Uri uri = Uri.parse("https://blog.csdn.net/Leslie_LN/article/details/91584144");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);*/
    }
}
