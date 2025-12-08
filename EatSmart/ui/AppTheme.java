package com.eatsmart.ui;

public class AppTheme {
    public String name, background, cardBg, sidebarBg, textColor, accentColor, mainCardGradient, iconBg;

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

    // 1. Modern Világos (Az eredeti)
    public static final AppTheme MODERN_GREEN = new AppTheme(
            "Világos (Zöld)", "#E6F7F3", "#FFFFFF", "#FFFFFF", "#022c22", "#10b981",
            "linear-gradient(to bottom right, #0d9488, #10b981)", "#FFF7ED"
    );

    // 2. Sötét Mód (Dark Mode)
    public static final AppTheme DARK_MODE = new AppTheme(
            "Sötét Mód", "#111827", "#1F2937", "#1F2937", "#F9FAFB", "#34D399",
            "linear-gradient(to bottom right, #064E3B, #10B981)", "#374151"
    );

    // 3. Óceán Kék
    public static final AppTheme OCEAN_BLUE = new AppTheme(
            "Óceán Kék", "#F0F9FF", "#FFFFFF", "#FFFFFF", "#0C4A6E", "#0EA5E9",
            "linear-gradient(to bottom right, #0284C7, #38BDF8)", "#E0F2FE"
    );

    // 4. Naplemente (Lila/Rózsaszín)
    public static final AppTheme SUNSET_PURPLE = new AppTheme(
            "Naplemente", "#FFF1F2", "#FFFFFF", "#FFFFFF", "#881337", "#E11D48",
            "linear-gradient(to bottom right, #BE123C, #FB7185)", "#FFE4E6"
    );

    @Override
    public String toString() { return name; }
}
