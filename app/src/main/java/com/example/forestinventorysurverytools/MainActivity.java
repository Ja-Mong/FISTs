package com.example.forestinventorysurverytools;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.forestinventorysurverytools.ui.diameter.DiameterFragment;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.example.forestinventorysurverytools.ui.height.HeightFragment;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerFragment;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerFragment;
import com.example.forestinventorysurverytools.ui.userheight.UserheightFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {


    // Fragment
    public UserheightFragment mUserheightFragment;
    public DiameterFragment mDiameterFragment;
    public HeightFragment mHeightFragment;


    // TextView
    public TextView mInclinometer_tv;
    public TextView mDistance_tv;
    public TextView mDiameter_tv;
    public TextView mHeight_tv;
    public TextView mAzimuth_tv;
    public TextView mAltitude_tv;
    public EditText mInputUH;


    //Storage the Distance, DBH, Height value variable
    public float mInclinometer_val;
    public float mDistance_val;
    public float mDiameter_val;
    public float mHeight_val;
    public float mAzimuth_val;
    public float mAltitude_val;
    public float mLongitude;
    public float mLatitude;


    // 데이터 관리
    public ArrayList<Info> infoArray = new ArrayList<Info>(); //Save the data


    //AR
    public ArFragment mArfragment;
    public Anchor mAnchor = null;
    public AnchorNode mAnchorNode;
    public ModelRenderable mBotModelRender;
    public ModelRenderable mMovModelRender;
    public ModelRenderable mDBHModelRender;
    public ModelRenderable mUHModelRender;


    //AR controller
    public int mTreeIndex = -1;
    public int mRadi = 100;
    public int mHeight = 0;
    public int mAxis_Z = 0;
    public int mAxis_X = 0;
    public ImageButton mDelete_Anchor;


    //Values
    public float mMain_UserHeight = 1.2f;
    public float mDistMeter;
    public float mDx;
    public float mDy;
    public float mDz;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TextView
        mInclinometer_tv = (TextView) this.findViewById(R.id.tv_inclinometer);
        mDistance_tv = (TextView) this.findViewById(R.id.tv_distance);
        mDiameter_tv = (TextView) this.findViewById(R.id.tv_diameter);
        mHeight_tv = (TextView) this.findViewById(R.id.tv_height);
        mAzimuth_tv = (TextView) this.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView) this.findViewById(R.id.tv_alititude);
        mInputUH = (EditText)this.findViewById(R.id.input_height);


        //ImageButton
        mDelete_Anchor = (ImageButton) this.findViewById(R.id.Btn_delete);
        mDelete_Anchor.setOnClickListener(delSelect_anchor);


        //Fragment
        mDiameterFragment = new DiameterFragment(this);
        mHeightFragment = new HeightFragment(this);
        mUserheightFragment = new UserheightFragment(this);

        //Navigation
        FragmentManager fm = getSupportFragmentManager();
        mArfragment = (ArFragment) fm.findFragmentById(R.id.camera_preview_fr);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, mUserheightFragment).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_userheight:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, mUserheightFragment).commit();
                        return true;
                    case R.id.navigation_diameter:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, mDiameterFragment).commit();
                        return true;
                    case R.id.navigation_height:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, mHeightFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    //Bottom model
    public void setBot_model() {
        MaterialFactory.makeTransparentWithColor(this, new Color(1.0f, 1.27f, 0.0f, 1.0f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float)mAxis_X/100, 0f, (float)mAxis_Z/100);
                            mBotModelRender = ShapeFactory.makeSphere(0.03f, vector3, material);

                            mBotModelRender.setShadowCaster(false);
                            mBotModelRender.setShadowReceiver(false);
                            Boolean b = (mBotModelRender == null);
                        });
    }

    //Mover model
    public void setMov_model() {
        MaterialFactory.makeTransparentWithColor(this, new Color(0f,0f,0f,0f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float)mAxis_X/100, 0.6f, (float)mAxis_Z/100);
                            mMovModelRender = ShapeFactory.makeCylinder(0.1f, 1.2f, vector3, material);

                            mMovModelRender.setShadowCaster(false);
                            mMovModelRender.setShadowReceiver(false);
                            Boolean b = (mMovModelRender == null);
                        });
    }

    //Diameter model
    public void setDBH_model() {
        MaterialFactory.makeTransparentWithColor(this, new Color(1.0f, 0.0f, 0.0f, 1.0f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float) mAxis_X/100, 0f, (float) mAxis_Z/100);
                            mDBHModelRender = ShapeFactory.makeCylinder
                                    ((float) mRadi / 1000, 0.05f,
                                            vector3, material);

                            mDBHModelRender.setShadowCaster(false);
                            mDBHModelRender.setShadowReceiver(false);
                            Boolean b = (mDBHModelRender == null);

                        });
    }


    //UserHeight model
    public void setUH_model() {
        MaterialFactory.makeTransparentWithColor(this, new Color(0.0f, 0.0f, 1.0f, 1.0f))
                .thenAccept(
                        material -> {
                            if (!mInputUH.getText().toString().isEmpty()) {
                                mMain_UserHeight = Float.valueOf(mInputUH.getText().toString()) / 100f;
                                Vector3 vector3 = new Vector3((float) mAxis_X / 100, mMain_UserHeight,
                                        (float) mAxis_Z / 100);
                                mUHModelRender = ShapeFactory.makeSphere(0.05f, vector3, material);

                                mUHModelRender.setShadowCaster(false);
                                mUHModelRender.setShadowReceiver(false);
                                Boolean b = (mUHModelRender == null);
                            }
                        });
    }

    //Text View renderable
    public void RenderText(int r){
        //AR ViewRenderable

        TextView ar_textview = new TextView(this);
        ar_textview.setText((mTreeIndex+1)+"번 나무\n" +
                "직경 : "+ String.format("%.1f", (((r*2)/10) * ((mDistMeter*100)+(((r*2)/10)+2)))/(mDistMeter * 100)) + "cm\n" +
                "거리 : " + String.format("%.1f",infoArray.get(mTreeIndex).getDist())+"m");
        ar_textview.setBackgroundColor(android.graphics.Color.GRAY);
        ViewRenderable.builder()
                .setView(this, ar_textview)
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.getView().clearFocus();
                    if(infoArray.size()>0) {
                        Node text = infoArray.get(mTreeIndex).mText;
                        text.setRenderable(null);
                        text.setRenderable(viewRenderable);
                        text.setParent(infoArray.get(mTreeIndex).getUHNode());
                        text.setLocalPosition(new Vector3(infoArray.get(mTreeIndex).getUHNode().getLocalPosition().x
                                + (float)r/1000+0.2f,
                                mMain_UserHeight,
                                infoArray.get(mTreeIndex).getUHNode().getLocalPosition().z));

                        viewRenderable.setShadowCaster(false);
                        viewRenderable.setShadowReceiver(false);
                    }
                });
    }
    //AR update
    @Override
    public void onUpdate(FrameTime frameTime) {
        com.google.ar.core.Camera camera = mArfragment.getArSceneView().getArFrame().getCamera();
        if (camera.getTrackingState() == TrackingState.TRACKING) {
            mArfragment.getPlaneDiscoveryController().hide();
        }
    }


    //Delete Anchor when user create new Anchor onTouch the screen
    public void clearAnchor() {
        mAnchor = null;
        if (mAnchorNode != null) {
            mArfragment.getArSceneView().getScene().removeChild(mAnchorNode);
            mAnchorNode.getAnchor().detach();
            mAnchorNode.setParent(null);
            mAnchorNode = null;
        }
    }


    //Save Anchor to ArrayList
    Anchor tmpA;
    AnchorNode tmpAN;
    TransformableNode node;
    public void saveAnchor() {
        tmpA = mAnchor;
        tmpAN = mAnchorNode;

    }
    public void retrieveAnchor() {
        mAnchor = tmpA;
        mAnchorNode = tmpAN;
        node = new TransformableNode(mArfragment.getTransformationSystem());
        node.setRenderable(mDBHModelRender);
        node.setParent(mAnchorNode);
        mArfragment.getArSceneView().getScene().addOnUpdateListener(mArfragment);
        mArfragment.getArSceneView().getScene().addChild(mAnchorNode);
        node.select();
    }


    //Check the sdcard mount
    private boolean CheckWrite() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }


    //ImageButton
    //Refresh
    public void tv_Reset(View v) {

        if(infoArray.size()!=0){
            setBot_model();
            setMov_model();
            setDBH_model();
            setUH_model();
            for(int i=0; i<infoArray.size(); i++){
                infoArray.get(i).getBotNode().setRenderable(null);
                infoArray.get(i).getMovNode().setRenderable(null);
                infoArray.get(i).getDBHNode().setRenderable(null);
                infoArray.get(i).getUHNode().setRenderable(null);
            }
            infoArray.clear();
            mInclinometer_tv.setText("경        사 :");
            mDistance_tv.setText("거        리 :");
            mDiameter_tv.setText("흉 고 직 경 :");
            mHeight_tv.setText("수        고 :");
            mAzimuth_tv.setText("방        위 :");
            mAltitude_tv.setText("고        도 :");
            mInclinometer_val = 0.0f;
            mDistance_val = 0.0f;
            mDiameter_val = 0.0f;
            mHeight_val = 0.0f;
        }else{
            Log.d("tag","infoArray 비어있음");
            showToast( "지울 정보가 없습니다.");
        }
    }


    //Save data
    String dirPath;
    public void Save_data(View v) {

        if (infoArray.size() != 0) {
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
                    JSONArray jArray = new JSONArray();
                    for(int i=0; i<infoArray.size(); i++){
                        JSONObject obj = new JSONObject();

                        obj.put("id",infoArray.get(i).getId());
                        obj.put("clino", infoArray.get(i).getClino());
                        obj.put("distance",infoArray.get(i).getDist() );
                        obj.put("diameter",infoArray.get(i).getDBH() );
                        obj.put("height", infoArray.get(i).getHeight());
                        obj.put("azimuth", infoArray.get(i).getAzi());
                        obj.put("altitude", infoArray.get(i).getAlti());
                        // GPS 좌표도 넣기
                        jArray.put(obj);
                    }
                    FileWriter fw = new FileWriter(savefile);
                    fw.write(jArray.toString());
                    fw.flush();
                    fw.close();

                    Log.d("tag", "File 생성완료");
                    showToast(dirPath + "에 저장 하였습니다.");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("tag", "infoArray 비어있음 ");
            showToast("저장할 정보가 없습니다. 값을 측정해주세요");
        }
    }


    //Delete create current Anchor.
    ImageButton.OnClickListener delSelect_anchor = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View delete_anchor) {

            if (delete_anchor == mDelete_Anchor) {

                setBot_model();
                setMov_model();
                setDBH_model();
                setUH_model();
                int idx = mTreeIndex;
                infoArray.get(idx).mText.setRenderable(null);
                infoArray.get(idx).getBotNode().setRenderable(null);
                infoArray.get(idx).getMovNode().setRenderable(null);
                infoArray.get(idx).getDBHNode().setRenderable(null);
                infoArray.get(idx).getUHNode().setRenderable(null);
                infoArray.remove(idx);
                mArfragment.getArSceneView().getScene().addOnUpdateListener(mArfragment);
            }
        }
    };

    // Toast
    public void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }
}