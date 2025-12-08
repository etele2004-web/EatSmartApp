package com.eatsmart.model;

public class User {
    private String username;
    private String displayName;
    private double weight;
    private double height;
    private int age;
    private String gender;
    private String goal;
    private double targetWeight;
    private int dailyTarget;

    public User(String username, String displayName, double weight, double height, int age, String gender, String goal, double targetWeight, int dailyTarget) {
        this.username = username;
        this.displayName = displayName;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.goal = goal;
        this.targetWeight = targetWeight;
        this.dailyTarget = dailyTarget;
    }

    // Getters
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getGoal() { return goal; }
    public double getTargetWeight() { return targetWeight; }
    public int getDailyTarget() { return dailyTarget; }

    public void setWeight(double w) {
    }

    public void setDailyTarget(int i) {
    }

    public void setHeight(double h) {
    }

    public void setAge(int a) {
    }

    public void setTargetWeight(double tw) {
    }

    public void setGoal(String s) {
    }
}
