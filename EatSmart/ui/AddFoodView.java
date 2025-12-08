package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AddFoodView {

    private final EatSmartApp app;
    private final AppTheme theme;

    public AddFoodView(EatSmartApp app) {
        this.app = app;
        this.theme = app.getTheme();
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: " + theme.background + ";");

        Label title = new Label("Mit ettél?");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(theme.textColor));

        VBox form = new VBox(15);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TextField nameF = new TextField(); nameF.setPromptText("Étel neve");
        nameF.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-padding: 12;");

        TextField calF = new TextField(); calF.setPromptText("Kalória (kcal)");
        calF.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-padding: 12;");

        Button saveBtn = new Button("Hozzáadás");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(45);
        saveBtn.setStyle("-fx-background-color: " + theme.accentColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand;");

        Button backBtn = new Button("Mégse");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand;");
        backBtn.setOnAction(e -> app.showDashboard());

        saveBtn.setOnAction(e -> {
            try {
                String n = nameF.getText();
                int c = Integer.parseInt(calF.getText());
                if (!n.isEmpty() && c > 0) {
                    DatabaseManager.addFood(app.getCurrentUser().getUsername(), n, c);
                    app.showToast("Sikeresen hozzáadva!");
                    app.showDashboard();
                }
            } catch (Exception ex) { app.showToast("Hibás adatok!"); }
        });

        form.getChildren().addAll(nameF, calF, saveBtn);
        root.getChildren().addAll(title, form, backBtn);
        return root;
    }
}
