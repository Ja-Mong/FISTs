package com.example.forestinventorysurverytools.ui.height;

import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.forestinventorysurverytools.CameraAPI;
import com.example.forestinventorysurverytools.R;



public class HeightFragment extends Fragment implements CameraAPI.Camera2Interface, TextureView.SurfaceTextureListener{
    View root;
    CameraAPI mHeightCameraAPI;
    TextureView mCameraPreview_height;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_height, container, false);
        mHeightCameraAPI = new CameraAPI(this);
        mCameraPreview_height = (TextureView) root.findViewById(R.id.camera_preview);

        return root;
    }


    // Toast
    public void showToast(String data){
        Toast.makeText(root.getContext(),data, Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        CameraManager cameraManager = mHeightCameraAPI.cameraManager_1_H(this);
        String cameraID = mHeightCameraAPI.CameraCharacteristics_2(cameraManager);
        mHeightCameraAPI.CameraDevice_3_H(cameraManager, cameraID);
        showToast("수고 기능 수행");
    }


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
        SurfaceTexture surfaceTexture = mCameraPreview_height.getSurfaceTexture();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(),cameraSize.getHeight());
        }
        Surface surface = new Surface(surfaceTexture);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mHeightCameraAPI.CaptureSession_4_H(cameraDevice, surface);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mHeightCameraAPI.CaptureSession_5(cameraDevice,surface);
        }
    }

    private void closeCamera(){
        mHeightCameraAPI.closeCamera();
    }

    @Override
    public void onPause() {
        closeCamera();
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mCameraPreview_height.isAvailable()){
            openCamera();
        }else{
            mCameraPreview_height.setSurfaceTextureListener(this);
        }
    }
}

