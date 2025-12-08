import streamlit as st
from datetime import datetime

# --- KONFIGURÁCIÓ ---
st.set_page_config(page_title="EatSmart", page_icon="🍏", layout="centered")

# --- STÍLUS (CSS) - ITT TÖRTÉNT A JAVÍTÁS ---
st.markdown("""
    <style>
    /* Fő kártya (Zöld gradiens) */
    .main-card {
        background: linear-gradient(135deg, #10b981, #047857);
        padding: 20px; border-radius: 20px; color: white;
        margin-bottom: 20px; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
    }

    /* Profil kártyák (JAVÍTVA: Árnyék, szín, keret) */
    .stat-card {
        background-color: #f1f5f9; /* Enyhe szürke háttér, nem fehér */
        padding: 20px;
        border-radius: 15px;
        text-align: center;
        border: 2px solid #cbd5e1; /* Erősebb, sötétebb keret */
        box-shadow: 0 4px 6px rgba(0,0,0,0.1); /* Árnyék, hogy kiemelkedjen */
        transition: transform 0.2s; /* Kis animáció */
    }
    .stat-card:hover {
        transform: scale(1.02); /* Ha ráviszed az egeret, kicsit megnő */
        border-color: #10b981; /* Zöld lesz a keret */
    }

    /* Számok és szövegek stílusa a kártyán belül */
    .stat-value {
        color: #047857;
        font-size: 1.8rem;
        font-weight: bold;
        margin: 0;
    }
    .stat-label {
        color: #64748b;
        font-size: 0.9rem;
        text-transform: uppercase;
        letter-spacing: 1px;
    }
    </style>
""", unsafe_allow_html=True)

# --- ADATKEZELÉS ---
if 'user' not in st.session_state:
    st.session_state['user'] = None

if 'food_log' not in st.session_state:
    st.session_state['food_log'] = []


# --- FÜGGVÉNYEK ---
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


# --- 1. REGISZTRÁCIÓ (HA NINCS ADAT) ---
if st.session_state['user'] is None:
    st.title("👋 Üdv az EatSmart-ban!")
    st.info("Kezdéshez töltsd ki az adataidat!")

    with st.form("reg_form"):
        nev = st.text_input("Hogy hívnak?")
        nem = st.radio("Nemed", ["Férfi", "Nő"])
        col1, col2 = st.columns(2)
        suly = col1.number_input("Súly (kg)", min_value=30, max_value=200, value=70)
        magassag = col2.number_input("Magasság (cm)", min_value=100, max_value=250, value=170)
        kor = st.number_input("Életkor", min_value=10, max_value=100, value=30)
        cel = st.selectbox("Mi a célod?", ["Fogyás", "Súlytartás", "Hízás"])

        submit = st.form_submit_button("Indítás! 🚀")

        if submit and nev:
            napi_cel = calculate_calories(suly, magassag, kor, nem, cel)
            st.session_state['user'] = {
                "name": nev, "weight": suly, "height": magassag,
                "age": kor, "gender": nem, "goal": cel, "daily_target": napi_cel
            }
            st.rerun()

# --- 2. AZ ALKALMAZÁS ---
else:
    user = st.session_state['user']

    with st.sidebar:
        st.title(f"🍏 {user['name']}")
        page = st.radio("Menü", ["Főoldal", "Étel Hozzáadása", "Profil"])

        st.write("---")
        if st.button("Kijelentkezés"):
            st.session_state['user'] = None
            st.session_state['food_log'] = []
            st.rerun()

    # --- FŐOLDAL ---
    if page == "Főoldal":
        mai_datum = datetime.now().strftime("%Y-%m-%d")
        mai_kaloria = 0
        mai_etelek = []
        for etel in st.session_state['food_log']:
            if etel['date'] == mai_datum:
                mai_kaloria += etel['calories']
                mai_etelek.append(etel)

        maradek = user['daily_target'] - mai_kaloria
        szazalek = min(mai_kaloria / user['daily_target'], 1.0) * 100

        st.markdown(f"""
            <div class="main-card">
                <h3>Napi Cél: {user['daily_target']} kcal</h3>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:20px;">
                    <div>
                        <p style="margin:0; opacity:0.8;">Hátralévő</p>
                        <h1 style="margin:0; font-size:3em;">{maradek}</h1>
                    </div>
                     <div style="text-align:right;">
                        <p style="margin:0; opacity:0.8;">Bevitt</p>
                        <h2 style="margin:0;">{mai_kaloria}</h2>
                    </div>
                </div>
                <div style="background:rgba(255,255,255,0.3); height:10px; border-radius:5px; margin-top:15px;">
                    <div style="background:white; width:{szazalek}%; height:100%; border-radius:5px;"></div>
                </div>
            </div>
        """, unsafe_allow_html=True)

        st.subheader("Mai napló")
        if not mai_etelek:
            st.info("Még nem ettél ma semmit! 🥗")
        else:
            for item in mai_etelek:
                col1, col2 = st.columns([4, 1])
                col1.write(f"🍽 **{item['name']}**")
                col2.write(f"**{item['calories']}** kcal")
                st.markdown("---")

    # --- ÉTEL HOZZÁADÁSA ---
    elif page == "Étel Hozzáadása":
        st.title("Mit ettél? 🥗")

        with st.form("food_form"):
            etel_nev = st.text_input("Étel neve")
            etel_kaloria = st.number_input("Kalória (kcal)", min_value=1, step=10)
            submit_food = st.form_submit_button("Hozzáadás")

            if submit_food and etel_nev:
                uj_tetel = {
                    "name": etel_nev, "calories": int(etel_kaloria),
                    "date": datetime.now().strftime("%Y-%m-%d")
                }
                st.session_state['food_log'].append(uj_tetel)
                st.success(f"Hozzáadva: {etel_nev}")

    # --- PROFIL (JAVÍTOTT MEGJELENÉS) ---
    elif page == "Profil":
        st.title("Profilod")
        st.write("Itt láthatod a jelenlegi adataidat.")

        col1, col2, col3 = st.columns(3)

        # Itt használjuk az új CSS osztályokat
        with col1:
            st.markdown(f"""
            <div class="stat-card">
                <p class="stat-value">{user["weight"]}</p>
                <p class="stat-label">kg</p>
            </div>
            """, unsafe_allow_html=True)

        with col2:
            st.markdown(f"""
            <div class="stat-card">
                <p class="stat-value">{user["height"]}</p>
                <p class="stat-label">cm</p>
            </div>
            """, unsafe_allow_html=True)

        with col3:
            st.markdown(f"""
            <div class="stat-card">
                <p class="stat-value">{user["age"]}</p>
                <p class="stat-label">év</p>
            </div>
            """, unsafe_allow_html=True)

        st.write("")
        st.markdown("### Célod")
        st.info(f"Mivel a célod **{user['goal']}**, ezért a napi kereted: **{user['daily_target']} kcal**.")
