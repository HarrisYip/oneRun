package com.onerun.onerun.onerun.Model;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class RunModel {
    private int id;
    private int personid;
    private Date starttime;
    private Date endtime;
    private float pace;
    private float distance;

    public RunModel(int id, int personid, Date starttime, Date endtime, float pace, float distance) {
        this.id = id;
        this.personid = personid;
        this.starttime = starttime;
        this.endtime = endtime;
        this.pace = pace;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonid() {
        return personid;
    }

    public void setPersonid(int personid) {
        this.personid = personid;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
