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
    MainActivity ma;
    //Sensor
    SensorManager mSensorManager;
    LocationManager mLocationManager;
    MySensorEventListener mMySensorEventListener;

    ImageButton upButton;
    ImageButton downButton;
    ImageButton check;
    public Boolean isCreated = false;
    public TransformableNode tfn =null;

    HeightIndicator hi= null;
    SeekBar sizeBar;


    public float userHeigth=1.2f;
    public float size = 0.15f;

    public ModelRenderable modelRenderable_userHeight;
    public UserheightFragment(MainActivity ma){this.ma=ma;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_userheight, null);

        upButton = (ImageButton)root.findViewById(R.id.userHeightUp);
        downButton=(ImageButton)root.findViewById(R.id.userHeightDown);
        check = (ImageButton) root.findViewById(R.id.check);
        check.setOnTouchListener(new ImageButton.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(ma,"측정높이는 "+Float.toString(userHeigth*100)+"cm 입니다.",Toast.LENGTH_LONG).show();
                tfn.setRenderable(null);
                isCreated=!isCreated;
                ma.main_userHeight=userHeigth;
                return false;
            }
        });
        hi = (HeightIndicator)root.findViewById(R.id.inclinometerInUserHeight);



        upButton.setOnTouchListener(moveUp);
        downButton.setOnTouchListener(moveDown);
        //Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mMySensorEventListener = new MySensorEventListener(ma, mSensorManager);

        initModel_userHeight();
        ma.arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            mMySensorEventListener.updateOrientationAngles();
            if (modelRenderable_userHeight == null)
                return;


            if(!isCreated) {
                isCreated=!isCreated;
                Toast.makeText(ma, "위, 아래 버튼을 활용하여 AR 객체를 빨간선에 맞추세요.", Toast.LENGTH_SHORT).show();
                // Creating Anchor.
                Anchor anchor2 = hitResult.createAnchor();
                AnchorNode anchorNode2 = new AnchorNode(anchor2);

                anchorNode2.setParent(ma.arFragment.getArSceneView().getScene());

                initModel_userHeight();

                // Create the transformable object and add it to the anchor.
                ma.anchor = anchor2;
                ma.anchorNode = anchorNode2;


                tfn = new TransformableNode(ma.arFragment.getTransformationSystem());
                tfn.setRenderable(modelRenderable_userHeight);
                tfn.setParent(anchorNode2);

                ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
                ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);

                //Get the Anchor distance to User and other value(Altitude, Compass. Diameter)
            }
        });
        return root;
    }

    ImageButton.OnTouchListener moveUp = new ImageButton.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userHeigth+=0.005f;
            initModel_userHeight();
            tfn.setRenderable(modelRenderable_userHeight);
            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.mInputHeight.setText(Float.toString(userHeigth*100)+"cm");
            return false;
        }
    };
    ImageButton.OnTouchListener moveDown = new ImageButton.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userHeigth-=0.005f;
            initModel_userHeight();
            tfn.setRenderable(modelRenderable_userHeight);
            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
            ma.mInputHeight.setText(Float.toString(userHeigth*100)+"cm");
            return false;
        }
    };

    //AR model 4 = userHeightModel
    public void initModel_userHeight() {
        MaterialFactory.makeTransparentWithColor(ma, new Color(0.8f, 0.0f, 0.0f, 1.0f))
                .thenAccept(
                        material -> {
                            modelRenderable_userHeight = ShapeFactory.makeCube(new Vector3(0.3f,0.01f,0.01f),
                                    new Vector3(0, userHeigth, 0),material);
                            modelRenderable_userHeight.setShadowReceiver(false);
                            modelRenderable_userHeight.setShadowReceiver(false);
                            Boolean b = (modelRenderable_userHeight == null);
                        });
    }


    @Override
    public void onUpdate(FrameTime frameTime) {

    }
}
