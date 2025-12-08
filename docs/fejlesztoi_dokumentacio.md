EatSmart Calorie Tracker DokumentációA projekt egy egyszerű, helyi adatbázison alapuló kalóriaszámláló webalkalmazás, amely a felhasználói beavatkozás nélkül azonnal elindul, és egyetlen fix profilt (Vendég) kezel.


I. Projekt áttekintése
Paraméter,                  Érték,                                Leírás
Keretrendszer,              Streamlit,                            A felhasználói felületért felelős.
Backend Logika,             Python,                               Minden számításért és adatkezelésért felel.
Adatbázis,                  SQLite (eatsmart.db),                 Helyi fájl alapú adatbázis az adatok tartós tárolásáért.
Működési Mód,               Egyfelhasználós (Vendég),             "A belépési rendszert kihagyja, a fix GUEST_ID alatt tárolja az adatokat."
Fő Funkciók,                "Kalória számítás (BMR/TDEE),         Étel naplózás, Adatok módosítása.",


1. Előkészületek és Futtatás
Függőségek (Requirements):

Csak a Streamlit szükséges: pip install streamlit

Adatbázis Tisztítás: Mivel a séma stabilizálódott (nincs email/jelszó), a sikeres induláshoz törölni kell a régi, hibásan tárolt eatsmart.db fájlt (ha létezik).

Indítás: streamlit run app.py


II. Technikai Komponensek és Adatstruktúra 💾
1. Adatbázis Séma (Táblák)
Az alkalmazás két fő táblát használ az eatsmart.db fájlban:

A) users tábla (9 oszlop)
Ez tárolja az alkalmazás beállításait és a felhasználó fizikai adatait.



Index,                  Oszlopnév,                  Típus,                    Leírás
0,                      username,                   TEXT PRIMARY KEY,         "A belső, fix azonosító (mindig GUEST_ID)."
1,                      display_name,               TEXT,                     "A felhasználó által megadott és szerkeszthető név (Pl. ""Anna"")."
2,                      weight,                     REAL,                     Jelenlegi súly (kg).
3,                      height,                     REAL,                     Magasság (cm).
4,                      age,                        INTEGER,                  Életkor.
5,                      gender,                     TEXT,                     "Nem (""Férfi"" / ""Nő"")."
6,                      goal,                       TEXT,                     "Kalóriacél (""Fogyás"", ""Hízás"", ""Súlytartás"")."
7,                      target_weight,              REAL,                     A felhasználó által megadott célsúly (kg).
8,                      daily_target,               INTEGER,                  A napi kiszámított kalóriakeret.





B) food_log tábla
Ez tárolja az összes felvitt étkezési adatot.



Oszlopnév,                    Típus,                      Leírás
id,                           INTEGER PRIMARY KEY,        Egyedi ételazonosító (törléshez használva).
username,                     TEXT,                       Azonosító a users táblához (mindig GUEST_ID).
food_name,                    TEXT,                       Az étel neve.
calories,                     INTEGER,                    Kalóriaérték (kcal).





2. Core Függvények Összefoglalása



Függvény,                      Szakasz,                        Leírás
init_db(),                     DB Kereszt,                     Létrehozza a users és food_log táblákat az induláskor (ha még nem léteznek).
get_user_data(username),       DB Kereszt,                     Lekéri a felhasználó teljes profiladatát a táblából.
create_guest_user(...),        DB Kereszt,                     Létrehozza az alapértelmezett GUEST_ID rekordot az első indításkor.
update_user_data(...),         Profil,                         Frissíti a felhasználó fizikai adatait és a szerkeszthető nevét. Újraszámolja a napi kalóriacélt.
add_food(...),                 Étel hozzáadása,                Beszúr egy új ételrekordot a food_log táblába.
delete_food(food_id),          Dashboard,                      Törli az ételrekordot az id alapján.
calculate_calories(...),       Számítás,                       A Mifflin-St Jeor képlet alapján kiszámítja a TDEE-t és a célnak megfelelő napi kalória célt.








III. Alkalmazás Működési Ciklusa
Indítás (Entry Point): A program ellenőrzi, hogy létezik-e a GUEST_ID profil. Ha nem, létrehozza az alapértelmezett beállításokkal.

Sidebar (Oldalsáv): Mindig a szerkeszthető nevet (display_name) mutatja, és tartalmazza a navigációt és a Frissítés gombot (ami frissíti a böngészőben lévő adatokat).

Főoldal (Dashboard): Lekéri az adott napra vonatkozó összes ételt a food_log táblából, kiszámolja a maradványt, és megjeleníti a haladást a zöld kártyán.

Profil: Lehetővé teszi az összes személyes adat (beleértve a Megjelenített nevet és a Célsúlyt) szerkesztését. A mentés azonnal frissíti a users táblát, újraszámolja a napi keretet, és újra betölti az alkalmazást.
