package com.example.forestinventorysurverytools;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

public class MySensorEventListener extends Fragment implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mSensor;

    //Motion sensor
    final float[] mAccelerometerReading = new float[3];
    final float[] mMagnetometerReading = new float[3];
    final float[] mGyroscopeReading = new float[3];

    //Position sensor
    final float[] mOrientationAngles = new float[3];
    final float[] mRotationMatrix = new float[9];



    public MySensorEventListener(SensorManager mSensorManager) {
        super();
        this.mSensorManager = mSensorManager;
    }



    //각 센서값들을 수신하여 행렬에 저장
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, mGyroscopeReading,
                    0, mGyroscopeReading.length);
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }



    //최신 센서값으로 로딩
    public void updateOrientationAngles() {

        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
    }



    public float getYaw() {
        float yaw = -mOrientationAngles[0];
        return yaw;
    }



    //Compass
    public String matchDirection(float compass) {
        if(compass >= 90 && compass < 113) { return "N"; } //각도
        if(compass >=68 && compass <= 90) {return "N"; }
        if(compass >= 113 && compass < 158) { return "NE"; }
        if(compass >= 158 && compass < 203) { return "E"; }
        if(compass >= 203 && compass < 248) { return "SE"; }
        if(compass >= 248 && compass < 293) { return "S"; }
        if(compass >= 293 && compass < 338) { return "SW"; }
        if(compass >= 338 && compass <=360) { return "W"; }
        if(compass >= 0 && compass < 23) { return "W"; }
        if(compass >= 23 && compass < 68) { return "NW"; }
        return null;
    }
}