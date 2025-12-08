import streamlit as st
import sqlite3
from datetime import datetime

# --- FÜGGVÉNY: ÁLLANDÓ FELHASZNÁLÓ ---
FIXED_USER_ID = "GUEST_ID" # Egyedi azonosító, amit nem mutatunk
DEFAULT_DISPLAY_NAME = "Vendég"

# --- 1. KONFIGURÁCIÓ ---
st.set_page_config(page_title="EatSmart", page_icon="🍏", layout="centered")

# --- 2. STÍLUS (CSS) ---
st.markdown("""
    <style>
    .main-card {
        background: linear-gradient(135deg, #10b981, #047857);
        padding: 20px; border-radius: 20px; color: white;
        margin-bottom: 20px; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
    }
    .stat-card {
        background-color: #f8fafc; padding: 15px; border-radius: 15px;
        text-align: center; border: 2px solid #e2e8f0; box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
    .stat-value { color: #047857; font-size: 1.4rem; font-weight: bold; margin: 0; }
    .stat-label { color: #64748b; font-size: 0.8rem; text-transform: uppercase; margin: 0;}
    .stButton button[kind="secondary"] { color: #ef4444; border-color: #fca5a5; }
    .stButton button[kind="secondary"]:hover { border-color: #ef4444; background-color: #fef2f2; }
    </style>
""", unsafe_allow_html=True)

# --- 3. ADATBÁZIS KEZELÉS ---
def init_db():
    """Adatbázis inicializálása, felvéve a display_name oszlopot."""
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    # JAVÍTVA: Hozzáadva a display_name oszlop
    c.execute('''CREATE TABLE IF NOT EXISTS users
                 (username TEXT PRIMARY KEY, display_name TEXT, 
                  weight REAL, height REAL, age INTEGER, gender TEXT, 
                  goal TEXT, target_weight REAL, daily_target INTEGER)''')
    c.execute('''CREATE TABLE IF NOT EXISTS food_log
                 (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, food_name TEXT, calories INTEGER, date TEXT, time TEXT)''')
    conn.commit()
    conn.close()

def get_user_data(username):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("SELECT * FROM users WHERE username=?", (username,))
    user = c.fetchone()
    conn.close()
    return user

# JAVÍTVA: display_name hozzáadva a paraméterekhez (9 paraméter)
def create_guest_user(username, display_name, weight, height, age, gender, goal, target_weight, daily_target):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("INSERT INTO users VALUES (?,?,?,?,?,?,?,?,?)", 
              (username, display_name, weight, height, age, gender, goal, target_weight, daily_target))
    conn.commit()
    conn.close()

# JAVÍTVA: display_name hozzáadva a frissítéshez
def update_user_data(username, display_name, weight, height, age, gender, goal, target_weight, daily_target):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("""UPDATE users 
                 SET display_name=?, weight=?, height=?, age=?, gender=?, goal=?, target_weight=?, daily_target=?
                 WHERE username=?""", 
              (display_name, weight, height, age, gender, goal, target_weight, daily_target, username))
    conn.commit()
    conn.close()

def add_food(username, food_name, calories):
    try:
        conn = sqlite3.connect('eatsmart.db')
        c = conn.cursor()
        now_date = datetime.now().strftime("%Y-%m-%d")
        now_time = datetime.now().strftime("%H:%M")
        c.execute("INSERT INTO food_log (username, food_name, calories, date, time) VALUES (?,?,?,?,?)", 
                  (username, food_name, int(calories), now_date, now_time))
        conn.commit()
        conn.close()
        return True
    except sqlite3.Error as e:
        print(f"SQL HIBA AZ ÉTEL MENTÉSEKOR: {e}")
        st.error("Hiba történt a mentés során. Kérlek ellenőrizd a terminált.")
        return False

def delete_food(food_id):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("DELETE FROM food_log WHERE id=?", (food_id,))
    conn.commit()
    conn.close()

def get_today_food(username):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    today = datetime.now().strftime("%Y-%m-%d")
    c.execute("SELECT id, food_name, calories FROM food_log WHERE username=? AND date=?", (username, today))
    data = c.fetchall()
    conn.close()
    return data

# --- 4. SZÁMÍTÁSI LOGIKA ---
def calculate_calories(weight, height, age, gender, goal):
    if gender == "Férfi":
        bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
    else:
        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161
    tdee = bmr * 1.3
    if goal == "Fogyás": return int(tdee - 500)
    elif goal == "Hízás": return int(tdee + 500)
    else: return int(tdee)

def determine_goal(current, target):
    if target < current: return "Fogyás"
    elif target > current: return "Hízás"
    else: return "Súlytartás"

# --- 5. FŐPROGRAM INDÍTÁSA ---
init_db()

# Ellenőrzés, hogy létezik-e a FIX "Vendég" profil (SINGLE-USER MODE)
user_data = get_user_data(FIXED_USER_ID)

if user_data is None:
    # Ha nincs, létrehozunk egy alapértelmezett profilt
    default_target = calculate_calories(80.0, 175, 30, "Férfi", "Fogyás")
    # JAVÍTVA: display_name-t is létrehozzuk
    create_guest_user(FIXED_USER_ID, DEFAULT_DISPLAY_NAME, 80.0, 175, 30, "Férfi", "Fogyás", 75.0, default_target)
    user_data = get_user_data(FIXED_USER_ID) # Betöltjük a friss adatot

st.session_state['current_user'] = user_data

# --- B) ALKALMAZÁS ELINDÍTÁSA ---

# Adatok kicsomagolása (Figyelem: az indexek ELCSÚSZTAK 1-gyel a display_name miatt!)
# 0:username(ID), 1:display_name, 2:weight, 3:height, 4:age, 5:gender, 6:goal, 7:target_w, 8:target_cal
u_id, u_name, u_weight, u_height, u_age, u_gender, u_goal, u_target_weight, u_target = st.session_state['current_user']

# OLDALSÁV (MENÜ)
with st.sidebar:
    st.title(f"👋 {u_name}") # A szerkeszthető nevet mutatjuk
    page = st.radio("Menü", ["Főoldal", "Étel Hozzáadása", "Profil"])
    
    st.write("---")
    if st.button("🔄 Adatok Frissítése"):
        st.session_state['current_user'] = get_user_data(u_id)
        st.rerun()
    st.write("---")
    
# 1. OLDAL: FŐOLDAL (Dashboard)
if page == "Főoldal":
    mai_etelek = get_today_food(u_id) # Az ID-t használjuk a lekérdezéshez
    mai_kaloria = sum(t[2] for t in mai_etelek)
    maradek = u_target - mai_kaloria
    szazalek = min(mai_kaloria / u_target, 1.0) * 100

    # Nagy kártya
    st.markdown(f"""
        <div class="main-card">
            <h3 style="margin:0;">Napi Cél: {u_target} kcal</h3>
            <div style="display:flex; justify-content:space-between; align-items:center; margin-top:20px;">
                <div><p style="margin:0; opacity:0.8;">Hátralévő</p><h1 style="margin:0; font-size:3em;">{maradek}</h1></div>
                <div style="text-align:right;"><p style="margin:0; opacity:0.8;">Bevitt</p><h2 style="margin:0;">{mai_kaloria}</h2></div>
            </div>
            <div style="background:rgba(255,255,255,0.3); height:10px; border-radius:5px; margin-top:15px;">
                <div style="background:white; width:{szazalek}%; height:100%; border-radius:5px;"></div>
            </div>
        </div>
    """, unsafe_allow_html=True)

    # Napló lista
    st.subheader(f"{u_name} mai étkezései")
    if not mai_etelek:
        st.info("Még üres a mai naplód.")
    else:
        for eid, nev, kal in mai_etelek:
            c1, c2, c3 = st.columns([3, 1, 1])
            c1.write(f"🍽 **{nev}**")
            c2.write(f"**{kal}** kcal")
            if c3.button("🗑️", key=f"del_{eid}", type="secondary"):
                delete_food(eid)
                st.rerun()
            st.markdown("---")


# 2. OLDAL: ÉTEL HOZZÁADÁSA
elif page == "Étel Hozzáadása":
    st.title("Mit ettél? 🥗")
    with st.form("add_food"):
        nev = st.text_input("Étel neve (pl. Alma, Szendvics)")
        kal = st.number_input("Kalória (kcal)", min_value=1, step=10)
        if st.form_submit_button("Hozzáadás"):
            if nev and kal > 0:
                if add_food(u_id, nev, int(kal)):
                    st.success(f"Hozzáadva: {nev}")
                    st.rerun() 
            else:
                st.error("Adj meg nevet és pozitív kalória értéket!")

# 3. OLDAL: PROFIL ÉS SZERKESZTÉS
elif page == "Profil":
    st.title(f"{u_name} profilja")
    
    # Cél kijelzése
    delta = u_target_weight - u_weight
    uzenet = "Tartod a súlyod."
    if delta < 0: uzenet = f"Még {abs(delta):.1f} kg fogyás a célig."
    elif delta > 0: uzenet = f"Még {delta:.1f} kg hízás a célig."
    
    st.success(f"🎯 **Célsúlyod: {u_target_weight} kg** ({uzenet})")

    # Statisztikák
    c1, c2, c3 = st.columns(3)
    with c1: st.markdown(f'<div class="stat-card"><p class="stat-value">{u_weight}</p><p class="stat-label">Jelenlegi kg</p></div>', unsafe_allow_html=True)
    with c2: st.markdown(f'<div class="stat-card"><p class="stat-value">{u_target_weight}</p><p class="stat-label">Célsúly</p></div>', unsafe_allow_html=True)
    with c3: st.markdown(f'<div class="stat-card"><p class="stat-value">{u_age}</p><p class="stat-label">év</p></div>', unsafe_allow_html=True)
    
    st.write("---")

    # SZERKESZTÉS LEHETŐSÉG
    with st.expander("✏️ Adatok módosítása"):
        with st.form("edit_profile"):
            # JAVÍTVA: A név is szerkeszthető
            uj_nev = st.text_input("Megjelenített név", value=u_name)
            
            uj_suly = st.number_input("Jelenlegi súly", value=float(u_weight))
            uj_celsuly = st.number_input("Célsúly", value=float(u_target_weight))
            uj_kor = st.number_input("Kor", value=int(u_age))
            uj_magassag = st.number_input("Magasság", value=int(u_height))
            
            if st.form_submit_button("Mentés"):
                # Új cél és kalória számítása
                uj_cel = determine_goal(uj_suly, uj_celsuly)
                uj_napi_kaloria = calculate_calories(uj_suly, uj_magassag, uj_kor, u_gender, uj_cel)
                
                # Adatbázis frissítése a Vendég profilon
                update_user_data(u_id, uj_nev, uj_suly, uj_magassag, uj_kor, u_gender, uj_cel, uj_celsuly, uj_napi_kaloria)
                
                # Session frissítése és újratöltés
                st.session_state['current_user'] = get_user_data(u_id)
                st.success("Sikeres mentés!")
                st.rerun()
