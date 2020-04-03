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
    TextView mDistance_tv;
    TextView mDiameter_tv;
    TextView mHeight_tv;
    TextView mCompass_tv;
    TextView mAltitude_tv;
    EditText mInputHeight;

    float angle;
    float angle2;
    int click_count = 0;

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
        mDistance_tv = (TextView) root.findViewById(R.id.tv_distance);
        mDiameter_tv = (TextView) root.findViewById(R.id.tv_diameter);
        mHeight_tv = (TextView) root.findViewById(R.id.tv_height);
        mCompass_tv = (TextView) root.findViewById(R.id.tv_compass);
        mAltitude_tv = (TextView) root.findViewById(R.id.tv_alititude);
        mInputHeight = (EditText) root.findViewById(R.id.input_height);

        mBtn_height.setOnClickListener(measureHeight);
        mBtn_calculate.setOnClickListener(calculateHeight);

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
    Vector<Float> angle_vec = new Vector<Float>();
    Vector<Float> quadrant_vec = new Vector<Float>();
    float angle_quad;

    float distance = 0;
    float T_height = 0;
    float angleT = 0;
    float angleXY = 0;
    float angleX = 0;
    float angleY = 0;
    float angleNew = 0;

    /**
     * float f_theta = 0;
     * float t_theta = 0;
     * float xy_theta = 0;
     * float x_theta = 0;
     * float y_theta = 0;
     * float n_theta = 0;
     *
     * final ImageButton.OnClickListener measureHeight = new ImageButton.OnClickListener() {
     *
     * if (click_count % 3 == 0) {
     *     float phoneHeight = Float.volueOf(mInputHeight.getText().toString()) / 100f;
     *     f_theta = Math.abs(mMySensorEventListener.getRoll());
     *     click_count++;
     * } else if (click_count % 3 == 1) {
     *     t_theta = Math.abs(mMySensorEventListener.getRoll());
     *     xy_theta = t_theta - f_theta;
     *     x_theta = 90 - f_theta;
     *     y_theta = Math.abs(xy_theta - x_theta);
     *     click_count++;
     * } else if (click_count % 3 == 2) {
     *     t_theta = Math.abs(mMySensorEventListener.getRoll());
     *     xy_theta = t_theta - f_theta;
     *     y_theta = Math.abs(xy_theta - x_theta);
     *     y_theta = y_theta - n_theta;
     *     click_count++;
     * }
     */


    //Button
    final ImageButton.OnClickListener measureHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View height) {

            mMySensorEventListener.updateOrientationAngles();
            if (!mInputHeight.getText().toString().isEmpty()) {
                if (click_count % 3 == 0) {
                    float phoneHeight = Float.valueOf(mInputHeight.getText().toString()) / 100f;
                    angle = Math.abs(mMySensorEventListener.getRoll());
                    click_count++;
                    showToast("1");
                } else if (click_count % 3 == 1) {
                    angleT = Math.abs(mMySensorEventListener.getRoll());
                    angleXY = angleT - angle;
                    angleX = 90 - angle;
                    angleY = Math.abs(angleXY - angleX);
                    click_count++;
                    showToast("2");
                }

//                if(angle_vec.isEmpty()){
//                    angle = Math.abs(mMySensorEventListener.getRoll());
//                    angle_vec.add(angle);
//                    showToast(Integer.toString(angle_vec.size()));
//                }else{
//                    angle = Math.abs(mMySensorEventListener.getRoll());
//                    angle_vec.add(angle);
//                    float quadrant = mMySensorEventListener.getRollQuadrantUpDown();
//                    angle = angle * Math.signum(quadrant);
//                    angle_quad = angle*Math.signum(quadrant);
//                    quadrant_vec.add(angle_quad);
//                    showToast(Integer.toString(angle_vec.size()));
                }
            }
        };

    /**
     * double distance = 0;
     * double t_height = 0;
     * double x_height = 0;
     * double x1_height = 0;
     *
     * final ImageButton.OnClickListener calculateHeight = new ImageButton.OnClickListener() {
     *
     * if (calculate.getId() == R.id.Btn_calculate) {
     *     float phoneHeight = Float.valueOf(mInputHeight.getText().toString()) / 100f;
     *     distance = phoneHeight * Math.tan(y_theta); //distance: 지속적으로 갱신되는 값 / y_theta: 지속적으로 갱신되는 값
     *     x_height = distance * Math.tan(y_theta);
     *     x1_height = distance * Math.tan(y_theta);
     *     n_height = x1_height - x_height;
     *     t_height = x_height + n_height + phoneHeight;
     * }
     *
     */

        double length = 0.0;
        double angleCalc = 0.0;
        double dist = 0.0;
        double finalDisp = 0.0;
        double height = 0.0;
        double angleCal = 0.0;

        ImageButton.OnClickListener calculateHeight = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View calculate) {
                if (calculate.getId() == R.id.Btn_calculate) {
                    float phoneHeight = Float.valueOf(mInputHeight.getText().toString()) / 100f;
                    double distance = phoneHeight * Math.tan(angleY); //distance를 구하는  angle 값이 계속 새로운 angle이 들어와야 될 수 도 있음.. Test 필요
                    double T_height = distance * Math.tan(angleY);
//                double T_height2 = distance * Math.tan(angleNew);
//                double New_height = T_height2 - T_height;
                    double height = T_height + phoneHeight;

                    String height_value = String.format("%.1f", height);
                    mHeight_tv.setText("수        고 :" + height_value + "m");
                    showToast("calculate");
//                length = phoneHeight * Math.tan(angle + angle_1);
//                angleCalc = Math.PI/2.0 - Math.abs(angle2 + angle2_1);
//                dist = length * Math.tan(angleCalc);
//                finalDisp = dist * (-1)/Math.signum(angle2 + angle2_1);
//                length = userHeight * Math.tan(angle + angle_1);
//                angleCalc = Math.PI/2.0 - Math.abs(angle2 + angle2_1);
//                dist = length * Math.tan(angleCalc);
//                finalDisp = dist * (-1)/Math.signum(angle2 + angle2_1);
//                System.out.println(angle_vec.elementAt(0));
//                length = phoneHeight*Math.tan(angle_vec.elementAt(0)+angle_vec.elementAt(1));
//                if(quadrant_vec.size() == 1){ // 나무를 두번만 마크 찍고 했을때
//                    angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec.elementAt(0));
//                    dist = length * Math.tan(angleCalc);
//                    finalDisp += dist*(-1)/Math.signum(quadrant_vec.elementAt(0));
//                }else{ // 나무를 N번찍고 수고 측정할때
//                    for(int i=0; i<quadrant_vec.size(); i++){
//                        angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec.elementAt(0) + quadrant_vec.elementAt(1));
//                        dist = length * Math.tan(angleCalc);
//                        finalDisp += dist*(-1)/Math.signum(quadrant_vec.elementAt(0)+quadrant_vec.elementAt(1));
//                    }
//
//
//                angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec[0] + quadrant_vec[1]);
//                dist = length * Math.tan(angleCalc);
//                finalDisp += dist*(-1)/Math.signum(quadrant_vec[0]+quadrant_vec[1]);
//                }
//
//                angle_vec.removeAllElements();
//                quadrant_vec.removeAllElements();


                }// end if


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


