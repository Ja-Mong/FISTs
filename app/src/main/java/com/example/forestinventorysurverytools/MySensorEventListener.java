package com.example.forestinventorysurverytools;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

public class MySensorEventListener extends Fragment implements SensorEventListener {

    SensorManager mSensorManager;

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


    /**
     * 업데이트 되는 OrientationAngles Values[]를 호출
     * 단, 오차에 대한 보정이 별도로 필요할 듯 함.
     * <p>
     * 오차가 발생되는 이유
     * 1. 적분오차
     * 2. 기종별 센서 기능
     */
    public float getYaw() {
        float yaw = -mOrientationAngles[0];
        return yaw;
    }//z축

    public float getPitch() {
        float pitch = (mOrientationAngles[1]);
        return pitch;
    }//x축

    public float getRoll() {
        float roll = mOrientationAngles[2];
        return roll;
    }//y축

    /**
     * Compute the three orientation angles based on the most recent readings from
     * the devices accelerometer and magnetometer
     */
    public void updateOrientationAngles() {
        //Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        //mRotationMatrix now haw up-to-date information and mOrientation now haw up-to-date information.
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
    }

    //Use calculate three orientation angles
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}