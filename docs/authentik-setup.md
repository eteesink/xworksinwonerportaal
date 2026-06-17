# Authentik-gatekeeper — instellingen voor het inwonerportaal

Het inwonerportaal draait als één container (`vs-inwonerportaal`, poort 10040) op het netwerk
`verius-gatekeeper_gatekeeper`, **zonder host-poort**. Authentik staat ervoor als gatekeeper en
proxy't naar de container. De container serveert op dezelfde origin zowel de SPA als de `/api`-endpoints.

> De image is correct: zowel `/` als `/api/...` geven rechtstreeks HTTP 200. Werkt `/api` niet via
> de gatekeeper, dan zit het in de Authentik **Proxy Provider**-configuratie hieronder.

## 1. Proxy Provider (modus: Proxy)

Maak/gebruik een **Proxy Provider** in modus **Proxy** (niet alleen Forward-auth). In deze modus
proxy't de outpost *alle* paden naar de app — dus ook `/api`.

| Veld | Waarde |
|---|---|
| Name | `inwonerportaal` |
| Authorization flow | jullie standaard (bijv. `default-provider-authorization-explicit-consent`) |
| **External host** | de publieke URL, bijv. `https://inwonerportaal.verius.local` |
| **Internal host** | `http://vs-inwonerportaal:10040` |
| Internal host SSL validation | **uit** (plain http binnen het docker-netwerk) |

> **Let op:** de *Internal host* moet exact `http://vs-inwonerportaal:10040` zijn — **zonder pad**
> erachter en zonder rewrite. Een verkeerd pad hier laat de SPA (root) vaak nog laden terwijl
> `/api` afketst.

## 2. Application + Outpost

- Koppel de provider aan een **Application** (slug bijv. `inwonerportaal`) voor de toegangsrechten.
- Wijs de provider toe aan een **Outpost** (embedded of een eigen proxy-outpost).
- De **outpost-container moet op het netwerk `verius-gatekeeper_gatekeeper`** zitten, anders kan hij
  de naam `vs-inwonerportaal` niet resolven. (De app-container hangt er al aan via de compose.)

## 3. Geen extra unauthenticated-paths voor /api

Laat `/api` gewoon door de normale auth lopen. Zet `/api` **niet** bij "Unauthenticated Paths/URLs"
met afwijkend gedrag; het heeft dezelfde sessie nodig als de rest van het portaal.

## 4. Verificatie

Controleer welke containers op het netwerk zitten en of de outpost de app kan bereiken:

```powershell
docker ps --filter "network=verius-gatekeeper_gatekeeper" --format "{{.Names}}"
docker exec <authentik-outpost> wget -qO- http://vs-inwonerportaal:10040/api/persoon
```

- JSON van *Jan de Vries* terug → routering en naam-resolutie zijn goed; een eventueel `/api`-probleem
  zit dan in de provider-config (Internal host / modus).
- Geen antwoord → de outpost zit niet (goed) op het netwerk of de containernaam wijkt af.

## 5. Sessieverloop en de SPA

De frontend roept `/api/...` **relatief** aan (zelfde origin), dus dat werkt onder elke hostname.
Bij een **verlopen sessie** stuurt Authentik een `fetch('/api/...')` via een redirect naar de login
(of geeft 401/403). De frontend vangt dit nu af en doet een volledige navigatie naar de login
(zie `frontend/src/api/client.ts`, functie `herauthenticeer`), zodat de gebruiker netjes opnieuw
inlogt in plaats van een gebroken `/api` te zien.

## 6. Toekomst: Authentik ook als DigiD-stap?

Voor productie kan Authentik tevens de **OIDC-broker** richting DigiD zijn (zie
`architectuur-inwonerportaal.md`, hoofdstuk 6): de BFF wordt dan OIDC-client van Authentik, en
Authentik regelt de DigiD/SAML-koppeling. De BSN komt dan via het id_token in de BFF-sessie.
In de huidige demo (profiel `stub`) gebruikt het portaal nog een demo-BSN.
