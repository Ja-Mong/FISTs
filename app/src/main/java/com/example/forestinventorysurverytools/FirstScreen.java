package com.example.forestinventorysurverytools;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;

import java.util.Objects;

public class FirstScreen extends AppCompatActivity implements View.OnClickListener {

    //TextView
    TextView mTitle;
    TextView mSub_title;
    TextView mContent;
    public EditText mInputUserID;
    public EditText mInputUserHeight;


    //Values
    public String userDefaultID = "홍길동";
    public String userDefaultHeight = "160";


    //ImageView
    ImageView mKfs_mark;
    ImageView mKnu_mark;
    ImageView mNotice;


    //Permission
    boolean cameraPermission;
    boolean writePermission;
    boolean readPermission;
    boolean locationPermission;


    //Check the support to AR
    public Session mSession;
    private boolean mUserRequestedInstall = true;


    //Button
    Button mGuide_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);

        //TextView
        mTitle = (TextView) findViewById(R.id.title);
        mSub_title = (TextView) findViewById(R.id.sub_title);
        mContent = (TextView) findViewById(R.id.content);
        mInputUserID = (EditText) findViewById(R.id.userID);
        mInputUserHeight = (EditText) findViewById(R.id.userHeight);

        //EditText default values
        mInputUserID.setText(userDefaultID);
        mInputUserID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mInputUserID.getText().toString().equals(userDefaultID)) {
                    mInputUserID.setText("");
                }
                return false;
            }
        });

        mInputUserID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(mInputUserID.getText().toString())) {
                    mInputUserID.setText(userDefaultID);
                } else if (hasFocus && mInputUserID.getText().toString().equals(userDefaultID)) {
                    mInputUserID.setText("");
                }
            }
        });

        mInputUserHeight.setText(userDefaultHeight);
        mInputUserHeight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mInputUserHeight.getText().toString().equals(userDefaultHeight)) {
                    mInputUserHeight.setText("");
                }
                return false;
            }
        });

        mInputUserHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(mInputUserHeight.getText().toString())) {
                    mInputUserHeight.setText(userDefaultHeight);
                } else if (hasFocus && mInputUserHeight.getText().toString().equals(userDefaultHeight)) {
                    mInputUserHeight.setText("");
                }
            }
        });


        //ImageView
        mKfs_mark = (ImageView) findViewById(R.id.kfs_mark);
        mKnu_mark = (ImageView) findViewById(R.id.knu_mark);
        mNotice = (ImageView) findViewById(R.id.notice);


        //Button
        mGuide_btn = (Button) findViewById(R.id.guide_btn);
        mGuide_btn.setOnClickListener(this);


        //Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraPermission = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            writePermission = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readPermission = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true;
        }

        if (!cameraPermission || writePermission || readPermission || locationPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        //ARCore API 접근 개체 생성
        try {
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        mSession = new Session(this);
                        break;
                    case INSTALL_REQUESTED:
                        mUserRequestedInstall = false;
                        break;
                }
            }
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        } catch (UnavailableUserDeclinedInstallationException e) {
            e.printStackTrace();
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        }


        //AR 지원 가능 여부 체크
        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(this, "본 디바이스는 AR을 지원하지 않습니다.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mGuide_btn) {

            Intent intent = new Intent(getApplicationContext(), UserGuide.class);
            startActivity(intent);
        }
    }

    public void onRequestPermissionsResults(int requsetCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requsetCode, permissions, grantResults);
        if (requsetCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraPermission = true;
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                writePermission = true;
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                locationPermission = true;
        }
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


    //Input user height
    public void userHeight() {
        if (!mInputUserHeight.getText().toString().isEmpty()) {
            float userheight = Float.valueOf(mInputUserHeight.getText().toString()) / 100f;
        } else if (!mInputUserHeight.getText().toString().equals(userDefaultHeight)) {
            float userheight2 = Float.valueOf(mInputUserHeight.getText().toString()) / 100f;
        }
    }
}
