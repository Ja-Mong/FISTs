package com.example.forestinventorysurverytools.ui.distance;



import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.MainActivity;
import com.example.forestinventorysurverytools.MySensorEventListener;
import com.example.forestinventorysurverytools.R;
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
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import android.util.Log;

import java.util.Objects;

public class DistanceFragment extends Fragment implements  Scene.OnUpdateListener {

    View root;
    CameraAPI mDistCameraAPI;
    TextureView mCameraPreview_dist;

    SensorManager mSensorManager;
    MySensorEventListener mMySensorEventListener;

    Handler mCameraHandler;
    HandlerThread mCameraThread;

    ImageButton mBtn_distance;

    float angle;

    MainActivity ma=null;
    public DistanceFragment(MainActivity ma){this.ma=ma;}

    ArFragment distance_arFragment;
    Anchor anchor = null;
    AnchorNode anchorNode;
    ModelRenderable modelRenderable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //AR 지원 가능 여부 체크
        if (!checkIsSupportedDeviceOrFinish((MainActivity)getActivity())) {
            Toast.makeText(ma.getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
        }

        root = inflater.inflate(R.layout.fragment_distance, null);

        //layout에 정의한 아이디 구현 및 정의
        FragmentManager fm = getChildFragmentManager();
       distance_arFragment = (ArFragment) fm.findFragmentById(R.id.camera_preview_fr);


        initModel();

        distance_arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {


            if (modelRenderable == null)
                return;

            // Creating Anchor.
            Anchor anchor2 = hitResult.createAnchor();
            AnchorNode anchorNode2 = new AnchorNode(anchor2);

            anchorNode2.setParent(distance_arFragment.getArSceneView().getScene());

            clearAnchor();

            anchor = anchor2;
            anchorNode = anchorNode2;

            TransformableNode node = new TransformableNode(distance_arFragment.getTransformationSystem());
            node.setRenderable(modelRenderable);
            node.setParent(anchorNode2);
            distance_arFragment.getArSceneView().getScene().addOnUpdateListener(distance_arFragment);
            distance_arFragment.getArSceneView().getScene().addChild(anchorNode2);
            node.select();



            if (anchorNode != null) {
                Frame frame = distance_arFragment.getArSceneView().getArFrame();

                Pose objectPose = anchor.getPose();
                Pose cameraPose = frame.getCamera().getPose();

                float dx = objectPose.tx() - cameraPose.tx();
                float dy = objectPose.ty() - cameraPose.ty();
                float dz = objectPose.tz() - cameraPose.tz();

                ///Compute the straight-line distance.
                float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                String meter = String.format("%.2f", distanceMeters);;
                ma.mDistance_tv.setText("거        리 : " + meter+"m");
                Toast.makeText(ma.getApplicationContext(), meter, Toast.LENGTH_LONG).show();
            }
        });



        return root;
    }

    public boolean checkIsSupportedDeviceOrFinish(final Activity activity) {

        String openGlVersionString =
                ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < 3.0) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }


    public void initModel() {
        MaterialFactory.makeTransparentWithColor(this.getContext(), new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3(0.05f, 0.01f, 0.01f);
                            modelRenderable = ShapeFactory.makeCube(vector3, Vector3.zero(), material);
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                            Boolean b  = (modelRenderable==null);

                        });
    }
    private void clearAnchor() {
        anchor = null;


        if (anchorNode != null) {
            distance_arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
            anchorNode = null;
        }
    }




    // Toast
    public void showToast(String data) {
        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onUpdate(FrameTime frameTime) {
        // 실시간으로 거리 측정해주는것 같지만... 아직 동작은 안됨. (위에 setonTap으로 옮겨놨음.)
        Frame frame = distance_arFragment.getArSceneView().getArFrame();

        Log.d("API123", "onUpdateframe... current anchor node " + (anchorNode == null));

        // 거리값 계산=> 데이터 크기 때문에 float으로 한듯???
        if (anchorNode != null) {
            Pose objectPose = anchor.getPose();
            Pose cameraPose = frame.getCamera().getPose();

            float dx = objectPose.tx() - cameraPose.tx();
            float dy = objectPose.ty() - cameraPose.ty();
            float dz = objectPose.tz() - cameraPose.tz();

            ///Compute the straight-line distance.
            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            String meter=String.format("%.2f",distanceMeters);;

            ma.mDistance_tv.setText("거리: " + meter);
            Toast.makeText(ma.getApplicationContext(),meter,Toast.LENGTH_LONG).show();

            //float[] distance_vector = currentAnchor.getPose().inverse()
            //        .compose(cameraPose).getTranslation();
            //float totalDistanceSquared = 0;
            //for (int i = 0; i < 3; ++i)
            //    totalDistanceSquared += distance_vector[i] * distance_vector[i];
        }
    }




}