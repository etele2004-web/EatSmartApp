import streamlit as st
import sqlite3
from datetime import datetime

# --- 1. KONFIGURÁCIÓ ---
st.set_page_config(page_title="EatSmart", page_icon="🍏", layout="centered")

# --- 2. STÍLUS (CSS) ---
st.markdown("""
    <style>
    /* Fő kártya (Zöld gradiens) */
    .main-card {
        background: linear-gradient(135deg, #10b981, #047857);
        padding: 20px; border-radius: 20px; color: white;
        margin-bottom: 20px; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
    }
    /* Statisztika kártyák */
    .stat-card {
        background-color: #f8fafc; padding: 15px; border-radius: 15px;
        text-align: center; border: 2px solid #e2e8f0; box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
    .stat-value { color: #047857; font-size: 1.4rem; font-weight: bold; margin: 0; }
    .stat-label { color: #64748b; font-size: 0.8rem; text-transform: uppercase; margin: 0;}

    /* Piros törlés gomb */
    .stButton button[kind="secondary"] { color: #ef4444; border-color: #fca5a5; }
    .stButton button[kind="secondary"]:hover { border-color: #ef4444; background-color: #fef2f2; }
    </style>
""", unsafe_allow_html=True)


# --- 3. ADATBÁZIS KEZELÉS ---
def init_db():
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    # Felhasználók tábla
    c.execute('''CREATE TABLE IF NOT EXISTS users
                 (username TEXT PRIMARY KEY, weight REAL, height REAL, age INTEGER, gender TEXT, 
                  goal TEXT, target_weight REAL, daily_target INTEGER)''')
    # Ételek tábla
    c.execute('''CREATE TABLE IF NOT EXISTS food_log
                 (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, food_name TEXT, calories INTEGER, date TEXT, time TEXT)''')
    conn.commit()
    conn.close()


def get_user(username):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("SELECT * FROM users WHERE username=?", (username,))
    user = c.fetchone()
    conn.close()
    return user


def create_user(username, weight, height, age, gender, goal, target_weight, daily_target):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("INSERT INTO users VALUES (?,?,?,?,?,?,?,?)",
              (username, weight, height, age, gender, goal, target_weight, daily_target))
    conn.commit()
    conn.close()


def update_user_data(username, weight, height, age, gender, goal, target_weight, daily_target):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("""UPDATE users 
                 SET weight=?, height=?, age=?, gender=?, goal=?, target_weight=?, daily_target=?
                 WHERE username=?""",
              (weight, height, age, gender, goal, target_weight, daily_target, username))
    conn.commit()
    conn.close()


def add_food(username, food_name, calories):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    now_date = datetime.now().strftime("%Y-%m-%d")
    now_time = datetime.now().strftime("%H:%M")
    c.execute("INSERT INTO food_log (username, food_name, calories, date, time) VALUES (?,?,?,?,?)",
              (username, food_name, calories, now_date, now_time))
    conn.commit()
    conn.close()


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
    # Mifflin-St Jeor képlet
    if gender == "Férfi":
        bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
    else:
        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161

    tdee = bmr * 1.3  # Átlagos aktivitás

    if goal == "Fogyás":
        return int(tdee - 500)
    elif goal == "Hízás":
        return int(tdee + 500)
    else:
        return int(tdee)


def determine_goal(current, target):
    if target < current:
        return "Fogyás"
    elif target > current:
        return "Hízás"
    else:
        return "Súlytartás"


# --- 5. FŐPROGRAM ---
init_db()

if 'current_user' not in st.session_state:
    st.session_state['current_user'] = None

# --- A) BEJELENTKEZÉS / REGISZTRÁCIÓ ---
if st.session_state['current_user'] is None:
    st.title("🍏 EatSmart")
    st.info("Add meg a neved a belépéshez. Ha új vagy, regisztrálunk!")

    username_input = st.text_input("Felhasználónév").strip()

    if st.button("Tovább"):
        if username_input:
            user_data = get_user(username_input)
            if user_data:
                st.session_state['current_user'] = user_data
                st.rerun()
            else:
                st.session_state['new_username'] = username_input
                st.session_state['registering'] = True
                st.rerun()

    # Regisztrációs űrlap
    if st.session_state.get('registering'):
        st.write("---")
        st.subheader(f"Szia {st.session_state['new_username']}! Állítsuk be a célodat.")
        with st.form("reg_form"):
            nem = st.radio("Nemed", ["Férfi", "Nő"])
            c1, c2 = st.columns(2)
            suly = c1.number_input("Jelenlegi súly (kg)", 30.0, 200.0, 70.0)
            magassag = c2.number_input("Magasság (cm)", 100, 250, 170)
            kor = st.number_input("Kor", 10, 100, 30)
            st.markdown("### 🎯 Mi a célod?")
            celsuly = st.number_input("Hány kiló szeretnél lenni?", 30.0, 200.0, 70.0)

            if st.form_submit_button("Mentés és Indítás"):
                cel_tipus = determine_goal(suly, celsuly)
                napi_cel = calculate_calories(suly, magassag, kor, nem, cel_tipus)

                create_user(st.session_state['new_username'], suly, magassag, kor, nem, cel_tipus, celsuly, napi_cel)

                st.session_state['current_user'] = get_user(st.session_state['new_username'])
                st.rerun()

# --- B) BELÉPETT FELÜLET ---
else:
    # Adatok kicsomagolása
    u_name, u_weight, u_height, u_age, u_gender, u_goal, u_target_weight, u_target = st.session_state['current_user']

    # OLDALSÁV (MENÜ)
    with st.sidebar:
        st.title(f"👤 {u_name}")
        page = st.radio("Menü", ["Főoldal", "Étel Hozzáadása", "Profil"])

        st.write("---")
        # FRISSÍTÉS GOMB (Ez kell neked!)
        if st.button("🔄 Adatok Frissítése"):
            # Újra lekérjük a felhasználót az adatbázisból
            st.session_state['current_user'] = get_user(u_name)
            st.rerun()

        st.write("---")
        if st.button("Kijelentkezés"):
            st.session_state['current_user'] = None
            st.session_state['registering'] = False
            st.rerun()

    # 1. OLDAL: FŐOLDAL (Dashboard)
    if page == "Főoldal":
        mai_etelek = get_today_food(u_name)
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
        st.subheader("Mai étkezések")
        if not mai_etelek:
            st.info("Még üres a mai naplód.")
        else:
            for eid, nev, kal in mai_etelek:
                c1, c2, c3 = st.columns([3, 1, 1])
                c1.write(f"🍽 **{nev}**")
                c2.write(f"**{kal}** kcal")
                # Törlés gomb (másodlagos stílus - piros)
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
                if nev:
                    add_food(u_name, nev, int(kal))
                    st.success(f"Hozzáadva: {nev}")
                else:
                    st.error("Adj meg nevet!")

    # 3. OLDAL: PROFIL ÉS SZERKESZTÉS
    elif page == "Profil":
        st.title("Profilod")

        # Cél kijelzése
        delta = u_target_weight - u_weight
        uzenet = "Tartod a súlyod."
        if delta < 0:
            uzenet = f"Még {abs(delta):.1f} kg fogyás a célig."
        elif delta > 0:
            uzenet = f"Még {delta:.1f} kg hízás a célig."

        st.success(f"🎯 **Célsúlyod: {u_target_weight} kg** ({uzenet})")

        # Statisztikák
        c1, c2, c3 = st.columns(3)
        with c1:
            st.markdown(
                f'<div class="stat-card"><p class="stat-value">{u_weight}</p><p class="stat-label">kg</p></div>',
                unsafe_allow_html=True)
        with c2:
            st.markdown(
                f'<div class="stat-card"><p class="stat-value">{u_height}</p><p class="stat-label">cm</p></div>',
                unsafe_allow_html=True)
        with c3:
            st.markdown(f'<div class="stat-card"><p class="stat-value">{u_age}</p><p class="stat-label">év</p></div>',
                        unsafe_allow_html=True)

        st.write("---")

        # SZERKESZTÉS LEHETŐSÉG
        with st.expander("✏️ Adatok módosítása"):
            with st.form("edit_profile"):
                st.write("Változtak az adataid? Írd át itt:")
                uj_suly = st.number_input("Jelenlegi súly", value=float(u_weight))
                uj_celsuly = st.number_input("Célsúly", value=float(u_target_weight))
                uj_kor = st.number_input("Kor", value=int(u_age))
                uj_magassag = st.number_input("Magasság", value=int(u_height))

                if st.form_submit_button("Mentés"):
                    # Új cél és kalória számítása
                    uj_cel = determine_goal(uj_suly, uj_celsuly)
                    uj_napi_kaloria = calculate_calories(uj_suly, uj_magassag, uj_kor, u_gender, uj_cel)

                    # Adatbázis frissítése
                    update_user_data(u_name, uj_suly, uj_magassag, uj_kor, u_gender, uj_cel, uj_celsuly,
                                     uj_napi_kaloria)

                    # Session frissítése és újratöltés
                    st.session_state['current_user'] = get_user(u_name)
                    st.success("Sikeres mentés!")
                    st.rerun()
