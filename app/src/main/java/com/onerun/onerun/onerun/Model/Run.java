package com.onerun.onerun.onerun.Model;

import java.util.Date;

/**
 * Created by Jason on 15-02-27.
 */
public class Run {
    private int id;
    private int sportid;
    private Date starttime;
    private Date endtime;
    private double pace;
    private double distance;
    private double calories;

    public Run(int id, int sportid, Date starttime, Date endtime, double pace, double distance, double calories) {
        this.id = id;
        this.sportid = sportid;
        this.starttime = starttime;
        this.endtime = endtime;
        this.pace = pace;
        this.distance = distance;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSportid() {
        return sportid;
    }

    public void setSportid(int sportid) {
        this.sportid = sportid;
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

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }
}
