package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.User;
import com.eatsmart.service.CalorieService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private final EatSmartApp app;

    public LoginView(EatSmartApp app) {
        this.app = app;
    }

    public VBox getView() {
        AppTheme theme = app.getTheme();
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + theme.background + ";");

        Label title = new Label("EatSmart");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: " + theme.accentColor + ";");

        Label subtitle = new Label("Jelentkezz be a folytatáshoz");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Írd be a neved...");
        nameInput.setMaxWidth(300);
        nameInput.setPrefHeight(45);
        nameInput.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        Button loginBtn = new Button("Indítás");
        loginBtn.setPrefWidth(300);
        loginBtn.setPrefHeight(45);
        loginBtn.setStyle("-fx-background-color: " + theme.accentColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 12; -fx-cursor: hand;");

        loginBtn.setOnAction(e -> {
            String name = nameInput.getText().trim();
            if (!name.isEmpty()) {
                String userId = name.toLowerCase();
                User user = DatabaseManager.getUser(userId);
                if (user == null) {
                    int defaultTarget = CalorieService.calculateCalories(70, 170, 30, "Férfi", "Súlytartás");
                    user = new User(userId, name, 70, 170, 30, "Férfi", "Súlytartás", 70, defaultTarget);
                    DatabaseManager.saveUser(user);
                }
                app.setCurrentUser(user);
                app.showDashboard();
            }
        });

        root.getChildren().addAll(title, subtitle, nameInput, loginBtn);
        return root;
    }
}
