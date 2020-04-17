package com.example.forestinventorysurverytools.ui.distance;

import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import static android.content.Context.SENSOR_SERVICE;


public class DistanceFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener, Scene.OnUpdateListener {

    View root;
    CameraAPI mDistCameraAPI;
    TextureView mCameraPreview_dist;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;

    Handler mCameraHandler;
    HandlerThread mCameraThread;

    ImageButton mBtn_distance;

    float angle;

    MainActivity ma=null;
    public DistanceFragment(MainActivity ma){this.ma=ma;}

    ArFragment distance_arFragment;
    Anchor anchor = null;
    AnchorNode anchorNode;
    ModelRenderable modelRenderable;

    DistanceFragment distanceFR = null;
    public DistanceFragment(DistanceFragment distanceFR){this.distanceFR=distanceFR;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_distance, container, false);
        mDistCameraAPI = new CameraAPI(this);
        mCameraPreview_dist = (TextureView) root.findViewById(R.id.camera_preview);


        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mBtn_distance = (ImageButton) root.findViewById(R.id.Btn_distance);
        mBtn_distance.setOnClickListener(measureDistance);


        distance_arFragment = (ArFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.camera_preview);

        initModel();

        distance_arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            if (modelRenderable == null)
                return;

            //Create Anchor
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(distance_arFragment.getArSceneView().getScene());

            clearAnchor();

            this.anchor = anchor;
            this.anchorNode = anchorNode;

            TransformableNode transformableNode = new TransformableNode(distance_arFragment.getTransformationSystem());
            transformableNode.setRenderable(modelRenderable);
            transformableNode.setParent(anchorNode);
            distance_arFragment.getArSceneView().getScene().addOnUpdateListener(this);
            distance_arFragment.getArSceneView().getScene().addChild(anchorNode);
            transformableNode.select();

        }));


        return root;
    }

    public void initModel() {
        MaterialFactory.makeTransparentWithColor(distanceFR, new Color(android.graphics.Color.RED));
    }



    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }



    //Camera
    private void openCamera() {

        CameraManager cameraManager = mDistCameraAPI.cameraManager_1_D(this);
        String cameraID = mDistCameraAPI.CameraCharacteristics_2(cameraManager);
        mDistCameraAPI.CameraDevice_3_D(cameraManager, cameraID);
        showToast("거리측정 기능 수행");
        // 카메라 가로 변환
        mDistCameraAPI.transformImage(mCameraPreview_dist, mCameraPreview_dist.getWidth(),mCameraPreview_dist.getHeight());
    }


    @Override
    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture surfaceTexture = mCameraPreview_dist.getSurfaceTexture();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDistCameraAPI.CaptureSession_4_D(cameraDevice, surface);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDistCameraAPI.CaptureSession_5(cameraDevice, surface);
        }
    }


    private void closeCamera() {
        mDistCameraAPI.closeCamera();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mCameraPreview_dist.isAvailable()) {
            openCamera();
        } else {
            mCameraPreview_dist.setSurfaceTextureListener(this);
        }
        startCameraHandlerThread();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);
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
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }


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




    //Button
    ImageButton.OnClickListener measureDistance = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View distance) {
            mMySensorEventListener.updateOrientationAngles();
            if (distance.getId() == R.id.Btn_distance) {
//                angle = Math.abs(mMySensorEventListener.getPitch());
                angle = Math.abs(mMySensorEventListener.getRoll());
                if (!ma.mInputHeight.getText().toString().isEmpty()) {
                    float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) / 100f; // 왜? 100을 나누지??? 170을 입력시 170m로 되니 100을 나눔으로 인해서 170cm로 변환하는 건가?


                    float presure = mMySensorEventListener.getYaw(); // 바로미터, 기압을 고도로 바꾸기 위해서 사용
                    presure = (float) (Math.round(presure*100)/100.0);
                    float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,presure);
                    altitude = (float) (Math.round(altitude*100)/100.0);


                    ma.mDistance_val = phoneHeight * (float) Math.tan(angle);
                    ma.mDistance_tv.setText("거        리 :" + String.format("%.1f",  ma.mDistance_val) + "m");
                    ma.mAltitude_tv.setText("고        도 :"+altitude+"m");
                } else {
                    showToast("핸드폰의 높이를 입력해주세요.");
                }
            }
        }
    };

    @Override
    public void onUpdate(FrameTime frameTime) {

    }
}