package performance;

import com.eatsmart.db.DatabaseManager;
import com.eatsmart.model.User;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTest {

    @Test
    public void testDatabasePerformance() throws InterruptedException {
        // Előkészítés: Adatbázis indítása és egy teszt user mentése
        DatabaseManager.initDb();
        User testUser = new User("load_test_user", "Load Tester", 80, 180, 25, "Férfi", "Fogyás", 75, 2000);
        DatabaseManager.saveUser(testUser);

        int numberOfThreads = 1000; // 1000 párhuzamos "felhasználó"
        ExecutorService service = Executors.newFixedThreadPool(50); // Egyszerre 50 szál futhat

        System.out.println("--- TELJESÍTMÉNY TESZT INDÍTÁSA ---");
        System.out.println("Felhasználók száma: " + numberOfThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                // Ez a művelet, amit tesztelünk (pl. felhasználó lekérése)
                DatabaseManager.getUser("load_test_user");
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / numberOfThreads;

        System.out.println("--- EREDMÉNYEK ---");
        System.out.println("Összes idő: " + totalTime + " ms");
        System.out.println("Átlagos válaszidő: " + avgTime + " ms/kérés");

        if (avgTime < 2000) {
            System.out.println("EREDMÉNY: SIKER (A válaszidő 2 mp alatt van)");
        } else {
            System.out.println("EREDMÉNY: BUKOTT (Túl lassú)");
        }
    }
}