//package com.example.forestinventorysurverytools.ui.slope;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.RectF;
//import android.graphics.SurfaceTexture;
//import android.hardware.Sensor;
//import android.hardware.SensorManager;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.util.AttributeSet;
//import android.util.Size;
//import android.view.LayoutInflater;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.fragment.app.Fragment;
//
//import com.example.forestinventorysurverytools.CameraAPI;
//import com.example.forestinventorysurverytools.MySensorEventListener;
//import com.example.forestinventorysurverytools.R;
//import static android.content.Context.SENSOR_SERVICE;
//import static android.graphics.Color.TRANSPARENT;
//
//public class SlopeFragment extends Fragment implements CameraAPI.Camera2Interface,
//        TextureView.SurfaceTextureListener {
//
//    View root;
//    CameraAPI mSlopeCameraAPI;
//    TextureView mCameraPreview_slope;
//
//    SensorManager mSensorManager;
//    MySensorEventListener mMySensorEventListener;
//    Handler mCameraHandler;
//    HandlerThread mCameraThread;
//    SlopeIndicator mSlopeIndicator;
//
//    ImageButton mBtn_inclinometer;
//    TextView mDistance_tv;
//    TextView mDiameter_tv;
//    TextView mHeight_tv;
//    TextView mCompass_tv;
//    TextView mAltitude_tv;
//    TextView mInclinometer_tv;
//    EditText mInputHeight;
//
//    float angle;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.fragment_inclinometer, container, false);
//        mSlopeCameraAPI = new CameraAPI(this);
//        mCameraPreview_slope = (TextureView) root.findViewById(R.id.camera_preview);
//
//        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
//        mMySensorEventListener = new MySensorEventListener(mSensorManager);
//        mSlopeIndicator = (SlopeIndicator) root.findViewById(R.id.inclinometer_indicator);
//
//        mSlopeIndicator = new SlopeIndicator(getActivity().getApplicationContext());
//
//
//        mDistance_tv = (TextView) root.findViewById(R.id.tv_distance);
//        mDiameter_tv = (TextView) root.findViewById(R.id.tv_diameter);
//        mHeight_tv = (TextView) root.findViewById(R.id.tv_height);
//        mCompass_tv = (TextView) root.findViewById(R.id.tv_compass);
//        mAltitude_tv = (TextView) root.findViewById(R.id.tv_alititude);
//        mInclinometer_tv = (TextView) root.findViewById(R.id.tv_inclinometer);
//        mInputHeight = (EditText) root.findViewById(R.id.input_height);
//
//        mBtn_inclinometer.setOnClickListener(measureSlope);
//
//        return root;
//    }
//
//    //Toast
//    public void showToast(String data) {
//        Toast.makeText(root.getContext(), data, Toast.LENGTH_SHORT).show();
//    }
//
//    //Camera
//    private void openCamera() {
//        CameraManager cameraManager = mSlopeCameraAPI.cameraManager_1_D(this);
//        String cameraID = mSlopeCameraAPI.CameraCharacteristics_2(cameraManager);
//        mSlopeCameraAPI.CameraDevice_3_D(cameraManager, cameraID);
//        showToast("경사측정 기능 수행");
//    }
//
//    @Override
//    public void onCameraDeviceOpen(CameraDevice cameraDevice, Size cameraSize) {
//        SurfaceTexture surfaceTexture = mCameraPreview_slope.getSurfaceTexture();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            surfaceTexture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
//        }
//        Surface surface = new Surface(surfaceTexture);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSlopeCameraAPI.CaptureSession_4_D(cameraDevice, surface);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSlopeCameraAPI.CaptureSession_5(cameraDevice, surface);
//        }
//    }
//
//    private void closeCamera() {
//        mSlopeCameraAPI.closeCamera();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mCameraPreview_slope.isAvailable()) {
//            openCamera();
//        } else {
//            mCameraPreview_slope.setSurfaceTextureListener(this);
//        }
//        startCameraHandlerThread();
//        mSensorManager.registerListener(mMySensorEventListener,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);
//
//        mSensorManager.registerListener(mMySensorEventListener,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_FASTEST);
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        closeCamera();
//        mSensorManager.unregisterListener(mMySensorEventListener);
//    }
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        openCamera();
//    }
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//    }
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        return true;
//    }
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//    }
//
//    //Button
//    ImageButton.OnClickListener measureSlope = new ImageButton.OnClickListener() {
//        @Override
//        public void onClick(View slope) {
//            mMySensorEventListener.updateOrientationAngles();
//            if (slope.getId() == R.id.Btn_distance) {
//                angle = Math.abs(mMySensorEventListener.getRoll());
//                double slope_angle = angle * Math.PI / 180;
//                String slope_value = String.format("%.1f", slope_angle);
//                mDistance_tv.setText("경        사 :" + slope_value + "m");
//            }
//        }
//    };
//
//    //Handler
//    private void startCameraHandlerThread() {
//        mCameraThread = new HandlerThread("Camera Background");
//        mCameraThread.start();
//        mCameraHandler = new Handler(mCameraThread.getLooper());
//    }
//
//    private void stopCameraHandlerThread() {
//        mCameraThread.quitSafely();
//        mCameraThread = null;
//        mCameraHandler = null;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mMySensorEventListener.startListening((MySensorEventListener.Listener) this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mMySensorEventListener.stopListening();
//    }

//    @Override
//    public void onOrientationChanged(float clinoPitch, float clinoRoll) {
//        mSlopeIndicator.setSlope(clinoPitch, clinoRoll);
//    }
//}

//class SlopeIndicator extends View {
//
//    private static final boolean LOG_FPS = false;
//
//    private static final int MIN_PLANE_COLOR = Color.parseColor("#FF8F00");
//    private static final int BACK_GROUND_COLOR = Color.parseColor("#00FFFFFF");
//    private static final float TOTAL_VISIBLE_ROLL_DEGREES = 45 * 2;
//
//    private final PorterDuffXfermode mXfermode;
//    private final Paint mBitmapPaint;
//    private final Paint mRollLadderPaint;
//    private final Paint mHorizonPaint;
//    private final Paint mMinPlanePaint;
//    private final Paint mBottomRollLadderPaint;
//    private final Paint mBackGroundPaint;
//
//    private Bitmap mSrcBitmap;
//    private Bitmap mDstBitmap;
//    private Canvas mSrcCanvas;
//
//    private int mWidth;
//    private int mHeight;
//
//    private float mPitch = 0;
//    private float mRoll = 0;
//
//    public SlopeIndicator(Context context) {
//        this(context, null);
//    }
//
//    public SlopeIndicator(Context context, AttributeSet attributeSet) {
//        super(context, attributeSet);
//
//        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN); //소스픽셀 유지
//        mBitmapPaint = new Paint();
//        mBitmapPaint.setFilterBitmap(false);
//
//        mRollLadderPaint = new Paint();
//        mRollLadderPaint.setAntiAlias(true); //가장자리를 부드럽게
//        mRollLadderPaint.setColor(Color.WHITE);
//        mRollLadderPaint.setStrokeWidth(5); //너비 설정
//
//        mHorizonPaint = new Paint();
//        mHorizonPaint.setAntiAlias(true);
//        mHorizonPaint.setColor(Color.BLACK);
//        mHorizonPaint.setStrokeWidth(5);
//
//        mMinPlanePaint = new Paint();
//        mMinPlanePaint.setAntiAlias(true);
//        mMinPlanePaint.setColor(MIN_PLANE_COLOR);
//        mMinPlanePaint.setStrokeWidth(25);
//        mMinPlanePaint.setStyle(Paint.Style.STROKE);
//
//        mBottomRollLadderPaint = new Paint();
//        mBottomRollLadderPaint.setAntiAlias(true);
//        mBottomRollLadderPaint.setColor(Color.RED);
//        mBottomRollLadderPaint.setStrokeWidth(5);
//        mBottomRollLadderPaint.setAlpha(100);
//
//        mBackGroundPaint = new Paint();
//        mBackGroundPaint.setAntiAlias(true);
//        mBackGroundPaint.setColor(BACK_GROUND_COLOR);
//        mBackGroundPaint.setStyle(Paint.Style.STROKE);
//    }
//
//    public float getmPitch() {
//        return mPitch;
//    }
//    public float getmRoll(){
//        return mRoll;
//    }
//    public void setSlope(float pitch, float roll) {
//        mPitch = pitch;
//        mRoll = roll;
//        invalidate();
//    }
//
//    @Override
//    protected void onSizeChanged(int width, int height, int i, int i2) {
//        super.onSizeChanged(width, height, i, i2);
//        mWidth = width;
//        mHeight = height;
//    }
//
//    private Bitmap getmSrcBitmap() {
//        if (mSrcBitmap == null) {
//            mSrcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//            mSrcCanvas = new Canvas(mSrcBitmap);
//        }
//        Canvas canvas = mSrcCanvas;
//
//        float centerX = mWidth / 2;
//        float centerY = mHeight / 2;
//
//        //배경
//        canvas.drawColor(TRANSPARENT);
//
//        //후대폰이 회전하더라도 canvas는 고정되도록
//        canvas.save();
//
//        //canvas 그림이 Pitch와 Roll 각도를 반영하도록 작업
//        canvas.rotate(mRoll, centerX, centerY);
//        canvas.translate(0, (mPitch / TOTAL_VISIBLE_ROLL_DEGREES) * mHeight);
//        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mBackGroundPaint);
//
//
//        //수평선 및 수평선 위의 사다리 그리기
//        float ladderStepY = mHeight / 12; //사다리 간격설정
//        canvas.drawLine(-mHeight, centerY, mWidth * 2, centerY, mHorizonPaint);
//        for (int i = 1; 1 <= 4; i++) {
//            float y = centerY = ladderStepY * i;
//            float width = mWidth / 6;
//            canvas.drawLine(centerX - width / 2, y, centerX + width / 2, y, mRollLadderPaint);
//        }
//
////        //수평선 아래 사다리 그리기
////        float bottomLadderStepX = mWidth / 8;
////        float bottomLadderStepY = mWidth / 8;
////        for (int i = 1; i <= 3; i++) {
////            float y = centerY + bottomLadderStepY * i;
////            canvas.drawLine(centerX - bottomLadderStepX * i, y,
////                    centerX + bottomLadderStepX * i, y, mBottomRollLadderPaint);
////        }
////        canvas.restore();
////        canvas.drawPoint(centerX, centerY, mMinPlanePaint);
//
////        return mSrcBitmap;
//    }
//
//    private Bitmap getmDstBitmap() {
//        if (mDstBitmap == null) {
//            mDstBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(mDstBitmap);
//            canvas.drawColor(TRANSPARENT);
//
//            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setColor(Color.RED);
//            canvas.drawOval(new RectF(0, 0, mWidth, mHeight), paint);
//        }
//        return mDstBitmap;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (LOG_FPS) {
//            countFps();
//        }
//        Bitmap src = getmSrcBitmap();
//        Bitmap dst = getmDstBitmap();
//
//        int sc = saveLayer(canvas);
//        canvas.drawBitmap(src,0, 0, mBitmapPaint);
//        mBitmapPaint.setXfermode(null);
//        canvas.drawBitmap(dst, 0, 0, mBitmapPaint);
//        mBitmapPaint.setXfermode(mXfermode);
//
//        canvas.restoreToCount(sc);
//    }
//
//    private int saveLayer(Canvas canvas) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return canvas.saveLayer(0, 0, mWidth, mHeight, null);
//        } else {
//            return canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
//        }
//    }
//
//    private long frameCountStartedAt = 0;
//    private long getFrameCountStartedAt = 0;
//
//    private void countFps() {
//        getFrameCountStartedAt++;
//        if (frameCountStartedAt == 0) {
//            frameCountStartedAt = System.currentTimeMillis();
//        }
//        long elapsed = System.currentTimeMillis() - frameCountStartedAt;
//        if (elapsed >= 1000) {
////            Log.i("FPS:" + getFrameCountStartedAt);
//            getFrameCountStartedAt = 0;
//            frameCountStartedAt = System.currentTimeMillis();
//        }
//    }
//}

