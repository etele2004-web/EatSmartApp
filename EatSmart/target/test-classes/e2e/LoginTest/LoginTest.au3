Run("java -jar EatSmartApp.jar")
WinWaitActive("EatSmart Bejelentkezés")

Send("teszt_user")
Send("{TAB}")

Send("titkosjelszo")
Send("{ENTER}")

If WinWaitActive("EatSmart Főoldal", "", 5) Then
    MsgBox(0, "Teszt Siker", "Sikeres bejelentkezés!")
Else
    MsgBox(0, "Teszt Hiba", "Nem sikerült belépni.")
EndIf
