package com.onerun.onerun.onerun.Model;

/**
 * Created by Jason on 15-03-07.
 */
public class Sport {
    private int id;
    private String type;
    private double multiplier;

    public Sport(int id, String type, double multiplier) {
        this.id = id;
        this.type = type;
        this.multiplier = multiplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
