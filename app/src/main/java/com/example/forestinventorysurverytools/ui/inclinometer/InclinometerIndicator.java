package com.example.forestinventorysurverytools.ui.inclinometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InclinometerIndicator extends View {

    public static final boolean LOG_FPS = false;

//    public static final int POINT_COLOR = Color.parseColor("#FF8F00");
    public static final int ROLL_DEGREES = 45*2;

    PorterDuffXfermode mPorterDuffXfermode;

    Paint mBitmapPaint;
    Paint mRollLadderPaint;
    Paint mPointColorPaint;
    Paint mHorizon;
    Paint mBottomRollLadderPaint;

    Bitmap mSrcBitmap;
    Bitmap mDstBitmap;
    Canvas mSrcCanvas;

    int mWidth;
    int mHeight;

    float mPitch = 0;
    float mRoll = 0;


    public InclinometerIndicator(Context context) {
        this(context, null);
    }

    public InclinometerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);

        mRollLadderPaint = new Paint();
        mRollLadderPaint.setAntiAlias(true);
        mRollLadderPaint.setColor(Color.WHITE);
        mRollLadderPaint.setStrokeWidth(10);

//        mPointColorPaint = new Paint();
//        mPointColorPaint.setAntiAlias(true);
//        mPointColorPaint.setColor(POINT_COLOR);

        mHorizon = new Paint();
        mHorizon.setAntiAlias(true);
        mHorizon.setColor(Color.BLACK);
        mHorizon.setStrokeWidth(10);

        mBottomRollLadderPaint = new Paint();
        mBottomRollLadderPaint.setAntiAlias(true);
        mBottomRollLadderPaint.setColor(Color.RED);
        mBottomRollLadderPaint.setStrokeWidth(10);
    }

    public float getmPitch() {
        return mPitch;
    }
    public float getmRoll() {
        return mRoll;
    }

    public void setInclinometer(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int i, int i2) {
        super.onSizeChanged(width, height, i, i2);
        mWidth = width;
        mHeight = height;
    }
    public Bitmap getmSrcBitmap() {
        if (mSrcBitmap == null) {
            mSrcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mSrcCanvas = new Canvas(mSrcBitmap);
        }
        Canvas canvas = mSrcCanvas;

        float centerX = mWidth/2;
        float centerY = mHeight/2;

        //Background
        canvas.drawColor(Color.TRANSPARENT);
        canvas.save();
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch/ROLL_DEGREES) * mHeight);

        //Horizon and TopLadder
//        float ladderStepY = mHeight/12;
        float topLadderStepX = mWidth/12;
        float topLadderStepY = mWidth/12;
        canvas.drawLine(-mWidth, centerY, mWidth * 2,
                centerY, mHorizon);
        for (int i=1; i <=5; i++) {
            float y = centerY - topLadderStepY * i;
            canvas.drawLine(centerX - topLadderStepX * i,
                    y, centerX + topLadderStepX * i, y, mRollLadderPaint);
        }

        //Bottom Ladder
        float bottomLadderStepX = mWidth/12;
        float bottomLadderStepY = mWidth/12;
        for (int i = 1; i <= 5; i++) {
            float y = centerY + bottomLadderStepY * i;
            canvas.drawLine(centerX - bottomLadderStepX * i,
                    y, centerX + bottomLadderStepX * i, y, mBottomRollLadderPaint);
        }
        canvas.restore();

        //Center Point
//        canvas.drawPoint(centerX, centerY, mPointColorPaint);

        return mSrcBitmap;
    }
    public Bitmap getmDstBitmap() {
        if (mDstBitmap == null) {
            mDstBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mDstBitmap);
            c.drawColor(Color.TRANSPARENT);

            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.RED);
            c.drawOval(new RectF(0, 0, mWidth, mHeight), p);
        }
        return mDstBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (LOG_FPS) {
            countFPS();
        }
        Bitmap src = getmSrcBitmap();
        Bitmap dst = getmDstBitmap();

        int sc = saveLayer(canvas);
        canvas.drawBitmap(dst, 0, 0, mBitmapPaint);
        mBitmapPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawBitmap(src, 0, 0, mBitmapPaint);
        mBitmapPaint.setXfermode(null);

        canvas.restoreToCount(sc);
    }

    public int saveLayer(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return canvas.saveLayer(0, 0, mWidth, mHeight, null);
        } else {
            return canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        }
    }

    public long frameCountStartedInClino = 0;
    public long frameCount = 0;

    public void countFPS() {
        frameCount++;
        if (frameCountStartedInClino == 0) {
            frameCountStartedInClino = System.currentTimeMillis();
        }
        long elapsed = System.currentTimeMillis() - frameCountStartedInClino;
        if (elapsed >= 1000) {
            frameCount = 0;
            frameCountStartedInClino = System.currentTimeMillis();
        }
    }
}
