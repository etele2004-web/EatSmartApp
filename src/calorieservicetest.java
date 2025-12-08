
package com.eatsmart.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalorieServiceTest {

    // --- 1. calculateCalories metódus tesztelése ---

    /**
     * Happy Path: Férfi, Súlytartás cél.
     * Számítás (Mifflin-St Jeor Férfi BMR + TDEE szorzó 1.3):
     * BMR = (10 * 80) + (6.25 * 180) - (5 * 30) + 5
     * BMR = 800 + 1125 - 150 + 5 = 1780
     * TDEE = 1780 * 1.3 = 2314
     * Várható eredmény (Súlytartás, int): 2314
     */
    @Test
    public void testCalculateCalories_HappyPath_FerfiSulyStartas() {
        int expected = 2314;
        int actual = CalorieService.calculateCalories(80, 180, 30, "Férfi", "Súlytartás");
        assertEquals(expected, actual, "A Súlytartás kalória számítás (Férfi) nem megfelelő.");
    }

    /**
     * Happy Path: Nő, Fogyás cél.
     * Számítás (Mifflin-St Jeor Nő BMR + TDEE szorzó 1.3 - 500):
     * BMR = (10 * 60) + (6.25 * 165) - (5 * 25) - 161
     * BMR = 600 + 1031.25 - 125 - 161 = 1345.25
     * TDEE = 1345.25 * 1.3 = 1748.825
     * Fogyás = 1748.825 - 500 = 1248.825
     * Várható eredmény (Fogyás, int): 1248
     */
    @Test
    public void testCalculateCalories_HappyPath_NoFogyas() {
        int expected = 1248; // (int) 1248.825
        int actual = CalorieService.calculateCalories(60, 165, 25, "Nő", "Fogyás");
        assertEquals(expected, actual, "A Fogyás kalória számítás (Nő) nem megfelelő.");
    }

    /**
     * Happy Path: Férfi, Hízás cél.
     * Számítás: 2314 + 500 = 2814 (a fenti Férfi példa alapján)
     * Várható eredmény (Hízás, int): 2814
     */
    @Test
    public void testCalculateCalories_HappyPath_FerfiHizas() {
        int expected = 2814;
        int actual = CalorieService.calculateCalories(80, 180, 30, "férfi", "HÍZÁS"); // Kis- és nagybetűs teszt
        assertEquals(expected, actual, "A Hízás kalória számítás (Férfi) nem megfelelő.");
    }

    // Kivételes esetek (Edge Cases)

    /**
     * Edge Case: Ismeretlen Cél.
     * Várható viselkedés: Nincs módosítás (+/- 500), csak TDEE.
     */
    @Test
    public void testCalculateCalories_EdgeCase_IsmeretlenCel() {
        int expected = 2314; // Eredeti TDEE a Férfi mintán
        int actual = CalorieService.calculateCalories(80, 180, 30, "Férfi", "Fenntartás");
        assertEquals(expected, actual, "Ismeretlen cél esetén is a TDEE-vel kell visszatérni.");
    }

    /**
     * Edge Case: Kisbetűs/Speciális nem megadása.
     * Várható viselkedés: Mivel a "Férfi" nem egyezik, a "Nő" képletet kell alkalmaznia.
     */
    @Test
    public void testCalculateCalories_EdgeCase_MasNem() {
        // Nő BMR = (10 * 80) + (6.25 * 180) - (5 * 30) - 161 = 1614
        // TDEE = 1614 * 1.3 = 2098.2
        int expected = 2098;
        int actual = CalorieService.calculateCalories(80, 180, 30, "Mas Nem", "Súlytartás");
        assertEquals(expected, actual, "Ismeretlen nem esetén a női képletnek kell aktiválódnia.");
    }

    // --- 2. determineGoal metódus tesztelése ---

    /**
     * Happy Path: Fogyás.
     */
    @Test
    public void testDetermineGoal_HappyPath_Fogyas() {
        String expected = "Fogyás";
        String actual = CalorieService.determineGoal(75.5, 70.0); // 75.5 > 70.0
        assertEquals(expected, actual, "Helytelen cél meghatározás fogyás esetén.");
    }

    /**
     * Happy Path: Hízás.
     */
    @Test
    public void testDetermineGoal_HappyPath_Hizas() {
        String expected = "Hízás";
        String actual = CalorieService.determineGoal(65.0, 70.0); // 65.0 < 70.0
        assertEquals(expected, actual, "Helytelen cél meghatározás hízás esetén.");
    }

    /**
     * Happy Path: Súlytartás.
     */
    @Test
    public void testDetermineGoal_HappyPath_SulyStartas() {
        String expected = "Súlytartás";
        String actual = CalorieService.determineGoal(70.0, 70.0); // 70.0 == 70.0
        assertEquals(expected, actual, "Helytelen cél meghatározás súlytartás esetén.");
    }

    // Kivételes esetek (Edge Cases)

    /**
     * Edge Case: Nagyon kis különbség (floating point összehasonlítás).
     */
    @Test
    public void testDetermineGoal_EdgeCase_KisKulonbseg() {
        String expected = "Fogyás";
        // Kisebb, mint az aktuális súly
        String actual = CalorieService.determineGoal(70.001, 70.0);
        assertEquals(expected, actual, "A lebegőpontos fogyás eset kezelése nem megfelelő.");
    }

    /**
     * Edge Case: Negatív bemenetek (bár a UI-nak ezt kezelnie kellene, a metódusnak kezelnie kell, amit kap).
     * Elméletileg Súlytartás, ha a cél és az aktuális súly is negatív (vagy null) és egyenlő.
     */
    @Test
    public void testDetermineGoal_EdgeCase_Nullak() {
        String expected = "Súlytartás";
        String actual = CalorieService.determineGoal(0.0, 0.0);
        assertEquals(expected, actual, "Nulla értékeknél a súlytartásnak kell visszatérnie.");
    }
}
