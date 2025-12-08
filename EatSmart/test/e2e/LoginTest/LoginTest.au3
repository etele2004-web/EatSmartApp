; AutoIt teszt script a bejelentkezéshez
; Ez a script szimulálja a felhasználói interakciót

Run("java -jar EatSmartApp.jar")
WinWaitActive("EatSmart Bejelentkezés")

; Felhasználónév mező kitöltése
Send("teszt_user")
Send("{TAB}")

; Jelszó mező kitöltése
Send("titkosjelszo")
Send("{ENTER}")

; Ellenőrizzük, hogy sikerült-e belépni (megjelent-e a Főoldal)
If WinWaitActive("EatSmart Főoldal", "", 5) Then
    MsgBox(0, "Teszt Siker", "Sikeres bejelentkezés!")
Else
    MsgBox(0, "Teszt Hiba", "Nem sikerült belépni.")
EndIf