package com.example.lla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

//LocationListener ~ 위치서비스 호출 위해
public class MainActivity extends AppCompatActivity implements LocationListener {
    final int dex = 1; //value 값 지정
    TextView txt;
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView)findViewById(R.id.txt);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // gps - network provider 사용 가능 여부 확인 기능??
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        dex);
            }


        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }


    @Override
    protected  void onPause() {
        super.onPause();

        lm.removeUpdates(this);
    }


    @Override
    //위치 데이터 수신 시 메소드 - Location 객체 실행
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();

        txt.setText("위도: "+Integer.toString((int)longitude)+"\n"+"경도: "+Integer.toString((int)latitude)+"\n"+"고도: "+Integer.toString((int)altitude));
    }

    @Override
    //gps 신호 수신 안될 시 호출 (객체 정리만)
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // LocationProvider.OUT_OF_SERVICE   ==  위치 제공자 이용불가
        // LocationProvider.TEMPORARILY_UNAVAILABLE == 위치 제공자 현재 이용불가, 조치가능
        // LocationProvider.AVAILABLE == 위치 제공자 이용가능
        // 조건별로 정리해서 상황처리 유도 Toast Message 호출기능??
    }

    @Override
    //onStatusChanged와 비슷한 역활이지만 사용자가 위치제공자를 위치 설정 메뉴에서 사용 가능-불가능 변경 시 통지 받을 수 있도록 하는 메소드
    // LocationProvider 객체 사용
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}