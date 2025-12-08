
package com.eatsmart.db;

import com.eatsmart.model.FoodEntry;
import com.eatsmart.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // Csak egy URL változót használunk
    private static final String URL = "jdbc:sqlite:eatsmart.db";

    // FONTOS: Ez a formátum kell, hogy egyezzen a UI naptárával!
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

    public static void initDb() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, display_name TEXT, " +
                    "weight REAL, height REAL, age INTEGER, gender TEXT, " +
                    "goal TEXT, target_weight REAL, daily_target INTEGER)";
            stmt.execute(sqlUsers);

            String sqlFood = "CREATE TABLE IF NOT EXISTS food_log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, " +
                    "food_name TEXT, calories INTEGER, date TEXT, time TEXT)";
            stmt.execute(sqlFood);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"), rs.getString("display_name"),
                        rs.getDouble("weight"), rs.getDouble("height"),
                        rs.getInt("age"), rs.getString("gender"),
                        rs.getString("goal"), rs.getDouble("target_weight"),
                        rs.getInt("daily_target")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveUser(User user) {
        String sql = "INSERT OR REPLACE INTO users VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getDisplayName());
            pstmt.setDouble(3, user.getWeight());
            pstmt.setDouble(4, user.getHeight());
            pstmt.setInt(5, user.getAge());
            pstmt.setString(6, user.getGender());
            pstmt.setString(7, user.getGoal());
            pstmt.setDouble(8, user.getTargetWeight());
            pstmt.setInt(9, user.getDailyTarget());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addFood(String username, String foodName, int calories) {
        String sql = "INSERT INTO food_log(username, food_name, calories, date) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, foodName);
            pstmt.setInt(3, calories);
            // JAVÍTVA: A formátumnak egyeznie kell a naptáréval (yyyy. MM. dd.)
            pstmt.setString(4, LocalDate.now().format(DATE_FORMATTER));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<FoodEntry> getTodayFood(String username) {
        List<FoodEntry> list = new ArrayList<>();
        String sql = "SELECT id, food_name, calories, date FROM food_log WHERE username = ? AND date = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            // Itt is a formázót használjuk
            pstmt.setString(2, LocalDate.now().format(DATE_FORMATTER));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                list.add(new FoodEntry(
                        rs.getInt("id"),
                        rs.getString("food_name"),
                        rs.getInt("calories"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- JAVÍTOTT METÓDUS A NAPTÁRHOZ ---
    public static List<FoodEntry> getFoodByDate(String username, String dateString) {
        List<FoodEntry> foods = new ArrayList<>();
        // JAVÍTVA: a tábla neve 'food_log', nem 'foods'
        String sql = "SELECT id, food_name, calories, date FROM food_log WHERE username = ? AND date = ?";

        // JAVÍTVA: 'URL' használata 'DB_URL' helyett
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, dateString);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // JAVÍTVA: A FoodEntry konstruktor paraméterei egyeztetve a getTodayFood-dal
                foods.add(new FoodEntry(
                        rs.getInt("id"),
                        rs.getString("food_name"),
                        rs.getInt("calories"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Hiba a dátum szerinti lekérdezésnél: " + e.getMessage());
        }
        return foods;
    }

    public static void deleteFood(int id) {
        String sql = "DELETE FROM food_log WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
