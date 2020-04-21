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

public class DiameterFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener, Scene.OnUpdateListener {

    View root;
    CameraAPI mDiamCameraAPI;
    TextureView mCameraPreview_diam;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;

    Handler mCameraHandler;
    HandlerThread mCameraThread;

    ImageButton mBtn_diameter;
    ImageButton mBtn_calculate;

    double dbh_Height = 12000/100f;
    double distance;
    double diameter1;
    double diameter2;
    double t_diameter;

    float angle;
    float angle2;
    int click_count = 0;

    MainActivity ma=null;
    public DiameterFragment(MainActivity ma){this.ma=ma;}

    ArFragment diameter_arFragment;
    Anchor anchor = null;
    AnchorNode anchorNode;
    ModelRenderable modelRenderable;

    DistanceFragment distanceFragment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, null);

        FragmentManager fm = getChildFragmentManager();
        diameter_arFragment = (ArFragment) fm.findFragmentById(R.id.ar_preview_fr);

        mDiamCameraAPI = new CameraAPI(this);
        mCameraPreview_diam = (TextureView) root.findViewById(R.id.camera_preview_fr);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mBtn_diameter = (ImageButton) root.findViewById(R.id.Btn_diameter);
        mBtn_calculate = (ImageButton) root.findViewById(R.id.Btn_calculate);

        mBtn_diameter.setOnClickListener(measureDiameter);
        mBtn_calculate.setOnClickListener(getMeasureDiameter);

        initModel();

        diameter_arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {


            if (modelRenderable == null)
                return;

            // Creating Anchor.
            Anchor anchor2 = hitResult.createAnchor();
            AnchorNode anchorNode2 = new AnchorNode(anchor2);

            anchorNode2.setParent(diameter_arFragment.getArSceneView().getScene());

            clearAnchor();

            anchor = anchor2;
            anchorNode = anchorNode2;

            TransformableNode node = new TransformableNode(diameter_arFragment.getTransformationSystem());
            node.setRenderable(modelRenderable);
            node.setParent(anchorNode2);
            diameter_arFragment.getArSceneView().getScene().addOnUpdateListener(diameter_arFragment);
            diameter_arFragment.getArSceneView().getScene().addChild(anchorNode2);
            node.select();



            if (anchorNode != null) {
                Frame frame = diameter_arFragment.getArSceneView().getArFrame();

                Pose objectPose = anchor.getPose();
                Pose cameraPose = frame.getCamera().getPose();

                float dx = objectPose.tx() - cameraPose.tx();
                float dy = objectPose.ty() - cameraPose.ty();
                float dz = objectPose.tz() - cameraPose.tz();

                ///Compute the straight-line distance.
                float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                String meter = String.format("%.2f", distanceMeters);;
//                ma.mDistance_tv.setText("거        리 : " + meter+"m");
//                Toast.makeText(ma.getApplicationContext(), meter, Toast.LENGTH_LONG).show();
            }
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
        MaterialFactory.makeTransparentWithColor(this.getContext(), new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3(0.05f, 0.01f, 0.01f);
                            modelRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b  = (modelRenderable==null);

                        });
    }

    private void clearAnchor() {
        anchor = null;


        if (anchorNode != null) {
            diameter_arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
            anchorNode = null;
        }
    }

    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }



    //Camera
    private void openCamera() {

        CameraManager cameraManager = mDiamCameraAPI.cameraManager_1_DBH(this);
        String cameraID = mDiamCameraAPI.CameraCharacteristics_2(cameraManager);
        mDiamCameraAPI.CameraDevice_3_DBH(cameraManager, cameraID);
        showToast("흉고직경측정 기능 수행");
        // 카메라 가로 변환
        mDiamCameraAPI.transformImage(mCameraPreview_diam,mCameraPreview_diam.getWidth(), mCameraPreview_diam.getHeight());
    }


    @Override
    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture surfaceTexture = mCameraPreview_diam.getSurfaceTexture();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDiamCameraAPI.CaptureSession_4_DBH(cameraDevice, surface);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDiamCameraAPI.CaptureSession_5(cameraDevice, surface);
        }
    }


    private void closeCamera() {
        mDiamCameraAPI.closeCamera();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mCameraPreview_diam.isAvailable()) {
            openCamera();
        } else {
            mCameraPreview_diam.setSurfaceTextureListener(this);
        }
        startCameraHandlerThread();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onPause() {
        closeCamera();
        stopCameraHandlerThread();
        mSensorManager.unregisterListener(mMySensorEventListener);
        super.onPause();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera();
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }


    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }



    //Handler
    private void startCameraHandlerThread() {
        mCameraThread = new HandlerThread("Camera Background");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    private void stopCameraHandlerThread() {
        mCameraThread.quitSafely();
        mCameraThread = null;
        mCameraHandler = null;
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
            if (!ma.mInputHeight.getText().toString().isEmpty()) {
                if (click_count % 2 == 0) {
                    angle = Math.abs(mMySensorEventListener.getPitch());
                    click_count++;
                    showToast("1");
                } else if (click_count % 2 == 1) {
                    angle2 = Math.abs(mMySensorEventListener.getPitch());
                    click_count++;
                    showToast("2");
                }
            }
        }
    };


    ImageButton.OnClickListener getMeasureDiameter = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {
            if (calculate.getId() == R.id.Btn_calculate) {
                float userHeight = Float.valueOf(ma.mInputHeight.getText().toString()) / 100f;
                double length = userHeight * Math.tan(angle);
                double angleCalc = Math.PI / 2.0 - Math.abs(angle2);
                double dist = length * Math.tan(angleCalc);
                double finalDisp = dist * (-1) / Math.signum(angle2);
                String height_value = String.format("%.1f", userHeight + finalDisp);
                ma.mDiameter_val = userHeight + finalDisp;
                ma.mDiameter_tv.setText("흉고직경 :" + height_value + "cm");
                showToast("calculate"); //단위 변환이 필요함
            }
        }
    };

    @Override
    public void onUpdate(FrameTime frameTime) {

    }
}


