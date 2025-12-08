package com.eatsmart.ui;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.User;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EatSmartApp extends Application {

    private Stage primaryStage;
    private User currentUser;
    
    // Alapértelmezett téma
    private AppTheme currentTheme = AppTheme.MODERN_GREEN;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        DatabaseManager.initDb();
        updateAppIcon(); // Ikon frissítése induláskor
        
        showLoginScreen();
        primaryStage.setTitle("EatSmart");
        primaryStage.show();
    }

    // --- TÉMAVÁLTÓ METÓDUS ---
    public void setTheme(AppTheme newTheme) {
        this.currentTheme = newTheme;
        updateAppIcon(); // Ikon színének frissítése
        // Újrarajzoljuk az aktuális képernyőt (pl. Profilt), hogy látszódjon a változás
        showProfile(); 
    }

    public AppTheme getTheme() {
        return currentTheme;
    }

    // --- NAVIGÁCIÓ ---

    public void showLoginScreen() {
        LoginView view = new LoginView(this);
        setScene(view.getView());
    }

    public void showDashboard() {
        if (currentUser == null) return;
        DashboardView view = new DashboardView(this);
        setScene(view.getView());
    }

    public void showCalendar() {
        CalendarView view = new CalendarView(this);
        setScene(view.getView());
    }

    public void showProfile() {
        ProfileView view = new ProfileView(this);
        setScene(view.getView());
    }

    public void showAddFood() {
        AddFoodView view = new AddFoodView(this);
        setScene(view.getView());
    }

    private void setScene(Pane root) {
        Scene scene = new Scene(root, 400, 750);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showToast(String message) {
        Popup popup = new Popup();
        Label toast = new Label(message);
        // Toast stílus (ez mindig sötét marad, hogy jól olvasható legyen)
        toast.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 15; -fx-background-radius: 10; -fx-font-size: 14px;");
        popup.getContent().add(toast);

        if (primaryStage != null && primaryStage.isShowing()) {
            popup.show(primaryStage);
            popup.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 100);
            popup.setY(primaryStage.getY() + primaryStage.getHeight() - 100);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.seconds(0.5), toast);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(ev -> popup.hide());
                fade.play();
            });
            delay.play();
        }
    }

    private void updateAppIcon() {
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(generateAppIcon());
    }

    private WritableImage generateAppIcon() {
        int size = 64;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web(currentTheme.accentColor)); // A téma színét használja!
        gc.fillRoundRect(0, 0, size, size, 20, 20);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText("ES", size / 2.0, size / 2.0);
        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    public static void main(String[] args) { launch(args); }
}
