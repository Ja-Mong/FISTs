package com.example.forestinventorysurverytools;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
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
    public DiameterFragment diameterFragment;
    public HeightFragment heightFragment;


    // TextView
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


    // 데이터 관리
    public Vector<Double> height_vec = new Vector<Double>(); // 측정하는 모든 angle 값 저장
    public Vector<Float> angle_vec = new Vector<Float>(); // 측정하는 모든 angle 값 저장
    public Vector<Double> altitude_vec = new Vector<Double>(); // 측정하는 모든 altitude 값 저장
    public Vector<Float> compass_vec = new Vector<Float>(); // 측정한 모든 compass 값 저장
    public ArrayList<Info> infoArray = new ArrayList<Info>(); // 생성된 모든 Anchor 정보 저장


    //AR
    public ArFragment arFragment;
    public Anchor anchor = null;
    public AnchorNode anchorNode;
    public ModelRenderable modelRenderable;
    public ModelRenderable modelRenderable2;


    //AR controller
    public  int tree_id;
    public int radi = 100;
    public int height = 0;
    public int axis_Z = 0;
    public int axis_X = 0;
    public ImageButton mDelete_anchor;




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
        mCompass_tv = (TextView) this.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView) this.findViewById(R.id.tv_alititude);
        mInputHeight = (EditText)this.findViewById(R.id.input_height);


        //ImageButton
        mDelete_anchor = (ImageButton) this.findViewById(R.id.Btn_delete);
        mDelete_anchor.setOnClickListener(delSelect_anchor);


        //Fragment
        diameterFragment = new DiameterFragment(this);
        heightFragment = new HeightFragment(this);


        //Navigation
        FragmentManager fm = getSupportFragmentManager();
        arFragment = (ArFragment) fm.findFragmentById(R.id.camera_preview_fr);

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


    //AR model 1 = Diameter
    public void initModel() {
        MaterialFactory.makeTransparentWithColor(this,
                new Color(1.0f, 0.0f, 0.0f, 0.5f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float) axis_X/100, 1.2f,
                                    (float) axis_Z/100);
                            modelRenderable = ShapeFactory.makeCylinder
                                    ((float) radi / 1000, 0.5f,
                                            vector3, material);

                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b = (modelRenderable == null);

                        });
    }


    //AR model 2 = Height
    public void initModel2() {
        MaterialFactory.makeTransparentWithColor(this, new Color(0.0f, 0.0f, 1.0f, 0.5f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float)axis_X/100, 1.2f+(float)height/200,
                                    (float)axis_Z/100);
                            modelRenderable2 = ShapeFactory.makeCylinder
                                    ((float) radi/1000, (float) height/100,
                                            vector3, material);

                            modelRenderable2.setShadowCaster(false);
                            modelRenderable2.setShadowReceiver(false);
                            Boolean b = (modelRenderable2 == null);
                        });
    }


    public void RenderText(int r){
        //AR ViewRenderable

        TextView ar_textview = new TextView(this);
        ar_textview.setText((tree_id+1)+"번 나무\n"+(float)r/10+"cm");
        ar_textview.setBackgroundColor(android.graphics.Color.GRAY);
        ViewRenderable.builder()
                .setView(this, ar_textview) //렉이 많이 걸림..
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.getView().clearFocus();
                    if(infoArray.size()>0) {
                        Node text = infoArray.get(tree_id).text;
                        text.setRenderable(null);
                        text.setRenderable(viewRenderable);
                        text.setParent(infoArray.get(tree_id).getNode());
                        text.setLocalPosition(new Vector3(infoArray.get(tree_id).getNode().getLocalPosition().x+(float)r/1000+0.2f, 1.2f,
                                infoArray.get(tree_id).getNode().getLocalPosition().z));

                        viewRenderable.setShadowCaster(false);
                        viewRenderable.setShadowReceiver(false);
                    }
                });
    }
    //AR update
    @Override
    public void onUpdate(FrameTime frameTime) {
        com.google.ar.core.Camera camera = arFragment.getArSceneView().getArFrame().getCamera();
        if (camera.getTrackingState() == TrackingState.TRACKING) {
            arFragment.getPlaneDiscoveryController().hide();
        }
    }


    //Delete Anchor when user create new Anchor onTouch the screen
    public void clearAnchor() {
        anchor = null;
        if (anchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
            anchorNode = null;
        }
    }


    //Save Anchor to ArrayList
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
        // 초기화(리셋) 버튼 기능,
        //type 1: 측정한 모든 걸 지울 경우 05.14 (은지)
        // 고도 값을 지울지 말지 고민.. 흉고에서 처음 tap 할때 추가되는데
        // reset하고 새로 노드를 추가하니 고도는 새로 setting이 안되는 듯 함
        if(infoArray.size()!=0){
            initModel();
            initModel2();
            for(int i=0; i<infoArray.size(); i++){
                infoArray.get(i).getNode().setRenderable(null);
                infoArray.get(i).getH_Node().setRenderable(null);
            }
            infoArray.clear();
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
            altitude_vec.removeAllElements();
            compass_vec.removeAllElements();
        }else{
            Log.d("tag","infoArray 비어있음");
            Toast.makeText(this, "지울 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
        //type 2:현재 측정한 노드만 초기화
        // 시크바로 바로 수정할 수 있고 delete 버튼이 따로 있어서
        // 과연 필요할지?? 05.14 (은지)
        /*if(tree_id){

        }else{

        }*/
    }


    //Save data
    String dirPath;
    public void Save_data(View v) {
        // array 에 맞춰 수정 05.14 (은지)
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
                        obj.put("distance",infoArray.get(i).getDistance() );
                        obj.put("diameter",infoArray.get(i).getDiameter() );
                        obj.put("height", infoArray.get(i).getHeight());
                        jArray.put(obj);
                    }
                    FileWriter fw = new FileWriter(savefile);
                    fw.write(jArray.toString());
                    fw.flush();
                    fw.close();

                    Log.d("tag", "File 생성완료");
                    Toast.makeText(this, dirPath + "에 저장 하였습니다.", Toast.LENGTH_LONG).show();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("tag", "infoArray 비어있음 ");
            Toast.makeText(this, "저장할 정보가 없습니다. 값을 측정해주세요", Toast.LENGTH_LONG).show();
        }
    }


    //Delete create current Anchor.
    ImageButton.OnClickListener delSelect_anchor = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View delete_anchor) {

            if (delete_anchor == mDelete_anchor) {
                // 이거... 제가 짜놓긴 했는데...
                // 왜 정상작동하는지..... 추후 생각 해봐야할듯 싶습니다. 05.10 (선재)

                initModel();
                int idx = tree_id;
                infoArray.get(idx).text.setRenderable(null);
                infoArray.get(idx).getNode().setRenderable(null);
                infoArray.get(idx).getH_Node().setRenderable(null);
                infoArray.remove(idx);
                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
            }
        }
    };


}

//    public SeekBar radiusbar;
//    public SeekBar heightbar; //동작은 heightFragment에서 생성한 anchor
//    public ImageButton mTop;
//    public ImageButton mBottom;
//    public ImageButton mRight;
//    public ImageButton mLeft;


//경사를 측정할 수 있는 Fragment가 필요함. 기존에 만들어 놓은 Inclinometer 부활 시키기..

//        mTop = (ImageButton) this.findViewById(R.id.top);
//        mBottom = (ImageButton) this.findViewById(R.id.bottom);
//        mLeft = (ImageButton) this.findViewById(R.id.left);
//        mRight = (ImageButton) this.findViewById(R.id.right);
//        mTop.setOnTouchListener(controll_BtnTop);
//        mBottom.setOnTouchListener(controll_BtnBottom);
//        mLeft.setOnTouchListener(controll_BtnLeft);
//        mRight.setOnTouchListener(controll_BtnRight);
//        //control radius
//        radiusbar = (SeekBar) this.findViewById(R.id.radi_controller1);
//        radiusbar.setMin(30);
//        radiusbar.setMax(800);
//        radiusbar.setProgress(radi);
//
//        radiusbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                radi = progress;
//                initModel();
//                initModel2(); //heightBar 직경도 조절할 수 있도록 추가
//                infoArray.get(tree_id).getNode().setRenderable(modelRenderable);
//                infoArray.get(tree_id).getH_Node().setRenderable(modelRenderable2);
//                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//                mDiameter_tv.setText("흉 고 직 경 : " + Float.toString((float)radi/10)+"cm" );
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                tree_id = (infoArray.size()==0)? 0 : diameterFragment.id;
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                infoArray.get(tree_id).setDiameter((float)radi);
//                infoArray.get(tree_id).getNode().setRenderable(modelRenderable);
//                infoArray.get(tree_id).getH_Node().setRenderable(modelRenderable2);
//                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//            }
//        });


//        //heightBar 추가
//        heightbar = (SeekBar) this.findViewById(R.id.heigth_controller1);
//        heightbar.setMax(900);
//        heightbar.setProgress(height);
//
//        heightbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                height = progress;
//                radi = (int)infoArray.get(tree_id).getDiameter();
//                initModel2();
//                infoArray.get(tree_id).getH_Node().setRenderable(modelRenderable2);
//                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//                infoArray.get(tree_id).setHeight((float)height);
//                mHeight_tv.setText("수      고 : " + Float.toString(1.2f+(float)height/100)+"m" );
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                initModel2();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                infoArray.get(tree_id).getH_Node().setRenderable(modelRenderable2);
//                infoArray.get(tree_id).setHeight((float)height);
//                arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//            }
//        });


//arFragment정의
//    //control the object
//    //Top
//    ImageButton.OnTouchListener controll_BtnTop = new ImageButton.OnTouchListener() {
//        @Override
//        public boolean onTouch(View controllTop, MotionEvent event) {
//            if (controllTop == mTop) {
//                initModel();
//                for(int i=0; i<infoArray.size(); i++) {
//                    if(infoArray.get(i).getNode().isSelected()) {
//                        Vector3 tmpVec = infoArray.get(i).getNode().getLocalPosition();
//                        infoArray.get(i).getNode().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
//                                ((tmpVec.z*100)-1)/100));
//                        infoArray.get(i).getH_Node().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
//                                ((tmpVec.z*100)-1)/100));
//                        arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//                    }
//                }
//            }
//            return false;
//        }
//
//    };
//
//
//
//
//    //Bottom
//    ImageButton.OnTouchListener controll_BtnBottom = new ImageButton.OnTouchListener() {
//        @Override
//        public boolean onTouch(View controllBottom, MotionEvent event) {
//            if (controllBottom == mBottom) {
//                initModel();
//                for(int i=0; i<infoArray.size(); i++) {
//                    if(infoArray.get(i).getNode().isSelected()) {
//                        Vector3 tmpVec = infoArray.get(i).getNode().getLocalPosition();
//                        infoArray.get(i).getNode().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
//                                ((tmpVec.z*100)+1)/100));
//                        infoArray.get(i).getH_Node().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
//                                ((tmpVec.z*100)+1)/100));
//                        arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//                    }
//                }
//
//            }
//            return true;
//        }
//    };
//
//
//
//
//    //Right
//    ImageButton.OnTouchListener controll_BtnRight = new ImageButton.OnTouchListener() {
//        @Override
//        public boolean onTouch(View controllRight, MotionEvent event) {
//            if (controllRight == mRight) {
//                initModel();
//                for(int i=0; i<infoArray.size(); i++) {
//                    if(infoArray.get(i).getNode().isSelected()) {
//                        Vector3 tmpVec = infoArray.get(i).getNode().getLocalPosition();
//                        infoArray.get(i).getNode().setLocalPosition(new Vector3(((tmpVec.x*100)+1)/100,
//                                tmpVec.y, tmpVec.z));
//                        infoArray.get(i).getH_Node().setLocalPosition(new Vector3(((tmpVec.x*100)+1)/100,
//                                tmpVec.y, tmpVec.z));
//                        arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//
//                    }
//                }
//            }
//            return false;
//        }
//    };
//
//
//
//    //Left
//    ImageButton.OnTouchListener controll_BtnLeft = new ImageButton.OnTouchListener() {
//        @Override
//        public boolean onTouch(View controllLeft, MotionEvent event) {
//            if (controllLeft == mLeft) {
//                initModel();
//                for(int i=0; i<infoArray.size(); i++) {
//                    if(infoArray.get(i).getNode().isSelected()) {
//                        Vector3 tmpVec = infoArray.get(i).getNode().getLocalPosition();
//                        infoArray.get(i).getNode().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
//                                tmpVec.y, tmpVec.z));
//                        infoArray.get(i).getH_Node().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
//                                tmpVec.y, tmpVec.z));
//                        arFragment.getArSceneView().getScene().addOnUpdateListener(arFragment);
//                    }
//                }
//
//            }
//            return false;
//        }
//    };
