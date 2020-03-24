package com.example.compass_v03;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    //implements ~ 회전-gravity-geomagnetic vector 다중상속을 위해


    // 나침반 이미지

    private float currentDegree = 0f;
    // 현재 방위각 측정

    private SensorManager mSensorManager;
    // 안드로이드 센서
    TextView tvHeading;
    // tvHeading=0.0으로 설정 되어있고, TextView를 통해 현재 방위각 표시
    TextView direct;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        direct = (TextView) findViewById(R.id.direct);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        // tvHeading 통해 방위각 확인
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 센서 초기 기능 설정
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        // 방향 센서 등록
    }

    @Override
    protected void onPause() {
        super.onPause();


        mSensorManager.unregisterListener(this);
        // 멈춤
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        Integer degree = Math.round(event.values[0]);
        // 상-하축(z-axis)를 중심으로 한 회전 각도 추출
        tvHeading.setText("현재 방위:" + Integer.toString(degree)+"°");
        direct.setText(getDirectionFromDegrees(degree));


    }



    private String getDirectionFromDegrees(int degree) {
        if(degree >= 0 && degree < 23) { return "N"; }
        if(degree >=338) {return "N"; }
        if(degree >= 23 && degree < 68) { return "NE"; }
        if(degree >= 68 && degree < 113) { return "E"; }
        if(degree >= 113 && degree < 158) { return "SE"; }
        if(degree >= 158 && degree < 203) { return "S"; }
        if(degree >= 203 && degree < 248) { return "SW"; }
        if(degree >= 248 && degree < 293) { return "W"; }
        if(degree >= 293 && degree < 338) { return "NW"; }
        // 0<=N<45, 45<=E<135, 135<=S<225, 225<=W<=0
        return null;

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}