package com.example.forestinventorysurverytools.ui.height;

import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import java.util.Vector;

import static android.content.Context.SENSOR_SERVICE;


public class HeightFragment extends Fragment implements CameraAPI.Camera2Interface, TextureView.SurfaceTextureListener {
    View root;
    CameraAPI mHeightCameraAPI;
    TextureView mCameraPreview_height;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;
    Handler mCameraHandler;
    HandlerThread mCameraThread;

    ImageView focusImage;
    ImageButton mBtn_height;
    ImageButton mBtn_calculate;

    MainActivity ma=null;

    public HeightFragment(MainActivity ma){this.ma=ma;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_height, container, false);
        mHeightCameraAPI = new CameraAPI(this);
        mCameraPreview_height = (TextureView) root.findViewById(R.id.camera_preview);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        focusImage = (ImageView) root.findViewById(R.id.focus);
        mBtn_height = (ImageButton) root.findViewById(R.id.Btn_height);
        mBtn_calculate = (ImageButton) root.findViewById(R.id.Btn_calculate);


        mBtn_height.setOnClickListener(measureHeight);
        mBtn_calculate.setOnClickListener(getCalculateHeight);

        return root;
    }


    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    //Camera
    private void openCamera() {
        CameraManager cameraManager = mHeightCameraAPI.cameraManager_1_H(this);
        String cameraID = mHeightCameraAPI.CameraCharacteristics_2(cameraManager);
        mHeightCameraAPI.CameraDevice_3_H(cameraManager, cameraID);
        showToast("수고 기능 수행");
        // 카메라 가로 변환
        mHeightCameraAPI.transformImage(mCameraPreview_height,mCameraPreview_height.getWidth(),mCameraPreview_height.getHeight());
    }

    @Override
    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture surfaceTexture = mCameraPreview_height.getSurfaceTexture();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHeightCameraAPI.CaptureSession_4_H(cameraDevice, surface);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHeightCameraAPI.CaptureSession_5(cameraDevice, surface);
        }
    }

    private void closeCamera() {
        mHeightCameraAPI.closeCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraPreview_height.isAvailable()) {
            openCamera();
        } else {
            mCameraPreview_height.setSurfaceTextureListener(this);
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
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    //n번 넣을 수 있게 하는 자료구조 전역 선언
    Vector<Float> theta_vec = new Vector<Float>(); // 측정하는 모든 angle 값 저장

    float f_theta = 0;
    float t_theta = 0;
    float xy_theta = 0;
    float x_theta = 0;
    float y_theta = 0;
//    float y2_theta = 0;
//    float n_theta = 0;


    // 이렇게 하게 되면 "theta_vec"에 저장되는 값은
    // [0] 처음 측정했을 때의  f_theta
    // [1] xy_theta - x_theta를 한 y_theta 값
    // [2] y2_theta
    // ....
    // [N] angleYn == n번 측정했을 때의 y_theta (n_theta)

    //Button
    final ImageButton.OnClickListener measureHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View height) {

            mMySensorEventListener.updateOrientationAngles();
            if (!ma.mInputHeight.getText().toString().isEmpty()) {
                if (theta_vec.isEmpty()) {
                    f_theta = Math.abs(mMySensorEventListener.getRoll());
                    theta_vec.add(f_theta);
                    showToast(Integer.toString(theta_vec.size()));
                    x_theta = 90 - theta_vec.elementAt(0);
                } else {
                    t_theta = Math.abs(mMySensorEventListener.getRoll());
                    xy_theta = t_theta - theta_vec.elementAt(0);
                    y_theta = Math.abs(xy_theta - x_theta);
                    theta_vec.add(y_theta);
                }
            }
        }
    };

    /* theta_vec : 구간별 theta 벡터, dist_vec : 구간별 수고 벡터*/

    Vector<Double> height_vec = new Vector<Double>(); // 측정하는 모든 angle 값 저장

    double x_height;
    double t_height;
    double new_height;

    final ImageButton.OnClickListener getCalculateHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {
            if (calculate.getId() == R.id.Btn_calculate) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_theta) * phoneHeight);

                for (int i = 1; i < theta_vec.size(); i++) {
                    if (height_vec.isEmpty()) {
                        x_height = distance * Math.tan(theta_vec.elementAt(i));
                        height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(theta_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        height_vec.add(new_height);
                        t_height += new_height;
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_tv.setText("수        고 :" + totalHeightValue + "m");
            }
        }
    };


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
}


