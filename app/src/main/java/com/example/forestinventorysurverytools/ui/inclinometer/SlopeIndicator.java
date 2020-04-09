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
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.View;
//
//import androidx.fragment.app.Fragment;
//
//import static android.graphics.Color.TRANSPARENT;
//
//public class SlopeIndicator extends View {
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
