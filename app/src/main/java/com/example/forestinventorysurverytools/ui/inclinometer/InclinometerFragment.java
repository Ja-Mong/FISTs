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

//import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;

public class InclinometerFragment extends Fragment implements Scene.OnUpdateListener, InclinometerOrientation.Listener {


    //View
    View root;


    //Sensor
    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;


    //Draw the inclinometer view
    WindowManager mWindowManager;
    InclinometerOrientation mInclinometerOrientation;
    InclinometerIndicator mInclinometerIndicator;


    //ImageButton
    ImageButton mBtn_inclinometer;


    //Value
    float angle;


    //Activity
    MainActivity ma = null;
    public InclinometerFragment(MainActivity ma) {this.ma = ma;}






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_inclinometer, container, false);


        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);


        //Draw
        mWindowManager = getActivity().getWindow().getWindowManager();
        mInclinometerOrientation = new InclinometerOrientation(ma);
        mInclinometerIndicator = (InclinometerIndicator) root.findViewById(R.id.inclinometer);
        mInclinometerIndicator.ma= this.ma;

        //ImageButton
        mBtn_inclinometer = (ImageButton) root.findViewById(R.id.Btn_inclinometer);
        mBtn_inclinometer.setOnClickListener(measureSlope);


        return root;
    }



    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    //ImageButton
    //get the slope value
    ImageButton.OnClickListener measureSlope = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View inclino) {
            mMySensorEventListener.updateOrientationAngles();
            if (inclino.getId() == R.id.Btn_inclinometer) {
                angle = Math.abs(mMySensorEventListener.getRoll());
                float slopeAngle = (float) Math.abs(90 - Math.toDegrees(angle));
                ma.mInclinometer_val = slopeAngle;
                ma.mInclinometer_tv.setText("경        사 :" + String.format("%.1f", ma.mInclinometer_val) + "\u02DA");
            }
        }
    };


    //Sensor
    @Override
    public void onResume() {
        super.onResume();
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
        mSensorManager.unregisterListener(mMySensorEventListener);
    }


    //Move the window
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


    //AR
    @Override
    public void onUpdate(FrameTime frameTime) { }
}
