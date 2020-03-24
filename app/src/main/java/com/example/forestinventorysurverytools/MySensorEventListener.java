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

    //Position sensor
    final float[] mOrientationAngles = new float[3];
    final float[] mRotationMatrix = new float[9];

    public MySensorEventListener(SensorManager mSensorManager) {
        super();
        this.mSensorManager = mSensorManager;
    }

    //UpDown(slope)
    public float getPitch() {
        float pitch = (float)Math.atan(mRotationMatrix[7] / mRotationMatrix[8]);
    return pitch;
    }

    //Value Listener for D, DBH, H
    public float getPitchQuadrantRigthLeft() {
        return mRotationMatrix[7];
    }
    public float getPitchQuadrantUpDown() {
        return mRotationMatrix[8];
    }

    //Rotation(Coordinate)
    public float getYaw() {
        float yaw = (float)Math.atan(
                -mRotationMatrix[6]
                        /
                Math.sqrt(mRotationMatrix[7] * mRotationMatrix[7] +
                        mRotationMatrix[8] * mRotationMatrix[8]));
        return yaw;
    }

    //Roll(Diameter)
    public float getRoll() {
        float roll = (float)Math.atan(
                mRotationMatrix[3]
                /
                mRotationMatrix[0]);
        return roll;
    }

    /**
     *  Compute the three orientation angles based on the most recent readings from
     *  the devices accelerometer and magnetometer
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
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}