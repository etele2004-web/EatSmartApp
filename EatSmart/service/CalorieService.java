package com.eatsmart.service;

public class CalorieService {

    public static int calculateCalories(double weight, double height, int age, String gender, String goal) {
        double bmr;
        // Mifflin-St Jeor képlet
        if (gender.equalsIgnoreCase("Férfi")) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        double tdee = bmr * 1.3;

        if (goal.equalsIgnoreCase("Fogyás")) {
            return (int) (tdee - 500);
        } else if (goal.equalsIgnoreCase("Hízás")) {
            return (int) (tdee + 500);
        } else {
            return (int) tdee;
        }
    }

    public static String determineGoal(double currentWeight, double targetWeight) {
        if (targetWeight < currentWeight) {
            return "Fogyás";
        } else if (targetWeight > currentWeight) {
            return "Hízás";
        } else {
            return "Súlytartás";
        }
    }
}
