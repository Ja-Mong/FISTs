package com.example.forestinventorysurverytools.ui.diameter;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.R;

public class DiameterFragment extends Fragment implements CameraAPI.Camera2Interface, TextureView.SurfaceTextureListener{

    ImageView focusImage;
    ImageButton mBtn_diameter;

    /*추가한 변수*/
    CameraAPI mDiamCameraAPI;
    TextureView mCameraPreview_diam;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_diameter, container, false);

        focusImage = (ImageView)root.findViewById(R.id.focus);
        mBtn_diameter = (ImageButton)root.findViewById(R.id.Btn_diameter);

        mDiamCameraAPI = new CameraAPI(this);
        mCameraPreview_diam = (TextureView)root.findViewById(R.id.camera_preview);

        return root;
    }

    public void showToast(String data){
        Toast.makeText(root.getContext(),data, Toast.LENGTH_SHORT).show();
    } // Toast 메세지 사용 간단하게 만듦

    private void openCamera() {

        CameraManager cameraManager = mDiamCameraAPI.cameraManager_1_DBH(this);
        String cameraID = mDiamCameraAPI.CameraCharacteristics_2(cameraManager);
        mDiamCameraAPI.CameraDevice_3_DBH(cameraManager, cameraID);
        showToast("흉고직경 기능 수행");
    }

    /*
     * Surface Callbacks
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture surfaceTexture = mCameraPreview_diam.getSurfaceTexture();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(),cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mDiamCameraAPI.CaptureSession_4_DBH(cameraDevice, surface);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mDiamCameraAPI.CaptureSession_5(cameraDevice,surface);
        }
    }

    private void closeCamera(){
        mDiamCameraAPI.closeCamera();
    }

    /*재정의*/
    @Override
    public void onPause() {
        closeCamera();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCameraPreview_diam.isAvailable()){
            openCamera();
        }else{
            mCameraPreview_diam.setSurfaceTextureListener(this);
        }
    }
}
