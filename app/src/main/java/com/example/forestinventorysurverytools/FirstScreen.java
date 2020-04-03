package com.example.forestinventorysurverytools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FirstScreen extends AppCompatActivity implements View.OnClickListener{

    TextView mTitle;
    TextView mSub_title;
    TextView mContent;

    ImageView mKfs_mark;
    ImageView mKnu_mark;
    ImageView mNotice;
//    ImageView mBackGround;

    Button mGuide_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);

        mTitle = (TextView) findViewById(R.id.title);
        mSub_title = (TextView)findViewById(R.id.sub_title);
        mContent = (TextView)findViewById(R.id.content);

        mKfs_mark = (ImageView)findViewById(R.id.kfs_mark);
        mKnu_mark = (ImageView)findViewById(R.id.knu_mark);
        mNotice = (ImageView)findViewById(R.id.notice);

        mGuide_btn = (Button)findViewById(R.id.guide_btn);
        mGuide_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mGuide_btn) {
            Intent intent = new Intent(getApplicationContext(), UserGuide.class);
            startActivity(intent);
        }
    }
}
