# POC gap-plan — van huidige demo naar baseline-dekking

**Datum:** 26-06-2026
**Basis:** `docs/design/Baseline beschrijving nieuw inwonerportaal.md` (v1.0)
**Doel:** concreet, incrementeel plan om de POC stap voor stap richting de
functionele baseline (10 epics) te brengen, bovenop de bestaande architectuur
(Spring BFF + React, `XworksClient`-ACL met `stub`/`xworks`-profiel).

---

## 1. Huidige dekking

| Epic (baseline) | Status POC | Bestaande bouwstenen |
|---|---|---|
| 1 — Toegang & Auth | ◐ deels | Authentik ervoor; `HuidigeBurger` (BSN). In-app DigiD/sessie nog niet |
| 2 — Persoonlijke gegevens | ✅ kern | `getPersoon`, `updateContactgegevens`; `MijnGegevens.tsx`, `Contactgegevens.tsx` |
| 3 — Lopende zaken | ✅ kern | `getZaken`; `MijnZaken.tsx` |
| 4 — Voorzieningen (Wmo/Jeugd) | ✗ | — |
| 5 — Uitkeringen & vorderingen | ✗ | — |
| 6 — Documenten | ✗ | — |
| 7 — Zelf regelen (aanvragen) | ✅ kern | volledige aanvraag-lifecycle + evaluatie + bijlagen; `Aanvraag.tsx` |
| 7.7 — Mede-ondertekening | ✅ | `nodigPartnerUit`/`getMedeondertekenVerzoek`/`partnerOndertekent`; `Medeondertekenen.tsx` |
| 8 — Inrichting & Beheer | ◐ impliciet | metadata-gedreven vragenlijsten; geen beheer-UI |
| 9 — Notificaties & 2-weg | ✗ | mail wordt niet echt verstuurd |
| 10 — WIZportaal-modules | ✗ | — |

**Conclusie:** de lees-/schrijfkern (Epic 1–3 + 7) staat. De goedkoopste, meest
zichtbare volgende winst is het **afmaken van de inzage-epics 4, 5 en 6** — ze
volgen exact het bestaande `getPersoon`/`getZaken`-patroon en zijn read-only
(laag risico, geen schrijfpad naar X-Works nodig).

---

## 2. Aanbevolen volgorde

1. **Increment A — Inzage compleet (Epic 4, 5, 6).** Read-only, kopieert het
   `Persoon`/`Zaak`-patroon. Levert direct een "vol" dashboard op voor demo's.
2. **Increment B — Notificaties & uploads (Epic 9).** Vereist de `Mailer`-poort
   (al voorzien) + upload-bij-bestaande-zaak. Sluit aan op de bestaande
   bijlage-logica uit Epic 7.
3. **Increment C — Beheer-laag zichtbaar maken (Epic 8).** Publicatie-vlaggen
   expliciet maken (welke zaaktypen/statussen/documenttypen zichtbaar zijn).
4. **Increment D — Echte X-Works-koppeling (`xworks`-profiel).** SOAP/StUF, en
   **deployment per klant** (per X-Works-webserver).
5. **Increment E — WIZportaal-modules (Epic 10).** Configureerbaar/uitschakelbaar.

---

## 3. Increment A — Inzage compleet (Epic 4, 5, 6)

Volledig analoog aan de bestaande `Zaak`-inzage. Per onderdeel: model + ACL-methode
+ stub-data + controller-endpoint + frontend-tab.

### 3.1 Model (`acl/model/`)

- `Voorziening` — `id`, `soort` (`WMO`/`JEUGD`), `omschrijving`, `zorgleverancier`, `datumVanaf`, `datumTot`, `actueel` (bool). *(Epic 4)*
- `Uitkering` — `id`, `soort`, `uitkeringsnummer`, `datumVanaf`, `datumTot`, `actueel`. *(Epic 5.1)*
- `Vordering` — `id`, `omschrijving`, `bedrag`, `datum`, `gerelateerdeZaak`, `openstaand` (bool). *(Epic 5.3)*
- `Document` — `id`, `documentnummer`, `omschrijving`, `documenttype`, `datum`, `downloadUrl`. *(Epic 6)*
- (Uitkeringsspecificaties komen als `Document` met type "Uitkeringsspecificatie" — Story 5.2.1.)

### 3.2 ACL — uitbreiden `XworksClient`

```java
// --- Inzage (uitbreiding) ---
List<Voorziening> getVoorzieningen(String bsn);   // X-Works lsd: Wmo + Jeugd
List<Uitkering>   getUitkeringen(String bsn);      // X-Works lsd: uitkeringenoverzicht
List<Vordering>   getVorderingen(String bsn);      // X-Works lsd: vorderingen
List<Document>    getDocumenten(String bsn);       // X-Works lsd: documenten (gepubliceerde typen)
byte[]            downloadDocument(String bsn, String documentId);
```

- **Stub** (`XworksClientStub`): in-memory lijstjes voor demo-BSN 999993653, met
  zowel `actueel` als `historisch` items zodat de tab-telling (Actueel/Historisch)
  demonstreerbaar is. `downloadDocument` levert een placeholder-PDF.
- **`XworksSoapClient`**: `throw new UnsupportedOperationException` met TODO naar
  de juiste X-Works-operatie (consistent met de bestaande placeholders).

### 3.3 Controller

Uitbreiden van `PortaalController` (of een nieuwe `DossierController`):

| Method | Path | Bron |
|---|---|---|
| GET | `/api/voorzieningen` | `getVoorzieningen` |
| GET | `/api/uitkeringen` | `getUitkeringen` |
| GET | `/api/vorderingen` | `getVorderingen` |
| GET | `/api/documenten` | `getDocumenten` |
| GET | `/api/documenten/{id}/download` | `downloadDocument` (octet-stream) |

BSN steeds via `HuidigeBurger` (zoals bestaand). Lege lijst → `200 []` (de
frontend toont de lege staat, Story 1.3.2), geen 404.

### 3.4 Frontend

- Types in `types.ts` bijwerken (Voorziening, Uitkering, Vordering, Document).
- Nieuwe pagina's/tabs: `Voorzieningen.tsx` (Wmo/Jeugd, Actueel/Historisch),
  `Uitkeringen.tsx` (+ vorderingen-sectie), `Documenten.tsx` (gegroepeerd per
  type, downloadknop). Navigatie conform Story 1.3.1.
- Hergebruik het bestaande tab-/telling-patroon uit `MijnZaken.tsx`.

### 3.5 Acceptatie (uit baseline)

- Voorziening toont omschrijving, zorgleverancier, datum vanaf/tot; tellingen
  per Actueel/Historisch (Story 4.1.x/4.2.x).
- Uitkering toont soort, nummer, looptijd (Story 5.1.x); vordering toont
  omschrijving, bedrag, datum, gerelateerde zaak, openstaand/afgehandeld (5.3.1).
- Documenten gegroepeerd per gepubliceerd documenttype met telling + download
  (Story 6.1.x).

**Inschatting:** klein–middel; volgt 1-op-1 het bestaande inzage-patroon.

---

## 4. Increment B — Notificaties & 2-richting (Epic 9)

- **`Mailer`-poort** (al voorzien als pending): interface `Mailer` met
  `LogMailer` (default, logt) + later `XworksMailer` (core-email) of `SmtpMailer`.
  Lost meteen het openstaande punt op dat mede-ondertekenuitnodigingen nu alleen
  gelogd worden.
- **Notificatie bij statuswijziging/besluit** (Story 9.1.1) — kanaal
  configureerbaar (portaal/e-mail/sms).
- **Stukken aanleveren bij bestaande zaak** (Feature 9.2) — hergebruik de
  bijlage-logica (`voegBijlageToe`) maar gericht op een bestaande `Zaak` i.p.v.
  een lopende aanvraag. Nieuwe ACL-methode `voegStukToeAanZaak(bsn, zaakId, ...)`.
- **Tweerichtingsberichten met professional** (Feature 9.3) — bericht-model +
  ACL `getBerichten`/`stuurBericht`, gekoppeld aan zaak.

**Inschatting:** middel. De `Mailer`-poort is een kleine, op zichzelf staande stap
die los kan landen.

---

## 5. Increment C — Beheer-laag (Epic 8)

In X-Works bepaalt de functioneel beheerder via publicatie-vlaggen wat zichtbaar
is ("Publiceren op digitaal loket", "Omschrijving generiek"). In de POC nu impliciet.

- Stub-data verrijken met een `gepubliceerd`-vlag + `omschrijvingGeneriek` op
  zaaktypen, statussen, documenttypen, producten.
- Inzage-endpoints filteren op `gepubliceerd` en tonen de generieke omschrijving
  (Story 3.3.1, 6.1.1, 8.x). Geen beheer-UI in de POC — beheer blijft in X-Works.

**Inschatting:** klein (vooral filteren + omschrijvingen), maakt de demo
realistischer t.o.v. het echte gedrag.

---

## 6. Increment D — Echte X-Works-koppeling (`xworks`-profiel)

- `XworksSoapClient` implementeren tegen `xws` SOAP / StUF / `wiz-vltmpl00-getocc`.
- Endpoint-config per omgeving via environment-variabelen (zie `application.yml`).
- **Deployment per klant**: het portaal draait op de webserver van elke
  X-Works-instantie (afspraak uit de PvE-notitie). Raakt `docker-compose.yml`
  (één deployment per klant i.p.v. één gedeelde container) en de
  Authentik/gatekeeper-inrichting per omgeving.
- Velden-metadata-discovery (zie `docs/field-metadata-discovery.md`) blijft
  read-only/discovery; runtime altijd via de servicelaag.

**Inschatting:** groot; dit is het echte integratiewerk (#1 uit de scope-slide).

---

## 7. Increment E — WIZportaal-modules (Epic 10)

Alle modules **configureerbaar/uitschakelbaar** en afhankelijk van een
WIZportaal-koppeling: Afspraken (10.1), Integraal Plan (10.2, raakt #4 uit de
slide), Contactpersonenportaal (10.4), alternatieve DigiD-inlog (10.5). Webformulieren
→ Signaal API (slide #3) hoort hier conceptueel bij (alleen X-Works+WIZ-klanten,
vermoedelijk Opmeer/Koggenland).

**Inschatting:** groot + afhankelijk van externe koppeling; laatste prioriteit.

---

## 8. Cross-cutting (geldt over increments heen)

- **U7 — WCAG 2.1 AA / EN 301 549**: meenemen in de React-componenten vanaf nu
  (semantische HTML, focus, contrast), niet achteraf.
- **U3 — geen dubbele registratie**: alle mutaties via de servicelaag, nooit
  direct naar het datamodel (al verankerd in de ACL-Javadoc).
- **AVG-opschoning** (Feature 7.6): concept-aanvragen pas zaak bij indienen;
  opschoning + audit-logging bij niet-ingediende/niet-inwoner.
- **Stipter-huisstijl vs. Verius Samen UX-design**: zodra de UX-designs
  (`docs/design/screenshots/`) er zijn, bepalen of de huidige huisstijl wijkt
  voor het nieuwe Verius Samen-design.

---

## 9. Voorgestelde eerste stap

**Increment A starten met Epic 6 (Documenten)** als kleinste, meest op zichzelf
staande toevoeging (model + 1 ACL-methode + 1 endpoint + 1 tab + download), of
met **Epic 4 (Voorzieningen)** voor de meeste demo-waarde. Beide volgen het
bestaande patroon en zijn los te reviewen.
