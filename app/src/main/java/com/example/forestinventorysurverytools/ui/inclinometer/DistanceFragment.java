//package com.example.forestinventorysurverytools.ui.distance;
//
//
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.hardware.SensorManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.view.LayoutInflater;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.forestinventorysurverytools.CameraAPI;
//import com.example.forestinventorysurverytools.MainActivity;
//import com.example.forestinventorysurverytools.R;
//import com.google.ar.core.Anchor;
//import com.google.ar.core.Frame;
//import com.google.ar.core.HitResult;
//import com.google.ar.core.Pose;
//import com.google.ar.sceneform.AnchorNode;
//import com.google.ar.sceneform.FrameTime;
//import com.google.ar.sceneform.Scene;
//import com.google.ar.sceneform.math.Vector3;
//import com.google.ar.sceneform.rendering.Color;
//import com.google.ar.sceneform.rendering.MaterialFactory;
//import com.google.ar.sceneform.rendering.ModelRenderable;
//import com.google.ar.sceneform.rendering.ShapeFactory;
//import com.google.ar.sceneform.ux.ArFragment;
//import com.google.ar.sceneform.ux.TransformableNode;
//
//import android.util.Log;
//
//import java.util.Objects;
//import java.util.Vector;
//
//import static android.content.Context.LOCATION_SERVICE;
//
//public class DistanceFragment extends Fragment implements LocationListener, Scene.OnUpdateListener {
//
//    View root;
//
//    LocationManager mLocationManager;
//
//
//    double longitude;
//    double latitude;
//    double altitude;
//
//    MainActivity ma = null;
//    public DistanceFragment(MainActivity ma) {
//        this.ma = ma;
//    }
//    public TransformableNode node;
//
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//
//        root = inflater.inflate(R.layout.fragment_distance, null);
//
//        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//
//
//
//        ma.initModel();
//
//
//        ma.arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
//
//
//            if (ma.modelRenderable == null)
//                return;
//
//            // Creating Anchor.
//            Anchor anchor2 = hitResult.createAnchor();
//            AnchorNode anchorNode2 = new AnchorNode(anchor2);
//
//            anchorNode2.setParent(ma.arFragment.getArSceneView().getScene());
//
//            ma.clearAnchor();
//
//            ma.anchor = anchor2;
//            ma.anchorNode = anchorNode2;
//
//            node = new TransformableNode(ma.arFragment.getTransformationSystem());
//            node.setRenderable(ma.modelRenderable);
//            node.setParent(anchorNode2);
//            ma.arFragment.getArSceneView().getScene().addOnUpdateListener(ma.arFragment);
//            ma.arFragment.getArSceneView().getScene().addChild(anchorNode2);
//            node.select();
//
//
//            if (ma.anchorNode != null) {
//                Frame frame = ma.arFragment.getArSceneView().getArFrame();
//
//                Pose objectPose = ma.anchor.getPose();
//                Pose cameraPose = frame.getCamera().getPose();
//
//                float dx = objectPose.tx() - cameraPose.tx();
//                float dy = objectPose.ty() - cameraPose.ty();
//                float dz = objectPose.tz() - cameraPose.tz();
//
//                ///Compute the straight-line distance.
//                float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
//                String meter = String.format("%.2f", distanceMeters);
//
//                ma.mDistance_tv.setText("거        리 : " + meter + "m");
//                Toast.makeText(ma.getApplicationContext(), meter, Toast.LENGTH_LONG).show();
//
//                if (ma.altitude_vec.isEmpty()) {
//                    ma.altitude_vec.add(altitude);
//
//                }
//            }
//        });
//
//
//
//        return root;
//    }
//
//
//
//
//    ImageButton.OnClickListener getDistanceValue = new ImageButton.OnClickListener() {
//        @Override
//        public void onClick(View distance) {
//            if (ma.modelRenderable == null) {
//                return;
//
//
//
//
//            }
//        }
//    };
//
//
//
//
//
//    // Toast
//    public void showToast(String data) {
//        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
//    }
//
//
//    //Altitude
//    @Override
//    public void onLocationChanged(Location location) {
//        longitude = location.getLongitude();
//        latitude = location.getLatitude();
//        altitude = location.getAltitude();
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    @Override
//    public void onUpdate(FrameTime frameTime) {
//        // 실시간으로 거리 측정해주는것 같지만... 아직 동작은 안됨. (위에 setonTap으로 옮겨놨음.)
//        Frame frame = ma.arFragment.getArSceneView().getArFrame();
//
//        Log.d("API123", "onUpdateframe... current anchor node " + (ma.anchorNode == null));
//
//        // 거리값 계산=> 데이터 크기 때문에 float으로 한듯???
//        if (ma.anchorNode != null) {
//            Pose objectPose = ma.anchor.getPose();
//            Pose cameraPose = frame.getCamera().getPose();
//
//            float dx = objectPose.tx() - cameraPose.tx();
//            float dy = objectPose.ty() - cameraPose.ty();
//            float dz = objectPose.tz() - cameraPose.tz();
//
//            ///Compute the straight-line distance.
//            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
//            String meter=String.format("%.2f",distanceMeters);;
//
//            ma.mDistance_tv.setText("거리: " + meter);
//            Toast.makeText(ma.getApplicationContext(),meter,Toast.LENGTH_LONG).show();
//
//       }
//    }
//}