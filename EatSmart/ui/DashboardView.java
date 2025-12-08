package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.FoodEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class DashboardView {

    private final EatSmartApp app;
    private final AppTheme theme;

    public DashboardView(EatSmartApp app) {
        this.app = app;
        this.theme = app.getTheme();
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + theme.background + ";");

        root.getChildren().addAll(createHeader(), createMainCard(), createListHeader(), createFoodList());
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerText = new VBox(2);
        Label greeting = new Label("Szia,");
        greeting.setStyle("-fx-text-fill: " + theme.accentColor + "; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label nameLbl = new Label(app.getCurrentUser().getDisplayName() + "!");
        nameLbl.setStyle("-fx-text-fill: " + theme.textColor + "; -fx-font-weight: bold; -fx-font-size: 28px;");
        headerText.getChildren().addAll(greeting, nameLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane profileIcon = new StackPane();
        Circle profileBg = new Circle(20, Color.web(theme.accentColor)); // Accent színt használunk, kicsit átlátszóbban kéne de így is jó
        profileBg.setOpacity(0.2);
        
        String initial = app.getCurrentUser().getDisplayName().substring(0, 1).toUpperCase();
        Label profileInitial = new Label(initial);
        profileInitial.setStyle("-fx-text-fill: " + theme.accentColor + "; -fx-font-weight: bold;");
        profileIcon.getChildren().addAll(profileBg, profileInitial);

        MenuButton menuBtn = new MenuButton("", profileIcon);
        menuBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        MenuItem mProfile = new MenuItem("Profil"); mProfile.setOnAction(e -> app.showProfile());
        MenuItem mCalendar = new MenuItem("Naptár"); mCalendar.setOnAction(e -> app.showCalendar());
        MenuItem mAdd = new MenuItem("Hozzáadás"); mAdd.setOnAction(e -> app.showAddFood());
        MenuItem mLogout = new MenuItem("Kijelentkezés"); mLogout.setOnAction(e -> { app.setCurrentUser(null); app.showLoginScreen(); });

        menuBtn.getItems().addAll(mAdd, mCalendar, mProfile, mLogout);
        header.getChildren().addAll(headerText, spacer, menuBtn);
        return header;
    }

    private VBox createMainCard() {
        List<FoodEntry> todayFood = DatabaseManager.getTodayFood(app.getCurrentUser().getUsername());
        int consumed = todayFood.stream().mapToInt(FoodEntry::getCalories).sum();
        int remaining = Math.max(0, app.getCurrentUser().getDailyTarget() - consumed);

        VBox mainCard = new VBox(10);
        mainCard.setPadding(new Insets(25));
        mainCard.setStyle("-fx-background-color: " + theme.mainCardGradient + "; -fx-background-radius: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 8);");

        HBox cardTop = new HBox();
        cardTop.setAlignment(Pos.CENTER_LEFT);

        VBox cardTexts = new VBox(5);
        Label lblRem = new Label("Hátralévő"); lblRem.setTextFill(Color.web("#ccfbf1")); lblRem.setFont(Font.font("Segoe UI", 14));
        Label lblValue = new Label(String.valueOf(remaining)); lblValue.setTextFill(Color.WHITE); lblValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        Label lblUnit = new Label("kcal"); lblUnit.setTextFill(Color.WHITE); lblUnit.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        cardTexts.getChildren().addAll(lblRem, lblValue, lblUnit);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        double progress = (double) consumed / app.getCurrentUser().getDailyTarget();
        StackPane progressPane = new StackPane();
        Circle outerCircle = new Circle(35, Color.TRANSPARENT);
        outerCircle.setStroke(Color.web("rgba(255,255,255,0.2)")); outerCircle.setStrokeWidth(6);
        Label percentLbl = new Label((int)(Math.min(progress, 1.0) * 100) + "%");
        percentLbl.setTextFill(Color.WHITE); percentLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        progressPane.getChildren().addAll(outerCircle, percentLbl);

        cardTop.getChildren().addAll(cardTexts, spacer, progressPane);

        HBox macros = new HBox(10);
        macros.getChildren().addAll(createMacroTag("Fehérje"), createMacroTag("Szénh."), createMacroTag("Zsír"));

        mainCard.getChildren().addAll(cardTop, new Region(), macros);
        return mainCard;
    }

    private Label createListHeader() {
        Label listHeader = new Label("Mai étkezések");
        listHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        listHeader.setStyle("-fx-text-fill: " + theme.textColor + ";"); // Téma színét használja!
        return listHeader;
    }

    private ScrollPane createFoodList() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox foodList = new VBox(15);
        foodList.setPadding(new Insets(5));

        List<FoodEntry> todayFood = DatabaseManager.getTodayFood(app.getCurrentUser().getUsername());
        if (todayFood.isEmpty()) {
            Label empty = new Label("Még nem ettél ma semmit.");
            // Sötét módban a szürke is legyen olvasható
            empty.setStyle("-fx-text-fill: #94a3b8;"); 
            foodList.getChildren().add(empty);
        } else {
            for (FoodEntry f : todayFood) {
                foodList.getChildren().add(createFoodRow(f));
            }
        }

        Button addBtn = new Button("+ Új étel rögzítése");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(45);
        // A gomb is a témához igazodik
        addBtn.setStyle("-fx-background-color: " + theme.cardBg + "; -fx-text-fill: " + theme.accentColor + "; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: " + theme.accentColor + "; -fx-border-radius: 15; -fx-border-style: dashed; -fx-cursor: hand;");
        addBtn.setOnAction(e -> app.showAddFood());
        foodList.getChildren().add(addBtn);

        scrollPane.setContent(foodList);
        return scrollPane;
    }

    private HBox createFoodRow(FoodEntry f) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        // A kártya háttere a témából jön (Sötét módban sötétszürke, Világosban fehér)
        row.setStyle("-fx-background-color: " + theme.cardBg + "; -fx-background-radius: 18; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        StackPane iconPane = new StackPane();
        Rectangle bg = new Rectangle(45, 45, Color.web(theme.iconBg));
        bg.setArcWidth(15); bg.setArcHeight(15);
        
        Label icon = new Label(getIconForFood(f.getFoodName()));
        icon.setStyle("-fx-font-size: 20px;");
        iconPane.getChildren().addAll(bg, icon);

        VBox texts = new VBox(2);
        
        // --- SZÍN JAVÍTÁS ---
        Label name = new Label(f.getFoodName());
        // Itt most a theme.textColor-t használjuk!
        name.setStyle("-fx-text-fill: " + theme.textColor + "; -fx-font-weight: bold; -fx-font-size: 15px;"); 
        
        Label cals = new Label(f.getCalories() + " kcal");
        cals.setStyle("-fx-text-fill: " + theme.accentColor + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        // --------------------
        
        texts.getChildren().addAll(name, cals);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button delBtn = new Button("🗑");
        delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-cursor: hand;");
        delBtn.setOnAction(e -> {
            DatabaseManager.deleteFood(f.getId());
            app.showDashboard();
        });

        row.getChildren().addAll(iconPane, texts, spacer, delBtn);
        return row;
    }

    private Label createMacroTag(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setPadding(new Insets(5, 12, 5, 12));
        l.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 8; -fx-font-size: 11px;");
        return l;
    }

    private String getIconForFood(String name) {
        if (name == null) return "🍽️";
        String lower = name.toLowerCase();
        if (containsAny(lower, "kenyér", "kenyer", "kifli", "zsemle", "szendvics", "péksüti", "kalács")) return "🍞";
        if (containsAny(lower, "csirke", "hús", "hus", "sonka", "kolbász", "kolbasz", "pörkölt", "porkolt", "marha", "sertés", "sertes", "hal", "tonhal")) return "🍗";
        if (containsAny(lower, "tojás", "tojas", "rántotta", "rantotta", "omlett")) return "🍳";
        if (containsAny(lower, "rizs", "krumpli", "burgonya", "tészta", "teszta")) return "🍚";
        if (containsAny(lower, "saláta", "salata", "zöldség", "zoldseg", "uborka", "paradicsom", "paprika", "répa", "repa")) return "🥗";
        if (containsAny(lower, "alma", "banán", "banan", "gyümölcs", "gyumolcs", "narancs", "barack", "szőlő", "szolo")) return "🍎";
        if (containsAny(lower, "tej", "joghurt", "sajt", "túró", "turo", "kefir")) return "🥛";
        if (containsAny(lower, "kávé", "kave", "tea", "latte", "cappuccino")) return "☕";
        if (containsAny(lower, "víz", "viz", "cola", "kóla", "üdítő", "udito", "szörp", "szorp")) return "🥤";
        if (containsAny(lower, "csoki", "süti", "suti", "torta", "fánk", "fank", "keksz", "chips")) return "🍫";
        if (containsAny(lower, "pizza", "lángos", "langos")) return "🍕";
        if (containsAny(lower, "hamburger", "burger", "gyros")) return "🍔";
        if (containsAny(lower, "leves")) return "🍲";
        return "🍽️";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
