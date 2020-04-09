package com.example.forestinventorysurverytools.ui.distance;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import static android.content.Context.SENSOR_SERVICE;


public class DistanceFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener {

    View root;
    CameraAPI mDistCameraAPI;
    TextureView mCameraPreview_dist;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;
//    WindowManager mWindowManager;
//    Sensor mRotationSensor;
    Handler mCameraHandler;
    HandlerThread mCameraThread;

    ImageView focusImage;
    ImageButton mBtn_distance;
<<<<<<< HEAD
    TextView mDistance_tv;
    TextView mDiameter_tv;
    TextView mHeight_tv;
    TextView mCompass_tv;
    TextView mAltitude_tv;
    TextView mInclinometer_tv;
    EditText mInputHeight;
=======
>>>>>>> a95421060153e0f97b4d45bbf4db1baa9febfe1e


    MainActivity ma=null;

    float angle;

    public DistanceFragment(MainActivity ma){this.ma=ma;}
    //layout View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_distance, container, false);
        mDistCameraAPI = new CameraAPI(this);
        mCameraPreview_dist = (TextureView) root.findViewById(R.id.camera_preview);


        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);
//        mWindowManager = getActivity().getWindow().getWindowManager();
//        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        focusImage = (ImageView) root.findViewById(R.id.focus); /* 버튼을 제외하고 공통적으로 나타내는 View들의 경우 굳이 코드를 작성하지 않아도 되는 듯 함 */
        mBtn_distance = (ImageButton) root.findViewById(R.id.Btn_distance);
<<<<<<< HEAD
        mDistance_tv = (TextView)root.findViewById(R.id.tv_distance);
        mDiameter_tv = (TextView)root.findViewById(R.id.tv_diameter);
        mHeight_tv = (TextView)root.findViewById(R.id.tv_height);
        mCompass_tv = (TextView)root.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView)root.findViewById(R.id.tv_alititude);
        mInclinometer_tv = (TextView)root.findViewById(R.id.tv_inclinometer);
        mInputHeight = (EditText)root.findViewById(R.id.input_height);
=======

>>>>>>> a95421060153e0f97b4d45bbf4db1baa9febfe1e

        mBtn_distance.setOnClickListener(measureDistance);



        return root;
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
<<<<<<< HEAD
        showToast("거리측정 기능 수행");
=======
        showToast("거리 기능 수행");
        // 카메라 가로 변환
        mDistCameraAPI.transformImage(mCameraPreview_dist, mCameraPreview_dist.getWidth(),mCameraPreview_dist.getHeight());
>>>>>>> a95421060153e0f97b4d45bbf4db1baa9febfe1e
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
        super.onPause();
        closeCamera();
        stopCameraHandlerThread();
        mSensorManager.unregisterListener(mMySensorEventListener);
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
                    ma.mDistance_val = phoneHeight * (float) Math.tan(angle);
                    ma.mDistance_tv.setText("거        리 :" + String.format("%.1f",  ma.mDistance_val) + "m");
                } else {
                    showToast("핸드폰의 높이를 입력해주세요.");
                }
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








