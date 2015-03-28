package com.onerun.onerun.onerun.Model;

/**
 * Created by harriswarrenyip on 15-02-27.
 */
public class Person {
    private int id;
    private String runpassid;
    private String name;
    private int age;
    private double height;
    private double weight;
    private boolean runPass;

    public Person(int id, String runpassid, String name, int age, double weight, double height, boolean runPass) {
        this.id = id;
        this.runpassid = runpassid;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.runPass = runPass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRunpassid() {
        return runpassid;
    }

    public void setRunpassid(String runpassid) {
        this.runpassid = runpassid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean getRunPass() { return runPass; }

    public void setRunPass(boolean runPass) { this.runPass = runPass; }
}
