package com.onerun.onerun.onerun;

/**
 * Created by Terrence on 3/25/2015.
 */
public class WalkCalorie implements CalorieCalculator{
    public double calc(double duration, double weight) {
        return duration * (3.5 * 3.5 * weight) / 200;
    }
}
