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
    private String azi;
    private String clino;
    private String alti;

    private String latitude;
    private String longitude;

    private String pid;

    public treeDTO(){};
    public treeDTO(String tid, String dist, String dbh, String h, String l1, String l2, String l3, String l4, String l5,String pid){

        this.tid = tid;
        this.dist = dist;
        this.dbh = dbh;
        this.height = h;
        this.azi = l1;
        this.clino = l2;
        this.alti = l3;
        this.latitude=l4;
        this.longitude=l5;
        this.pid = pid;
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
    public String getAzi() {
        return azi;
    }
    public String getClino() {
        return clino;
    }
    public String getAlti() {
        return alti;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPid() {
        return pid;
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
    public void setAzi(String azi) {
        this.azi = azi;
    }
    public void setClino(String clino) {
        this.clino = clino;
    }
    public void setAlti(String alti) {
        this.alti = alti;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
