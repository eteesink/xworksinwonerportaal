# Checklist / vragen aan de X-Works-leverancier — ontsluiting vragenlijst-template

> Doel: vaststellen of we de vragenlijst-template (per gemeente) en de bijbehorende schrijfacties
> uit X-Works kunnen ophalen/aanroepen vanuit een nieuw inwonerportaal (Spring BFF), bij voorkeur via
> de bestaande `xws` SOAP-laag, of anders via een nieuw te bouwen endpoint (optie C).
> Onderstaande termen komen uit de X-Works XSLT-resources (module `wiz`/`xws`).

## Context (kort, voor de leverancier)
We bouwen een moderne voorkant (React + Spring) die het X-Works "klantenportaal" vervangt. De koppeling
loopt via één anti-corruption layer; we schrijven **niet** rechtstreeks naar de Uniface-DB, maar willen
de bestaande X-Works-operaties/services aanroepen. We zien dat de vragenlijst-template als data bestaat
(`VRAGENLIJSTTEMPLATE → PAGINA → VRAAGBOOM → SVRAAG`, gekoppeld via `TAG_*`, per `ADMINISTRATION`), en dat
`wiz-vltmpl00-getocc.xslt` slechts de METRO-UI-rendering is. We willen het onderliggende data-document.

## A. xws-endpoint & WSDL
1. Wat is de **xws SOAP-endpoint-URL** per omgeving (dev / acc / prod)?
2. Is er een **WSDL** beschikbaar? Waar (URL of bestand in `resources/fil`)? Kunnen we die ontvangen?
3. Welke **operaties** publiceert de WSDL nu? (lijst, of de WSDL zelf)
4. Welke **authenticatie** geldt op het endpoint (mTLS, basic, API-key, IP-allowlist)?
5. Welke **namespace(s)** en berichtnaam-conventie worden gebruikt (we zien `http://wxs.company-x.nl`, `ZKN`)?

## B. Template-ophaal (lezen)
6. Zijn de **getocc-operaties** voor de template als **web-service/`USIOPER`** gepubliceerd, of alleen intern (XRIA)?
   Concreet: `vltmpl00-getocc`, `pagina00-getocc`, `vraagboom00-getocc`, `svraag00-getocc`.
7. Zo ja: kunnen we per **`ADMINISTRATION`** (gemeentecode, bijv. `opmr`) en per **template-type** het
   **"XML Form document"** (de `dataSet`/`occ`-structuur) ophalen? Graag een **voorbeeldrespons**.
8. **Veld-metadata** (deels al gevonden op de `VRAAG`-entiteit): bevestig svp:
   - de **domeinwaarden van `VRAAG.CD_WIDGETTYPE`** en hun betekenis (welke code = tekst/getal/bedrag/datum/keuze);
   - de **codelijst achter `VRAAG.CD_BRONANTWOORD`** (optie-waarden + labels) — en wanneer `VC_BRONANTWOORD` (inline) wordt gebruikt;
   - **waar `verplicht` wordt vastgelegd**: een tag op `PAGINA_ELEMENTEN`/`TAG_VRAAG` (`CD_TAG`/`VC_VALUE`/`JSON_VALUE`) of een proc-validatie?
9. Hoe is **volgorde** (we zien `NM_IDX` op `TAG_*`) en **geldigheid** (`DM_GELDIG_VAN`/`DM_GELDIG_TM`) bedoeld?
10. Is er een **catalogus**-bevraging (lijst beschikbare `VRAGENLIJSTTEMPLATE` per `ADMINISTRATION`)?
11. **Versie/wijziging**: kunnen we `MODIFIEDDATIM`/`VERSION` meekrijgen voor caching/invalidatie?

## C. Optie C — samengesteld template-endpoint (als B niet kan)
12. Kunnen jullie een **nieuwe operatie/endpoint** leveren die de **complete template-boom** in één call
    teruggeeft (root → pagina's → vraagboom → svraag, mét volgorde, geldigheid en veld-metadata),
    bij voorkeur als **JSON** (of XML), op iets als `GET /templates/{type}?administration={code}`?
13. Wat is daarvoor de **inschatting** (bouw + test) en op welke termijn?

## D. Schrijfacties (voor later, zelfde koppeling)
14. Zijn deze operaties ook als service aanroepbaar (of te ontsluiten)?
    - aanvraag opslaan/indienen: `vragenlijst-save`, `vragenlijst-submit` → `zak-saveWizard`
    - regelevaluatie: `vragenlijst-eval`
    - contactgegevens muteren (CONTACTGEGEVENS, `showTransactionOnInput`)
    - bijlagen: `dropzone-verwerk` / `removeAttachedFile` (en documentcreatie via StUF-DCR)
15. Hoe werkt het **transactie-/concurrencyprotocol** bij schrijven (`_id`/`_status`/`_crc`/`VERSION`)?

## E. Inzage (lezen, voor de overige portaalschermen)
16. Persoon (`PERSOON` + `ADRES`/`CONTACTGEGEVENS`/`BANKREKENING`) per BSN — via service of StUF-BG (`npsLv01`)?
17. Zaken/dossiers per BSN — via StUF-ZDS (`ZDS0120-beantwoordVraag`) of een eigen operatie?

## F. Randvoorwaarden
18. **Omgevingen & testdata**: een dev/acc-endpoint + een bekende template-`type` per `ADMINISTRATION` om tegen te testen.
19. **Autorisatie/scoping**: hoe borgt X-Works dat een burger alleen zijn eigen gegevens ziet (BSN→`PERSOON.NM_BSN`)?
20. **Contactpersoon** voor de technische afstemming (we zien `richardbollee` als SVN-author van de `wiz`/`xws`-resources).

---
*Antwoorden op A–B bepalen of we direct via `xws` kunnen (snelste), C is de fallback (zie `xworks-template-service.md` en `optie-c-template-endpoint.md`).*
