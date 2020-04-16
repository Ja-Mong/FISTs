package com.example.forestinventorysurverytools.ui.diameter;

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

import static android.content.Context.SENSOR_SERVICE;

public class DiameterFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener {

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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_diameter, container, false);
        mDiamCameraAPI = new CameraAPI(this);
        mCameraPreview_diam = (TextureView) root.findViewById(R.id.camera_preview);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mBtn_diameter = (ImageButton) root.findViewById(R.id.Btn_diameter);
        mBtn_calculate = (ImageButton) root.findViewById(R.id.Btn_calculate);

        mBtn_diameter.setOnClickListener(measureDiameter);
        mBtn_calculate.setOnClickListener(getMeasureDiameter);

        return root;
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

}

