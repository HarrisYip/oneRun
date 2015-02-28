package com.onerun.onerun.onerun.Model;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class Run {
    private int id;
    private int personid;
    private Date starttime;
    private Date endtime;
    private double pace;
    private double distance;

    public Run(int id, int personid, Date starttime, Date endtime, double pace, double distance) {
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

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
