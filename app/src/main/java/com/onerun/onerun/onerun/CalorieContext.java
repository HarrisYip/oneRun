package com.onerun.onerun.onerun;

/**
 * Created by Terrence on 3/25/2015.
 */
public class CalorieContext {
    private CalorieCalculator calCalc;

    public CalorieContext(CalorieCalculator cc) {
        this.calCalc = cc;
    }

    public double calc(double duration, double weight) {
        return this.calCalc.calc(duration, weight);
    }
}