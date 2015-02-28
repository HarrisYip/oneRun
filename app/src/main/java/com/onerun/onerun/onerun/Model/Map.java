package com.onerun.onerun.onerun.Model;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class Map {
    private int id;
    private int runid;
    private float latitude;
    private float longitude;
    private Date time;

    public Map(int id, int runid, float latitude, float longitude, Date time) {
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
