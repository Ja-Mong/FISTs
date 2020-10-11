package com.example.forestinventorysurverytools.sever;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class treeDTO {
    private String tid;
    private String dist;
    private String dbh;
    private String height;
//    private String latitude;
//    private String longitude;
//    private String pid;
//    private String imgloc;
//    private String imgid;

    public treeDTO(){};
    public treeDTO(String tid, String dist, String dbh, String h){
        this.tid = tid;
        this.dist = dist;
        this.dbh = dbh;
        this.height = h;
    }

    public String getDbh() {
        return dbh;
    }
    public String getHeight() {
        return height;
    }
    public String getTid() {
        return tid;
    }
    public String getDist() {
        return dist;
    }

    public void setDbh(String dbh) {
        this.dbh = dbh;
    }
    public void setDist(String dist) {
        this.dist = dist;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public void setTid(String tid) {
        this.tid = tid;
    }

//    @Override
//    public String toString() {
//        return "treeDTO{" +
//                "tid='" + tid + '\'' +
//                ", dist='" + dist + '\'' +
//                ", dbh='" + dbh + '\'' +
//                ", height='" + height + '\'' +
//                '}';
//    }

}
