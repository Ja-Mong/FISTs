package com.example.forestinventorysurverytools;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Collections;

//public class CameraAPI extends Fragment {
//    public interface Camera2Interface {
//        void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize);
//    }
//
//    public Camera2Interface mCamera2Interface_D;
//    public Camera2Interface mCamera2Interface_DBH;
//    public Camera2Interface mCamera2Interface_H;
//
//    Size mSize_D;
//    Size mSize_DBH;
//    Size mSize_H;
//
//    CameraCaptureSession mCameraCaptureSession_D;
//    CameraCaptureSession mCameraCaptureSession_DBH;
//    CameraCaptureSession mCameraCaptureSession_H;
//
//    CameraDevice mCameraDevice_D;
//    CameraDevice mCameraDevice_DBH;
//    CameraDevice mCameraDevice_H;
//
//    CaptureRequest.Builder mPreviewRequestBuilder_D;
//    CaptureRequest.Builder mPreviewRequestBuilder_DBH;
//    CaptureRequest.Builder mPreviewRequestBuilder_H;
//
//
//
//
//    public CameraAPI(Camera2Interface camera2Interface) {
//        mCamera2Interface_D = camera2Interface;
//        mCamera2Interface_DBH = camera2Interface;
//        mCamera2Interface_H = camera2Interface;
//    }
//
//
//
//    public void transformImage(TextureView tv, int width, int height)
//    {
//
//        if (tv == null) {
//            return;
//        } else try {
//            {
//                Matrix matrix = new Matrix();
//                int rotation = Surface.ROTATION_90;
//                RectF textureRectF = new RectF(0, 0, width, height);
//                RectF previewRectF = new RectF(0, 0, tv.getHeight(), tv.getWidth());
//                float centerX = textureRectF.centerX();
//                float centerY = textureRectF.centerY();
//                if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
//                    previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
//                    matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
//                    float scale = Math.max((float) width / width, (float) height / width);
//                    matrix.postScale(scale, scale, centerX, centerY);
//                    matrix.postRotate(90 * (rotation - 2), centerX, centerY);
//                }
//                tv.setTransform(matrix);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//    }
//    public CameraManager cameraManager_1_D(Fragment mDistanceCameraAPI) {
//        CameraManager cameraManager_D = (CameraManager)mDistanceCameraAPI.getActivity().getSystemService(Context.CAMERA_SERVICE);
//        return cameraManager_D;
//    }
//
//
//    public CameraManager cameraManager_1_DBH(Fragment mDiameterCameraAPI) {
//        CameraManager cameraManager_DBH = (CameraManager)mDiameterCameraAPI.getActivity().getSystemService(Context.CAMERA_SERVICE);
//        return cameraManager_DBH;
//    }
//
//
//    public CameraManager cameraManager_1_H(Fragment mHeightCameraAPI) {
//        CameraManager cameraManager_H = (CameraManager)mHeightCameraAPI.getActivity().getSystemService(Context.CAMERA_SERVICE);
//        return cameraManager_H;
//    }
//
//
//
//
//    public String CameraCharacteristics_2(CameraManager cameraManager) {
//        try {
//            for (String cameraID : cameraManager.getCameraIdList()) {
//                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
//                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
//                    StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                    Size[] sizes = configurationMap.getOutputSizes(SurfaceTexture.class);
//                    mSize_D = sizes[0];
//                    mSize_DBH = sizes[0];
//                    mSize_H = sizes[0];
//                    for (Size size : sizes) {
//                        if (size.getWidth() > mSize_D.getWidth()
//                                && size.getWidth() > mSize_DBH.getWidth()
//                                && size.getWidth() > mSize_H.getWidth()) {
//                            mSize_D = size;
//                            mSize_DBH = size;
//                            mSize_H = size;
//                        }
//                    }
//                    return cameraID;
//                }
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//
//    private CameraDevice.StateCallback mCameraDeviceStateCallback_D = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(@NonNull CameraDevice camera_D) {
//            mCameraDevice_D = camera_D;
//            mCamera2Interface_D.onCameraDeviceOpen(camera_D, mSize_D);
//        }
//
//        @Override
//        public void onDisconnected(@NonNull CameraDevice camera_D) {
//            camera_D.close();
//        }
//
//        @Override
//        public void onError(@NonNull CameraDevice camera_D, int error) {
//            camera_D.close();
//        }
//
//
//    };
//
//
//
//    private CameraDevice.StateCallback mCameraDeviceStateCallback_DBH = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(@NonNull CameraDevice camera_DBH) {
//            mCameraDevice_DBH = camera_DBH;
//            mCamera2Interface_DBH.onCameraDeviceOpen(camera_DBH, mSize_DBH);
//        }
//        @Override
//        public void onDisconnected(@NonNull CameraDevice camera_DBH) {
//            camera_DBH.close();
//        }
//        @Override
//        public void onError(@NonNull CameraDevice camera_DBH, int error) {
//            camera_DBH.close();
//        }
//    };
//
//
//
//    private CameraDevice.StateCallback mCameraDeviceStateCallback_H = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(@NonNull CameraDevice camera_H) {
//            mCameraDevice_H = camera_H;
//            mCamera2Interface_H.onCameraDeviceOpen(camera_H, mSize_H);
//        }
//        @Override
//        public void onDisconnected(@NonNull CameraDevice camera_H) {
//            camera_H.close();
//        }
//        @Override
//        public void onError(@NonNull CameraDevice camera_H, int error) {
//            camera_H.close();
//        }
//    };
//
//
//
//    public void CameraDevice_3_D(CameraManager cameraManager, String cameraId) {
//        try {
//            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback_D, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public void CameraDevice_3_DBH(CameraManager cameraManager, String cameraId) {
//        try {
//            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback_DBH, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public void CameraDevice_3_H(CameraManager cameraManager, String cameraId) {
//        try {
//            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback_H, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private CameraCaptureSession.CaptureCallback mCaptureCallback_D = new CameraCaptureSession.CaptureCallback() {
//        @Override
//        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
//            super.onCaptureProgressed(session, request, partialResult);
//        }
//
//        @Override
//        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
//            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
//        }
//    };
//
//
//
//    private CameraCaptureSession.CaptureCallback mCaptureCallback_DBH = new CameraCaptureSession.CaptureCallback() {
//        @Override
//        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
//            super.onCaptureProgressed(session, request, partialResult);
//        }
//
//        @Override
//        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
//            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
//        }
//    };
//
//
//
//    private CameraCaptureSession.CaptureCallback mCaptureCallback_H = new CameraCaptureSession.CaptureCallback() {
//        @Override
//        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
//            super.onCaptureProgressed(session, request, partialResult);
//        }
//
//        @Override
//        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
//            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
//        }
//    };
//
//
//
//    private CameraCaptureSession.StateCallback mCaptureSessionCallback_D = new CameraCaptureSession.StateCallback() {
//        @Override
//        public void onConfigured(@NonNull CameraCaptureSession session_D) {
//            try {
//                mCameraCaptureSession_D = session_D;
//                mPreviewRequestBuilder_D.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                session_D.setRepeatingRequest(mPreviewRequestBuilder_D.build(), mCaptureCallback_D, null);
//
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
//    };
//
//
//
//    private CameraCaptureSession.StateCallback mCaptureSessionCallback_DBH = new CameraCaptureSession.StateCallback() {
//        @Override
//        public void onConfigured(@NonNull CameraCaptureSession session_DBH) {
//            try {
//                mCameraCaptureSession_DBH = session_DBH;
//                mPreviewRequestBuilder_DBH.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                session_DBH.setRepeatingRequest(mPreviewRequestBuilder_DBH.build(), mCaptureCallback_DBH, null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
//    };
//
//
//
//    private CameraCaptureSession.StateCallback mCaptureSessionCallback_H = new CameraCaptureSession.StateCallback() {
//        @Override
//        public void onConfigured(@NonNull CameraCaptureSession session_H) {
//            try {
//                mCameraCaptureSession_H = session_H;
//                mPreviewRequestBuilder_H.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                session_H.setRepeatingRequest(mPreviewRequestBuilder_H.build(), mCaptureCallback_H, null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
//    };
//
//
//
//    public void CaptureSession_4_D(CameraDevice cameraDevice, Surface surface) {
//        try {
//            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback_D, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void CaptureSession_4_DBH(CameraDevice cameraDevice, Surface surface) {
//        try {
//            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback_DBH, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public void CaptureSession_4_H(CameraDevice cameraDevice, Surface surface) {
//        try {
//            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback_H, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public void CaptureSession_5(CameraDevice cameraDevice, Surface surface) {
//        try {
//            mPreviewRequestBuilder_D = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mPreviewRequestBuilder_D.addTarget(surface);
//
//            mPreviewRequestBuilder_DBH = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mPreviewRequestBuilder_DBH.addTarget(surface);
//
//            mPreviewRequestBuilder_H = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mPreviewRequestBuilder_H.addTarget(surface);
//
//        } catch (CameraAccessException cae) {
//            cae.printStackTrace();
//        }
//    }
//
//
//
//    public void closeCamera() {
//        if (null != mCameraCaptureSession_D) {
//            mCameraCaptureSession_D.close();
//            mCameraCaptureSession_D = null;
//        }
//
//        if (null != mCameraCaptureSession_DBH) {
//            mCameraCaptureSession_DBH.close();
//            mCameraCaptureSession_DBH = null;
//        }
//
//        if (null != mCameraCaptureSession_H) {
//            mCameraCaptureSession_H.close();
//            mCameraCaptureSession_H = null;
//        }
//
//        if (null != mCameraDevice_D) {
//            mCameraDevice_D.close();
//            mCameraDevice_D = null;
//        }
//
//        if (null != mCameraDevice_DBH) {
//            mCameraDevice_DBH.close();
//            mCameraDevice_D = null;
//        }
//
//        if (null != mCameraDevice_H) {
//            mCameraDevice_H.close();
//            mCameraDevice_H = null;
//        }
//    }
//}
