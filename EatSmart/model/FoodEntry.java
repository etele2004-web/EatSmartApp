package com.eatsmart.model;

public class FoodEntry {
    private int id;
    private String foodName;
    private int calories;
    private String date;

    public FoodEntry(int id, String foodName, int calories, String date) {
        this.id = id;
        this.foodName = foodName;
        this.calories = calories;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public int getCalories() { return calories; }
    public String getDate() { return date; }
}
