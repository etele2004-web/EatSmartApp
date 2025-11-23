# Egy egyszerű osztály ételek kezelésére

class Etel:
    def __init__(self, nev, kaloria, feherje, zsir, szenhidrat):
        self.nev = nev
        self.kaloria = kaloria
        self.feherje = feherje
        self.zsir = zsir
        self.szenhidrat = szenhidrat

    def info(self):
        return f"{self.nev} - {self.kaloria} kcal"

