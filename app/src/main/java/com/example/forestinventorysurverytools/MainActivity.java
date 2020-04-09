package com.example.forestinventorysurverytools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.forestinventorysurverytools.ui.diameter.DiameterFragment;
import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.example.forestinventorysurverytools.ui.height.HeightFragment;
//import com.example.forestinventorysurverytools.ui.slope.SlopeFragment;
//import com.example.forestinventorysurverytools.ui.slope.SlopeIndicator;
//import com.example.forestinventorysurverytools.ui.slope.SlopeIndicator;
import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerFragment;
import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerIndicator;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerOrientation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    InclinometerFragment inclinometerFragment;
    DistanceFragment distanceFragment;
    DiameterFragment diameterFragment;
    HeightFragment heightFragment;

//    public InclinometerOrientation mInclinometerOrientation;
//    InclinometerIndicator mInclinometerIndicator;

    boolean cameraPermission;
    boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inclinometerFragment = new InclinometerFragment();
        distanceFragment = new DistanceFragment();
        diameterFragment = new DiameterFragment();
        heightFragment = new HeightFragment();

//        mInclinometerOrientation = new InclinometerOrientation(this);
//        mInclinometerIndicator = (InclinometerIndicator)findViewById(R.id.inclinometer);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, inclinometerFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_inclinometer:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, inclinometerFragment).commit();
                        return true;
                    case R.id.navigation_distance:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, distanceFragment).commit();
                        return true;
                    case R.id.navigation_diameter:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, diameterFragment).commit();
                        return true;
                    case R.id.navigation_height:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, heightFragment).commit();
                        return true;
                }
                return false;
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraPermission = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            writePermission = true;
        }
        if (!cameraPermission || writePermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public void onRequestPermissionsResults(int requsetCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requsetCode, permissions, grantResults);
        if (requsetCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraPermission = true;
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                writePermission = true;
        }
    }

//    @Override
//    public void onOrientationChanged(float pitch, float roll) {
//        mInclinometerIndicator.setInclimeter(pitch, roll);
//    }

//    @Override
//    public void onOrientationChanged(float clinoPitch, float clinoRoll) {
//        slopeIndicator.setSlope(clinoPitch, clinoRoll);
//    }
}
