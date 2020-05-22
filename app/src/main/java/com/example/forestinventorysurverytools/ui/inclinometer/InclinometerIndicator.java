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
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.PixelCopy;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.forestinventorysurverytools.MainActivity;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.parseColor;

public class InclinometerIndicator extends View {


    //Roading
    public static final boolean LOG_FPS = false;


    //Orientation
    public static final int ROLL_DEGREES = 45*2;


    //Paint
    public static final int BACKGROUND_COLOR = parseColor("#1A003F00");
    PorterDuffXfermode mPorterDuffXfermode;
    Paint mCenterPointPaint;
    Paint mBitmapPaint;
    Paint mRollLadderPaint;
    Paint mBackGround;
    Paint mHorizon;
    Paint mBottomRollLadderPaint;
    Bitmap mSrcBitmap;
    Bitmap mDstBitmap;
    Canvas mSrcCanvas;

    MainActivity ma;

    //Value
    int mWidth;
    int mHeight;
    float mPitch = 0;
    float mRoll = 0;


    //Inclinometer Draw
    public InclinometerIndicator(Context context) {
        this(context, null);
    }
    public InclinometerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        //Paint
        //Shape
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);


        //Center Point
        mCenterPointPaint = new Paint();
        mCenterPointPaint.setAntiAlias(true);
        mCenterPointPaint.setColor(BLUE);
        mCenterPointPaint.setStrokeWidth(10);


        //Top paint
        mRollLadderPaint = new Paint();
        mRollLadderPaint.setAntiAlias(true);
        mRollLadderPaint.setColor(BLACK);
        mRollLadderPaint.setStrokeWidth(3);


        //Background paint
        mBackGround = new Paint();
        mBackGround.setAntiAlias(true);
        mBackGround.setColor(TRANSPARENT);


        //Horizontal paint
        mHorizon = new Paint();
        mHorizon.setAntiAlias(true);
        mHorizon.setColor(RED);
        mHorizon.setStrokeWidth(3);


        //Bottom paint
        mBottomRollLadderPaint = new Paint();
        mBottomRollLadderPaint.setAntiAlias(true);
        mBottomRollLadderPaint.setColor(BLACK);
        mBottomRollLadderPaint.setStrokeWidth(3);
    }


    //Orientation
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


    //Window
    @Override
    protected void onSizeChanged(int width, int height, int i, int i2) {
        super.onSizeChanged(width, height, i, i2);
        mWidth = width;
        mHeight = height;
    }


    //Draw
    public Bitmap getmSrcBitmap() {

        if (mSrcBitmap == null) {
            mSrcBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mSrcCanvas = new Canvas(mSrcBitmap);
        }
        Canvas canvas = mSrcCanvas;

        float centerX = mWidth/2;
        float centerY = mHeight/2;

        //Background
        canvas.drawColor(BACKGROUND_COLOR);
        canvas.save();
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch/ROLL_DEGREES) * mHeight);
        //Background shape
        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mBackGround);



        //Horizon and TopLadder
        float topLadderStepX = mWidth/2;
        float topLadderStepY = mWidth/2;
        canvas.drawLine(-mWidth, centerY, mWidth*2,
                centerY, mHorizon);
        for (int i=1; i <=100; i++) {
            float y = centerY - topLadderStepY * i;
            canvas.drawLine(centerX, y,
                    centerX + topLadderStepX * i, y, mRollLadderPaint);
        }

//        Bottom Ladder
        float bottomLadderStepX = mWidth/2;
        float bottomLadderStepY = mWidth/2;
        for (int i = 1; i <= 100; i++) {
            float y = centerY + bottomLadderStepY * i;
            canvas.drawLine(centerX, y,
                    centerX + bottomLadderStepX * i, y, mBottomRollLadderPaint);
        }
        canvas.restore();

        //Center Point
        canvas.drawPoint(centerX, centerY, mCenterPointPaint);

        return mSrcBitmap;
    }
    public Bitmap getmDstBitmap() {
        if (mDstBitmap == null) {
            mDstBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mDstBitmap);
            c.drawColor(BACKGROUND_COLOR);
            c.drawRect(200,200,400,400,mBackGround);

            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(RED);
            c.drawOval(new RectF(0, 0, mWidth, mHeight), p);
//            c.drawOval(new RectF(100,100,mWidth,mHeight), p);
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
        //canvas.drawBitmap(dst, 0, 0, mBitmapPaint);
//        mBitmapPaint.setXfermode(mPorterDuffXfermode);
        //canvas.drawBitmap(src, 0, 0, mBitmapPaint);
//        mBitmapPaint.setXfermode(null);



        /**************************************************************/
        canvas.drawColor(BACKGROUND_COLOR);
        float centerX = mWidth/2;
        float centerY = mHeight/2;
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch/ROLL_DEGREES) * mHeight);
        //Background shape
        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mBackGround);



        //Horizon and TopLadder
        float topLadderStepX = mWidth/10;
        float topLadderStepY = mWidth/10;
        canvas.drawLine(-mWidth, centerY, mWidth*2,
                centerY, mHorizon);
        for (int i=1; i <=100; i++) {
            float y = centerY - topLadderStepY * i;
            canvas.drawLine(centerX - topLadderStepX * i, y,
                    centerX + topLadderStepX * i, y, mRollLadderPaint);
        }

//        Bottom Ladder
        float bottomLadderStepX = mWidth/10;
        float bottomLadderStepY = mWidth/10;
        for (int i = 1; i <= 100; i++) {
            float y = centerY + bottomLadderStepY * i;
            canvas.drawLine(centerX- topLadderStepX * i, y,
                    centerX + bottomLadderStepX * i, y, mBottomRollLadderPaint);
        }




        /**************************************************************/

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
