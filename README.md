# VS X-Works InwonerPortaal

Eerste opzet voor de migratie van het X-Works "klantenportaal" (Uniface 9 + XSLT/XRIA) naar een
moderne stack: **Java 17 / Spring Boot** (backend-for-frontend) + **React / TypeScript / Vite**.

De koppeling met X-Works loopt uitsluitend via een **anti-corruption layer (ACL)**. In deze opzet is
die ACL afgevangen met een **stub** (in-memory testdata), zodat het hele portaal draait zonder een
live X-Works/Uniface-omgeving.

## Architectuurprincipe

```
React (Vite, :5173)
      │  REST /api
      ▼
Spring Boot BFF (:8080)
      │  XworksClient  ← anti-corruption layer (de enige poort naar X-Works)
      ├─ profiel "stub"   → XworksClientStub      (in-memory testdata)   ← nu actief
      └─ profiel "xworks" → XworksSoapClient       (StUF / xws SOAP)      ← nog te bouwen
```

**Belangrijk:** het portaal schrijft nooit rechtstreeks naar het X-Works datamodel. Alle schrijfacties
gaan via de X-Works servicelaag (StUF/ZDS, de `xws` SOAP-adapter, of de `xria-wiz_*`-operaties), zodat
business-logica, validatie, sleutelgeneratie en concurrency in X-Works blijven — en de gemeente de door
de burger ingevoerde gegevens automatisch in haar eigen omgeving ziet.

## Functionaliteit in deze opzet

| Functie | Bron in X-Works | Type |
|---|---|---|
| Mijn gegevens (inzage) | `lsd-persoon`, entiteit PERSOON | lezen |
| Mijn zaken (inzage) | `lsd-zaken`, ZS/ZKN | lezen |
| Contactgegevens wijzigen | CONTACTGEGEVENS (editable) | muteren |
| Aanvraag/vragenlijst invullen | `lsd-generate-form` + `vragenlijstTemplate` | muteren |
| Regelevaluatie (totaal, zichtbaarheid, validatie) | `vragenlijst-eval` | berekenen |
| Kostenpost-rij toevoegen/verwijderen | `addocc` / `remocc` | muteren |
| Concept opslaan | `vragenlijst-save` | muteren |
| Bijlage toevoegen/verwijderen | `dropzone-verwerk` / `removeAttachedFile` | muteren |
| Aanvraag indienen | `vragenlijst-submit` → `zak-saveWizard` | muteren |
| Ondertekenen met DigiD | `vragenlijst-signDigiD` | handeling |
| Aanvraag afbreken | `vragenlijst-abort` | handeling |

> De handelingen spiegelen exact de analyse van wat de burger in het huidige X-Works-portaal kan
> doen. De regelevaluatie zit nu vereenvoudigd in de stub; in de echte koppeling wordt `vragenlijst-eval`
> in X-Works aangeroepen.

## Draaien met Docker (poort 10040) — aanbevolen

Eén container: de React-build wordt door Spring Boot als statische bestanden geserveerd, frontend
en `/api` draaien op dezelfde origin op poort **10040**.

```powershell
cd C:\Stipter\verius\Verius-SW\VS_Xworks_InwonerPortaal
docker compose up --build -d
```

Open `http://localhost:10040`. Stoppen: `docker compose down`.

Zonder compose kan ook:

```powershell
docker build -t vs-xworks-inwonerportaal:0.1.0 .
docker run --rm -p 10040:10040 vs-xworks-inwonerportaal:0.1.0
```

## Lokaal draaien (zonder Docker)

### Backend (poort 8080)

```powershell
cd backend
mvn spring-boot:run
```

Test: `http://localhost:8080/api/persoon`

### Frontend (poort 5173)

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. De Vite-dev-server proxy't `/api` naar de backend.

## Authenticatie (DigiD) — nog te doen

In deze opzet wordt de ingelogde burger bepaald door {@code HuidigeBurger} via een tijdelijke
header `X-Demo-Bsn` met terugval op een demo-BSN. Vervang dit door echte DigiD-authenticatie
(Spring Security SAML2 of een Logius-broker/OIDC); de BSN komt dan uit de sessie, nooit uit een
client-header.

## Volgende stappen

1. `XworksSoapClient` implementeren tegen de StUF/`xws`-servicelaag (begin met `getPersoon`).
2. DigiD-authenticatie aansluiten + autorisatie ("mag deze BSN dit?") en audit-logging.
3. De dynamische vragenlijst-/aanvraagengine bouwen (X-Works `lsd-generate-form` + `vragenlijst-eval`).
4. Documenten (upload/download) en eventueel DigiD-ondertekening (story 14408).

Zie de analyse in de kennisrepo: `wiki/synthesis/xworks-burger-inzageportaal.md`.
