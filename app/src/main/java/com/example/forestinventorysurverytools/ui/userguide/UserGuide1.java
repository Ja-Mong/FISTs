package com.example.forestinventorysurverytools.ui.userguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.forestinventorysurverytools.R;

public class UserGuide1 extends Fragment {

    //View
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_userguide1, null);


        return root;
    }
}
