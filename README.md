# .NET REST API za upravljanje z uporabniki in inventarjem

## Opis

Moj .NET REST API predstavlja strežniški del aplikacije, ki obdeluje zahtevke iz mobilne aplikacije. API omogoča upravljanje z uporabniki in inventarjem ter uporablja shranjene procedure za izvajanje operacij v povezavi s podatkovno bazo.

## Funkcionalnosti

- Obdelava zahtevkov iz mobilne aplikacije
- Upravljanje z uporabniki in inventarjem
- Uporaba shranjenih procedur za manipulacijo s podatki

## Uporabljene tehnologije

- .NET Framework
- RESTful API
- Microsoft SQL Server Management Studio

## Navodila za uporabo

1. Namestite .NET Framework in Microsoft SQL Server Management Studio.
2. Uvozite projekt v razvojno okolje.
3. Konfigurirajte povezavo s podatkovno bazo v datoteki `appsettings.json`.
4. Zaženite API in preverite delovanje z uporabo različnih metod.

## Opis kontrol za obdelavo

### GET /api/Users

Vrne seznam vseh uporabnikov.

### POST /api/Users

Ustvari novega uporabnika.

### PUT /api/Users/{id}

Posodobi podatke obstoječega uporabnika z določenim ID-jem.

### DELETE /api/Users/{id}

Izbriše uporabnika z določenim ID-jem.

### GET /api/Users/{id}

Vrne podrobnosti uporabnika z določenim ID-jem.

### GET /api/Users/username/{username}

Vrne podrobnosti uporabnika z določenim uporabniškim imenom.

### GET /api/Users/email/{email}

Vrne podrobnosti uporabnika z določenim elektronskim naslovom.

### POST /api/Users/login

Omogoča prijavo uporabnika v sistem.

### GET /api/Users/usernameById/{userId}

Vrne uporabniško ime uporabnika na podlagi določenega ID-ja.

## Kontrole za obdelavo inventarja

### POST /api/Users/Inventory/AddToInventory

Dodajanje novega inventarja.

### GET /api/Users/Inventory/GetInventory

Vrne seznam vsega inventarja.

### GET /api/Users/Inventory/GetInventoryByUser/{userId}

Vrne inventar določenega uporabnika na podlagi ID-ja.

### DELETE /api/Users/Inventory/DeleteInventory/{id}

Izbriše inventar z določenim ID-jem.
