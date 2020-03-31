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

//    private double mAccPitch, mAccRoll;
//    private float mPitch, mRoll;


//    private float a = 0.2f;
//    private static final float NS2S = 1.0f/1000000000.0f;
//    private double pitch = 0, roll = 0;
//    private double timestamp;
//    private double dt;
//    private double temp;
//    private boolean running;
//    private boolean gyroRunning;
//    private boolean accRunning;

    public MySensorEventListener(SensorManager mSensorManager) {
        super();
        this.mSensorManager = mSensorManager;
    }


    /**
     * 업데이트 되는 OrientationAngles Values[]를 호출
     * 단, 오차에 대한 보정이 별도로 필요할 듯 함.
     *
     * 오차가 발생되는 이유
     * 1. 적분오차
     * 2. 기종별 센서 기능
     * */
    public float getYaw() {
        float yaw = (float) -mOrientationAngles[0];
        return yaw;
    }//z축

    public float getPitch() {
        float pitch = (float) (mOrientationAngles[1]);
        return pitch;
    }//x축

    public float getRoll() {
        float roll = (float) mOrientationAngles[2];
        return roll;
    }//y축

//    //UpDown(slope)
//    public float getPitch() {
//        float pitch = (float)Math.atan(mRotationMatrix[7] / mRotationMatrix[8]);
//    return pitch;
//    }
//
    //Value Listener for D, DBH, H
    public float getPitchQuadrantRigthLeft() {
        return mRotationMatrix[7];
    }
    public float getPitchQuadrantUpDown() {
        return mRotationMatrix[8];
    } // 수정이 필요함
//
//    //Rotation(Coordinate)
//    public float getYaw() {
//        float yaw = (float)Math.atan(
//                -mRotationMatrix[6]
//                        /
//                Math.sqrt(mRotationMatrix[7] * mRotationMatrix[7] +
//                        mRotationMatrix[8] * mRotationMatrix[8]));
//        return yaw;
//    }
//
//    //Roll(Diameter)
//    public float getRoll() {
//        float roll = (float)Math.atan(
//                mRotationMatrix[3]
//                /
//                mRotationMatrix[0]);
//        return roll;
//    }

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
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, mGyroscopeReading,
                    0, mGyroscopeReading.length);
        }
//        else if (gyroRunning && accRunning) {
//            complementaty(event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //    private void complementaty(double new_ts) {
//
//        /*자이로 및 가속센서 해제*/
//        gyroRunning = false;
//        accRunning = false;
//
//        /*센서 값을 처음 출력시 dt=timestamp - event.timestamp)에 오차가 생기므로 처음엔 break*/
//        if (timestamp == 0) {
//            timestamp = new_ts;
//            return;
//        }
//        dt = (new_ts - timestamp) * NS2S; //ns - > s 변환
//        timestamp = new_ts;
//
//        /* pitch, Roll 값 산정 */
//        mAccRoll = -Math.atan2(mOrientationAngles[0], mOrientationAngles[2])
//                * 180.0 / Math.PI; //y축 기준
//
//        mAccPitch = Math.atan2(mOrientationAngles[1], mOrientationAngles[2])
//                * 180.0 / Math.PI; //x축 기준
//
//        /**
//         * 1st complementary filter.
//         * mGroscopeReading : 각속도 성분
//         * mAccPitch : 가속도계를 통해 얻어낸 회전각.
//         */
//        temp = (1/a) * (mAccRoll - roll) + mGyroscopeReading[1];
//        this.mRoll = (float) (roll + (temp * dt));
//
//        temp = (1/a) * (mAccPitch - pitch) + mGyroscopeReading[0];
//        this.mPitch = (float) (pitch + (temp * dt));
//    }
}