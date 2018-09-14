package com.qmtv.crashintercept;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Main4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        WebView webView = findViewById(R.id.web_view);

        webView.loadUrl("https://blog.csdn.net/feiyangyang980/article/details/77008405");
    }
}
