package com.example.forestinventorysurverytools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

public class AnalyzeView extends AppCompatActivity {

    public String UserID;
    public String baseurl;
    WebView webView;
    Button mReturn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Intent anViewIntent = getIntent();
        UserID = anViewIntent.getStringExtra("UserID");
        baseurl = anViewIntent.getStringExtra("baseurl");

        mReturn = (Button)findViewById(R.id.btn_return);
        webView = (WebView)findViewById(R.id.webView);
        mReturn.setOnClickListener(return_Listener);

        webView.setWebViewClient(new WebViewClient());
        String weburl = "";
        webView.loadUrl(weburl);

    }

    View.OnClickListener return_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


}