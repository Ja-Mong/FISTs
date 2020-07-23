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


    //Activity
    MainActivity ma = null;

    public MySensorEventListener(MainActivity ma, SensorManager mSensorManager) {
        super();
        this.mSensorManager = mSensorManager;
        this.ma = ma;
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

    //y축
    public float getRoll() {
        float roll = mOrientationAngles[2];
        return roll;
    }


    //x축
    public float getPitch() {
        float pitch = mOrientationAngles[1];
        return pitch;
    }


    //z축
    public float getYaw() {
        float yaw = -mOrientationAngles[0];
        return yaw;
    }



    //Compass
    public String matchDirection(int azimuth) {
        if (azimuth == 0) {ma.mAzimuth_tv.setText(90 +"°"); return "N";}
        if (azimuth >= 1 && azimuth <= 89) {ma.mAzimuth_tv.setText(Integer.toString((int)azimuth+90)+"°"); return "NE";}
        if (azimuth == 90) {ma.mAzimuth_tv.setText(180 + "°" ); return "E";}
        if (azimuth >= 91 && azimuth <= 179) {ma.mAzimuth_tv.setText(Integer.toString((int)azimuth+90)+"°"); return "SE"; }
        if (azimuth == 180) {ma.mAzimuth_tv.setText(270 + "°"); return "S";}
        if (azimuth >= 181 && azimuth <= 269) {ma.mAzimuth_tv.setText(Integer.toString((int)azimuth+90)+"°"); return "SW";}
        if (azimuth == 270) {ma.mAzimuth_tv.setText(0 + "°"); return "W";}
        if (azimuth >= 271 && azimuth <= 359) {ma.mAzimuth_tv.setText(Integer.toString((int)azimuth-270)+"°"); return "NW";}
        if (azimuth == 360) {ma.mAzimuth_tv.setText(90 + "°"); return "N";}
        return null;
    }
}