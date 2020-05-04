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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
//import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
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

    ImageButton mBtn_capture;

    CheckBox mSavePortraitScr;
    CheckBox mSaveOriginImage;

    double longitude;
    double latitude;
    double altitude;


    float compass;



    MainActivity ma = null;

    public HeightFragment(MainActivity ma) {
        this.ma = ma;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_height, container, false);


        mBtn_capture = (ImageButton) root.findViewById(R.id.Btn_capture);



        mSaveOriginImage = (CheckBox)root.findViewById(R.id.saveOriginImage);
        mSavePortraitScr = (CheckBox)root.findViewById(R.id.savePortraitScreen);


        mBtn_capture.setOnClickListener(takeCapture);

        return root;
    }


    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }



    /*캡쳐*/
    final ImageButton.OnClickListener takeCapture = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View capture) {
            String mPath;

            ArFragment af = ma.arFragment;
            ArSceneView view = af.getArSceneView();


            try {
                SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
                String filename = "FistIMG_" + dateformat.format(System.currentTimeMillis());

                String dirPath = Environment.getExternalStorageDirectory().toString() + "/FIST";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                mPath = dirPath + "/" + filename + ".jpg";
                // create bitmap screen capture
                // 화면 이미지 만들기




                if (mSaveOriginImage.isChecked()) {


                    view.getSession().pause();
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

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable()  {
                        public void run() {
                            SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
                            String filename = "FistIMG_" + dateformat.format(System.currentTimeMillis());
                            String dirPath = Environment.getExternalStorageDirectory().toString() + "/FIST";
                            String mPath = dirPath + "/" + filename + ".jpg";

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
                        }
                    }, 200);

                    view.getSession().resume();
                } else {

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
                }
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


        if (ma.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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

