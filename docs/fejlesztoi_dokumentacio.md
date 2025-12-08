EatSmart – Kalóriakövető Alkalmazás
Felhasználói és Technikai Dokumentáció
Verzió: 1.0.0 Dátum: 2024 Technológia: Python + Streamlit + SQLite

📑 Tartalomjegyzék
Bevezetés

Telepítési Útmutató

Felhasználói Kézikönyv

Bejelentkezés

Főoldal (Dashboard)

Étel hozzáadása

Profil és Beállítások

Technikai Dokumentáció

Rendszerarchitektúra

Adatbázis Szerkezet

Főbb Függvények

Hibaelhárítás

1. Bevezetés
Az EatSmart egy modern, felhasználóbarát webalkalmazás, amely segít a napi kalóriabevitel nyomon követésében. Az alkalmazás célja, hogy egyszerűsítse az egészséges életmód fenntartását azáltal, hogy automatikusan kiszámolja a felhasználó kalóriaszükségletét (BMR és TDEE alapján), és vizuális visszajelzést ad a napi haladásról.

Főbb jellemzők:

Személyre szabott: Minden felhasználó saját adatbázis-bejegyzést kap a neve alapján.

Perzisztens adattárolás: Az adatok megmaradnak kilépés után is (SQLite).

Modern UI: "Turbo" stílusú kártyák, animációk és toast üzenetek.

Eszközfüggetlen: Mobilon és asztali gépen is reszponzív felület.

2. Telepítési Útmutató
Az alkalmazás futtatásához Python környezet szükséges.

Előfeltételek:

Python 3.8 vagy újabb telepítése.

Lépések:

Függőségek telepítése: Nyissa meg a terminált (parancssort), és futtassa a következő parancsot:

Bash

pip install streamlit
Az alkalmazás indítása: Navigáljon a mappába, ahol az app.py található, és futtassa:

Bash

streamlit run app.py
Használat: Az alkalmazás automatikusan megnyílik az alapértelmezett böngészőben (általában a http://localhost:8501 címen). Mobilos eléréshez csatlakozzon ugyanarra a Wifi hálózatra, és használja a terminálban megjelenő Network URL-t.

3. Felhasználói Kézikönyv
3.1 Bejelentkezés
Az alkalmazás "Egyszerű Azonosítást" használ. Nem szükséges jelszó vagy e-mail cím.

Lépés: Írja be a nevét (pl. "Peti", "Anya") a kezdőképernyőn.

Működés: Ha a név már létezik, az alkalmazás betölti a korábbi adatokat. Ha új név, a rendszer automatikusan létrehoz egy új profilt alapértelmezett beállításokkal.

3.2 Főoldal (Dashboard)
Itt látható a napi összesítés.

Napi Cél Kártya: Zöld, animált kártya, amely mutatja a napi keretet és a százalékos teljesítést.

Progress Bar: Vizuális csík, amely telik, ahogy ételeket ad hozzá.

Napló: Alul listázza a mai napon rögzített ételeket.

Törlés: A lista elemei mellett található kuka (🗑️) ikonnal törölhető egy hibás bejegyzés.

3.3 Étel Hozzáadása
A bal oldali menüben válassza az "Étel Hozzáadása" opciót.

Adja meg az étel nevét (pl. "Banán").

Adja meg a kalóriaértéket (kcal).

Kattints a "Hozzáadás" gombra.

Visszajelzés: Sikeres mentés esetén egy felugró üzenet (Toast) jelzi a rögzítést.

3.4 Profil és Beállítások
Itt módosíthatja fizikai adatait és céljait.

Szerkesztés: Nyissa le az "Adatok szerkesztése" fület.

Adatok: Súly, Magasság, Kor, Célsúly.

Automatikus Számítás: A "Mentés" gomb megnyomásakor az alkalmazás a Mifflin-St Jeor képlet alapján azonnal újraszámolja a napi kalóriakeretét a megadott cél (Fogyás/Hízás) alapján.

4. Technikai Dokumentáció
Ez a fejezet fejlesztőknek szól a kód karbantartásához.

4.1 Rendszerarchitektúra
Frontend: Streamlit (Python alapú Web UI).

Backend: Python 3.

Adatbázis: SQLite (eatsmart.db fájl).

Stílus: Custom CSS (st.markdown-ba ágyazva) a modern megjelenésért.

4.2 Adatbázis Szerkezet
Az alkalmazás két táblát kezel.

1. tábla: users (Felhasználói adatok) | Oszlopnév | Típus | Leírás | | :--- | :--- | :--- | | username | TEXT (PK) | Egyedi azonosító (a beírt név kisbetűsítve/eredetiben). | | display_name | TEXT | A felületen megjelenített név. | | weight | REAL | Testsúly (kg). | | height | REAL | Magasság (cm). | | age | INTEGER | Életkor (év). | | gender | TEXT | Nem ("Férfi"/"Nő"). | | goal | TEXT | Cél típusa ("Fogyás", "Hízás", "Súlytartás"). | | target_weight | REAL | A kívánt testsúly. | | daily_target | INTEGER | Számított napi kalóriakeret (TDEE +/- 500). |

2. tábla: food_log (Étkezési napló) | Oszlopnév | Típus | Leírás | | :--- | :--- | :--- | | id | INTEGER (PK) | Automatikus azonosító. | | username | TEXT | Kapcsolat a users táblához. | | food_name | TEXT | Étel megnevezése. | | calories | INTEGER | Kalóriaérték. | | date | TEXT | Dátum (YYYY-MM-DD formátum). |

4.3 Főbb Függvények (app.py)
init_db(): Létrehozza a szükséges táblákat, ha azok nem léteznek. Induláskor fut le.

calculate_calories(...):

Kiszámolja a BMR-t (Alapanyagcsere).

Beszorozza az aktivitási szinttel (1.3 - alapértelmezett).

Korrigálja a célnak megfelelően (-500 kcal fogyásnál, +500 kcal hízásnál).

add_food(...): Tranzakcióbiztos SQL beszúrás hibakezeléssel (try-except block).

delete_food(...): Törlés ID alapján.

5. Hibaelhárítás
Hiba: sqlite3.OperationalError: no such column...

Ok: Az adatbázis szerkezete megváltozott a kódban, de a régi fájl még ott van.

Megoldás: Állítsa le a programot, törölje az eatsmart.db fájlt, és indítsa újra.

Hiba: A mobilon nem tölt be az oldal.

Megoldás: Ellenőrizze, hogy a telefon és a számítógép ugyanazon a Wifi hálózaton van-e. Használja a "Network URL"-t a terminálból. Ellenőrizze a Windows Tűzfal beállításait.
