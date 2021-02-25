package com.example.forestinventorysurverytools.ui.userheight;

import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.forestinventorysurverytools.Info;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
import com.example.forestinventorysurverytools.ui.height.HeightIndicator;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.TransformableNode;

import java.text.SimpleDateFormat;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class UserheightFragment extends Fragment implements Scene.OnUpdateListener{


    //View
    View root;


    //Sensor
    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;


    //Inclinometer Indicator
    HeightIndicator mHeightIndicator = null;
    SeekBar mSizeControl;


    //ImageButton
    ImageButton mUpButton;
    ImageButton mDownButton;
    ImageButton mSetUH;
    public Boolean mIsCreated = false;
    public TransformableNode mTransNode = null;


    //Values
    public float mUserHeight = 1.5f;
    public float mSize = 0.15f;


    //Extends Class
    MainActivity ma = null;
    public ModelRenderable mModelRender_UH;
    public UserheightFragment(MainActivity ma){this.ma=ma;}



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_userheight, null);

        mUpButton = (ImageButton)root.findViewById(R.id.userHeightUp);
        mDownButton=(ImageButton)root.findViewById(R.id.userHeightDown);
        mSetUH = (ImageButton) root.findViewById(R.id.check);
        mSetUH.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTransNode.setRenderable(null);
                mIsCreated =! mIsCreated;
                ma.mMain_UserHeight = mUserHeight;
                return false;
            }
        });


        mHeightIndicator = (HeightIndicator)root.findViewById(R.id.inclinometerInUserHeight);

        mUpButton.setOnTouchListener(moveUp);
        mDownButton.setOnTouchListener(moveDown);


        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(ma, mSensorManager);

        uhModel();
        ma.mArfragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            mMySensorEventListener.updateOrientationAngles();
            if (mModelRender_UH == null)
                return;


            if(!mIsCreated) {
                mIsCreated =! mIsCreated;
                ma.showToast("위, 아래 버튼을 활용하여 AR 객체를 빨간선에 맞추세요.");

                // Creating Anchor.
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(ma.mArfragment.getArSceneView().getScene());
                uhModel();

                // Create the transformable object and add it to the anchor.
                ma.mAnchor = anchor;
                ma.mAnchorNode = anchorNode;

                mTransNode = new TransformableNode(ma.mArfragment.getTransformationSystem());
                mTransNode.setRenderable(mModelRender_UH);
                mTransNode.setParent(anchorNode);

                ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
                ma.mArfragment.getArSceneView().getScene().addChild(anchorNode);

                //Get the Anchor distance to User and other value(Altitude, Compass. Diameter)
            }
        });
        return root;
    }


    //Control the uhModel
    ImageButton.OnTouchListener moveUp = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mUserHeight += 0.005f;
            uhModel();
            mTransNode.setRenderable(mModelRender_UH);
            ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
            ma.mInputUH.setText(Integer.toString((int) (mUserHeight * 100)));
            return false;
        }
    };
    ImageButton.OnTouchListener moveDown = new ImageButton.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mUserHeight -= 0.005f;
            uhModel();
            mTransNode.setRenderable(mModelRender_UH);
            ma.mArfragment.getArSceneView().getScene().addOnUpdateListener(ma.mArfragment);
            ma.mInputUH.setText(Integer.toString((int) ((mUserHeight * 100) + 3)));
            return false;
        }
    };


    //Set the UserHeight Model
    public void uhModel() {
        MaterialFactory.makeTransparentWithColor(ma, new Color(0.8f, 0.0f, 0.0f, 1.0f))
                .thenAccept(
                        material -> {
                            Vector3 vector3 = new Vector3(0.0f, mUserHeight, 0.0f);
                            mModelRender_UH = ShapeFactory.makeSphere(0.01f, vector3, material);
                            mModelRender_UH.setShadowReceiver(false);
                            mModelRender_UH.setShadowReceiver(false);
                            Boolean b = (mModelRender_UH == null);
                        });
    }


    @Override
    public void onUpdate(FrameTime frameTime) {
    }
}
