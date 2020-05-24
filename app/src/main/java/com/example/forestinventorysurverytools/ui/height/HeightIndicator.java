package com.example.forestinventorysurverytools.ui.height;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.forestinventorysurverytools.MainActivity;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.parseColor;

public class HeightIndicator extends View {


    //Roading
    public static final boolean LOG_FPS = false;


    //Orientation
    public static final int ROLL_DEGREES = 45*2;


    //Paint
    public static final int BACKGROUND_COLOR = parseColor("#1A003F00");
    Paint mCenterPointPaint;
    Paint mBackGround;
    Paint mHorizon;


    //Activity
    MainActivity ma;


    //Value
    int mWidth;
    int mHeight;
    float mPitch = 0;
    float mRoll = 0;


    //Inclinometer Draw
    public HeightIndicator(Context context) {
        this(context, null);
    }
    public HeightIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);



        mCenterPointPaint = new Paint();
        mCenterPointPaint.setAntiAlias(true);
        mCenterPointPaint.setColor(BLUE);
        mCenterPointPaint.setStrokeWidth(15);


        //Background paint
        mBackGround = new Paint();
        mBackGround.setAntiAlias(true);
        mBackGround.setColor(TRANSPARENT);


        //Horizontal paint
        mHorizon = new Paint();
        mHorizon.setAntiAlias(true);
        mHorizon.setColor(RED);
        mHorizon.setStrokeWidth(3);
    }



    public void setInclinometer(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;

        invalidate();
    }


    //Window
    @Override
    protected void onSizeChanged(int width, int height, int i, int i2) {
        super.onSizeChanged(width, height, i, i2);
        mWidth = width;
        mHeight = height;
    }





    //Draw canvas
    @Override
    protected void onDraw(Canvas canvas) {
        if (LOG_FPS) {
            countFPS();
        }
        int sc = saveLayer(canvas);

        //BackGround
        canvas.drawColor(BACKGROUND_COLOR);
        float centerX = mWidth/2;
        float centerY = mHeight/2;
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch/ROLL_DEGREES) * mHeight);


        //Background shape
        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mBackGround);


        //Horizon
        canvas.drawLine(-mWidth, centerY, mWidth*2,
                centerY, mHorizon);

        canvas.restoreToCount(sc);
    }


    //SaveLayer
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

//    PorterDuffXfermode mPorterDuffXfermode;
//    Paint mBitmapPaint;
//    Paint mRollLadderPaint;
//    Paint mBottomRollLadderPaint;
//    Bitmap mSrcBitmap;
//    Bitmap mDstBitmap;
//    Canvas mSrcCanvas;
//Orientation
//    public float getmPitch() {
//        return mPitch;
//    }
//    public float getmRoll() {
//        return mRoll;
//    }
//Paint
//Shape
//        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
//        mBitmapPaint = new Paint();
//        mBitmapPaint.setFilterBitmap(false);


//Center Point

//
//    //Draw
//    public Bitmap getmSrcBitmap() {
//
//        if (mSrcBitmap == null) {
//            mSrcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//            mSrcCanvas = new Canvas(mSrcBitmap);
//        }
//        Canvas canvas = mSrcCanvas;
//
//        float centerX = mWidth/2;
//        float centerY = mHeight/2;
//
//
//        //Background
//        canvas.drawColor(BACKGROUND_COLOR);
//        canvas.save();
//        canvas.rotate(mRoll, centerX, centerY);
//        canvas.translate(0, (mPitch/ROLL_DEGREES) * mHeight);
//
//
//        //Background shape
//        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mBackGround);
//
//
//        //Horizon
//        canvas.drawLine(-mWidth, centerY, mWidth*2,
//                centerY, mHorizon);
//
//
//        //Center Point
//        canvas.drawPoint(centerX, centerY, mCenterPointPaint);
//        return mSrcBitmap;
//    }



//    //Bitmap
//    public Bitmap getmDstBitmap() {
//        if (mDstBitmap == null) {
//            mDstBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(mDstBitmap);
//            c.drawColor(BACKGROUND_COLOR);
//            c.drawRect(200,200,400,400,mBackGround);
//
//            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//            p.setColor(RED);
//            c.drawOval(new RectF(0, 0, mWidth, mHeight), p);
//        }
//        return mDstBitmap;
//    }
//        Bitmap src = getmSrcBitmap();
//        Bitmap dst = getmDstBitmap();