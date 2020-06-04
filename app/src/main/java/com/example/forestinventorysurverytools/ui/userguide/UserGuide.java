package com.example.forestinventorysurverytools.ui.userguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.R;


public class UserGuide extends AppCompatActivity implements View.OnClickListener {

    ImageView mImportance_mark;

    TextView mTitle;
//    TextView m;

    ImageButton mNext_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);

        mImportance_mark = (ImageView)findViewById(R.id.importance_mark);

        mTitle = (TextView)findViewById(R.id.userGuide_title);

        mNext_layout = (ImageButton)findViewById(R.id.next_layout);
        mNext_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == mNext_layout) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
//            Intent intent = new Intent(getApplicationContext(), Test.class);
//            startActivity(intent);
        }
    }
}
