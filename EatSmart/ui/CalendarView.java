package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.FoodEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarView {

    private final EatSmartApp app;
    private final AppTheme theme;

    public CalendarView(EatSmartApp app) {
        this.app = app;
        this.theme = app.getTheme();
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + theme.background + ";");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("←");
        backBtn.setStyle("-fx-background-color: white; -fx-background-radius: 50; -fx-min-width: 40; -fx-min-height: 40; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-cursor: hand;");
        backBtn.setOnAction(e -> app.showDashboard());
        Label title = new Label("Naptár");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        header.getChildren().addAll(backBtn, title);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        VBox resultBox = new VBox(10);

        updateCalendarList(LocalDate.now(), resultBox);

        datePicker.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                updateCalendarList(datePicker.getValue(), resultBox);
            }
        });

        root.getChildren().addAll(header, datePicker, new Separator(), resultBox);
        return root;
    }

    private void updateCalendarList(LocalDate date, VBox container) {
        container.getChildren().clear();
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd."));
        List<FoodEntry> foods = DatabaseManager.getFoodByDate(app.getCurrentUser().getUsername(), dateStr);
        int sum = foods.stream().mapToInt(FoodEntry::getCalories).sum();

        Label sumLbl = new Label("Összesen: " + sum + " kcal");
        sumLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        sumLbl.setTextFill(Color.web(theme.accentColor));
        container.getChildren().add(sumLbl);

        ScrollPane listScroll = new ScrollPane();
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox list = new VBox(10);
        for(FoodEntry f : foods) list.getChildren().add(createSimpleFoodRow(f));

        listScroll.setContent(list);
        container.getChildren().add(listScroll);
    }

    private HBox createSimpleFoodRow(FoodEntry f) {
        // Ez ugyanaz, mint a dashboard-on, csak egy kicsit egyszerűbb verzió is lehetne
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        StackPane iconPane = new StackPane();
        Rectangle bg = new Rectangle(45, 45, Color.web(theme.iconBg));
        bg.setArcWidth(15); bg.setArcHeight(15);
        Label icon = new Label("🍎");
        icon.setStyle("-fx-font-size: 20px;");
        iconPane.getChildren().addAll(bg, icon);

        VBox texts = new VBox(2);
        Label name = new Label(f.getFoodName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        name.setTextFill(Color.web(theme.textColor));
        Label cals = new Label(f.getCalories() + " kcal");
        cals.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        cals.setTextFill(Color.web(theme.accentColor));
        texts.getChildren().addAll(name, cals);

        row.getChildren().addAll(iconPane, texts);
        return row;
    }
}
