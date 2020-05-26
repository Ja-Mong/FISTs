package com.example.forestinventorysurverytools.ui.height;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;
//import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.FirstScreen;
import com.example.forestinventorysurverytools.MainActivity;
//import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerIndicator;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerOrientation;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class HeightFragment extends Fragment implements Scene.OnUpdateListener, HeightOrientation.Listener {


    //View
    View root;


    //Sensor
    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;


    //ImageButton
    ImageButton mBtn_measure;
    ImageButton mBtn_getHeight1;
    ImageButton mBtn_getHeight2;
    ImageButton mBtn_getHeight3;
    ImageButton mBtn_getHeight4;
    ImageButton mBtn_capture;


    //Draw the inclinometer view
    WindowManager mWindowManager;
    HeightOrientation mHeightOrientation;
    HeightIndicator mHeightIndicator;


    //CheckBox
    CheckBox mSavePortraitScr;
    CheckBox mSaveOriginImage;


    //Activity
    MainActivity ma = null;

    public HeightFragment(MainActivity ma) {
        this.ma = ma;
    }


    //Capture Data
    ArrayList<Renderable> tmpRend = new ArrayList<>();
    ArrayList<Renderable> h_tmpRend = new ArrayList<>();


    //Values
    float roll1;
    float getRoll1;
    float roll2;
    float getRoll2;
    int click_count = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_height, null);


        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mMySensorEventListener = new MySensorEventListener(ma, mSensorManager);


        //Draw
        mWindowManager = getActivity().getWindow().getWindowManager();
        mHeightOrientation = new HeightOrientation(ma);
        mHeightIndicator = (HeightIndicator) root.findViewById(R.id.inclinometer);
        mHeightIndicator.ma = this.ma;


        //ImageButton
        mBtn_measure = (ImageButton) root.findViewById(R.id.Btn_measure);
        mBtn_getHeight1 = (ImageButton) root.findViewById(R.id.Btn_platHeight);
        mBtn_getHeight2 = (ImageButton) root.findViewById(R.id.Btn_upHeight);
        mBtn_getHeight3 = (ImageButton) root.findViewById(R.id.Btn_down1Height);
        mBtn_getHeight4 = (ImageButton) root.findViewById(R.id.Btn_down2Height);
        mBtn_capture = (ImageButton) root.findViewById(R.id.Btn_capture);

        mBtn_measure.setOnClickListener(getHeightAngle);
        mBtn_getHeight1.setOnClickListener(getHeightValues1);
        mBtn_getHeight2.setOnClickListener(getHeightValues2);
        mBtn_getHeight3.setOnClickListener(getHeightValues3);
        mBtn_getHeight4.setOnClickListener(getHeightValues4);
        mBtn_capture.setOnClickListener(takeCapture);


        //CheckBox
        mSaveOriginImage = (CheckBox) root.findViewById(R.id.saveOriginImage);
        mSavePortraitScr = (CheckBox) root.findViewById(R.id.savePortraitScreen);

        return root;
    }


    //Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }


    //SensorListener
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(mMySensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventListener);
    }


    //Move the window
    @Override
    public void onStart() {
        super.onStart();
        mHeightOrientation.startListening(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHeightOrientation.stopListening();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {
        mHeightIndicator.setInclinometer(pitch, roll);
    }


    //ImageButton
    //Height
    final ImageButton.OnClickListener getHeightAngle = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View rollAngle1) {
            mMySensorEventListener.updateOrientationAngles();
            if (!ma.mInputHeight.getText().toString().isEmpty()) {
                if (click_count % 2 == 0) {

                    //Compute the straight-line distance.
                    ma.distanceMeters = (float) Math.sqrt(ma.dx * ma.dx + ma.dy * ma.dy + ma.dz * ma.dz);
                    String meter = String.format("%.1f", ma.distanceMeters);
                    ma.mDistance_tv.setText("거        리 : " + meter + "m");

                    //Get the roll1 angle
                    roll1 = Math.abs(mMySensorEventListener.getRoll());
                    showToast("초두부를 클릭하여주세요.");
                    getRoll1 = 90 - roll1;

                    //Get the Slope
                    int slopeValue = (int) Math.abs(90 - Math.toDegrees(roll1));
                    int slopeAngle = (int) Math.toRadians(slopeValue * 100);
                    ma.mInclinometer_val = slopeAngle;
                    ma.mInclinometer_tv.setText("경        사 :" + String.format(String.valueOf(ma.mInclinometer_val)) + "%");
                    click_count++;

                    //Get the roll2 angle
                } else if (click_count % 2 == 1) { //??
                    roll2 = Math.abs(mMySensorEventListener.getRoll());
                    getRoll2 = roll2 - 90;
                    showToast("상황에 맞는 계산 버튼을 클릭해주세요.");
                    click_count++;
                }
            }
        }
    };


        //Calculate Height depends on 4 case
        //Plat
        final ImageButton.OnClickListener getHeightValues1 = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View height1) {
                if (height1.getId() == R.id.Btn_platHeight) {
                    float h = (float) Math.abs((Math.tan(getRoll2) * ma.distanceMeters)); //+ (Float.parseFloat(ma.mInputHeight) / 100f));
                    ma.mHeight_val = h;
                    ma.mHeight_tv.setText("수        고 :" + String.format("%.1", String.valueOf(ma.mHeight_val)) + "m");
                }
            }
        };

        //UpSlope
        final ImageButton.OnClickListener getHeightValues2 = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View height2) {
                if (height2.getId() == R.id.Btn_upHeight) {
                    float length = (float) Math.abs(Math.cos(getRoll1) * ma.distanceMeters);
                    float y = (float) Math.abs((Math.tan(getRoll1) * length)); //- (Float.valueOf(String.valueOf(ma.mInputHeight)) / 100f));
                    float h = (float) Math.abs((Math.tan(getRoll2) * length) - y);
                    ma.mHeight_val = h;
                    ma.mHeight_tv.setText("수        고 :" + String.format("%.1", String.valueOf(ma.mHeight_val)) + "m");
                }
            }
        };

        //DownSlope1
        final ImageButton.OnClickListener getHeightValues3 = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View height3) {
                if (height3.getId() == R.id.Btn_down1Height) {
                    float length = (float) Math.abs(Math.cos(getRoll1) * ma.distanceMeters);
                    float y = (float) Math.abs(Math.tan(getRoll1) * length);
                    float h = (float) Math.abs((Math.tan(getRoll2) * length) + y); // + (Float.valueOf(String.valueOf(ma.mInputHeight)) / 100f));
                    ma.mHeight_val = h;
                    ma.mHeight_tv.setText("수        고 :" + String.format("%.1", String.valueOf(ma.mHeight_val)) + "m");
                }
            }
        };

        //DownSlope2
        final ImageButton.OnClickListener getHeightValues4 = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View height4) {
                if (height4.getId() == R.id.Btn_down2Height) {
                    float length = (float) Math.abs(Math.cos(getRoll1) * ma.distanceMeters);
                    float y = (float) Math.abs((Math.tan(getRoll1) * length));
                    float q = (float) Math.abs(Math.tan(getRoll2) * length);
                    float h = Math.abs(y - q); //+(Float.valueOf(String.valueOf(ma.mInputHeight)) / 100f));
                    ma.mHeight_val = h;
                    ma.mHeight_tv.setText("수        고 :" + String.format("%.1", String.valueOf(ma.mHeight_val)) + "m");
                }
            }
        };

        //Capture
        final ImageButton.OnClickListener takeCapture = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View capture) {
                String mPath;
                ArFragment af = ma.arFragment;
                ArSceneView view = af.getArSceneView();

                // AR이미지 포함한 사진
                try {
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
                    String filename = "FistIMG_" + dateformat.format(System.currentTimeMillis());

                    String dirPath = Environment.getExternalStorageDirectory().toString() + "/FIST";
                    File dir = new File(dirPath);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    mPath = dirPath + "/" + filename + "_" + ma.infoArray.get(ma.tree_id).getId() + ".jpg";

                    if (mSaveOriginImage.isChecked()) {
                        for (int i = 0; i < ma.infoArray.size(); i++) {
                            tmpRend.add(ma.infoArray.get(i).getNode().getRenderable());
                            h_tmpRend.add(ma.infoArray.get(i).getH_Node().getRenderable());
                            ma.infoArray.get(i).getNode().setRenderable(null);
                            ma.infoArray.get(i).getH_Node().setRenderable(null);
                        }
                        try {
                            view.getSession().update();
                        } catch (CameraNotAvailableException e) {
                            e.printStackTrace();
                        }

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

                        //AR 제외한 원본사진
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                SimpleDateFormat dateformat = new SimpleDateFormat("yyMMdd_HHmmss");
                                String filename = "FistIMG_" + dateformat.format(System.currentTimeMillis());
                                String dirPath = Environment.getExternalStorageDirectory().toString() + "/FIST";
                                String mPath = dirPath + "/" + filename + "_" + ma.infoArray.get(ma.tree_id).getId() + "_ori.jpg";

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
                        }, 300);

                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                for (int i = 0; i < ma.infoArray.size(); i++) {

                                    ma.infoArray.get(i).getNode().setRenderable(tmpRend.get(i));
                                    ma.infoArray.get(i).getH_Node().setRenderable(h_tmpRend.get(i));
                                }
                                tmpRend.clear();
                                h_tmpRend.clear();
                                try {
                                    view.getSession().update();
                                } catch (CameraNotAvailableException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, 600);

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
                } catch (Throwable e) {

                    // Several error may come out with file handling or OOM
                    e.printStackTrace();
                }
            }
        };


        //Image generate
        public void saveBitmapToDisk(Bitmap bitmap, String path) throws IOException {

            Bitmap rotatedImage = bitmap;

            if (mSavePortraitScr.isChecked()) {
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
    public void onUpdate(FrameTime frameTime) {

    }
}


        /*
        ma.initModel2();

        ma.arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            if (ma.modelRenderable2 == null)
                return;

            // Creating Anchor.
            Anchor anchor2 = hitResult.createAnchor();
            AnchorNode anchorNode2 = new AnchorNode(anchor2);
            anchorNode2.setParent(ma.arFragment.getArSceneView().getScene());

            // renewal of anchor
//            ma.clearAnchor();

            // Create the transformable object and add it to the anchor.
            ma.anchor = anchor2;
            ma.anchorNode = anchorNode2;
            node = new TransformableNode(ma.arFragment.getTransformationSystem());
            node.setRenderable(ma.modelRenderable2);
            node.setParent(anchorNode2);
            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);
            node.select();
        });

         */
