# Optie C — samengesteld template-endpoint (uitwerking)

> Eén nieuwe X-Works-service die de **complete vragenlijst-template** in één call levert, in een
> **portaal-vriendelijk contract** (JSON). De BFF hoeft dan geen Uniface-interne structuur te kennen.
> Alternatief voor het los aanroepen van `vltmpl`/`pagina`/`vraagboom`/`svraag` getocc via `xws`.

## 1. Waarom C (en niet de losse getocc's)
- **Eén round-trip** i.p.v. een boom van getocc-calls die de client zelf moet samenstellen.
- Het endpoint is **zelf een anti-corruption boundary**: het spreekt de *published language* van het
  portaal, niet de Uniface-interne entiteiten. Wijzigt X-Works intern iets, dan blijft het contract gelijk.
- **Geen METRO-UI/XSLT** in de keten — puur data.
- Veld-metadata (type/verplicht/opties), volgorde (`NM_IDX`) en geldigheid (`DM_GELDIG_VAN/TM`) worden
  **server-side** opgelost; de BFF krijgt een kant-en-klare definitie.

## 2. Endpoint-contract

**Catalogus** (welke vragenlijsten heeft deze gemeente):
```
GET /xworks/templates?administration={code}
→ 200 [ { "type": "schuldhulp", "titel": "Aanmelding schuldhulpverlening" }, ... ]
```

**Eén template** (de definitie):
```
GET /xworks/templates/{type}?administration={code}
→ 200  (zie schema hieronder)
→ 404  onbekend type/administration
```

### Response-schema (sluit aan op het portaal-model)
```jsonc
{
  "type": "schuldhulp",
  "titel": "Aanmelding schuldhulpverlening",
  "administration": "opmr",
  "version": "2026-02-10T11:32:00Z",      // uit MODIFIEDDATIM/VERSION → caching
  "paginas": [                             // X-Works PAGINA → wizardstappen (optioneel platgeslagen)
    {
      "code": "situatie",
      "titel": "Situatie",
      "vragen": [
        {
          "id": "woonsituatie",            // ← SVRAAG.VC_CODE
          "label": "Woonsituatie",         // ← SVRAAG.VC_OMSCHRIJVING / VC_TEKST
          "type": "keuze",                 // ← veld-metadata (zie §4)
          "verplicht": true,
          "opties": ["Huurwoning", "Koopwoning", "Inwonend", "Anders"],
          "zichtbaarAls": null,
          "subvragen": null
        },
        {
          "id": "schulden",                // ← VRAAGBOOM als herhaalbare groep
          "label": "Schuldeisers",
          "type": "groep",
          "verplicht": true,
          "subvragen": [
            { "id": "schuldeiser", "label": "Schuldeiser", "type": "tekst",  "verplicht": true },
            { "id": "bedrag",      "label": "Bedrag (€)",   "type": "bedrag", "verplicht": true }
          ]
        }
      ]
    }
  ]
}
```

Dit is bewust bijna identiek aan onze `VragenlijstDefinitie`/`VraagDefinitie`. Verschil: een extra
**`paginas`**-niveau (X-Works `PAGINA` → wizardstappen). Twee opties voor de BFF:
- **Platslaan** naar de huidige flat `vragen[]` (snelste; pagina's gaan verloren als stap-indeling), of
- het portaal-model uitbreiden met **`paginas`** → meerstaps-wizard (mooier, dichter bij X-Works).

## 3. Wat de X-Works/Uniface-kant doet
De nieuwe operatie stelt de boom samen en mapt naar het contract:

1. Zoek `VRAGENLIJSTTEMPLATE` voor (`type`, `ADMINISTRATION`).
2. Loop `PAGINA`'s; sorteer.
3. Per pagina: volg `TAG_SVRAAG → VRAAGBOOM` (groepen), sorteer op **`NM_IDX`**.
4. Per vraagboom: volg `TAG_VRAAG → SVRAAG` (vragen), sorteer op **`NM_IDX`**.
5. **Filter geldigheid**: alleen items waar `DM_GELDIG_VAN ≤ vandaag ≤ DM_GELDIG_TM` (of leeg).
6. Map velden: `VC_OMSCHRIJVING`/`VC_TEKST` → `label`; voeg **type/verplicht/opties** toe (§4).
7. Zet `version` = hoogste `MODIFIEDDATIM`/`VERSION` van de betrokken occurrences.

## 4. Veld-metadata mapping (grotendeels gelokaliseerd)
De metadata zit op de **`VRAAG`**-entiteit (model WIZ), waarnaar `SVRAAG` verwijst via `SVRAAG_VRAAG`:

| Portal-veld | X-Works-bron (entiteit.veld) | Status |
|---|---|---|
| `type` | **`VRAAG.CD_WIDGETTYPE`** | gevonden; domeinwaarden → portal-type nog te mappen |
| `opties[]` | **`VRAAG.CD_BRONANTWOORD`** (codelijst) + **`VRAAG.VC_BRONANTWOORD`** (inline) | gevonden; codelijst-waarden ophalen |
| `label` | **`VRAAG.VC_LABEL_EXTERN`** (extern/burger) | gevonden |
| hulptekst | **`VRAAG.VC_TOELICHTING`** | gevonden |
| `groep` + `subvragen[]` | `VRAAGBOOM`/`SVRAAG` met onderliggende `VRAAG`'s | gevonden |
| geldigheid | `VRAAG.DM_GELDIG_VAN/TM` | gevonden |
| **`verplicht`** | waarschijnlijk een **tag** op `PAGINA_ELEMENTEN`/`TAG_VRAAG` (`CD_TAG`/`VC_VALUE`/`JSON_VALUE`) of een proc-validatie | **te bevestigen** |
| `zichtbaarAls` | conditionele regel/tag op de plaatsing (indien aanwezig) | te bevestigen |

Nog te bevestigen (via DB-query op een echte template, zie `field-metadata-discovery.md`):
1. de **`CD_WIDGETTYPE`-domeinwaarden** en hun mapping naar `tekst/getal/bedrag/datum/keuze`;
2. de **codelijst** achter `CD_BRONANTWOORD` (optie-waarden + labels);
3. waar **`verplicht`** wordt vastgelegd (tag vs. proc-regel).

## 5. BFF-integratie
Profiel `xworks`: `XworksClient.getVragenlijstDefinitie(type)` →
```
1. HTTP GET /xworks/templates/{type}?administration={gemeente}   (REST-client met auth)
2. deserialiseer JSON → VragenlijstDefinitie  (Jackson, contract = ons model)
3. (optioneel) VragenlijstTemplateMapper voor platslaan paginas / validatie
```
Omdat het contract het portaal-model spreekt, is de mapping **triviaal** (directe deserialisatie), in plaats
van het parsen van een Uniface `dataSet`. De `VragenlijstTemplateMapper` blijft als dunne validatie/normalisatie.

## 6. Niet-functioneel
- **Auth**: service-to-service (mTLS of client-credentials), apart van de DigiD-burgersessie.
- **Caching**: cache per (`type`, `administration`) op `version`; stuur `ETag: <version>`, BFF doet
  `If-None-Match` → `304`. Templates wijzigen zelden → grote winst.
- **Read-only / idempotent**: geen `_crc`/`_status` nodig (dat is voor schrijven).
- **Fouten**: 404 onbekend, 502/503 bij X-Works-storing → nette melding in het portaal.
- **Per gemeente**: `administration` verplicht; lege/onbekende code → 400/404.

## 7. Aanpak: contract-first
1. **Leg dit JSON-contract vast** als de afspraak (deze sectie = de spec). 
2. In de BFF: zet de **stub** om naar dit JSON-contract (ipv de huidige XML-template-resource) zodat front-
   en backend al tegen het echte contract draaien vóór X-Works klaar is.
3. Leverancier implementeert de operatie achter `/xworks/templates/...`.
4. Zet profiel `xworks` aan → `XworksClient` wijst naar de echte service; **niets aan de frontend wijzigt**.
5. Idem uitrollen voor de catalogus en (later) de schrijfacties.

## 8. Open punten
- Veld-metadata-vindplaats (§4) — blokkerend voor volledige rendering.
- `paginas` wel/niet in het portaal-model (productkeuze: meerstaps-wizard?).
- Wie host het endpoint: binnen X-Works (Uniface-operatie + `xws`/REST) of een dunne adapter ervoor?

---
*Zie ook `xworks-template-service.md` (opties A/B/C) en `leverancier-checklist-xws.md` (vragen A–F).*
