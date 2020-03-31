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
    float angle_1;
    float angle2_1;
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
    Vector<Float>angle_vec = new Vector<Float>();
    Vector<Float>quadrant_vec = new Vector<Float>();
    float angle_quad;

    //Button
    ImageButton.OnClickListener measureHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View height) {

            mMySensorEventListener.updateOrientationAngles();
            if (!mInputHeight.getText().toString().isEmpty()) {

                if (click_count%3 == 0) {
                    angle = Math.abs(mMySensorEventListener.getPitch());
                    click_count++;
                    showToast("1");
                } else if (click_count%3 == 1) {
                    angle2 = Math.abs(mMySensorEventListener.getPitch());
                    float quadrant = mMySensorEventListener.getPitchQuadrantUpDown();
                    angle2 = angle2 * Math.signum(quadrant);
                    angle_1 = Math.abs(mMySensorEventListener.getPitch());
                    click_count++;
                    showToast("2");
                } else if (click_count%3 == 2) {
                    angle2_1 = Math.abs(mMySensorEventListener.getPitch());
                    float quadrant2 = mMySensorEventListener.getPitchQuadrantUpDown();
                    angle2_1 = angle2_1 * Math.signum(quadrant2);
                    click_count++;
                    showToast("3");
                } //2번까지 진행했을때의 값이 너무 작음.. 3번 말고 n번으로 할 수 있는 방법을 찾고, calculate 눌렀을때의 값을 구할 수 있게 작업해야됨.


                /*
                if(angle_vec.isEmpty()){
                    angle = Math.abs(mMySensorEventListener.getPitch());
                    angle_vec.add(angle);
                    showToast(Integer.toString(angle_vec.size()));
                }else{
                    angle = Math.abs(mMySensorEventListener.getPitch());
                    angle_vec.add(angle);
                    float quadrant = mMySensorEventListener.getPitchQuadrantUpDown();
                    angle = angle * Math.signum(quadrant);
                    angle_quad = angle*Math.signum(quadrant);
                    quadrant_vec.add(angle_quad);
                    showToast(Integer.toString(angle_vec.size()));
                }*/

            }
        }
    };

    double length=0.0;
    double angleCalc=0.0;
    double dist=0.0;
    double finalDisp=0.0;

    ImageButton.OnClickListener calculateHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {
            if (calculate.getId() == R.id.Btn_calculate) {
                float userHeight = Float.valueOf(mInputHeight.getText().toString()) / 100f;

                length = userHeight * Math.tan(angle + angle_1);
                angleCalc = Math.PI/2.0 - Math.abs(angle2 + angle2_1);
                dist = length * Math.tan(angleCalc);
                finalDisp = dist * (-1)/Math.signum(angle2 + angle2_1);

                /*System.out.println(angle_vec.elementAt(0));
                length = userHeight*Math.tan(angle_vec.elementAt(0)+angle_vec.elementAt(1));
                if(quadrant_vec.size() == 1){ // 나무를 두번만 마크 찍고 했을때
                    angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec.elementAt(0));
                    dist = length * Math.tan(angleCalc);
                    finalDisp += dist*(-1)/Math.signum(quadrant_vec.elementAt(0));
                }else{ // 나무를 N번찍고 수고 측정할때
                    for(int i=0; i<quadrant_vec.size(); i++){
                        angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec.elementAt(0) + quadrant_vec.elementAt(1));
                        dist = length * Math.tan(angleCalc);
                        finalDisp += dist*(-1)/Math.signum(quadrant_vec.elementAt(0)+quadrant_vec.elementAt(1));
                    }


                angleCalc = Math.PI/2.0 - Math.abs(quadrant_vec[0] + quadrant_vec[1]);
                dist = length * Math.tan(angleCalc);
                finalDisp += dist*(-1)/Math.signum(quadrant_vec[0]+quadrant_vec[1]);
                }*/



                String height_value = String.format("%.1f", userHeight+finalDisp);
                mHeight_tv.setText("수        고 :" + height_value + "m");
                showToast("calculate");

                angle_vec.removeAllElements();
                quadrant_vec.removeAllElements();


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


