package com.onerun.onerun.onerun;

import android.util.Log;

import com.onerun.onerun.onerun.Model.Person;
import com.onerun.onerun.onerun.Model.PersonDataSource;
import com.onerun.onerun.onerun.Model.Run;
import com.onerun.onerun.onerun.Model.RunDataSource;
import com.onerun.onerun.onerun.Model.SportDataSource;

import java.util.Date;

/**
 * Created by Terrence on 3/27/2015.
*/
public class CalorieInterface {

    private RunDataSource runDataSource;
    private SportDataSource sportDataSource;
    private PersonDataSource personDataSource;

    public void newSrategy(RunDataSource runDS, PersonDataSource personDS, int runId) {
        //some db things
        runDataSource = runDS;
        personDataSource = personDS;
        runDataSource.open();
        personDataSource.open();

        // get info
        Run myRun = runDataSource.getRun(runId);
        Person myPerson = personDataSource.getPerson(1);

        //make instance of a strategy implementation
        CalorieContext context = null;
        switch (myRun.getSportid()){
            case 1:
                context = new CalorieContext(new RunCalorie());
                break;
            case 2:
                context = new CalorieContext(new WalkCalorie());
                break;
            case 3:
                context = new CalorieContext(new CyclingCalorie());
                break;
            default:
                break;
        }
        double weight = myPerson.getWeight();
        long endTime = myRun.getEndtime().getTime();
        long startTime = myRun.getStarttime().getTime();
        long difference = endTime - startTime;
        double duration = (double)difference / 60000; // convert to minutes

        //store calorie count for this exercise
        double calories = context.calc(duration, weight);
        myRun.setCalories(calories);
        runDataSource.updateRunCalories(myRun);
        //Log.v("Calories Burned: ", String.valueOf(myRun.getCalories()));
        //close dbs
        runDataSource.close();
        personDataSource.close();
    }
}

