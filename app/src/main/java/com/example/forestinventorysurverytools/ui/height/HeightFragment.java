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
import com.example.forestinventorysurverytools.Info;
import com.example.forestinventorysurverytools.MainActivity;
//import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
//import com.example.forestinventorysurverytools.ui.distance.DistanceFragment;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerIndicator;
//import com.example.forestinventorysurverytools.ui.inclinometer.InclinometerOrientation;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

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
    ImageButton mBtn_getHeight;
    ImageButton mBtn_capture;


    //Draw the inclinometer view
    WindowManager mWindowManager;
    HeightOrientation mHeightOrientation;
    HeightIndicator mHeightIndicator;


    //CheckBox
    CheckBox mSavePortraitScr;


    //Activity
    MainActivity ma = null;
    public HeightFragment(MainActivity ma) {
        this.ma = ma;
    }


    //Capture Data
    ArrayList<Renderable> tmpRend = new ArrayList<>();
    ArrayList<Renderable> h_tmpRend = new ArrayList<>();
    ArrayList<Renderable> b_tmpRend = new ArrayList<>();


    //Values
    float mRoll_1;
    float mRoll_2;
    float mGetRoll_1;
    float mGetRoll_2;
    int mClikc_count = 0;
    float mDx;
    float mDy;
    float mDz;
    float mDistance;
    int mSlopeAngle;
    int mSlopeValue;
    int mSlope1;
    int mSlope2;


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
        mBtn_getHeight = (ImageButton) root.findViewById(R.id.Btn_platHeight);
        mBtn_capture = (ImageButton) root.findViewById(R.id.Btn_capture);

        mBtn_measure.setOnClickListener(getHeightAngle);
        mBtn_getHeight.setOnClickListener(getHeightValues);
        mBtn_capture.setOnClickListener(takeCapture);


        //CheckBox

        mSavePortraitScr = (CheckBox) root.findViewById(R.id.savePortraitScreen);

        return root;
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
            if (!ma.mInputUH.getText().toString().isEmpty()) {
                if (mClikc_count % 2 == 0) {

                    ma.mDistMeter = ma.infoArray.get(ma.mTreeIndex).getDist();

                    Frame frame = ma.mArfragment.getArSceneView().getArFrame();
                    Pose objectPose = ma.mAnchor.getPose();
                    Pose cameraPose = frame.getCamera().getPose();

                    //Get the Anchor Pose
                    mDx = objectPose.tx() - cameraPose.tx();
                    mDy = objectPose.ty() - cameraPose.ty();
                    mDz = objectPose.tz() - cameraPose.tz();
                    mDistance = (float)Math.sqrt((mDx * mDx) + (mDy * mDy) + (mDz * mDz));
                    ma.mDistance_val = mDistance;
                    String distance = String.format("%.1f", mDistance);
                    ma.mDistance_tv.setText("거        리 : " + distance + "m");


                    //Get the roll1 angle
                    mRoll_1 = Math.abs(mMySensorEventListener.getRoll1());
                    mGetRoll_1 = (int)  Math.abs(90 - mRoll_1);
                    ma.showToast("초두부를 향해 클릭하세요.");

                    //Get the Slope
                    float slope = Math.abs(mMySensorEventListener.getSlope());
                    mSlope1 = (int) (90 - Math.toDegrees(slope));
                    mSlope2 = (int) Math.toRadians(mSlope1 * 100); //Height에 사용

                    mSlopeValue = (int) Math.abs(90 - Math.toDegrees(slope));
                    mSlopeAngle = (int) Math.toRadians(mSlopeValue * 100);
                    ma.mInclinometer_val = mSlopeAngle;
                    ma.infoArray.get(ma.mTreeIndex).setClino(mSlopeAngle);
                    ma.mInclinometer_tv.setText("경        사 : " + Integer.toString(mSlopeAngle) + "%");
                    mClikc_count ++;

                    //Get the roll2 angle
                } else if (mClikc_count % 2 == 1) {
                    mRoll_2 = Math.abs(mMySensorEventListener.getRoll2());
                    mGetRoll_2 = (int) Math.abs(90 - mRoll_2);
                    ma.showToast("계산 버튼을 클릭하세요.");
                    mClikc_count ++;
                }
            }
        }
    };


        //Calculate Height
        final ImageButton.OnClickListener getHeightValues = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.Btn_platHeight) {

                    SimpleDateFormat dateformat = new SimpleDateFormat("dd_HHmmss");
                    String idstr = dateformat.format(System.currentTimeMillis());

                    if (mSlope2 >= -5 && mSlope2 <= 5) {
                        float h = (float) Math.abs(Math.tan(mGetRoll_2 * (Math.PI / 180)) * mDistance) + (Float.valueOf(ma.mInputUH.getText().toString())/100);
                        String height = String.format("%.1f", h);
                        ma.mHeight_val = h;
                        ma.infoArray.get(ma.mTreeIndex).setHeight(h);
                        ma.mHeight_tv.setText("수        고 : " + height + "m");
                        ma.showToast("tan1: " + Float.toString((float) Math.tan(mGetRoll_1 * (Math.PI / 180))));
                        ma.showToast("tan2: " + Float.toString((float) Math.tan(mGetRoll_2 * (Math.PI / 180))));

                    } else if (mSlope2 >= 6) {
                        float hori_dist = (float) Math.abs(Math.cos(mGetRoll_1 * (Math.PI / 180)) * mDistance);
                        float total_h = (float) Math.abs((Math.tan(mGetRoll_2 * (Math.PI / 180))) * hori_dist);
//                        float x = total_h - (Float.valueOf(ma.mInputUH.getText().toString())/100);
//                        total_h = total_h - x;
                        ma.mHeight_val = total_h;
                        ma.infoArray.get(ma.mTreeIndex).setHeight(total_h);
                        String height = String.format("%.1f", total_h);
                        ma.mHeight_tv.setText("수        고 : " + height + "m");
                    }
                }
            }
        };


        //Capture
        final ImageButton.OnClickListener takeCapture = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View capture) {
                String mPath;
                ArFragment af = ma.mArfragment;
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
                    mPath = dirPath + "/" + filename + "_" + ma.infoArray.get(ma.mTreeIndex).getId() + ".jpg";
                    for (int i = 0; i < ma.infoArray.size(); i++) {
                        if(i==ma.mTreeIndex) {
                            tmpRend.add(null);
                            h_tmpRend.add(null);
                            b_tmpRend.add(null);
                        }else {
                            tmpRend.add(ma.infoArray.get(i).getDBHNode().getRenderable());
                            h_tmpRend.add(ma.infoArray.get(i).getUHNode().getRenderable());
                            b_tmpRend.add(ma.infoArray.get(i).getBotNode().getRenderable());
                            ma.infoArray.get(i).getDBHNode().setRenderable(null);
                            ma.infoArray.get(i).getUHNode().setRenderable(null);
                            ma.infoArray.get(i).getBotNode().setRenderable(null);
//                            ma.infoArray.get(i).getmText().setRenderable(null);
                        }
                    }

                    Handler mHandler = new Handler();

                    mHandler.postDelayed(new Runnable() {
                        public void run() {
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
                                if(i!=ma.mTreeIndex) {;
                                    ma.infoArray.get(i).getDBHNode().setRenderable(tmpRend.get(i));
                                    ma.infoArray.get(i).getUHNode().setRenderable(h_tmpRend.get(i));
                                    ma.infoArray.get(i).getBotNode().setRenderable(b_tmpRend.get(i));
//                            ma.infoArray.get(i).getmText().setRenderable(null);
                                }
                            }
                            tmpRend.clear();
                            h_tmpRend.clear();
                            b_tmpRend.clear();
                            /*
                            try {
                                view.getSession().update();
                            } catch (CameraNotAvailableException e) {
                                e.printStackTrace();
                            }
                            */
                        }
                    }, 600);



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
