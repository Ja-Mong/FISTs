package com.example.forestinventorysurverytools.ui.userguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.R;

import java.util.ArrayList;


public class UserGuide extends AppCompatActivity implements View.OnClickListener {

    //View Pager
    ViewPager mViewPager;

    //ImageView & TextView & Button
    ImageView mImportance_mark;
    TextView mTitle;
    ImageButton mNext_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);

        mViewPager = (ViewPager)findViewById(R.id.guideline_pager);
        mViewPager.setOffscreenPageLimit(5);

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        UserGuide1 fragment1 = new UserGuide1();
        myPagerAdapter.addItem(fragment1);

        UserGuide2 fragment2 = new UserGuide2();
        myPagerAdapter.addItem(fragment2);

        UserGuide3 fragment3 = new UserGuide3();
        myPagerAdapter.addItem(fragment3);

        UserGuide4 fragment4 = new UserGuide4();
        myPagerAdapter.addItem(fragment4);

        UserGuide5 fragment5 = new UserGuide5();
        myPagerAdapter.addItem(fragment5);

        mViewPager.setAdapter(myPagerAdapter);

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
        }
    }


    //ViewPager 내부 클래스 선언
    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> guides = new ArrayList<Fragment>();
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public void addItem(Fragment item) {
            guides.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            return guides.get(position);
        }

        @Override
        public int getCount() {
            return guides.size();
        }
    }
}
