package com.example.forestinventorysurverytools.ui.diameter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

//import com.example.forestinventorysurverytools.CameraAPI;
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
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class DiameterFragment extends Fragment implements LocationListener, Scene.OnUpdateListener{

    View root;


    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;

    ArrayList<Info> ai;


    double longitude;
    double latitude;
    double altitude;
    float compass;


    MainActivity ma = null;

    public DiameterFragment(MainActivity ma) {this.ma = ma; ai=ma.infoArray;}
    public int id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, null);
        id = 0;


        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        ma.initModel();


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
            // renewal of anchor
//            ma.clearAnchor();


            // Create the transformable object and add it to the anchor.
            ma.anchor = anchor2;
            ma.anchorNode = anchorNode2;

            SimpleDateFormat dateformat = new SimpleDateFormat("dd_HHmmss");
            String idstr = dateformat.format(System.currentTimeMillis());
            Info tmp = new Info(new TransformableNode(ma.arFragment.getTransformationSystem()),
                                new TransformableNode(ma.arFragment.getTransformationSystem()), idstr);
            tmp.setDiameter(100);
            tmp.setHeight(0);
            tmp.getNode().setRenderable(ma.modelRenderable);
            tmp.getH_Node().setRenderable(ma.modelRenderable2);
            tmp.getNode().setParent(anchorNode2);
            tmp.getH_Node().setParent(anchorNode2);
            tmp.getNode().setOnTouchListener(touchNode);
            tmp.getH_Node().setOnTouchListener(touchNode);
            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);




            if (ma.anchorNode != null) {

                Frame frame = ma.arFragment.getArSceneView().getArFrame();

                Pose objectPose = ma.anchor.getPose();
                Pose cameraPose = frame.getCamera().getPose();

                float dx = objectPose.tx() - cameraPose.tx();
                float dy = objectPose.ty() - cameraPose.ty();
                float dz = objectPose.tz() - cameraPose.tz();

                ///Compute the straight-line distance.
                float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                tmp.setDistance(distanceMeters);
                String meter = String.format("%.2f", distanceMeters);

                ma.mDistance_tv.setText("거        리 : " + meter + "m");

                if (ma.altitude_vec.isEmpty()) {
                    ma.altitude_vec.add(altitude);
                    ma.mAltitude_tv.setText("고        도 :" +
                            Integer.toString((int) altitude) + "m");
                }

                if (ma.compass_vec.isEmpty()) {
                    compass = Math.abs(mMySensorEventListener.getYaw());
                    compass = Math.round(compass);
                    ma.mCompass_tv.setText("방        위 : " + compass + "°"
                            + mMySensorEventListener.matchDirection(compass));
                }
            }

            ma.mDiameter_tv.setText("흉 고 직 경 : " +
                    Float.toString((float) ma.radi / 10) + "cm");

            ai.add(tmp);
            id = ai.size() - 1;
            ai.get(id).getNode().select();
            ma.tree_id = id;
            ma.radiusbar.setProgress(100,true);
            ma.heightbar.setProgress(0,true);
        });
        return root;
    }



    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onUpdate(FrameTime frameTime) {

    }


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
                ma.mHeight_tv.setText("수      고 : " + Float.toString(1.2f+ai.get(id).getHeight()/100)+"m" );

            }
            return false;
        }


    };




    //Location and Altitude
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventListener);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        altitude = location.getAltitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

}

