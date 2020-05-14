package com.example.forestinventorysurverytools.ui.inclinometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;


public class InclinometerOrientation extends Fragment implements SensorEventListener {

    public interface Listener {
        void onOrientationChanged(float pitch, float roll);
    }

    public final WindowManager mWindowManager;
    public final SensorManager mSensorManager;

    public final Sensor mRotationSensor;

    public int mLastAccuracy;
    public Listener mListener;

        public InclinometerOrientation(WindowManager mWindowManager,
                                       SensorManager mSensorManager,
                                       Sensor mRotationSensor){
        this.mWindowManager = mWindowManager;
        this.mSensorManager = mSensorManager;
        this.mRotationSensor = mRotationSensor;
        }


    public InclinometerOrientation(Activity activity) {
        mWindowManager = activity.getWindow().getWindowManager();
        mSensorManager = (SensorManager)activity.getSystemService(Activity.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void startListening(Listener listner) {
        if (mListener == listner) {
            return;
        }
        mListener = listner;
        if (mRotationSensor == null) {
            return;
        }
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopListening() {
        mSensorManager.unregisterListener(this);;
        mListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener == null) {
            return;
        }
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        if (event.sensor == mRotationSensor) {
            updatedOrientation(event.values);
        }
    }

    public void updatedOrientation(float[] values) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);

        final int deviceAxisX;
        final int deviceAxisY;

        switch (mWindowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                deviceAxisX = SensorManager.AXIS_X;
                deviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                deviceAxisX = SensorManager.AXIS_Z;
                deviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                deviceAxisX = SensorManager.AXIS_MINUS_X;
                deviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                deviceAxisX = SensorManager.AXIS_MINUS_Z;
                deviceAxisY = SensorManager.AXIS_X;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, deviceAxisX,
                deviceAxisY, adjustedRotationMatrix);

        float[] orientationAngles = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientationAngles);

        float pitch = orientationAngles[1] * -57;
        float roll = orientationAngles[2] * -57;

        mListener.onOrientationChanged(pitch, roll);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }
}
