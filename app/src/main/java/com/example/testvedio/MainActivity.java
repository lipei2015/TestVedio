package com.example.testvedio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        /*File outputImage = new File(Environment.getExternalStorageDirectory(),
                "tempImage" + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);*/

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Toast.makeText(MainActivity.this,keyCode+"++",Toast.LENGTH_SHORT).show();
        if(keyCode ==   KeyEvent.KEYCODE_BACK)
        {
            return   true;
        }
        if(keyCode ==   KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            return   true;
        }
        if(keyCode ==   KeyEvent.KEYCODE_VOLUME_UP)
        {
            return   true;
        }
        if(keyCode ==   KeyEvent.KEYCODE_SEARCH)
        {
            return   true;
        }
        if(keyCode ==   KeyEvent.KEYCODE_MENU)
        {
            return   true;
        }
        return super.onKeyDown(keyCode,   event);
    }
}
