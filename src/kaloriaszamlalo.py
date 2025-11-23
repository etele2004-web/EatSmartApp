# Napi kalóriaszámítás több étkezésből

from etkezes import Etkezes

class KaloriaSzamolo:
    def __init__(self):
        self.etkezesek = []

    def hozzaad_etkezes(self, etkezes: Etkezes):
        self.etkezesek.append(etkezes)

    def napi_ossz(self):
        ossz = 0
        for e in self.etkezesek:
            ossz += e.ossz_kaloria()
        return ossz
