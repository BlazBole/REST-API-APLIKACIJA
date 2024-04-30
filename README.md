# Aplikacija za upravljanje inventarja

Ta repozitorij vsebuje kompleten sistem za upravljanje inventarja, ki ga sestavljata .NET REST API in mobilna aplikacija za Android, napisana v Kotlinu. Poleg tega so v repozitoriju na voljo tudi branche za .NET REST API, mobilno aplikacijo in branch za shranjevanje stored procedur.

## .NET REST API

.NET REST API je strežniški del aplikacije, ki obdeluje zahtevke iz mobilne aplikacije in zagotavlja podatke in funkcionalnosti uporabnikom. Ta branch vsebuje vse potrebne datoteke, vključno s kodo, ki obdeluje zahtevke uporabnikov, povezavo s podatkovno bazo in dokumentacijo.

### Funkcionalnosti
- Uporabniki lahko opravljajo različne operacije na svojem inventarju, vključno z dodajanjem, urejanjem in brisanjem elementov.
- Registracija in prijava uporabnikov ter upravljanje s profilom.
- Povezava s podatkovno bazo SQL Serverja in izvajanje stored procedur.

## Mobilna aplikacija (Kotlin)

Mobilna aplikacija, napisana v Kotlinu, je uporabniški vmesnik za upravljanje inventarja. Omogoča uporabnikom enostaven dostop do njihovega inventarja in izvajanje različnih operacij.

### Funkcionalnosti
- Registracija, prijava in upravljanje s profilom uporabnika.
- Dodajanje, urejanje in brisanje elementov iz inventarja.
- Skeniranje črtnih kodov za hitro dodajanje elementov.
- Povezava s strežnikom preko Retrofit2 za obdelavo HTTP API zahtevkov.

## Stored procedure

Branch s stored procedurami vsebuje SQL skripte za shranjevanje in izvajanje stored procedur v SQL Serverju. Te stored procedure se uporabljajo za obdelavo zahtevkov iz .NET REST API in zagotavljanje učinkovitega dostopa do podatkovne baze.

## Kako začeti

1. Prenesite repozitorij na svoj računalnik.
2. Zaženite .NET REST API in zagotovite, da je vzpostavljena povezava s podatkovno bazo.
3. Odpri projekt mobilne aplikacije v Android Studiu.
4. Povežite aplikacijo s .NET REST API in prilagodite potrebne nastavitve.
5. Zaženite aplikacijo in preizkusite njene funkcionalnosti.

## Avtor

Blaž Bole

## Izgled

![Image](image1.png)
![Image](image2.png)
![Image](image3.png)

