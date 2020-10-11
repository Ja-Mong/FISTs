package com.example.forestinventorysurverytools;

import android.location.Location;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

public class Info {

    public Info() {}

    TransformableNode mBotNode;
    TransformableNode mMovNode;
    TransformableNode mDBHNode;
    TransformableNode mUHNode;

    Node mText;
    String mId;
    float mClino;
    float mDist;
    float mDBH;
    float mHeight;
    float mAzi;
    float mAlti;


    Location locate; //done
    double mLatitude;
    double mLongitude;

    public Info(TransformableNode bot,
                TransformableNode mov,
                TransformableNode dbh,
                TransformableNode uh, String mId) {

        this.mBotNode = bot;
        this.mMovNode = mov;
        this.mDBHNode = dbh;
        this.mUHNode = uh;
        this.mId = mId;

        mClino = 0.0f;
        mDist = 0.0f;
        mDBH = 0.0f;
        mHeight = 0.0f;
        mAzi = 0.0f;
        mAlti = 0.0f;

        mText = new Node();
    } //done


    //setter Func
    public void setmBotNode(TransformableNode botNode) {
        this.mBotNode = botNode;
    }
    public void setmMovNode(TransformableNode movNode) {
        this.mMovNode = movNode;
    }
    public void setmDBHNode(TransformableNode dbhNode) {
        this.mDBHNode = dbhNode;
    }
    public void setmUHNode(TransformableNode uhNode) {
        this.mUHNode = uhNode;
    }

    //Values
    public void setClino(float clino) {
        mClino = clino;
    }
    public void setDist(float dist) {
        mDist = dist;
    }
    public void setDBH(float dbh) {
        mDBH = dbh;
    }
    public void setHeight(float height) {
        mHeight = height;
    }
    public void setAzi(float azi) {
        mAzi = azi;
    }
    public void setAlti(float alti) {
        mAlti = alti;
    }//done

    public void setLocate(double latitude, double longitude){this.mLatitude=latitude; this.mLongitude=longitude;}

    //getter Func
    public TransformableNode getBotNode() {
        return mBotNode;
    }
    public TransformableNode getMovNode() {
        return mMovNode;
    }
    public TransformableNode getDBHNode() {
        mDBHNode.setLocalPosition(new Vector3(0.0f, 1.2f, 0.0f));
        return mDBHNode;
    }//done
    public TransformableNode getUHNode() {
        return mUHNode;
    }

    //Values
    public float getClino() {
        return mClino;
    }
    public float getDist() {
        return mDist;
    }
    public float getDBH() {
        return mDBH;
    }
    public float getHeight() {
        return mHeight;
    }
    public float getAzi() {
        return mAzi;
    }
    public float getAlti() {
        return mAlti;
    }
    public String getId() {
        return mId;
    }
    public double getLatitude(){return mLatitude;}
    public double getLongitude(){return mLongitude;}

}//done
