package com.example.forestinventorysurverytools.ui.height;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;


public class HeightFragment extends Fragment implements LocationListener {

    View root;

    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;

    ImageButton mBtn_height;
    ImageButton mBtn_calculate;
    ImageButton mBtn_capture;
    ImageButton mBtn_calPlat;
    ImageButton mBtn_calDown;
    ImageButton mBtn_calUp;

    double x_height;
    double t_height;
    double new_height;
    double longitude;
    double latitude;
    double altitude;



    float compass;
    float f_angle = 0;
    float t_angle = 0;
    float xy_angle = 0;
    float x_angle = 0;
    float y_angle = 0;


    MainActivity ma = null;

    public HeightFragment(MainActivity ma) {
        this.ma = ma;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_height, container, false);


        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(mSensorManager);

        mBtn_height = (ImageButton) root.findViewById(R.id.Btn_height);
        mBtn_calculate = (ImageButton) root.findViewById(R.id.Btn_calculate);
        mBtn_capture = (ImageButton) root.findViewById(R.id.Btn_capture);
        mBtn_calPlat = (ImageButton) root.findViewById(R.id.Btn_calPlat);
        mBtn_calDown = (ImageButton) root.findViewById(R.id.Btn_calDown);
        mBtn_calUp = (ImageButton) root.findViewById(R.id.Btn_calUp);


        mBtn_height.setOnClickListener(measureHeight);
        mBtn_calculate.setOnClickListener(getCalculateHeight);
        mBtn_capture.setOnClickListener(takeCapture);
        mBtn_calPlat.setOnClickListener(getCalculatePlatHeight);
        mBtn_calDown.setOnClickListener(getCalculateDownHeight);
        mBtn_calUp.setOnClickListener(getCalculateUpHeight);
        return root;
    }


    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    // 이렇게 하게 되면 "theta_vec"에 저장되는 값은
    // [0] 처음 측정했을 때의  f_theta
    // [1] xy_theta - x_theta를 한 y_theta 값
    // [2] y2_theta
    // ....
    // [N] angleYn == n번 측정했을 때의 y_theta (n_theta)

    //Button
    final ImageButton.OnClickListener measureHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View height) {

            mMySensorEventListener.updateOrientationAngles();
            if (!ma.mInputHeight.getText().toString().isEmpty()) {
                if (ma.angle_vec.isEmpty()) {
                    f_angle = Math.abs(mMySensorEventListener.getRoll());
                    ma.angle_vec.add(f_angle);
                    showToast(Integer.toString(ma.angle_vec.size()));
                    x_angle = 90 - ma.angle_vec.elementAt(0);
                } else {
                    t_angle = Math.abs(mMySensorEventListener.getRoll());
                    xy_angle = t_angle - ma.angle_vec.elementAt(0);
                    y_angle = Math.abs(xy_angle - x_angle);
                    ma.angle_vec.add(y_angle);
                    showToast(Integer.toString(ma.angle_vec.size()));
                }
            }
        }
    };

    /* theta_vec : 구간별 theta 벡터, dist_vec : 구간별 수고 벡터 */

    /**
     * 두번째 고도값 가져오기
     * if(calculate.getId() == R.id.Btn_calculate) {
     * float altitude2 = Math.abs(mMySensorEventListener.getAltitude());
     * ...
     * for(...) { //Up slope
     * h = altitude - altitude2;
     * d = h/ Math.tan(slope);
     * t_height = (Math.tan(angle + slope) * distance) - h;
     * }
     * for(...) { //down slope
     * h = altitude2;
     * d = h/Math.tan(slope);
     * t_height = (Math.tan(angle - slope) * distance) + h;
     * }
     * }
     */



    /*캡쳐*/
    final ImageButton.OnClickListener takeCapture = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View capture) {
            String mPath;

            try{
                SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
                String filename = "fistIMG_"+dateformat.format(System.currentTimeMillis());

                String dirPath = Environment.getExternalStorageDirectory().toString()+  "/FIST";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                mPath =  dirPath+"/" + filename + ".jpg";
                // create bitmap screen capture
                // 화면 이미지 만들기
                ArFragment af = ma.arFragment;


                ArSceneView view = af.getArSceneView();
                final Bitmap mybitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                final HandlerThread handlerThread = new HandlerThread("PixelCopier");
                handlerThread.start();

                PixelCopy.request(view, mybitmap, (copyResult) -> {
                    if (copyResult == PixelCopy.SUCCESS) {
                        try {
                            saveBitmapToDisk(mybitmap, mPath);
                        } catch (IOException e) {
                            return;
                        }
                    }
                    handlerThread.quitSafely();
                }, new Handler(handlerThread.getLooper()));
                Toast.makeText(ma, mPath, Toast.LENGTH_LONG).show();
            } catch(Throwable e){
                // Several error may come out with file handling or OOM
                e.printStackTrace();
            }



        }
    };


    public void saveBitmapToDisk(Bitmap bitmap, String path) throws IOException {

        //  String path = Environment.getExternalStorageDirectory().toString() +  "/Pictures/Screenshots/";

        Bitmap rotatedImage = bitmap;


        if(ma.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(90);
            rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotationMatrix, true);
        }

        File mediaFile = new File(path);

        FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
        rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);


        fileOutputStream.flush();
        fileOutputStream.close();


    }
    private boolean CheckWrite() {  // sdcard mount check
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }

    }



    final ImageButton.OnClickListener getCalculateHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calculate) {


            if (calculate.getId() == R.id.Btn_calculate) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_angle) * phoneHeight);
                compass = Math.abs(mMySensorEventListener.getYaw());
                compass = Math.round(compass);



                for (int i = 1; i < ma.angle_vec.size(); i++) {
                    if (ma.height_vec.isEmpty()) {
                        x_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        ma.height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        ma.height_vec.add(new_height);
                        t_height += new_height;
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_val=t_height;
                ma.mHeight_tv.setText("수        고 :" + totalHeightValue + "m");
                ma.mCompass_tv.setText("방        위 :"+compass+"°"
                        + mMySensorEventListener.matchDirection(compass));
                ma.mAltitude_tv.setText("고        도 :" + Integer.toString((int) altitude) + "m");

            }
        }
    };




    final ImageButton.OnClickListener getCalculatePlatHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calPlat) {

            if (calPlat.getId() == R.id.Btn_calPlat) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_angle) * phoneHeight);
                compass = Math.abs(mMySensorEventListener.getYaw());
                compass = Math.round(compass);
                for (int i = 1; i < ma.angle_vec.size(); i++) {
                    if (ma.height_vec.isEmpty()) {
                        x_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        ma.height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        ma.height_vec.add(new_height);
                        t_height += new_height;
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_val=t_height;
                ma.mHeight_tv.setText("수        고 :" + totalHeightValue + "m");
                ma.mCompass_tv.setText("방        위 :"+compass+"°"+mMySensorEventListener.matchDirection(compass));

            }
        }
    };




    final ImageButton.OnClickListener getCalculateDownHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calDown) {

            if (calDown.getId() == R.id.Btn_calPlat) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_angle) * phoneHeight);
                compass = Math.abs(mMySensorEventListener.getYaw());
                compass = Math.round(compass);
                for (int i = 1; i < ma.angle_vec.size(); i++) {
                    if (ma.height_vec.isEmpty()) {
                        x_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        ma.height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        ma.height_vec.add(new_height);
                        t_height += new_height;
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_val=t_height;
                ma.mHeight_tv.setText("수        고 :" + totalHeightValue + "m");
                ma.mCompass_tv.setText("방        위 :"+compass+"°"+mMySensorEventListener.matchDirection(compass));
            }
        }
    };




    final ImageButton.OnClickListener getCalculateUpHeight = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View calUp) {

            if (calUp.getId() == R.id.Btn_calPlat) {
                float phoneHeight = Float.valueOf(ma.mInputHeight.getText().toString()) /100f;
                float distance = (float) (Math.tan(x_angle) * phoneHeight);
                compass = Math.abs(mMySensorEventListener.getYaw());
                compass = Math.round(compass);
                for (int i = 1; i < ma.angle_vec.size(); i++) {
                    if (ma.height_vec.isEmpty()) {
                        x_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        ma.height_vec.add(x_height);
                        t_height += x_height;
                    } else {
                        double tmp_height = distance * Math.tan(ma.angle_vec.elementAt(i));
                        new_height = tmp_height - t_height;
                        ma.height_vec.add(new_height);
                        t_height += new_height;
                    }
                }
                t_height += phoneHeight;
                String totalHeightValue = String.format("%.1f", t_height);
                ma.mHeight_val=t_height;
                ma.mHeight_tv.setText("수        고 :" + totalHeightValue + "m");
                ma.mCompass_tv.setText("방        위 :"+compass+"°"+ mMySensorEventListener.matchDirection(compass));
            }
        }
    };





    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventListener);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        altitude = location.getAltitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}


