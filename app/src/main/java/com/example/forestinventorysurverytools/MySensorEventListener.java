package com.example.forestinventorysurverytools;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

public class MySensorEventListener extends Fragment implements SensorEventListener {
//    public interface Listener {
//        void onOrientationChanged(float clinoPitch, float clinoRoll);
//    }

//    private static final int SENSOR_DELAY_MICROS = 16 * 1000; //16ms

//    WindowManager mWindowManager;
//    Sensor mRotationSensor;
//    int mLastAccuracy;
//    Listener mListener;

    /**
     * Measure H, DBH variable
     */
    SensorManager mSensorManager;

    //Motion sensor
    final float[] mAccelerometerReading = new float[3];
    final float[] mMagnetometerReading = new float[3];

    //Position sensor
    final float[] mOrientationAngles = new float[3];
    final float[] mRotationMatrix = new float[9];


    public MySensorEventListener(SensorManager sensorManager) {
        super();
        mSensorManager = sensorManager;
    }

//    public MySensorEventListener(Activity activity) {
//        super();
////        mWindowManager = activity.getWindow().getWindowManager();
//        mSensorManager = (SensorManager)activity.getSystemService(Activity.SENSOR_SERVICE);
//
////        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//    }

//    public void startListening(Listener listener) {
//        if (mListener == listener) {
//            return;
//        }
//        mListener = listener;
//        if (mRotationSensor == null) {
//            return;
//        }
//        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_UI);
//    }
//    public void stopListening() {
//        mSensorManager.unregisterListener(this);
//        mListener = null;
//    }

    /**
     * 업데이트 되는 OrientationAngles Values[]를 호출
     * 단, 오차에 대한 보정이 별도로 필요할 듯 함.
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
//        if (mListener == null) {
//            return;
//        }
//        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
//            return;
//        }
//        if (event.sensor == mRotationSensor) {
//            updateOrientation(event.values);
//        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
    }

//    private void updateOrientation(float[] rotationVector) {
//        SensorManager.getRotationMatrixFromVector(mRotationMatrix, rotationVector);

//        final int DeviceAxisX;
//        final int DeviceAxisY;

//        switch (mWindowManager.getDefaultDisplay().getRotation()) {
//            case Surface.ROTATION_0:
//            default:
//                DeviceAxisX = SensorManager.AXIS_X;
//                DeviceAxisY = SensorManager.AXIS_Z;
//                break;
//                case Surface.ROTATION_90:
//                    DeviceAxisX = SensorManager.AXIS_Z;
//                    DeviceAxisY = SensorManager.AXIS_MINUS_X;
//                    break;
//                    case Surface.ROTATION_180:
//                        DeviceAxisX = SensorManager.AXIS_MINUS_X;
//                        DeviceAxisY = SensorManager.AXIS_MINUS_Z;
//                        break;
//                        case Surface.ROTATION_270:
//                            DeviceAxisX = SensorManager.AXIS_MINUS_Z;
//                            DeviceAxisY = SensorManager.AXIS_X;
//                            break;


//        }
//        final float[] adjustedRotationMatrix = new float[9];
//        SensorManager.remapCoordinateSystem(mRotationMatrix, DeviceAxisX,
//                DeviceAxisY, adjustedRotationMatrix);
//        SensorManager.getOrientation(adjustedRotationMatrix,mOrientationAngles);
//
//        float clinoPitch = mOrientationAngles[1] * -57;
//        float clinoRoll = mOrientationAngles[2] * -57;
//
//        mListener.onOrientationChanged(clinoPitch, clinoRoll);
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
//        if (mLastAccuracy != i) {
//            mLastAccuracy = i;
//        }

    }
}