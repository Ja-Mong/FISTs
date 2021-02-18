package com.example.forestinventorysurverytools.ui.diameter;

import android.Manifest;
import android.animation.ObjectAnimator;
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
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

import com.example.forestinventorysurverytools.GpsTracker;

public class DiameterFragment extends Fragment implements Scene.OnUpdateListener, LocationListener{


    //View
    View root;


    //Sensor
    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;


    //Data
    ArrayList<Info> mListInfo;
    public int mID;
    float mDiameterValue;
    String mDiameter;
    int mAltitude;
    int mAzimuth;
    float mLongitude;
    float mLatitude;


    //Activity
    MainActivity ma = null;
    public DiameterFragment(MainActivity ma) {this.ma = ma; mListInfo = ma.infoArray;}


    //SeekBar
    public SeekBar mRadiusbar;
    public SeekBar mLr_rot;
    public SeekBar mFb_rot;


    //ImageButton
    public ImageButton mTop;
    public ImageButton mBottom;
    public ImageButton mRight;
    public ImageButton mLeft;


    //TextView
    public TextView mRadius_controller;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, null);
        mID = 0;


        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(ma, mSensorManager);


        //ImageButton
        mTop = (ImageButton)root.findViewById(R.id.top);
        mBottom = (ImageButton)root.findViewById(R.id.bottom);
        mRight = (ImageButton)root.findViewById(R.id.right);
        mLeft = (ImageButton)root.findViewById(R.id.left);

        mTop.setOnClickListener(control_BtnTop);
        mBottom.setOnClickListener(control_BtnBottom);
        mRight.setOnClickListener(control_BtnRight);
        mLeft.setOnClickListener(control_BtnLeft);


        //SeekBar
        mRadius_controller = (TextView) root.findViewById(R.id.radi_controller);
        mRadiusbar = (SeekBar) root.findViewById(R.id.radi_controller1);
        mRadiusbar.setMin(30);
        mRadiusbar.setMax(800);
        mRadiusbar.setProgress(ma.mRadi);
        mRadiusbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Frame frame = ma.mArfragment.getArSceneView().getArFrame();
                Pose objectPose = ma.mAnchor.getPose();
                Pose cameraPose = frame.getCamera().getPose();

                //Get the Anchor Pose
                ma.mDx = objectPose.tx() - cameraPose.tx();
                ma.mDy = objectPose.ty() - cameraPose.ty();
                ma.mDz = objectPose.tz() - cameraPose.tz();
                ma.mDistMeter = (float) Math.sqrt((ma.mDx * ma.mDx) + (ma.mDy * ma.mDy) + (ma.mDz * ma.mDz));


                ma.mRadi = progress;
                ma.setDBH_model();
                ma.infoArray.get(ma.mTreeIndex).getDBHNode().setRenderable(ma.mDBHModelRender);
                ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                mDiameterValue = (((ma.mRadi * 2) /10) * ((ma.mDistMeter * 100) + (((ma.mRadi * 2) / 10) + 2)))
                        / (ma.mDistMeter * 100);
                ma.mDiameter_val = mDiameterValue;
                mDiameter = String.format("%.1f", mDiameterValue);
                ma.mDiameter_tv.setText("흉 고 직 경 : " + mDiameter + "cm" );
                ma.infoArray.get(ma.mTreeIndex).setDBH(mDiameterValue);
//                ma.RenderText((int)ma.mRadi);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ma.mTreeIndex = (ma.infoArray.size() == 0)? 0 : mID;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ma.infoArray.get(ma.mTreeIndex).setDBH((((ma.mRadi * 2) /10) * ((ma.mDistMeter * 100) + ((ma.mRadi / 10) + 2)))
                        / (ma.mDistMeter * 100));
                ma.infoArray.get(ma.mTreeIndex).getDBHNode().setRenderable(ma.mDBHModelRender);

                //AR TextView
//                ma.RenderText(seekBar.getProgress());
                ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
            }
        });

        mLr_rot = (SeekBar)root.findViewById(R.id.LR_Rotation);
        mLr_rot.setMin(-90); mLr_rot.setMax(90); mLr_rot.setProgress(0);
        mLr_rot.setOnSeekBarChangeListener(LRROT);


        mFb_rot = (SeekBar)root.findViewById(R.id.FB_Rotation);
        mFb_rot.setMin(-90); mFb_rot.setMax(90); mFb_rot.setProgress(0);
        mFb_rot.setOnSeekBarChangeListener(FBROT);

        //AR
        ma.setBot_model();
//        ma.setMov_model();
        ma.setDBH_model();
        ma.setUH_model();
        ma.mArfragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            mMySensorEventListener.updateOrientationAngles();
            if (ma.mDBHModelRender == null)
                return;

            // Creating Anchor.
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(ma.mArfragment.getArSceneView().getScene());

            ma.mRadi = 100;
            ma.mHeight = 0;

            ma.setBot_model();
//            ma.setMov_model();
            ma.setDBH_model();
            ma.setUH_model();

            // Create the transformable object and add it to the anchor.
            ma.mAnchor = anchor;
            ma.mAnchorNode = anchorNode;
            String pattern = "yyMMdd";
            SimpleDateFormat dateformat = new SimpleDateFormat(pattern);
            String id1 = dateformat.format(System.currentTimeMillis());
            String pattern2 = "HHmmss";
            SimpleDateFormat dateformat2 = new SimpleDateFormat(pattern2);
            String id2 = dateformat2.format(System.currentTimeMillis());
            String idstr = id1+"_"+ ma.Place +"_"+ id2;
            Info tmp = new Info(new TransformableNode(ma.mArfragment.getTransformationSystem()),
                    new TransformableNode(ma.mArfragment.getTransformationSystem()),
                    new TransformableNode(ma.mArfragment.getTransformationSystem()),
                    new TransformableNode(ma.mArfragment.getTransformationSystem()), idstr);

            tmp.setDBH(100);
            tmp.setHeight(0);
            tmp.getBotNode().setRenderable(ma.mBotModelRender);
//            tmp.getMovNode().setRenderable(ma.mMovModelRender);
            tmp.getDBHNode().setRenderable(ma.mDBHModelRender);
            tmp.getUHNode().setRenderable(ma.mUHModelRender);

            tmp.getBotNode().setParent(anchorNode);
//            tmp.getMovNode().setParent(anchorNode);
            tmp.getDBHNode().setParent(tmp.getBotNode());
            tmp.getUHNode().setParent(tmp.getBotNode());

            tmp.getBotNode().setOnTouchListener(touchNode);
            tmp.getDBHNode().setOnTouchListener(touchNode);

            ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
            ma.mArfragment.getArSceneView().getScene().addChild(anchorNode);

            //Get the Anchor distance to User and other value(Altitude, Compass. Diameter)
            if (ma.mAnchorNode != null) {

                //GPS트래커 설정
                GpsTracker gpsTracker = new GpsTracker(ma, ma);


                // 위치권한 확인
                if (!gpsTracker.checkLocationServicesStatus()) {
                    gpsTracker.showDialogForLocationServiceSetting();
                }else {
                    gpsTracker.checkRunTimePermission();
                }

                // 측정위치 GPS값
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                Frame frame = ma.mArfragment.getArSceneView().getArFrame();
                Vector3 ov = tmp.getBotNode().getWorldPosition();
                Pose cameraPose = frame.getCamera().getPose();
                ma.mDx = ov.x - cameraPose.tx();
                ma.mDy = ov.y - cameraPose.ty();
                ma.mDz = ov.z - cameraPose.tz();
                ma.mDistMeter = (float) Math.sqrt((ma.mDx * ma.mDx) + (ma.mDy * ma.mDy) + (ma.mDz * ma.mDz));
                String distance = String.format("%.1f", ma.mDistMeter);

                //Show distance and Altitude
                ma.mDistance_tv.setText("거        리 : " + distance + "m");
                ma.mAltitude_tv.setText("고        도 :" + Integer.toString(mAltitude) + "m"); //객체 생성 시 방위, 고도 값 세팅이 안됨..

                //Get and Show Azimuth
                mAzimuth = (int) Math.abs(mMySensorEventListener.getYaw());
                mAzimuth = (int) Math.toDegrees(mAzimuth);
                ma.mAzimuth_val = mAzimuth;
                ma.mAzimuth_tv.setText("방        위 : " + Integer.toString(mAzimuth) + "°" + mMySensorEventListener.matchDirection(mAzimuth));

                //Save the distance, altitude, azimuth 오류발생
                tmp.setAlti(mAltitude);
                tmp.setAzi(mAzimuth);
                tmp.setDist(ma.mDistMeter);
                tmp.setDBH(mDiameterValue);
                tmp.setLocate(latitude,longitude);

            }


            mListInfo.add(tmp); //이거 필요한 부분??? Radiusbar 말고는 없어도 되지 않나욤?
            mID = mListInfo.size() - 1;
            mListInfo.get(mID).getDBHNode().select();
            ma.mTreeIndex = mID;
            mRadiusbar.setProgress(100,true);
            ma.showToast(Integer.toString(mID + 1) + "번째 노드 생성");
        });
        return root;
    }




    //AR
    @Override
    public void onUpdate(FrameTime frameTime) {
    }

    TransformableNode.OnTouchListener touchNode = new TransformableNode.OnTouchListener(){
        @Override
        public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {

            if(hitTestResult.getNode()!=null) {
                mID = (mListInfo.size() == 0) ? 0 : mListInfo.size() - 1;
                for (int i = 0; i < mListInfo.size(); i++) {
                    if (hitTestResult.getNode().equals(mListInfo.get(i).getBotNode()) ||
                            hitTestResult.getNode().equals(mListInfo.get(i).getDBHNode())) {
                        mID = i;
                        break;
                    }
                }
                ma.showToast(Integer.toString(mID + 1) + "번째 요소 선택(" + mListInfo.get(mID).getId() + ")");

                ma.mTreeIndex = mID;
                ma.mInclinometer_tv.setText("경        사 : " + Float.toString(mListInfo.get(mID).getClino()) + "%");
                String distance = String.format("%.1f", mListInfo.get(mID).getDist());
                ma.mDistance_tv.setText("거        리 : " + distance + "m");
                ma.mDiameter_tv.setText("흉 고 직 경 : " + Float.toString((mListInfo.get(mID).getDBH())) + "cm");
                ma.mHeight_tv.setText("수      고 : " + Float.toString(mListInfo.get(mID).getHeight()) + "m" );
                ma.mAzimuth_tv.setText("방        위 : " + Integer.toString((int) mListInfo.get(mID).getAzi()) + "°" + mMySensorEventListener.matchDirection(mAzimuth) );
                ma.mAltitude_tv.setText("고        도 :" + Integer.toString((int) mListInfo.get(mID).getAlti()) + "m");
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

//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
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
    ImageButton.OnClickListener control_BtnTop = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controlTop) {
            if (controlTop == mTop) {
                ma.setBot_model();
//                ma.setMov_model();
                if (ma.infoArray.get(mID).getBotNode().isSelected()) {

                    Vector3 tmpVec3 = ma.infoArray.get(mID).getBotNode().getWorldPosition();
                    ma.infoArray.get(mID).getBotNode().setWorldPosition(new Vector3(tmpVec3.x, tmpVec3.y,
                            ((tmpVec3.z * 100) - 1)
                                    / 100));
                    ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                }
            }
        }
    };

    //Bottom
    ImageButton.OnClickListener control_BtnBottom = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controlBottom) {
            if (controlBottom == mBottom) {
                ma.setBot_model();
                if (ma.infoArray.get(mID).getBotNode().isSelected()) {

                    Vector3 tmpVec3 = ma.infoArray.get(mID).getBotNode().getWorldPosition();
                    ma.infoArray.get(mID).getBotNode().setWorldPosition(new Vector3(tmpVec3.x, tmpVec3.y,
                            ((tmpVec3.z * 100) + 1)
                                    / 100));
                    ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                }
            }
        }
    };

    //Right
    ImageButton.OnClickListener control_BtnRight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controlRight) {
            if (controlRight == mRight) {
                ma.setBot_model();
                if (ma.infoArray.get(mID).getBotNode().isSelected()) {

                    Vector3 tmpVec3 = ma.infoArray.get(mID).getBotNode().getWorldPosition();
                    ma.infoArray.get(mID).getBotNode().setWorldPosition(new Vector3(((tmpVec3.x * 100) + 1) / 100,
                            tmpVec3.y, tmpVec3.z));
                    ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                }
            }
        }
    };

    //Left
    ImageButton.OnClickListener control_BtnLeft = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View controlLeft) {
            if (controlLeft == mLeft) {
                ma.setBot_model();
                if (ma.infoArray.get(mID).getBotNode().isSelected()) {


                    Vector3 tmpVec3 = ma.infoArray.get(mID).getBotNode().getWorldPosition();
                    ma.infoArray.get(mID).getBotNode().setWorldPosition(new Vector3(((tmpVec3.x * 100) - 1) / 100,
                            tmpVec3.y, tmpVec3.z));
                    ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                }
            }
        }
    };


    /******************  회전부   *********************/

    SeekBar.OnSeekBarChangeListener LRROT = new SeekBar.OnSeekBarChangeListener() {
        int cur_rot;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int degree = (progress-cur_rot >0)? 1:-1;
            ma.setDBH_model();
            if(ma.infoArray.get(mID).getBotNode().isSelected() || ma.infoArray.get(mID).getDBHNode().isSelected()) {
                Quaternion rotation1 = ma.infoArray.get(mID).getDBHNode().getLocalRotation();
                Quaternion rotation2 = Quaternion.axisAngle(new Vector3(0.0f, 0f, 1.0f), degree);

                ma.infoArray.get(mID).getDBHNode().setLocalRotation(Quaternion.multiply(rotation1, rotation2));
                ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                ma.infoArray.get(mID).getDBHNode().select();
            }
            cur_rot = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            cur_rot = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(0);
        }
    };

    SeekBar.OnSeekBarChangeListener FBROT = new SeekBar.OnSeekBarChangeListener() {
        int cur_rot;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int degree = (progress-cur_rot >0)? 1:-1;
            ma.setDBH_model();
            if(ma.infoArray.get(mID).getBotNode().isSelected() || ma.infoArray.get(mID).getDBHNode().isSelected()) {
                Quaternion rotation1 = ma.infoArray.get(mID).getDBHNode().getLocalRotation();
                Quaternion rotation2 = Quaternion.axisAngle(new Vector3(1.0f, 0f, 0.0f),degree);

                ma.infoArray.get(mID).getDBHNode().setLocalRotation(Quaternion.multiply(rotation1, rotation2));
                ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                ma.infoArray.get(mID).getDBHNode().select();
            }
            cur_rot=progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            cur_rot = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(0);
        }
    };


    @Override
    public void onLocationChanged(Location location) {

        mAltitude = (int) location.getAltitude();
        ma.mAltitude_val = mAltitude;
        mLongitude = (int) location.getLongitude();
        mLatitude = (int) location.getLatitude(); //위경도 세팅해야됨.

        ma.mAltitude_tv.setText("고        도 : " + Integer.toString(mAltitude) + "m");
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

