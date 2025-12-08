package com.eatsmart.ui;

public class AppTheme {
    public String name, background, cardBg, sidebarBg, textColor, accentColor, mainCardGradient, iconBg;

    // Alapértelmezett modern zöld téma
    public static final AppTheme MODERN_GREEN = new AppTheme(
            "Modern Green",
            "#E6F7F3",       // Háttér
            "#FFFFFF",       // Kártyák
            "#FFFFFF",
            "#022c22",       // Sötét szöveg
            "#10b981",       // Akcentus (Zöld)
            "linear-gradient(to bottom right, #0d9488, #10b981)", // Gradiens
            "#FFF7ED"        // Ikon háttér
    );

    public AppTheme(String name, String background, String cardBg, String sidebarBg,
                    String textColor, String accentColor, String mainCardGradient, String iconBg) {
        this.name = name;
        this.background = background;
        this.cardBg = cardBg;
        this.sidebarBg = sidebarBg;
        this.textColor = textColor;
        this.accentColor = accentColor;
        this.mainCardGradient = mainCardGradient;
        this.iconBg = iconBg;
    }
}
