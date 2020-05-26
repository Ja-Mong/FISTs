package com.example.forestinventorysurverytools.ui.diameter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

//import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.FirstScreen;
import com.example.forestinventorysurverytools.Info;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class DiameterFragment extends Fragment implements Scene.OnUpdateListener, LocationListener{


    //View
    View root;


    //Sensor
    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;


    //Data
    ArrayList<Info> ai;
    double longitude;
    double latitude;
    double altitude;
    float compass;
    public int id;


    //Activity
    MainActivity ma = null;
    public DiameterFragment(MainActivity ma) {this.ma = ma; ai=ma.infoArray;}


    //SeekBar
    public SeekBar radiusbar;


    //ImageButton
    public ImageButton mTop;
    public ImageButton mBottom;
    public ImageButton mRight;
    public ImageButton mLeft;
    public ImageButton mRightDown;
    public ImageButton mLeftDown;


    //TextView
    public TextView radius_controller;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, null);
        id = 0;



        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(ma, mSensorManager);


        //ImageButton
        mTop = (ImageButton)root.findViewById(R.id.top);
        mBottom = (ImageButton)root.findViewById(R.id.bottom);
        mRight = (ImageButton)root.findViewById(R.id.right);
        mLeft = (ImageButton)root.findViewById(R.id.left);
        mRightDown = (ImageButton)root.findViewById(R.id.rightDown);
        mLeftDown = (ImageButton)root.findViewById(R.id.leftDown);

        mTop.setOnTouchListener(controll_BtnTop);
        mBottom.setOnTouchListener(controll_BtnBottom);
        mRight.setOnTouchListener(controll_BtnRight);
        mLeft.setOnTouchListener(controll_BtnLeft);
        mRightDown.setOnTouchListener(controll_BtnRigntDown);
        mLeftDown.setOnTouchListener(controll_BtnLeftDown);



        //SeekBar
        radius_controller = (TextView) root.findViewById(R.id.radi_controller);
        radiusbar = (SeekBar) root.findViewById(R.id.radi_controller1);
        radiusbar.setMin(30);
        radiusbar.setMax(800);
        radiusbar.setProgress(ma.radi);
        radiusbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ma.radi = progress;
                ma.initModel();
                ma.infoArray.get(ma.tree_id).getNode().setRenderable(ma.modelRenderable);
                ma.infoArray.get(ma.tree_id).getH_Node().setRenderable(ma.modelRenderable2);
                ma.infoArray.get(ma.tree_id).getT_Node().setRenderable(ma.modelRenderable3);
                ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                ma.mDiameter_tv.setText("흉 고 직 경 : " + Float.toString((float)ma.radi/10)+"cm" );




            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ma.tree_id = (ma.infoArray.size() == 0)? 0 : id;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ma.infoArray.get(ma.tree_id).setDiameter((float)ma.radi);
                ma.infoArray.get(ma.tree_id).getNode().setRenderable(ma.modelRenderable);
                ma.infoArray.get(ma.tree_id).getH_Node().setRenderable(ma.modelRenderable2);
                ma.infoArray.get(ma.tree_id).getT_Node().setRenderable(ma.modelRenderable3);

                //AR TextView
                ma.RenderText(seekBar.getProgress());


                ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            }
        });


        //AR
        ma.initModel();
        ma.initModel2();
        ma.initModel3();
        ma.arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            mMySensorEventListener.updateOrientationAngles();
            if (ma.modelRenderable == null)
                return;

            // Creating Anchor.
            Anchor anchor2 = hitResult.createAnchor();
            AnchorNode anchorNode2 = new AnchorNode(anchor2);
            anchorNode2.setParent(ma.arFragment.getArSceneView().getScene());
            ma.radi = 100;
            ma.height=0;
            ma.initModel();
            ma.initModel2();
            ma.initModel3();

            // Create the transformable object and add it to the anchor.
            ma.anchor = anchor2;
            ma.anchorNode = anchorNode2;
            SimpleDateFormat dateformat = new SimpleDateFormat("dd_HHmmss");
            String idstr = dateformat.format(System.currentTimeMillis());
            Info tmp = new Info(new TransformableNode(ma.arFragment.getTransformationSystem()),
                    new TransformableNode(ma.arFragment.getTransformationSystem()),
                    new TransformableNode(ma.arFragment.getTransformationSystem()), idstr);
            tmp.setDiameter(100);
            tmp.setHeight(0);
            tmp.getNode().setRenderable(ma.modelRenderable);
            tmp.getH_Node().setRenderable(ma.modelRenderable2);
            tmp.getT_Node().setRenderable(ma.modelRenderable3);
            tmp.getNode().setParent(anchorNode2);
            tmp.getH_Node().setParent(anchorNode2);
            tmp.getT_Node().setParent(anchorNode2);
            tmp.getNode().setOnTouchListener(touchNode);
//            tmp.getH_Node().setOnTouchListener(touchNode);
//            tmp.getT_Node().setOnTouchListener(touchNode);

            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);


            //Get the Anchor distance to User and other value(Altitude, Compass. Diameter)
            if (ma.anchorNode != null) {
                Frame frame = ma.arFragment.getArSceneView().getArFrame();
                Pose objectPose = ma.anchor.getPose();
                Pose cameraPose = frame.getCamera().getPose();

                //Get the Anchor Pose
                ma.dx = objectPose.tx() - cameraPose.tx();
                ma.dy = objectPose.ty() - cameraPose.ty();
                ma.dz = objectPose.tz() - cameraPose.tz();

                //Get the altitude
                if (ma.altitude_vec.isEmpty()) {
                    ma.altitude_vec.add(altitude);
                    ma.mAltitude_tv.setText("고        도 :" +
                            Integer.toString((int) altitude) + "m");
                }

                //Get the compass
                if (ma.compass_vec.isEmpty()) {
                    compass = Math.abs(mMySensorEventListener.getYaw());
                    compass = (float) Math.toDegrees(compass);
                    ma.mCompass_tv.setText("방        위 : " + Integer.toString((int) compass) + "°"
                            + mMySensorEventListener.matchDirection(compass));
                }
            }


            //Get the Diameter
            ma.mDiameter_tv.setText("흉 고 직 경 : " +
                    Float.toString((float) ma.radi / 10) + "cm");

            ai.add(tmp);
            id = ai.size() - 1;
            ai.get(id).getNode().select();
            ma.tree_id = id;
            radiusbar.setProgress(100,true);
        });
        return root;
    }


    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }

    //AR
    @Override
    public void onUpdate(FrameTime frameTime) { }

    TransformableNode.OnTouchListener touchNode = new TransformableNode.OnTouchListener(){
        @Override
        public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
            if(hitTestResult.getNode()!=null) {
                id = (ai.size() == 0) ? 0 : ai.size() - 1;
                for (int i = 0; i < ai.size(); i++) {
                    if (hitTestResult.getNode().equals(ai.get(i).getNode())) {
                        id = i;
                        break;
                    }
                }
                showToast(Integer.toString(id + 1) + "번째 요소 선택("+ai.get(id).getId()+")");
                ma.tree_id=id;
                ai.get(id).getNode().select();

                String meter = String.format("%.2f", ai.get(id).getDistance());
                ma.mDistance_tv.setText("거        리 : " + meter + "m");
                ma.mDiameter_tv.setText("흉 고 직 경 : " + Float.toString(ai.get(id).getDiameter() / 10) + "cm");
                ma.mHeight_tv.setText("수      고 : " + Float.toString(1.2f+ai.get(id).getHeight()/100)+"m" ); //수정필요

            }
            return false;
        }
    };


    //Sensor
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }
    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventListener);
        mLocationManager.removeUpdates(this);
    }


    //Image Button
    //control the object
    //Top
    ImageButton.OnTouchListener controll_BtnTop = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllTop, MotionEvent event) {
            if (controllTop == mTop) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
                                ((tmpVec.z * 100)/-1)/100));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
                                ((tmpVec.z * 100)/-1)/100));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    //Bottom
    ImageButton.OnTouchListener controll_BtnBottom = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllBottom, MotionEvent event) {
            if (controllBottom == mBottom) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
                                ((tmpVec.z * 100)/+1)/100));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(tmpVec.x, tmpVec.y,
                                ((tmpVec.z * 100)/+1)/100));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    //Right
    ImageButton.OnTouchListener controll_BtnRight = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllRight, MotionEvent event) {
            if (controllRight == mRight) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(((tmpVec.x*100)+1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(((tmpVec.x*100)+1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    //Left
    ImageButton.OnTouchListener controll_BtnLeft = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllLeft, MotionEvent event) {
            if (controllLeft == mLeft) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    //RightDown
    ImageButton.OnTouchListener controll_BtnRigntDown = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllRightDown, MotionEvent event) {
            if (controllRightDown == mRightDown) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(tmpVec.x,
                                ((tmpVec.y*100)-1)/100, tmpVec.z));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(tmpVec.x,
                                ((tmpVec.y*100)-1)/100, tmpVec.z));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    //LeftDown
    ImageButton.OnTouchListener controll_BtnLeftDown = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View controllLeftDown, MotionEvent event) {
            if (controllLeftDown == mLeftDown) {
                ma.initModel();
                for (int i=0; i<ma.infoArray.size(); i++) {
                    if (ma.infoArray.get(i).getNode().isSelected()) {
                        Vector3 tmpVec = ma.infoArray.get(i).getNode().getLocalPosition();
                        ma.infoArray.get(i).getNode().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.infoArray.get(i).getH_Node().setLocalPosition(new Vector3(((tmpVec.x*100)-1)/100,
                                tmpVec.y, tmpVec.z));
                        ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                    }
                }
            }
            return false;
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        double altitude = location.getAltitude();

        ma.mAltitude_tv.setText("고        도 :"+Integer.toString((int)altitude)+"m");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

