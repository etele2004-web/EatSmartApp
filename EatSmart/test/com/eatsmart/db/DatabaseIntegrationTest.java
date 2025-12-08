package com.eatsmart.db;

import com.eatsmart.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIntegrationTest {

    @BeforeAll
    public static void setup() {
        
        DatabaseManager.initDb();
    }

    @Test
    public void testUserSaveAndRetrieve() {
       
        String uniqueName = "TesztUser_" + System.currentTimeMillis();

        User newUser = new User(
                uniqueName,
                "Teszt Elek",
                80.0,  // súly
                180.0, // magasság
                25,    // életkor
                "Férfi",
                "Fogyás",
                75.0,  // célsúly
                2500   // napi kalória
        );

    
        DatabaseManager.saveUser(newUser);

       
        User retrievedUser = DatabaseManager.getUser(uniqueName);

        
        assertNotNull(retrievedUser, "A visszakapott felhasználó nem lehet null (nem találtuk az adatbázisban)!");

        
        assertEquals(newUser.getUsername(), retrievedUser.getUsername(), "A felhasználónév nem egyezik!");
        assertEquals(newUser.getDisplayName(), retrievedUser.getDisplayName(), "A megjelenített név nem egyezik!");
        assertEquals(newUser.getWeight(), retrievedUser.getWeight(), 0.01, "A súly nem egyezik!");

        System.out.println("SIKER: A felhasználó mentése és visszakérése hibátlanul működik.");
    }
}
