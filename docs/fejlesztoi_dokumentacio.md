Eatsmart Alkalmazás - Fejlesztői Dokumentáció

Áttekintés

Ez a dokumentum az Eatsmart alkalmazás Python alapú háttérrendszerének (backend) osztályait és metódusait írja le. A rendszer célja a napi kalóriabevitel és makrotápanyagok nyomon követése.

Osztályok

1. Food osztály

Ez az osztály reprezentál egy konkrét élelmiszert vagy ételt az alkalmazásban. Tárolja a tápanyagértékeket.

Konstruktor: __init__

def __init__(self, name: str, calories: int, protein: float, carbs: float, fat: float)


Létrehoz egy új étel objektumot.

Paraméterek:

name (str): Az étel megnevezése (pl. "Alma").

calories (int): Kalóriatartalom (kcal).

protein (float): Fehérjetartalom grammban.

carbs (float): Szénhidráttartalom grammban.

fat (float): Zsírtartalom grammban.

Metódusok:

__str__(self) -> str

Visszaadja az étel szöveges reprezentációját, beleértve a nevét és a makrókat.

2. User osztály

A felhasználói profil adatait és céljait kezelő osztály.

Konstruktor: __init__

def __init__(self, user_id: int, username: str, current_weight: float)


Felhasználó inicializálása alapvető adatokkal.

Paraméterek:

user_id (int): A felhasználó egyedi azonosítója.

username (str): A felhasználó neve.

current_weight (float): Jelenlegi testsúly kg-ban.

Metódusok:

set_daily_calorie_goal(self, goal: int)

Beállítja a felhasználó napi kalóriakeretét.

Paraméterek: goal (int) - A napi cél kcal-ban.

calculate_bmi(self, height_m: float = 1.70) -> float

Kiszámolja a felhasználó testtömegindexét (BMI).

Paraméterek: height_m (float) - Magasság méterben (alapértelmezett: 1.70).

Visszatérési érték: A számított BMI (float).

3. DailyLog osztály

Ez az osztály felelős egy adott nap étkezéseinek rögzítéséért és a napi összegzésért.

Konstruktor: __init__

def __init__(self)


Létrehoz egy üres naplót a mai dátummal.

Metódusok:

add_food(self, food: Food)

Hozzáad egy ételt a napi listához.

Paraméterek: food (Food objektum) - A rögzítendő étel.

get_total_calories(self) -> int

Összesíti a naplóban lévő ételek kalóriatartalmát.

Visszatérési érték: Az összes bevitt kalória (int).

print_summary(self)

Kiírja a konzolra a napi étkezések listáját és az összesített kalóriát.

Használati Példa (Main)

A kód futtatásakor (if __name__ == "__main__":) a rendszer:

Létrehoz egy felhasználót (Lakatos Zsolt).

Létrehoz minta ételeket (Alma, Csirkemell, Rizs).

Hozzáadja ezeket a napi naplóhoz.

Kiszámolja a hátralévő kalóriakeretet és visszajelzést ad.
