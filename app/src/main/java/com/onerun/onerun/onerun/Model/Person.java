package com.onerun.onerun.onerun.Model;

/**
 * Created by harriswarrenyip on 15-02-27.
 */
public class Person {
    private int id;
    private int runpassid;
    private String name;
    private int age;
    private double height;
    private double weight;

    public Person(int id, int runpassid, String name, int age, double weight, double height) {
        this.id = id;
        this.runpassid = runpassid;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRunpassid() {
        return runpassid;
    }

    public void setRunpassid(int runpassid) {
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
}
