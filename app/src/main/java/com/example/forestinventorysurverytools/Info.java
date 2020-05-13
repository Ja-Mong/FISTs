package com.example.forestinventorysurverytools;

import android.location.Location;

import com.google.ar.sceneform.ux.TransformableNode;

public class Info {
    TransformableNode node;
    TransformableNode h_node;
    String id;                 // 노드 생성된 월,일,시,분,초 형식
    float distance;
    float diameter;
    float height;
    Location loc; // GPS 좌표 추후 넣기...

    public Info(){}
    public Info(TransformableNode n, TransformableNode hn,String id){
        this.node = n;
        this.h_node = hn;
        this.id = id;
        distance = 0.0f;
        diameter = 0.0f;
        height = 0.0f;
    }

    //setter함수
    public void setNode(TransformableNode node){this.node=node;}
    public void setH_Node(TransformableNode h_node){this.h_node=h_node;}
    public void setDistance(float f){distance=f;}
    public void setDiameter(float f){diameter=f;}
    public void setHeight(float f){height=f;}

    //getter함수
    public TransformableNode getNode(){return node;}
    public TransformableNode getH_Node(){return h_node;}
    public float getDistance(){return distance;}
    public float getDiameter(){return diameter;}
    public float getHeight(){return height;}
    public String getId(){return id;}


    /*
    public void changeID(int index){
        //삭제할 요소 보다 뒤쪽 인덱스이면 삭제할때마다 하나씩 줄여주기..
        // 이러면 사진 이미지도 나중에 바꿔줘야하는????;;; 잠시 보류... 읍읍
        if(id>index)
            id--;
    }

     */
}
