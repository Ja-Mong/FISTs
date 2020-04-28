package com.example.forestinventorysurverytools;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.forestinventorysurverytools.ui.diameter.DiameterFragment;
import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.example.forestinventorysurverytools.ui.height.HeightFragment;
import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    InclinometerFragment inclinometerFragment;
    DistanceFragment distanceFragment;
    DiameterFragment diameterFragment;
    HeightFragment heightFragment;

    boolean cameraPermission;
    boolean writePermission;
    boolean locationPermission;

    public TextView mInclinometer_tv;
    public TextView mDistance_tv;
    public TextView mDiameter_tv;
    public TextView mHeight_tv;
    public TextView mCompass_tv;
    public TextView mAltitude_tv;
    public EditText mInputHeight;

    // 거리, 흉고직경, 높이 실제 값 저장 변수
    public double mInclinometer_val;
    public double mDistance_val;
    public double mDiameter_val;
    public double mHeight_val;


    //heightfragment에서 메인으로 이동
    public Vector<Double> height_vec = new Vector<Double>(); // 측정하는 모든 angle 값 저장
    public Vector<Float> angle_vec = new Vector<Float>(); // 측정하는 모든 angle 값 저장
    public Vector<Double> diameter_vec = new Vector<Double>(); // 측정하는 모든 angle 값 저장

    //AR관련
    public ArFragment arFragment;
    public Anchor anchor = null;
    public AnchorNode anchorNode;
    public ModelRenderable modelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInclinometer_tv = (TextView)this.findViewById(R.id.tv_inclinometer);
        mDistance_tv = (TextView)this.findViewById(R.id.tv_distance);
        mDiameter_tv = (TextView)this.findViewById(R.id.tv_diameter);
        mHeight_tv = (TextView)this.findViewById(R.id.tv_height);
        mCompass_tv = (TextView)this.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView)this.findViewById(R.id.tv_alititude);
        mInputHeight = (EditText)this.findViewById(R.id.input_height);


        inclinometerFragment = new InclinometerFragment(this);
        distanceFragment = new DistanceFragment(this);
        diameterFragment = new DiameterFragment(this);
        heightFragment = new HeightFragment(this);



        //AR 지원 가능 여부 체크
        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_LONG).show();
        }
        //arFragment정의
        FragmentManager fm = getSupportFragmentManager();
        arFragment = (ArFragment) fm.findFragmentById(R.id.camera_preview_fr);

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true;
        }

        if (!cameraPermission || writePermission || locationPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public boolean checkIsSupportedDeviceOrFinish(final Activity activity) {

        String openGlVersionString =
                ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < 3.0) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }


    public void initModel() {
        MaterialFactory.makeTransparentWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3(0.05f, 0.01f, 0.01f);
                            modelRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b  = (modelRenderable==null);

                        });
    }
    public void clearAnchor() {
        anchor = null;
        if (anchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
            anchorNode = null;
        }
    }



    public void onRequestPermissionsResults(int requsetCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requsetCode, permissions, grantResults);
        if (requsetCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraPermission = true;
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                writePermission = true;
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                locationPermission = true;
        }
    }




    public void tv_Reset(){
        // 초기화(리셋) 버튼 기능, 버튼 연결 보류
        mInclinometer_tv.setText("경        사 :");
        mDistance_tv.setText("거        리 :");
        mDiameter_tv.setText("흉고직경 :");
        mHeight_tv.setText("수        고 :");
        mCompass_tv.setText("방        위 :");
        mAltitude_tv.setText("고        도 :");
        mInclinometer_val = 0.0;
        mDistance_val = 0.0;
        mDiameter_val = 0.0;
        mHeight_val = 0.0;
        height_vec.removeAllElements();
        angle_vec.removeAllElements();


    }



    public void Save_data(){
        //저장 버튼 기능, 버튼 연결 보류
        //POI 라이브러리 다운 필요
        if((mDistance_val != 0.0 ) &&(mDiameter_val != 0.0 ) &&(mHeight_val != 0.0))
        {
            SimpleDateFormat dateformat = new SimpleDateFormat("forest_yyMMdd_HHmmss");
            String filename = dateformat.format(System.currentTimeMillis());

        }

    }
}
