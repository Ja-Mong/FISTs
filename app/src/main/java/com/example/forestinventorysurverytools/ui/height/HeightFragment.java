package com.example.forestinventorysurverytools.ui.height;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;

import static android.content.Context.SENSOR_SERVICE;


public class HeightFragment extends Fragment implements CameraAPI.Camera2Interface,
        TextureView.SurfaceTextureListener {
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
        showToast("수고측정 기능 수행");
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

        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
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

    float f_theta = 0;
    float t_theta = 0;
    float xy_theta = 0;
    float x_theta = 0;
    float y_theta = 0;

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
                if (ma.theta_vec.isEmpty()) {
                    f_theta = Math.abs(mMySensorEventListener.getRoll());
                    ma.theta_vec.add(f_theta);
                    showToast(Integer.toString(ma.theta_vec.size()));
                    x_theta = 90 - ma.theta_vec.elementAt(0);
                } else {
                    t_theta = Math.abs(mMySensorEventListener.getRoll());
                    xy_theta = t_theta - ma.theta_vec.elementAt(0);
                    y_theta = Math.abs(xy_theta - x_theta);
                    ma.theta_vec.add(y_theta);
                }
            }
        }
    };

    /* theta_vec : 구간별 theta 벡터, dist_vec : 구간별 수고 벡터 */

    /**
     * 두번째 고도값 가져오기
     * if(calculate.getId() == R.id.Btn_calculate) {
     *     float altitude2 = Math.abs(mMySensorEventListener.getAltitude());
     *     ...
     *     for(...) { //Up slope
     *         h = altitude - altitude2;
     *         d = h/ Math.tan(slope);
     *         t_height = (Math.tan(angle + slope) * distance) - h;
     *     }
     *     for(...) { //down slope
     *         h = altitude2;
     *         d = h/Math.tan(slope);
     *         t_height = (Math.tan(angle - slope) * distance) + h;
     *     }
     * }
     */

    double x_height;
    double t_height;
    double new_height;

    final ImageButton.OnClickListener getCalculateHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {
            if (calculate.getId() == R.id.Btn_calculate) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_theta) * phoneHeight);
                float compass = Math.abs(mMySensorEventListener.getYaw());
                for (int i = 1; i < ma.theta_vec.size(); i++) {
                    if (ma.height_vec.isEmpty()) {
                        x_height = distance * Math.tan(ma.theta_vec.elementAt(i));
                        ma.height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(ma.theta_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        ma.height_vec.add(new_height);
                        t_height += new_height;
                        ma.mCompass_tv.setText("방        위 :"+compass+"°"+ma.matchDirection(compass));
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_val=t_height;
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


