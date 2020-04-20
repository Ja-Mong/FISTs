package com.example.forestinventorysurverytools.ui.inclinometer;

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
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;

public class InclinometerFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener,
        InclinometerOrientation.Listener {

    View root;
    CameraAPI mInclinometerCameraAPI;
    TextureView mCameraPreview_Inclino;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;

    Handler mCameraHandler;
    HandlerThread mCameraThread;

    WindowManager mWindowManager;
    InclinometerOrientation mInclinometerOrientation;
    InclinometerIndicator mInclinometerIndicator;

    ImageButton mBtn_inclinometer;

    float angle;

    MainActivity ma = null;
    public InclinometerFragment(MainActivity ma) {
        this.ma = ma;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_inclinometer, container, false);
        mInclinometerCameraAPI = new CameraAPI(this);
        mCameraPreview_Inclino = (TextureView) root.findViewById(R.id.camera_preview_fr);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mWindowManager = getActivity().getWindow().getWindowManager();
        mInclinometerOrientation = new InclinometerOrientation(ma);
        mInclinometerIndicator = (InclinometerIndicator) root.findViewById(R.id.inclinometer);

        mBtn_inclinometer = (ImageButton) root.findViewById(R.id.Btn_inclinometer);
        mBtn_inclinometer.setOnClickListener(measureSlope);



        return root;
    }

    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }




    ImageButton.OnClickListener measureSlope = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View inclino) {
            mMySensorEventListener.updateOrientationAngles();
            if (inclino.getId() == R.id.Btn_inclinometer) {
                angle = Math.abs(mMySensorEventListener.getRoll());
                float compass = Math.abs(mMySensorEventListener.getYaw());
                float slopeAngle = (float) Math.abs(90 - Math.toDegrees(angle));
                ma.mInclinometer_val = slopeAngle;
                ma.mInclinometer_tv.setText("경        사 :" + String.format("%.1f", ma.mInclinometer_val) + "\u02DA");
            }
        }
    };


    //Camera
    private void openCamera() {
        CameraManager cameraManager = mInclinometerCameraAPI.cameraManager_1_D(this);
        String cameraID = mInclinometerCameraAPI.CameraCharacteristics_2(cameraManager);
        mInclinometerCameraAPI.CameraDevice_3_D(cameraManager, cameraID);
        showToast("경사측정 기능 수행");
        // 카메라 가로 변환
        mInclinometerCameraAPI.transformImage(mCameraPreview_Inclino,
                mCameraPreview_Inclino.getWidth(),
                mCameraPreview_Inclino.getHeight());
    }


    @Override
    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture surfaceTexture = mCameraPreview_Inclino.getSurfaceTexture();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInclinometerCameraAPI.CaptureSession_4_D(cameraDevice, surface);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInclinometerCameraAPI.CaptureSession_5(cameraDevice, surface);
        }
    }
    public void closeCamera() { mInclinometerCameraAPI.closeCamera(); }


    @Override
    public void onResume() {
        super.onResume();
        if (mCameraPreview_Inclino.isAvailable()) {
            openCamera();
        } else {
            mCameraPreview_Inclino.setSurfaceTextureListener(this);
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
        super.onPause();
        closeCamera();
        stopCameraHandlerThread();
        mSensorManager.unregisterListener(mMySensorEventListener);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {openCamera();}
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {return true;}
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }


    public void startCameraHandlerThread() {
        mCameraThread = new HandlerThread("Camera Background");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }
    public void stopCameraHandlerThread() {
        mCameraThread.quitSafely();
        mCameraThread = null;
        mCameraHandler = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInclinometerOrientation.startListening(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mInclinometerOrientation.stopListening();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        mInclinometerIndicator.setInclinometer(pitch, roll);
    }
}
