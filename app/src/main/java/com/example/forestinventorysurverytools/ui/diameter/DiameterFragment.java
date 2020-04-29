package com.example.forestinventorysurverytools.ui.diameter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
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
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

import static android.content.Context.SENSOR_SERVICE;

public class DiameterFragment extends Fragment implements  Scene.OnUpdateListener {

    View root;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;

    ImageButton mBtn_diameter;
    ImageButton mBtn_calculate;

    double diameter1;
    double diameter2;

    float angle;
    float angle2;

    MainActivity ma=null;
    public DiameterFragment(MainActivity ma){this.ma=ma;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, null);


        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mBtn_diameter = (ImageButton) root.findViewById(R.id.Btn_diameter);
        mBtn_calculate = (ImageButton) root.findViewById(R.id.Btn_calculate);

        mBtn_diameter.setOnClickListener(measureDiameter);
        mBtn_calculate.setOnClickListener(getMeasureDiameter);

        initModel();

        // 현재 이 부분으로 인해서 거리에서 탭해서 생긴 앵커, 직경에서 탭해서 생긴 앵커 이렇게 따로따로 있음.
        // 추후 MainActivity에서 anchor, anchorNode를 List나 Vector타입으로 관리하면 될듯 싶습니다.
        ma.arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {


            if (ma.modelRenderable == null)
                return;

            // Creating Anchor.
            Anchor anchor2 = hitResult.createAnchor();
            AnchorNode anchorNode2 = new AnchorNode(anchor2);

            anchorNode2.setParent(ma.arFragment.getArSceneView().getScene());

            clearAnchor();

            ma.anchor = anchor2;
            ma.anchorNode = anchorNode2;

            TransformableNode node = new TransformableNode(ma.arFragment.getTransformationSystem());
            node.setRenderable(ma.modelRenderable);
            node.setParent(anchorNode2);
            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);
            node.select();


        });


        return root;
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
        MaterialFactory.makeTransparentWithColor(this.getContext(), new Color(android.graphics.Color.BLUE))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3(0.05f, 0.01f, 0.01f);
                            ma.modelRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            ma.modelRenderable.setShadowCaster(false);
                            ma.modelRenderable.setShadowReceiver(false);
                            Boolean b  = (ma.modelRenderable==null);

                        });
    }

    private void clearAnchor() {
        ma.anchor = null;


        if (ma.anchorNode != null) {
            ma.arFragment.getArSceneView().getScene().removeChild(ma.anchorNode);
            ma.anchorNode.getAnchor().detach();
            ma.anchorNode.setParent(null);
            ma.anchorNode = null;
        }
    }

    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    /**
     * distance(수평거리) = 경사를 측정할때 구하도록 작업하기
     * angle = Math.abs(mMySensorEventListener.getPitch());
     * x_angle = angle/2;
     * y_angle = angle/2;
     * diameter1 = Math.tan(x_angle) * distance;
     * diameter2 = Math.tan(y_angle) * distance;
     * t_diameter = diameter1 + diameter2;
     */

    //Button
    ImageButton.OnClickListener measureDiameter = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View diameter) {
            mMySensorEventListener.updateOrientationAngles();
            if (ma.angle_vec.isEmpty()) {
                angle = Math.abs(mMySensorEventListener.getPitch());
                ma.angle_vec.add((float) angle);
                showToast(Integer.toString(ma.angle_vec.size()));
            } else {
                angle2 = Math.abs(mMySensorEventListener.getPitch());
                angle2 = angle2 + angle;
                angle2 = angle2/2;
                ma.angle_vec.add((float) angle2);
                showToast(Integer.toString(ma.angle_vec.size()));
            }
        }
    };



    ImageButton.OnClickListener getMeasureDiameter = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {
            if (calculate.getId() == R.id.Btn_calculate) {
                float x_angle = angle2;
                float y_angle = angle2;
                diameter1 = Math.tan(x_angle) * ma.mDistance_val;
                diameter2 = Math.tan(y_angle) * ma.mDistance_val;
                ma.mDiameter_val = diameter1 + diameter2;
                String dbh = String.format("%.2f", ma.mDiameter_val);
                ma.mDiameter_tv.setText("흉고직경: " + dbh + "cm");
                showToast("계산완료");
            }
        }
    };



    @Override
    public void onUpdate(FrameTime frameTime) { }



    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }



    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventListener);
    }
}


