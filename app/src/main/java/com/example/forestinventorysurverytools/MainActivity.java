package com.example.forestinventorysurverytools;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.forestinventorysurverytools.ui.diameter.DiameterFragment;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.example.forestinventorysurverytools.ui.height.HeightFragment;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {


    public DiameterFragment diameterFragment;
    HeightFragment heightFragment;

    public TextView mInclinometer_tv;
    public TextView mDistance_tv;
    public TextView mDiameter_tv;
    public TextView mHeight_tv;
    public TextView mCompass_tv;
    public TextView mAltitude_tv;


    // 거리, 흉고직경, 높이 실제 값 저장 변수
    public double mInclinometer_val;
    public double mDistance_val;
    public double mDiameter_val;
    public double mHeight_val;


    //heightfragment에서 메인으로 이동
    public Vector<Double> height_vec = new Vector<Double>(); // 측정하는 모든 angle 값 저장
    public Vector<Float> angle_vec = new Vector<Float>(); // 측정하는 모든 angle 값 저장
    public Vector<Double> altitude_vec = new Vector<Double>(); // 측정하는 모든 altitude 값 저장
    public Vector<Float> compass_vec = new Vector<Float>(); // 측정한 모든 compass 값 저장


    //AR관련
    public ArFragment arFragment;
    public Anchor anchor = null;
    public AnchorNode anchorNode;
    public ModelRenderable modelRenderable;


    //AR controller
    public SeekBar radiusbar;
    int radi = 10;
    public SeekBar heightbar; //동작은 heightFragment에서 생성한 anchor
    int height = 10;
    public ImageButton mTop;
    public ImageButton mBottom;
    int axis_Z = 0;
    public ImageButton mRight;
    public ImageButton mLeft;
    int axis_X = 0;
    public ImageButton mAdd_anchor;
    public ImageButton mDelete_anchor;


    //경사를 측정할 수 있는 Fragment가 필요함. 기존에 만들어 놓은 Inclinometer 부활 시키기..




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mInclinometer_tv = (TextView) this.findViewById(R.id.tv_inclinometer);
        mDistance_tv = (TextView) this.findViewById(R.id.tv_distance);
        mDiameter_tv = (TextView) this.findViewById(R.id.tv_diameter);
        mHeight_tv = (TextView) this.findViewById(R.id.tv_height);
        mCompass_tv = (TextView) this.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView) this.findViewById(R.id.tv_alititude);

        mTop = (ImageButton) this.findViewById(R.id.top);
        mBottom = (ImageButton) this.findViewById(R.id.bottom);
        mLeft = (ImageButton) this.findViewById(R.id.left);
        mRight = (ImageButton) this.findViewById(R.id.right);
        mAdd_anchor = (ImageButton) this.findViewById(R.id.Btn_add);
        mDelete_anchor = (ImageButton) this.findViewById(R.id.Btn_delete);

        mTop.setOnClickListener(controll_BtnTop);
        mBottom.setOnClickListener(controll_BtnBottom);
        mLeft.setOnClickListener(controll_BtnLeft);
        mRight.setOnClickListener(controll_BtnRight);
        mAdd_anchor.setOnClickListener(addNew_anchor);
        mDelete_anchor.setOnClickListener(delSelect_anchor);


        diameterFragment = new DiameterFragment(this);
        heightFragment = new HeightFragment(this);


        FragmentManager fm = getSupportFragmentManager();
        arFragment = (ArFragment) fm.findFragmentById(R.id.camera_preview_fr);

        //control radius
        radiusbar = (SeekBar) this.findViewById(R.id.radi_controller1);
        radiusbar.setMax(100);
        radiusbar.setProgress(radi);

        radiusbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                radi = progress;
                initModel();
                initModel2(); //heightBar 직경도 조절할 수 있도록 추가
                diameterFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                initModel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
            }
        });


        //heightBar 추가
        heightbar = (SeekBar) this.findViewById(R.id.heigth_controller1);
        heightbar.setMax(100);
        heightbar.setProgress(height);

        heightbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                height = progress;
                initModel2();
                heightFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                initModel2();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
            }
        });


        //arFragment정의
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, diameterFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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

    }



    public void initModel() {
        MaterialFactory.makeTransparentWithColor(this,
                new Color(1.0f, 0.0f, 0.0f, 0.5f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float) axis_X/100, 0.6f, (float) axis_Z/100);
                            modelRenderable = ShapeFactory.makeCylinder
                                    ((float) radi / 100, 1.2f,
                                            vector3, material);

                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b = (modelRenderable == null);

                        });

    }

    //Height_Anchor 모델 추가
    public void initModel2() {
        MaterialFactory.makeTransparentWithColor(this, new Color(0.0f, 0.0f, 1.0f, 0.5f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3(0.0f, 1.8f, 0.0f);
                            modelRenderable = ShapeFactory.makeCylinder
                                    ((float) radi/100, (float) height/100,
                                            vector3, material);

                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b = (modelRenderable == null);
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

    Anchor tmpA;
    AnchorNode tmpAN;
    TransformableNode node;

    public void saveAnchor() {
        tmpA = anchor;
        tmpAN = anchorNode;

    }

    public void retrieveAnchor() {
        anchor = tmpA;
        anchorNode = tmpAN;
        node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(modelRenderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }





    public void tv_Reset() {
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


    String dirPath;

    public void Save_data(View v) {
        //txt대신 서버 전송시 유리한 json 형식으로 핸드폰 내부 저장.

        // 추후 조건식 (mDistance_val != 0.0 ) &&(mDiameter_val != 0.0 ) &&(mHeight_val != 0.0)
        if (true) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
            String filename = "fist_" + dateformat.format(System.currentTimeMillis());

            if (CheckWrite()) {
                dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIST";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                    Log.d("tag", "directory 생성");
                }
                //File savefile = new File(dirPath+"/"+filename+".txt");
                File savefile = new File(dirPath + "/" + filename + ".json");
                try {
                    Log.d("tag", "File 생성시작");

                    JSONObject obj = new JSONObject();
                    obj.put("distance", mDistance_val);
                    obj.put("diameter", mDiameter_val);
                    obj.put("height", mHeight_val);
                    obj.put("inclinometer", mInclinometer_val);

                    FileWriter fw = new FileWriter(savefile);
                    fw.write(obj.toString());
                    fw.flush();
                    fw.close();

                    Log.d("tag", "File 생성완료");
                    Toast.makeText(this, dirPath + "에 저장 하였습니다.", Toast.LENGTH_LONG).show();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {

        }

    }

    private boolean CheckWrite() {  // sdcard mount check
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        com.google.ar.core.Camera camera = arFragment.getArSceneView().getArFrame().getCamera();
        if (camera.getTrackingState() == TrackingState.TRACKING) {
            arFragment.getPlaneDiscoveryController().hide();
        }
    }

    public void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }



    //control the object
    //Top
    ImageButton.OnClickListener controll_BtnTop = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controllTop) {

            if (controllTop == mTop) {
                initModel();
                diameterFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
                axis_Z--;

            }
        }
    };




    //Bottom
    ImageButton.OnClickListener controll_BtnRight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controllRight) {

            if (controllRight == mRight) {
                initModel();
                diameterFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
                axis_X++;

            }
        }
    };




    //Right
    ImageButton.OnClickListener controll_BtnBottom = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controllBottom) {

            if (controllBottom == mBottom) {
                initModel();
                diameterFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
                axis_Z++;
            }
        }
    };



    //Left
    ImageButton.OnClickListener controll_BtnLeft = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controllLeft) {

            if (controllLeft == mLeft) {
                initModel();
                diameterFragment.node.setRenderable(modelRenderable);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
                axis_X--;
            }
        }
    };

    ImageButton.OnClickListener addNew_anchor = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View add_anchor) {

            if (add_anchor == mAdd_anchor) {
                initModel();


            }
        }
    };

    ImageButton.OnClickListener delSelect_anchor = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View delete_anchor) {

            if (delete_anchor == mDelete_anchor) {
                initModel();


            }
        }
    };
}
