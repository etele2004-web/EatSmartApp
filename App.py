import streamlit as st
import sqlite3
from datetime import datetime
import time

# --- 1. KONFIGURÁCIÓ ---
st.set_page_config(page_title="EatSmart", page_icon="🍏", layout="centered")

# --- 2. STÍLUS (CSS) ---
st.markdown("""
    <style>
    /* Fő kártya: Szebb árnyék és gradiens */
    .main-card {
        background: linear-gradient(135deg, #059669, #10b981);
        padding: 25px; 
        border-radius: 25px; 
        color: white;
        margin-bottom: 25px; 
        box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
        transition: transform 0.3s ease;
    }
    .main-card:hover {
        transform: translateY(-5px);
    }

    /* Statisztika kártyák */
    .stat-card {
        background-color: white; 
        padding: 20px; 
        border-radius: 20px;
        text-align: center; 
        border: 1px solid #e5e7eb; 
        box-shadow: 0 4px 6px rgba(0,0,0,0.05);
        transition: all 0.3s ease;
    }
    .stat-card:hover {
        transform: scale(1.03);
        border-color: #10b981;
        box-shadow: 0 10px 15px rgba(0,0,0,0.1);
    }
    .stat-value { color: #047857; font-size: 1.6rem; font-weight: 800; margin: 0; }
    .stat-label { color: #64748b; font-size: 0.85rem; text-transform: uppercase; font-weight: 600; letter-spacing: 1px; }

    /* Gombok modernizálása */
    .stButton>button {
        width: 100%;
        border-radius: 12px;
        font-weight: bold;
        height: 3em;
        transition: background-color 0.2s;
    }

    /* Törlés gomb (kicsi piros) */
    div[data-testid="stHorizontalBlock"] button[kind="secondary"] {
        background-color: #fee2e2;
        color: #ef4444;
        border: none;
    }
    div[data-testid="stHorizontalBlock"] button[kind="secondary"]:hover {
        background-color: #ef4444;
        color: white;
    }
    </style>
""", unsafe_allow_html=True)

# --- 3. ADATBÁZIS & LOGIKA ---
FIXED_USER_ID = "GUEST_ID"
DEFAULT_DISPLAY_NAME = "Vendég"


def init_db():
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
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


def create_guest_user(username, display_name, weight, height, age, gender, goal, target_weight, daily_target):
    conn = sqlite3.connect('eatsmart.db')
    c = conn.cursor()
    c.execute("INSERT INTO users VALUES (?,?,?,?,?,?,?,?,?)",
              (username, display_name, weight, height, age, gender, goal, target_weight, daily_target))
    conn.commit()
    conn.close()


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
        c.execute("INSERT INTO food_log (username, food_name, calories, date) VALUES (?,?,?,?)",
                  (username, food_name, int(calories), now_date))
        conn.commit()
        return True
    except sqlite3.Error:
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


def calculate_calories(weight, height, age, gender, goal):
    if gender == "Férfi":
        bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
    else:
        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161
    tdee = bmr * 1.3
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


# --- 4. INDÍTÁS ---
init_db()

user_data = get_user_data(FIXED_USER_ID)
if user_data is None:
    default_target = calculate_calories(80.0, 175, 30, "Férfi", "Fogyás")
    create_guest_user(FIXED_USER_ID, DEFAULT_DISPLAY_NAME, 80.0, 175, 30, "Férfi", "Fogyás", 75.0, default_target)
    user_data = get_user_data(FIXED_USER_ID)

st.session_state['current_user'] = user_data
u_id, u_name, u_weight, u_height, u_age, u_gender, u_goal, u_target_weight, u_target = st.session_state['current_user']

# --- UI LOGIKA ---

with st.sidebar:
    st.image("https://cdn-icons-png.flaticon.com/512/2921/2921822.png", width=100)
    st.title(f"Szia, {u_name}!")

    page = st.radio("Navigáció", ["Főoldal", "Étel Hozzáadása", "Profil"], label_visibility="collapsed")

    st.write("---")
    if st.button("🔄 Frissítés"):
        st.session_state['current_user'] = get_user_data(u_id)
        st.rerun()

# 1. FŐOLDAL
if page == "Főoldal":
    mai_etelek = get_today_food(u_id)
    mai_kaloria = sum(t[2] for t in mai_etelek)
    maradek = u_target - mai_kaloria

    szazalek = min(mai_kaloria / u_target, 1.0) * 100

    st.caption(f"📅 {datetime.now().strftime('%Y. %m. %d.')}")

    # JAVÍTÁS ITT: A HTML kód most teljesen balra van húzva, nincs behúzás!
    st.markdown(f"""
<div class="main-card">
<div style="display:flex; justify-content:space-between; align-items:flex-end;">
<div>
<span style="font-size:0.9rem; opacity:0.9; text-transform:uppercase; letter-spacing:1px;">Napi Cél</span><br>
<span style="font-size:2.5rem; font-weight:bold;">{u_target}</span> <span style="font-size:1rem;">kcal</span>
</div>
<div style="text-align:right;">
<span style="font-size:2rem; font-weight:bold;">{int(szazalek)}%</span>
</div>
</div>
<div style="margin-top:20px; margin-bottom:10px;">
<div style="background:rgba(255,255,255,0.2); height:12px; border-radius:10px; overflow:hidden;">
<div style="background:white; width:{szazalek}%; height:100%; border-radius:10px; transition: width 0.5s;"></div>
</div>
</div>
<div style="display:flex; justify-content:space-between; margin-top:15px; font-size:0.9rem;">
<div>🔥 Bevitt: <b>{mai_kaloria}</b></div>
<div>🍽️ Maradék: <b>{maradek}</b></div>
</div>
</div>
""", unsafe_allow_html=True)

    st.subheader("Mai napló")
    if not mai_etelek:
        st.info("👋 Még nem ettél ma semmit. Adj hozzá valamit a menüben!")
    else:
        for eid, nev, kal in mai_etelek:
            with st.container():
                c1, c2, c3 = st.columns([5, 2, 1])
                c1.write(f"**{nev}**")
                c2.write(f"{kal} kcal")
                if c3.button("🗑️", key=f"del_{eid}", type="secondary", help="Törlés"):
                    delete_food(eid)
                    st.toast("Étel törölve!", icon="🗑️")
                    time.sleep(0.5)
                    st.rerun()
                st.markdown("---")

# 2. HOZZÁADÁS
elif page == "Étel Hozzáadása":
    st.markdown("## 🥗 Mit ettél?")
    st.write("Rögzítsd a fogyasztásodat gyorsan.")

    with st.container():
        with st.form("add_food", clear_on_submit=True):
            col1, col2 = st.columns([2, 1])
            nev = col1.text_input("Étel neve", placeholder="Pl. Csirkemell rizzsel")
            kal = col2.number_input("Kalória (kcal)", min_value=1, step=10)

            submitted = st.form_submit_button("Hozzáadás a naplóhoz", use_container_width=True)

            if submitted:
                if nev and kal > 0:
                    add_food(u_id, nev, int(kal))
                    st.toast(f"Hozzáadva: {nev} ({kal} kcal)", icon="✅")
                    time.sleep(0.8)
                    st.rerun()
                else:
                    st.toast("Hiba: Adj meg nevet és kalóriát!", icon="❌")

# 3. PROFIL
elif page == "Profil":
    st.markdown("## 👤 Beállítások")

    st.info(f"Jelenlegi célod: **{u_target_weight} kg** elérése.")

    c1, c2, c3 = st.columns(3)
    with c1:
        st.markdown(
            f'<div class="stat-card"><div class="stat-value">{u_weight}</div><div class="stat-label">Súly (kg)</div></div>',
            unsafe_allow_html=True)
    with c2:
        st.markdown(
            f'<div class="stat-card"><div class="stat-value">{u_height}</div><div class="stat-label">Magasság</div></div>',
            unsafe_allow_html=True)
    with c3:
        st.markdown(
            f'<div class="stat-card"><div class="stat-value">{u_age}</div><div class="stat-label">Kor (év)</div></div>',
            unsafe_allow_html=True)

    st.write("")
    st.write("")

    with st.expander("✏️ Adatok szerkesztése", expanded=True):
        with st.form("edit_profile"):
            uj_nev = st.text_input("Megjelenített név", value=u_name)

            col_a, col_b = st.columns(2)
            uj_suly = col_a.number_input("Súly (kg)", value=float(u_weight))
            uj_celsuly = col_b.number_input("Célsúly (kg)", value=float(u_target_weight))

            col_c, col_d = st.columns(2)
            uj_magassag = col_c.number_input("Magasság (cm)", value=int(u_height))
            uj_kor = col_d.number_input("Kor", value=int(u_age))

            if st.form_submit_button("Mentés és Újraszámolás", use_container_width=True):
                uj_cel = determine_goal(uj_suly, uj_celsuly)
                uj_napi_kaloria = calculate_calories(uj_suly, uj_magassag, uj_kor, u_gender, uj_cel)

                update_user_data(u_id, uj_nev, uj_suly, uj_magassag, uj_kor, u_gender, uj_cel, uj_celsuly,
                                 uj_napi_kaloria)

                st.session_state['current_user'] = get_user_data(u_id)
                st.toast("Profil sikeresen frissítve!", icon="🎉")
                time.sleep(1)
                st.rerun()
