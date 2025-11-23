# Egyszerű teszteléshez, hogy látszódjon mit csinál a program

from etel import Etel
from etkezes import Etkezes
from kaloriaszamolo import KaloriaSzamolo

def main():
    alma = Etel("Alma", 52, 0.3, 0.2, 14)
    kifli = Etel("Kifli", 125, 4, 1.5, 25)

    reggeli = Etkezes("Reggeli")
    reggeli.hozzaad(alma)
    reggeli.hozzaad(kifli)

    szamolo = KaloriaSzamolo()
    szamolo.hozzaad_etkezes(reggeli)

    print("Reggeli összes kalória:", reggeli.ossz_kaloria(), "kcal")
    print("Napi összes kalória:", szamolo.napi_ossz(), "kcal")

if __name__ == "__main__":
    main()
