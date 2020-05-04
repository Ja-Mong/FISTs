package com.example.forestinventorysurverytools;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

//public class MySensorEventListener extends Fragment implements SensorEventListener {
//
//    SensorManager mSensorManager;
//    Sensor sensor;
//
//    //Motion sensor
//    final float[] mAccelerometerReading = new float[3];
//    final float[] mMagnetometerReading = new float[3];
//    final float[] mGyroscopeReading = new float[3];
//
//    //Position sensor
//    final float[] mOrientationAngles = new float[3];
//    final float[] mRotationMatrix = new float[9];
//
//
//    public MySensorEventListener(SensorManager mSensorManager) {
//        super();
//  //      mSensorManager = (SensorManager);
//  //      ㄴ mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);  이런식으로 센서매니저 등록해야 하는데 Fragment로 extends된 상황에서 명령어를 어떻게 수정해야 할지 모르겠습니다.
//  //      sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//  //      mSensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
//        this.mSensorManager = mSensorManager;
//    }
//
//
//
//    //각 센서값들을 수신하여 행렬에 저장
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            System.arraycopy(event.values, 0, mAccelerometerReading,
//                    0, mAccelerometerReading.length);
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            System.arraycopy(event.values, 0, mMagnetometerReading,
//                    0, mMagnetometerReading.length);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            System.arraycopy(event.values, 0, mGyroscopeReading,
//                    0, mGyroscopeReading.length);
//        }
//
//
//
//
//    }
//
//
//
//
//    //최신 센서값으로 로딩
//    public void updateOrientationAngles() {
//
//        mSensorManager.getRotationMatrix(mRotationMatrix, null,
//                mAccelerometerReading, mMagnetometerReading);
//
//        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
//
//    }
//
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) { }
//
//
//
//    /**
//     * 업데이트 되는 OrientationAngles Values[]를 호출
//     * 단, 오차에 대한 보정이 별도로 필요할 듯 함.
//     * <p>
//     * 오차가 발생되는 이유
//     * 1. 적분오차
//     * 2. 기종별 센서 기능
//     */
//    public float getYaw() {
//        float yaw = -mOrientationAngles[0];
//        return yaw;
//    }//z축
//
//    public float getPitch() {
//        float pitch = (mOrientationAngles[1]);
//        return pitch;
//    }//x축
//
//    public float getRoll() {
//        float roll = mOrientationAngles[2];
//        return roll;
//    }//y축
//
//
////    public void getAltitude(SensorEvent sensorEvent){
////        float presure = sensorEvent.values[0];
////        presure = (float)(Math.round(presure*100)/100.0);
////        float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,presure);
////        altitude = (float)(Math.round(altitude*100)/100.0);
////    }
//// 기압계 센서를 기반으로 고도 나타내나, mMySensorListener 메소드에서 센서매니저 등록이 안되어서 사용 시 앱 다운 현상 발생합니다.
//
//    //Compass
//    public String matchDirection(float compass) {
//        if(compass >= 90 && compass < 113) { return "N"; } //각도
//        if(compass >=68 && compass <= 90) {return "N"; }
//        if(compass >= 113 && compass < 158) { return "NE"; }
//        if(compass >= 158 && compass < 203) { return "E"; }
//        if(compass >= 203 && compass < 248) { return "SE"; }
//        if(compass >= 248 && compass < 293) { return "S"; }
//        if(compass >= 293 && compass < 338) { return "SW"; }
//        if(compass >= 338 && compass <=360) { return "W"; }
//        if(compass >= 0 && compass < 23) { return "W"; }
//        if(compass >= 23 && compass < 68) { return "NW"; }
//        return null;
//    }
//}