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

/*
//Example usage based on tutorial point

public class CalorieCalcStrat {
   public static void main(String[] args) {
        //some db things
        //make instance of a stategy implementation
        Context context = new Context(new CyclingCalorie());
        //call contexts calculate which calls the implementations calculate
        context.calc(duration, weight);
        //close db
   }
}
 */