package com.onerun.onerun.onerun;

/**
 * Created by Terrence on 3/25/2015.
 */
public class CyclingCalorie implements CalorieCalculator {
    public double calc(double duration, double weight) {
        return duration * (8.0 * 3.5 * weight) / 200;
    }
}
