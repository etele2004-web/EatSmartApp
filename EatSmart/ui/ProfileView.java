package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.User;
import com.eatsmart.service.CalorieService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfileView {

    private final EatSmartApp app;
    private final AppTheme theme;

    public ProfileView(EatSmartApp app) {
        this.app = app;
        this.theme = app.getTheme();
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: " + theme.background + ";");

        // FEJLÉC
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("←");
        backBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: " + theme.textColor + "; -fx-cursor: hand;");
        backBtn.setOnAction(e -> app.showDashboard());
        topBar.getChildren().add(backBtn);

        // AVATÁR
        StackPane avatarPane = new StackPane();
        Circle avatarBg = new Circle(50, Color.web("#FFF7ED"));
        String initial = app.getCurrentUser().getDisplayName().length() > 0 ? app.getCurrentUser().getDisplayName().substring(0, 1).toUpperCase() : "?";
        Label avatarLetter = new Label(initial);
        avatarLetter.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        avatarLetter.setTextFill(Color.web("#F97316"));
        avatarPane.getChildren().addAll(avatarBg, avatarLetter);

        // NÉV
        Label nameLbl = new Label(app.getCurrentUser().getDisplayName());
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        nameLbl.setTextFill(Color.web("#1e293b"));

        // STATISZTIKÁK
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getChildren().addAll(
                createStat((int)app.getCurrentUser().getWeight() + " kg", "SÚLY"),
                createStat((int)app.getCurrentUser().getHeight() + " cm", "MAGASSÁG"),
                createStat(app.getCurrentUser().getAge() + " év", "KOR")
        );

        // MENÜ
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnGoals = createMenuButton("Célok beállítása");
        btnGoals.setOnAction(e -> showEditGoalsDialog(root));

        Button btnPersonal = createMenuButton("Személyes adatok");
        btnPersonal.setOnAction(e -> showEditPersonalDataDialog(root));

        Button btnHelp = createMenuButton("Segítség");
        btnHelp.setOnAction(e -> app.showToast("Írj nekünk: p3887582@gmail.com"));

        menuBox.getChildren().addAll(btnGoals, btnPersonal, btnHelp);
        root.getChildren().addAll(topBar, avatarPane, nameLbl, statsBox, menuBox);
        return root;
    }

    private VBox createStat(String value, String label) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        valLbl.setTextFill(Color.web("#1e293b"));
        Label titleLbl = new Label(label);
        titleLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        titleLbl.setTextFill(Color.web("#94a3b8"));
        box.getChildren().addAll(valLbl, titleLbl);
        return box;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(60);
        btn.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4); -fx-cursor: hand;");

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(0, 15, 0, 15));
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lbl.setTextFill(Color.web("#1e293b"));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label arrow = new Label("›");
        arrow.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        arrow.setTextFill(Color.web("#10b981"));
        layout.getChildren().addAll(lbl, spacer, arrow);

        btn.setGraphic(layout);
        btn.setText(""); btn.setPadding(Insets.EMPTY);
        return btn;
    }

    // --- JAVÍTOTT DIALOGOK ---

    private void showEditPersonalDataDialog(VBox parent) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + theme.background + ";");

        Label title = new Label("Személyes adatok");
        title.setFont(Font.font(24));

        TextField wF = new TextField(String.valueOf(app.getCurrentUser().getWeight())); wF.setPromptText("Súly (kg)");
        TextField hF = new TextField(String.valueOf(app.getCurrentUser().getHeight())); hF.setPromptText("Magasság (cm)");
        TextField aF = new TextField(String.valueOf(app.getCurrentUser().getAge())); aF.setPromptText("Kor (év)");

        String style = "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;";
        wF.setStyle(style); hF.setStyle(style); aF.setStyle(style);

        Button saveBtn = new Button("Mentés");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(45);
        saveBtn.setStyle("-fx-background-color: " + theme.accentColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");

        saveBtn.setOnAction(e -> {
            try {
                // JAVÍTÁS 1: Vessző cseréje pontra, hogy ne legyen hiba
                double w = Double.parseDouble(wF.getText().replace(",", "."));
                double h = Double.parseDouble(hF.getText().replace(",", "."));
                int a = Integer.parseInt(aF.getText());

                User old = app.getCurrentUser();

                // Újrakalkuláljuk a napi keretet az új adatokkal
                int newDaily = CalorieService.calculateCalories(w, h, a, old.getGender(), old.getGoal());

                // JAVÍTÁS 2: ÚJ User objektum létrehozása a biztos mentéshez
                User newUser = new User(
                        old.getUsername(),
                        old.getDisplayName(),
                        w, h, a,
                        old.getGender(),
                        old.getGoal(),
                        old.getTargetWeight(),
                        newDaily // A frissített kalória
                );

                // Mentés adatbázisba és az app memóriájába
                DatabaseManager.saveUser(newUser);
                app.setCurrentUser(newUser);

                app.showToast("Személyes adatok mentve!");
                app.showProfile(); // Újratöltjük a nézetet
            } catch (Exception ex) {
                ex.printStackTrace();
                app.showToast("Hibás adatok! (Számokat adj meg)");
            }
        });

        Button backBtn = new Button("Mégse");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b;");
        backBtn.setOnAction(e -> app.showProfile());

        root.getChildren().addAll(title, new Label("Súly (kg)"), wF, new Label("Magasság (cm)"), hF, new Label("Kor (év)"), aF, saveBtn, backBtn);
        parent.getScene().setRoot(root);
    }

    private void showEditGoalsDialog(VBox parent) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + theme.background + ";");

        Label title = new Label("Célok beállítása");
        title.setFont(Font.font(24));

        TextField targetWeightF = new TextField(String.valueOf(app.getCurrentUser().getTargetWeight()));
        targetWeightF.setPromptText("Célsúly (kg)");
        TextField calorieF = new TextField(String.valueOf(app.getCurrentUser().getDailyTarget()));
        calorieF.setPromptText("Napi kalória keret");

        String style = "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;";
        targetWeightF.setStyle(style); calorieF.setStyle(style);

        Button saveBtn = new Button("Mentés");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(45);
        saveBtn.setStyle("-fx-background-color: " + theme.accentColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");

        saveBtn.setOnAction(e -> {
            try {
                // JAVÍTÁS: Vessző kezelése itt is
                double tw = Double.parseDouble(targetWeightF.getText().replace(",", "."));
                int cal = Integer.parseInt(calorieF.getText());

                User old = app.getCurrentUser();

                // Cél szöveg meghatározása (pl. Fogyás)
                String newGoalText = CalorieService.determineGoal(old.getWeight(), tw);

                // Új User objektum
                User newUser = new User(
                        old.getUsername(),
                        old.getDisplayName(),
                        old.getWeight(),
                        old.getHeight(),
                        old.getAge(),
                        old.getGender(),
                        newGoalText, // Frissült cél
                        tw,          // Frissült célsúly
                        cal          // Frissült kalória keret
                );

                DatabaseManager.saveUser(newUser);
                app.setCurrentUser(newUser);

                app.showToast("Célok mentve!");
                app.showProfile();
            } catch (Exception ex) {
                app.showToast("Hibás adatok!");
            }
        });

        Button backBtn = new Button("Mégse");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b;");
        backBtn.setOnAction(e -> app.showProfile());

        root.getChildren().addAll(title, new Label("Célsúly (kg)"), targetWeightF, new Label("Napi kalória keret"), calorieF, saveBtn, backBtn);
        parent.getScene().setRoot(root);
    }
}
