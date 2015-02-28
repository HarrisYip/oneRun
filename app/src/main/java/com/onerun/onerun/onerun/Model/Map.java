package com.onerun.onerun.onerun.Model;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class Map {
    private int id;
    private int runid;
    private double latitude;
    private double longitude;
    private Date time;

    public Map(int id, int runid, double latitude, double longitude, Date time) {
        this.id = id;
        this.runid = runid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRunid() {
        return runid;
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
